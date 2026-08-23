# SwitchScope — TODO

> Ниже — приоритетная очередь. Ниже неё, начиная с раздела «Implementation Guide»,
> лежит прежний справочный материал по editable-view.

---

# Точка входа

Ветка `dev`. Последние коммиты: `5c88c3b`, `8dd2acf` — Этап 1 концепта «конфигурируемые права».

**Что закрыто:** Этап 1 раздела 1.1 (схема, seed, аннотации, `PermissionRegistry` в audit-only)
и два пункта ПРИОРИТЕТА 2 (логгер, `AccessDeniedException`).

**Что дальше по порядку:** Этап 2 раздела 1.1 (включение принуждения) → раздел 1.2 (обнуление
через PUT) → остаток ПРИОРИТЕТА 2 (10 пунктов) → Этапы 3–4 раздела 1.1 (фронтенд, уборка).

**Ничего из сделанного не меняет поведение API.** Этап 1 только наблюдает; ни один маршрут не стал
отвечать иначе, чем до него.

**Факты окружения, которые не выводятся из репозитория:**

- dev-БД (`localhost:5433/switchscope`) **уже мигрирована** — таблицы `roles` / `permissions` /
  `role_permissions` созданы и засеяны, `user_role.role_id` заполнен. Повторно накатывать нечего.
- Учётки для миграции — в `backend/src/main/resources/config.properties`, для приложения —
  в `application-local.yaml` (профиль `local`). Приложение поднимается на **порту 8090**.
- Тестовые пользователи: `admin@gmail.com` / `admin`, `user@gmail.com` / `password`.
- Проверка Этапа 1:
  `./mvnw -o test -Dtest='PermissionCodeTest,PermissionCoverageTest' -Dspring.profiles.active=local`
  и `python3 tools/generate_permission_seed.py --check` (оба из `backend/`).
- Отчёт реестра печатается на старте в `log.info`; корневой уровень — `WARN`, пакет
  `net.switchscope` — `INFO`.
- Своей БД у тестов нет: `./mvnw verify` идёт в ту же живую dev-базу.

---

# ПРИОРИТЕТ 1 — незакрытые концепты бэкенда

Оба концепта спроектированы и обоснованы в **`docs/backend-concepts.md`** (части 2 и 3).
Контекст, откуда они взялись: **`docs/backend-audit.md`** (аудит слоёв)
и **`docs/backend-fixes.md`** (что уже исправлено).

Состояние: по 1.1 сделан Этап 1 (схема, seed, аннотации, реестр в audit-only) — подробности
в конце раздела 1.1. По 1.2 кода нет.

Коммиты-предшественники в ветке `dev`: `be4f97f` (шесть критичных дефектов write-path) и
`1f66256` (вывод дискриминатора из `componentTypeId`). В тексте ниже они местами упомянуты как
`b43f88c` / `2d14ec3` — таких хешей в текущей истории нет; чем вызвано расхождение, не выяснялось,
читать как указанные выше.

---

## 1.1 Конфигурируемые права доступа

**Задача.** Каждая операция API — отдельный пункт, который в конфигурации либо разрешён, либо
запрещён, без пересборки. Сейчас гранулярность нулевая: `Role` — enum из `USER`/`ADMIN`, права
зашиты в `@PreAuthorize("hasRole('ADMIN')")`, единственная «настройка» — выдать ADMIN, то есть всё
сразу.

**Модель** (детали — `docs/backend-concepts.md`, часть 2):

```
User ──< user_roles >── RoleEntity ──< role_permissions >── PermissionEntity
```

- `PermissionEntity` наследует `BaseCodedEntity`; код вида `<домен>.<ресурс>:<действие>`,
  например `catalog.component-type:update`.
- `RoleEntity` вместо enum `Role`.
- **`role_permissions` и есть конфигурация**: строка есть → разрешено, нет → запрещено.
- Проверка через `hasAuthority(...)`, **не** `hasRole` — `RoleHierarchy` применяется только к
  `hasRole`, смешение двух стилей даёт трудноуловимые баги.

**Центральная часть — верифицируемость, а не схема.** Таблица прав бесполезна, если нельзя
проверить покрытие: именно так и вышло с `@PreAuthorize` — аннотации были, механизма не было.
`PermissionRegistry` по образцу существующего `InstallableComponentRegistry` сканирует
`@RequiresPermission` на старте и сверяет с БД:

- [x] код есть, в БД нет → операция неконфигурируема — ERROR
- [x] в БД есть, в коде нет → мёртвая строка — WARN
- [x] **эндпоинт без аннотации** → ровно та дыра, что жила в проекте — ERROR
- [ ] `denyAll()` по умолчанию для `/api/**`, чтобы забытая аннотация закрывала, а не открывала — Этап 4
- [x] `GET /api/admin/permissions/matrix` — машиночитаемая матрица для CI и диффа между средами

**Обязательно с первого дня:** кэш authorities. Аутентификация stateless HTTP Basic →
`loadUserByUsername` выполняется на каждый запрос; сейчас это одна таблица, станет три.
`@EnableCaching` в `AppConfig` уже есть. Кэшировать authorities, не сущность `User`
(иначе в кэш попадёт пароль и detached-граф Hibernate).

> Срок — Этап 2, а не Этап 1: в audit-only `UserDetailsService` по-прежнему читает enum `Role`,
> лишних запросов на каждый HTTP-вызов ещё нет. Кэш нужен ровно в тот момент, когда authorities
> начнут собираться из `roles` + `role_permissions`.

**Порядок работ:**

- [x] Этап 1 — схема, seed, `@RequiresPermission` на всех эндпоинтах, реестр в режиме
      **audit-only** (только отчёт, ничего не блокирует). `@PreAuthorize` пока не трогать.
      **Сделано.** Отчёт на старте: 109 эндпоинтов — 101 под правом, 8 явно вне модели
      (`@AuthenticatedOnly`: `/api/auth/**`, `/api/profile`), 0 без аннотации; 81 право,
      0 отсутствующих в БД, 0 мёртвых строк. См. «Что сделано в Этапе 1» ниже
- [ ] Этап 2 — включать принуждение по доменам, начиная с `catalog`.
      **Механизм принуждения нужно выбрать до начала — см. «Открытый вопрос Этапа 2» ниже:**
      `@PreAuthorize` на методе `AbstractCrudController` физически не может дать девяти наследникам
      девять разных прав
- [ ] Этап 3 — фронтенд: `/api/auth/check` отдаёт `permissions`; `usePermissions()`;
      `meta.roles` → `meta.permission` в 46 маршрутах; гейт кнопок в `CellActions.vue`
- [ ] Этап 4 — удалить enum `Role`, `hasRole` оставить только на `/api/admin/**`, включить `denyAll()`

**Не забыть при этом:**

- [ ] `frontend/src/utils/roles.js` — **удалить**. Никем не импортируется и падает при загрузке
      (`ROLE_PERMISSIONS` ссылается сам на себя в собственном инициализаторе → `ReferenceError`);
      список прав в нём про MAC-адреса, наследие другого проекта
- [ ] `LoginResponseTo` — **добавить** `permissions`, `roles` оставить строками, иначе сломается
      `useAuth.js` / `services/auth.js` / `UserAccount.vue`
- [ ] Отдельно решить с ролями: после `be4f97f` метод-секьюрити включена, и USER,
      который раньше писал в каталоги, получает 403; фронтенд по-прежнему показывает ему
      эти маршруты. **Этап 1 это не изменил** — он ничего не принуждает.
      Что уже готово к решению: seed `fill/05` даёт USER ровно сегодняшний доступ (чтение везде,
      запись везде кроме каталогов, 56 прав из 81), то есть после Этапа 2 поведение останется
      прежним, а не станет строже. Развилка сузилась до одного выбора — **оставить каталоги
      закрытыми и убрать их из фронтенд-маршрутов USER, или выдать USER права
      `catalog.*:create/update/delete` строкой в `role_permissions`.** Второе — UPDATE в таблице,
      без пересборки; на этом требование «конфигурируемости» и проверяется первый раз

### Что сделано в Этапе 1

Реестр на старте печатает: **109 эндпоинтов — 101 под правом, 8 явно вне модели, 0 без
аннотации; 81 право, 0 отсутствующих в БД, 0 мёртвых строк.**

**Аннотации** (`net.switchscope.security.permission`):

- `@PermissionResource("catalog.component-type")` — на классе контроллера, даёт префикс
  `<домен>.<ресурс>`;
- `@RequiresPermission("update")` — на методе, только действие; код собирается из двух частей.
  Это и есть решение проблемы `AbstractCrudController`: он объявляет пять маппингов **один раз**
  на девять наследников, и действие, написанное там, дало бы всем девяти одно право. Действие
  живёт на общем методе, ресурс — на каждом конкретном контроллере, 20 аннотаций на классах
  вместо 45 переопределений методов;
- `@AuthenticatedOnly(reason = "…")` — явный выход из модели прав. Нужен, чтобы «прав не
  требуется» и «аннотацию забыли» были **разными** состояниями: без него self-service-маршруты
  пришлось бы либо неверно закрывать, либо молча терпеть, а терпимое исключение — это ровно то,
  как выжила прежняя дыра. Стоит на `/api/auth/**` (проверка «кто я и что мне можно» обязана
  отвечать до того, как право вообще можно вычислить) и на `/api/profile`.

**Реестр** — `PermissionRegistry`. Ключевое отличие от `InstallableComponentRegistry`, по образцу
которого он сделан: источник не classpath, а `RequestMappingHandlerMapping`. Это истина о том, что
приложение действительно обслуживает — мёртвый `AbstractCatalogController` в скан не попадает сам
собой, — и, главное, `HandlerMethod.getBeanType()` даёт **конкретный** класс контроллера, без чего
композиция «ресурс класса + действие метода» не работает.

Режим — свойство `switchscope.security.permission.mode` (`AUDIT` по умолчанию, `ENFORCE` роняет
старт при ошибках). В Этапе 1 именно `AUDIT`: смысл первого прохода — увидеть картину целиком, не
закрыв доступ живым пользователям.

**Схема и seed:** `init/03-roles`, `04-permissions`, `05-role-permissions`, `06-user-role-role-id`;
`fill/03…06`. Миграция `user_role` **аддитивная**: `role_id` добавлен, заполнен и связан FK, а
старая VARCHAR-колонка `role` остаётся авторитетной — enum `Role` ещё читают `SecurityConfig`,
`User` и `LoginResponseTo`, и удаление колонки здесь сломало бы работающее приложение. `DROP`
уходит в Этап 4.

Seed прав генерируется из самих аннотаций — `tools/generate_permission_seed.py`
(`--check` для CI). Руками 81 строку не набивают: разойдись список со сканом, и реестр
отрапортует либо неконфигурируемую операцию, либо мёртвую строку.

Раздача (`fill/05`) — **снимок сегодняшнего поведения**, а не новая политика: ADMIN получает всё,
USER — чтение везде и запись везде, кроме каталогов, которые `@PreAuthorize("hasRole('ADMIN')")`
уже закрыл (56 прав из 81). Сузить — это UPDATE в таблице, а не релиз.

**Проверка:** `PermissionCoverageTest` — те же три свойства, что и в отчёте, как тесты;
`PermissionCodeTest` — разбор и сборка кода. `GET /api/admin/permissions/matrix` отдаёт роли,
права, раздачу, все эндпоинты и три списка расхождений — для диффа между средами.

**Что Этап 1 сознательно не делает:** ничего не принуждает. `@PreAuthorize` не тронут, поведение
API не изменилось ни на один маршрут. Пункт про USER и 403 в каталогах остаётся открытым — его
закрывает Этап 2 вместе с включением принуждения.

### Открытый вопрос Этапа 2 — чем принуждать

Концепт предполагал `@PreAuthorize("hasAuthority('<код>')")` на каждом методе. На девяти
наследниках `AbstractCrudController` это **не работает**: аннотация ставится на общий метод
базового класса, выражение в ней одно, а прав нужно девять разных. Это выяснилось при реализации
Этапа 1 и той же причиной вызвано разделение `@PermissionResource` / `@RequiresPermission`.

Рассмотренные варианты:

| | Годится? |
|---|---|
| `@PreAuthorize` на методе | Нет для девяти наследников. Годится только для 11 контроллеров с собственными методами — то есть даёт ровно ту половинчатость, которой концепт велит избегать |
| Параметризованная мета-аннотация над `@PreAuthorize` (template annotations, Spring Security 6.4+; у нас 6.5 через Boot 3.5.5) | Не решает: параметр берётся из атрибутов аннотации, а не из класса бина, — на общем методе он по-прежнему один на всех |
| Переопределить пять методов в каждом из девяти наследников ради аннотации | Работает, но это 45 пустых переопределений; ровно то, чего разделение аннотаций позволило избежать |
| **`AuthorizationManager` / `HandlerInterceptor` поверх `PermissionRegistry.getRequiredCode(HandlerMethod)`** | **Рекомендуется.** Принуждение читает тот же скан, что и отчёт, — расходиться им неоткуда. Метод и ключ `(controller, method)` уже написаны и покрыты тестом |

Что учесть при реализации выбранного варианта:

- [ ] `getRequiredCode` возвращает `null` и для `@AuthenticatedOnly`, и для незаанотированного
      эндпоинта. Принуждение **не должно** трактовать `null` как «права не нужны»: забытая
      аннотация обязана закрывать, а не открывать. В `ENFORCE` реестр роняет старт на
      незаанотированном эндпоинте, так что до рантайма такой случай не доживает, — но полагаться
      на это молча нельзя, различие нужно провести явно
- [ ] «По доменам, начиная с `catalog`» — нужен способ включать принуждение частично
      (свойство со списком доменов рядом с `switchscope.security.permission.mode`)
- [ ] `UserDetailsService` переводится на `roles` + `role_permissions`; **вместе с этим** —
      кэш authorities, иначе stateless Basic даст три таблицы на каждый HTTP-вызов
- [ ] `hasRole` остаётся только на `/api/admin/**`; старые `@PreAuthorize("hasRole('ADMIN')")`
      в восьми каталожных контроллерах снимаются по мере включения домена `catalog`

---

**Осознанно вне объёма:** ABAC / row-level (`PermissionEvaluator`, `hasPermission`),
внешний движок политик (OPA), `RoleHierarchy`.

---

## 1.2 Обнуление полей через PUT

**Задача.** Сейчас PUT игнорирует и отсутствующее поле, и явный `null`
(`NullValuePropertyMappingStrategy.IGNORE`, введён в `b43f88c`). Это лучше прежнего затирания,
но `UpdatePolicyValidator` проверяет право обнулить поле, а применить это некому — слой прав
повис в воздухе.

**Решение** (детали — `docs/backend-concepts.md`, часть 3): `presentFields` + `NullFieldApplier`.
Половина уже есть — `UpdatePolicyValidator` и `FieldAccessMetadataCache`.

**Обязательное условие — сначала унифицировать чтение запроса.** Сырой JSON сейчас видят только
11 контроллеров; девять наследников `AbstractCrudController` принимают типизированный DTO.
Половинчатая поддержка **хуже** нынешнего единообразного IGNORE: `/api/components/{id}` умел бы
обнулять, а `/api/devices/switches/{id}` для той же сущности — нет.

- [ ] `AbstractCrudController.update` переводится на чтение сырого тела
      (шаблон уже написан в `ComponentPayloadReader` — переиспользовать, а не копировать)
- [ ] `NullFieldApplier` (~40 строк): поле present-and-null, прошедшее политику, обнуляется
      через `BeanWrapper`
- [ ] `FieldAccessMetadataCache` расширить отображением «поле DTO → свойство сущности»
      (имена совпадают, кроме FK `xxxId` → `xxx`)
- [ ] `resolver.applyReferences` тоже начинает учитывать `presentFields` — чтобы
      `componentNatureId: null` отвязывал, а отсутствие поля не трогало
- [ ] Проверить, что `REQUIRED` / `READ_ONLY` и NOT NULL-колонки не обнуляются ни при каких условиях

**Конвейер:**

```
raw JSON → ObjectNode → пин дискриминатора → treeToValue → presentFields
  → mapper.updateFromTo   (IGNORE: только non-null)
  → UpdatePolicyValidator (можно ли обнулять)
  → NullFieldApplier      (явные null)
  → resolver.applyReferences
  → save + mapToDto в той же транзакции
```

**Оговорка:** null-как-удаление — это PATCH по RFC 7386, а не PUT. Отклонение осознанное;
если понадобится строгость — добавить `PATCH` с `application/merge-patch+json`, PUT сделать
полной заменой. Менять глагол сейчас = ломать фронтенд без выгоды.

---

# ПРИОРИТЕТ 2 — хвост аудита

Находки 2.2–3.13 из `docs/backend-audit.md`, не входившие в шестёрку критичных.
Два пункта закрыты вместе с Этапом 1 (они были его предпосылкой), осталось **10**:

- [x] `RestExceptionHandler:33` — импорт `java.nio.file.AccessDeniedException` вместо
      `org.springframework.security.access.AccessDeniedException` → 403 отдаётся как 500.
      Уточнение: `AuthorizationDeniedException` (метод-секьюрити) в карте уже был отмаплен на 403,
      так что дефект бил только по `AccessDeniedException` из filter-цепочки
- [ ] `POST /api/profile` недоступен анонимному пользователю — зарегистрироваться может только
      уже вошедший; плюс регистрация минует `UserService.create` и проверку уникальности e-mail
- [ ] `ValidationUtil.assureIdConsistent` — `!=` вместо `equals` на UUID → `PUT /api/profile`
      с непустым `id` всегда 422
- [ ] Расхождения `@Size` DTO ↔ сущность ↔ DDL (`NamedTo.description` 1024 vs `NamedEntity` 512).
      Теперь, когда `@Valid` работает, значение 513–1024 пройдёт валидацию и упадёт на flush
- [ ] Коллекционные ассоциации не разрешаются: `CableRunTo.locationIds` / `connectorIds`,
      `PatchPanelTo.cableRunIds`, `LocationTypeTo.allowed*TypeIds`, `ComponentCategoryTo.componentTypeIds`
- [ ] `Installation.isValidLocationInstallation()` → `canContainComponent(null)` всегда false;
      `fitsInLocation()` — NPE при `rackPosition == null`
- [ ] `Component.canHoldOtherComponents()` возвращает true когда компонент **не** может содержать
      другие (дефект именования, оба вызова компенсируют)
- [ ] Bulk-delete обходит `cascade`/`orphanRemoval`: удаление здания не удалит этажи, а сделает
      их корневыми (FK `ON DELETE SET NULL`)
- [ ] Типобезопасность удаления: `ConnectivityRepository`/`HousingRepository` типизированы как
      `BaseRepository<Component>` → `DELETE /api/racks/{id}` удалит коммутатор по его id
- [x] `application.yaml:51` — `net.switchscope.backend: INFO`, а пакет `net.switchscope`;
      при `root: WARN` весь `log.info` подавлен. Вытащено вперёд: отчёт реестра — это `log.info`,
      без этой правки его не было бы видно
- [ ] `application.yaml:46` — `liquibase.clear-checksums: true` в основном профиле
- [ ] `AbstractCatalogController` — мёртвый код в проде: **ни один боевой контроллер его не
      наследует**, его пять маппингов не попадают в `RequestMappingHandlerMapping` и в скан
      Этапа 1. Но просто удалить нельзя: его наследует тестовый
      `src/test/.../TestEntityController`, на котором стоит `AbstractCatalogControllerTest`.
      Удалять вместе с этими двумя — либо перевести их на `AbstractCrudController`

---

# Устаревшее в разделах ниже

- [ ] Раздел «Architecture Summary» описывает ветку `instanceof UpdatableCrudService` с legacy-fallback
      в `AbstractCatalogController` — этот класс никем не наследуется, а `AbstractCrudController`
      переписан на `DtoCrudService` (коммит `b43f88c`)
- [ ] В «Pending» перечислены Location / Installation / Component как невыполненные — их write-path
      исправлен в `b43f88c`, но editable detail-view для них по-прежнему не сделан

---

# Implementation Guide

## Entity Relationship Map (FK Dependencies)

### Core Entity Hierarchy

```
ComponentCategoryEntity (catalog)
    │
    │ 1:N (categoryId)
    ▼
ComponentTypeEntity (catalog)
    │
    │ 1:N (componentTypeId)
    ▼
ComponentModel (catalog) ──────────────────┐
    │                                      │
    │ 1:N (specific model FK)              │
    ▼                                      │
Component (instance)                       │
    │ - componentTypeId ◄──────────────────┘
    │ - componentStatusId ──► ComponentStatusEntity (catalog)
    │ - componentNatureId ──► ComponentNatureEntity (catalog)
    │ - parentComponentId ──► Component (self-reference)
    │ - installationId ────► Installation
    │
    │ 1:N
    ▼
Port (abstract, STI)
    ├── EthernetPort
    └── FiberPort
```

### Location Hierarchy

```
LocationTypeEntity (catalog)
    │
    │ 1:N (typeId)
    ▼
Location
    │ - parentLocationId ──► Location (self-reference, hierarchy)
    │
    │ 1:N
    ▼
Installation
    │ - locationId ──────────► Location
    │ - componentId ─────────► Component (housing)
    │ - installedItemTypeId ─► InstallableTypeEntity (catalog)
    │ - statusId ────────────► InstallationStatusEntity (catalog)
```

### Complete FK Reference Table

| Entity | Field | Related Entity | Notes |
|--------|-------|----------------|-------|
| **ComponentTypeEntity** | categoryId | ComponentCategoryEntity | Required |
| **ComponentModel** | componentTypeId | ComponentTypeEntity | Required |
| **Component** | componentTypeId | ComponentTypeEntity | Required |
| **Component** | componentStatusId | ComponentStatusEntity | Required |
| **Component** | componentNatureId | ComponentNatureEntity | Optional |
| **Component** | parentComponentId | Component | Optional, self-ref |
| **Component** | installationId | Installation | Optional |
| **NetworkSwitch** | switchModelId | SwitchModel | Optional |
| **Rack** | rackTypeId | RackModelEntity | Required |
| **Port** | deviceId | Device | Required |
| **Port** | connectorId | Connector | Optional |
| **Location** | typeId | LocationTypeEntity | Required |
| **Location** | parentLocationId | Location | Optional, self-ref |
| **Installation** | locationId | Location | Required |
| **Installation** | componentId | Component | Optional (housing) |
| **Installation** | installedItemTypeId | InstallableTypeEntity | Required |
| **Installation** | statusId | InstallationStatusEntity | Required |
| **CableRun** | cableModelId | CableRunModel | Optional |
| **CableRun** | startLocationId | Location | Optional |
| **CableRun** | endLocationId | Location | Optional |
| **Connector** | connectorModelId | ConnectorModel | Optional |
| **Connector** | cableRunId | CableRun | Required |
| **PatchPanel** | patchPanelModelId | PatchPanelModel | Optional |

---

## Editable View Implementation Guide

### Phase 1: Backend - Gold Standard Update Pattern

#### 1.1 Create UpdatableCrudService Interface

**File:** `backend/src/main/java/net/switchscope/service/UpdatableCrudService.java`

```java
public interface UpdatableCrudService<E, T extends BaseTo> extends CrudService<E> {
    E updateFromDto(UUID id, T dto);
}
```

#### 1.2 Update Service Implementation

**Pattern:** Load existing → Handle FK changes → MapStruct updateFromTo → Save

```java
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class YourEntityService implements UpdatableCrudService<YourEntity, YourEntityTo> {

    private final YourEntityRepository repository;
    private final YourEntityMapper mapper;

    @Override
    @Transactional
    public YourEntity updateFromDto(UUID id, YourEntityTo dto) {
        // 1. Load existing with associations
        YourEntity existing = repository.findByIdWithAssociations(id)
                .orElseThrow(() -> new NotFoundException("Entity not found"));

        // 2. Use mapper for partial update (preserves ignored fields)
        mapper.updateFromTo(existing, dto);

        // 3. Save and return
        return repository.save(existing);
    }
}
```

#### 1.2.1 Handling FK Relations in Service (IMPORTANT!)

When entity has FK relations (e.g., `categoryId`), the mapper ignores the association
to preserve it. You must handle FK changes **manually** in the service:

```java
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ComponentTypeService implements UpdatableCrudService<ComponentTypeEntity, ComponentTypeTo> {

    private final ComponentTypeRepository repository;
    private final ComponentCategoryRepository categoryRepository;  // FK repository
    private final ComponentTypeMapper mapper;

    @Override
    @Transactional
    public ComponentTypeEntity updateFromDto(UUID id, ComponentTypeTo dto) {
        // 1. Load existing entity with all associations
        ComponentTypeEntity existing = repository.findByIdWithCategory(id)
                .orElseThrow(() -> new NotFoundException("Component type not found"));

        // 2. Handle FK change BEFORE mapper (mapper ignores 'category')
        if (dto.getCategoryId() != null &&
                !Objects.equals(dto.getCategoryId(), existing.getCategory().getId())) {
            ComponentCategoryEntity newCategory = categoryRepository.findById(dto.getCategoryId())
                    .orElseThrow(() -> new NotFoundException("Category not found"));
            existing.setCategory(newCategory);
        }

        // 3. Use mapper to update other fields (preserves properties)
        mapper.updateFromTo(existing, dto);

        // 4. Save and return
        return repository.save(existing);
    }
}
```

**Key Points:**
- Mapper has `@Mapping(target = "category", ignore = true)` — it won't update FK
- DTO contains `categoryId` (UUID), not the full `category` entity
- Service must manually: check if changed → load related entity → set it
- Order matters: handle FK changes BEFORE calling mapper

#### 1.3 Repository: Add Fetch Join Query

```java
@Query("SELECT e FROM YourEntity e LEFT JOIN FETCH e.association WHERE e.id = :id")
Optional<YourEntity> findByIdWithAssociations(@Param("id") UUID id);
```

#### 1.4 Mapper: Configure updateFromTo

```java
@Mapper(config = MapStructConfig.class)
public interface YourEntityMapper extends BaseMapper<YourEntity, YourEntityTo> {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "associations", ignore = true)  // Preserve collections
    @Mapping(target = "properties", ignore = true)    // Preserve maps
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Override
    YourEntity updateFromTo(@MappingTarget YourEntity entity, YourEntityTo to);
}
```

#### 1.5 Controller: Auto-detect UpdatableCrudService

`AbstractCatalogController` already handles this automatically:
- If service implements `UpdatableCrudService` → uses `updateFromDto()`
- Otherwise → fallback to legacy pattern

---

## Frontend Detail View Requirements (Component Models)

- **Discriminator-aware configs:** detail views must select the correct model config using a discriminator field from the API (e.g., `discriminatorType`) so the edit schema matches the entity type.
- **Component type UX:** view mode should show `componentTypeDisplayName` while edit mode should bind `componentTypeId` via searchable select.
- **Computed sections:** classification fields that are computed in the backend must remain read-only and can include a short description explaining their source.
- **Date inputs:** editable date fields should use a date picker input and store values as `YYYY-MM-DDT00:00:00` for API compatibility.

### Phase 2: Frontend - Edit Mode in GenericDetailView

#### 2.1 Detail Config with Edit Types

**File:** `frontend/src/configs/details/yourEntity.detail.js`

```javascript
export default {
  tableKey: 'yourEntities',
  composable: 'useYourEntities',

  sections: {
    basicInfo: {
      fields: [
        // Text input
        {
          key: 'name',
          label: 'Name',
          editType: 'input',
          required: true,
          validation: { maxLength: 128 }
        },

        // Read-only field (e.g., code)
        {
          key: 'code',
          label: 'Code',
          editType: 'readonly',
          editable: false
        },

        // Textarea
        {
          key: 'description',
          label: 'Description',
          editType: 'textarea',
          rows: 3
        },

        // Boolean toggle
        {
          key: 'active',
          label: 'Active',
          type: 'boolean',
          editType: 'checkbox'
        },

        // Number input
        {
          key: 'sortOrder',
          label: 'Sort Order',
          editType: 'number',
          validation: { min: 0, max: 999 }
        },

        // FK relation with searchable dropdown
        {
          key: 'categoryId',
          label: 'Category',
          editType: 'searchable-select',
          required: true,
          relation: {
            composable: 'useComponentCategories',
            dataKey: 'componentCategories',
            valueKey: 'id',
            labelKey: 'displayName',
            searchFields: ['name', 'code', 'displayName']
          }
        },

        // Color class picker
        {
          key: 'colorClass',
          label: 'Color',
          editType: 'color-class'
        },

        // Icon picker
        {
          key: 'iconClass',
          label: 'Icon',
          editType: 'icon-class'
        }
      ]
    }
  }
};
```

#### 2.2 Available Edit Types

| Type | Component | Use Case |
|------|-----------|----------|
| `input` | Text input | Names, codes, short text |
| `textarea` | Multiline input | Descriptions |
| `checkbox` / `boolean` | Toggle switch | Boolean flags |
| `number` | Number input | Sort order, quantities |
| `select` | Simple dropdown | Static options |
| `searchable-select` | SearchableDropdown | FK relations |
| `color-class` | Color picker | Tailwind color classes |
| `icon-class` | Icon picker | PrimeIcons classes |
| `readonly` | Display only | Immutable fields (code) |

#### 2.2.1 Searchable Select for FK Relations (Complete Example)

**Use Case:** ComponentType has FK to ComponentCategory (`categoryId`)

**Frontend Config:** `componentType.detail.js`
```javascript
{
  key: 'categoryId',           // DTO field name (UUID)
  label: 'Category',
  editType: 'searchable-select',
  required: true,
  relation: {
    composable: 'useComponentCategories',  // Composable to load options
    dataKey: 'componentCategories',        // Property name in composable result
    valueKey: 'id',                        // Option value field (sent to API)
    labelKey: 'displayName',               // Option display text
    searchFields: ['name', 'code', 'displayName']  // Fields for filtering
  }
}
```

**How It Works:**
1. `EditFieldRenderer` detects `editType: 'searchable-select'`
2. Loads relation composable dynamically: `composableRegistry['componentCategories']()`
3. Fetches data: `composable.fetchComponentCategories()`
4. Passes options to `SearchableDropdown` component
5. User selects category → `categoryId` (UUID) stored in form
6. On save → DTO sent to backend with `categoryId`
7. Backend service handles FK change (see section 1.2.1)

**SearchableDropdown Features:**
- Type-ahead search filtering
- Keyboard navigation (↑↓ Enter Escape)
- Shows icon/color from option if available
- Clear button to deselect
- Scrollable list (max 6 visible)

#### 2.3 Register in Detail View Registry

**File:** `frontend/src/configs/details/detailViewRegistry.js`

```javascript
import yourEntityDetail from './yourEntity.detail';

export const detailViewRegistry = {
  // ... existing
  yourEntity: yourEntityDetail,
};
```

#### 2.4 Add Route with detailKey

**File:** `frontend/src/router/index.js`

```javascript
{
  path: "/catalog/your-entities/:id",
  name: "your-entity-detail",
  component: GenericDetailView,
  meta: {
    requiresAuth: true,
    roles: ['USER', 'ADMIN'],
    detailKey: 'yourEntity'
  }
}
```

---

### Phase 3: Components Reference

#### 3.1 SearchableDropdown

**File:** `frontend/src/components/form/SearchableDropdown.vue`

**Features:**
- Search input with filtering
- Scrollable list (max 6 visible items)
- Keyboard navigation (up/down/enter/escape)
- Clear selection option
- Loading state
- Icon and color support from options

**Props:**
```javascript
{
  modelValue: [String, Number, null],  // Selected value
  options: Array,                       // Available options
  valueKey: String,                     // Default: 'id'
  labelKey: String,                     // Default: 'name'
  searchFields: Array,                  // Fields to search
  placeholder: String,
  disabled: Boolean,
  loading: Boolean,
  clearable: Boolean                    // Default: true
}
```

#### 3.2 EditFieldRenderer

**File:** `frontend/src/components/form/EditFieldRenderer.vue`

Renders appropriate input component based on `field.editType`.
Automatically loads relation data for `searchable-select` type.

---

### Phase 4: Checklist for New Editable Entity

#### Backend Checklist

- [ ] Repository: Add `findByIdWithAssociations()` method with JOIN FETCH
- [ ] Service: Implement `UpdatableCrudService<E, T>` interface
- [ ] Service: Implement `updateFromDto()` method
- [ ] Mapper: Configure `updateFromTo()` with proper `@Mapping(ignore=true)` for:
  - `id`
  - Collections/associations
  - `properties` (Map fields)
  - `createdAt`, `updatedAt`

#### Frontend Checklist

- [ ] Create detail config: `frontend/src/configs/details/yourEntity.detail.js`
- [ ] Define all fields with appropriate `editType`
- [ ] For FK fields: configure `relation` object with composable info
- [ ] Register in `detailViewRegistry.js`
- [ ] Ensure route has `meta.detailKey`
- [ ] Ensure table config has `routes.view` pointing to detail route

---

## Implementation Status

### Completed

- [x] **ComponentCategory** - Full edit support
  - Backend: UpdatableCrudService, findByIdWithComponentTypes
  - Frontend: All field types including color-class, icon-class
  - Properties preserved on update

- [x] **ComponentType** - Full edit support with FK relation
  - Backend: UpdatableCrudService, findByIdWithCategory, manual FK handling
  - Frontend: searchable-select for categoryId (dropdown with search)
  - Example of handling FK changes in service layer
  - Fixed GenericDetailView plural→singular conversion bug (`types` → `type`)

- [x] **ComponentModel** - Full edit support with FK relation
  - Backend: Custom controller (not AbstractCatalogController), findByIdWithComponentType, manual FK handling
  - Frontend: Custom ComponentModelDetailView.vue with edit mode support
  - Polymorphic: SwitchModel uses switchModel.detail.js config with searchable-select for componentTypeId

### Pending

- [ ] **ComponentStatus** - Workflow transitions (complex)
- [ ] **ComponentNature** - Simple catalog
- [ ] **LocationType** - Parent-child hierarchy
- [ ] **InstallationStatus** - Workflow transitions
- [ ] **InstallableType** - Simple catalog
- [ ] **Location** - Has typeId, parentLocationId FKs
- [ ] **Installation** - Multiple FKs (complex)
- [ ] **Component** - Multiple FKs (complex)

---

## Architecture Summary

```
┌─────────────────────────────────────────────────────────────────┐
│                         FRONTEND                                 │
├─────────────────────────────────────────────────────────────────┤
│  GenericDetailView.vue                                          │
│    ├── View Mode: FieldRenderer (read-only display)             │
│    └── Edit Mode: EditFieldRenderer (form inputs)               │
│                      └── SearchableDropdown (for FK fields)     │
│                                                                 │
│  Config-driven: detailConfig.sections.basicInfo.fields[]        │
│    - editType: 'input' | 'searchable-select' | 'checkbox' | ... │
│    - relation: { composable, dataKey, valueKey, labelKey }      │
└─────────────────────────────────────────────────────────────────┘
                              │
                              │ HTTP PUT /api/catalogs/entity/:id
                              │ Body: EntityTo (DTO)
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│                         BACKEND                                  │
├─────────────────────────────────────────────────────────────────┤
│  AbstractCatalogController                                      │
│    └── update() checks: service instanceof UpdatableCrudService │
│          ├── YES → service.updateFromDto(id, dto)               │
│          └── NO  → legacy: mapper.toEntity() + service.update() │
│                                                                 │
│  Service (implements UpdatableCrudService)                      │
│    └── updateFromDto(id, dto):                                  │
│          1. repository.findByIdWithAssociations(id)             │
│          2. mapper.updateFromTo(existing, dto)                  │
│          3. repository.save(existing)                           │
│                                                                 │
│  Mapper (@MappingTarget)                                        │
│    └── updateFromTo(): ignores id, associations, properties     │
│        → Only updates specified fields                          │
│        → Preserves collections and Map<> fields                 │
└─────────────────────────────────────────────────────────────────┘
```

---

## Files Modified/Created

### Backend

| File | Action | Description |
|------|--------|-------------|
| `service/UpdatableCrudService.java` | Created | New interface with updateFromDto |
| `service/component/ComponentCategoryService.java` | Modified | Implements UpdatableCrudService |
| `service/component/ComponentTypeService.java` | Modified | Implements UpdatableCrudService, handles FK change |
| `repository/component/ComponentCategoryRepository.java` | Modified | Added findByIdWithComponentTypes |
| `repository/component/ComponentTypeRepository.java` | Modified | Added findByIdWithCategory |
| `web/AbstractCatalogController.java` | Modified | Auto-detect UpdatableCrudService |
| `web/catalog/ComponentModelController.java` | Modified | Manual FK handling for componentTypeId |
| `repository/component/ComponentModelRepository.java` | Modified | Added findByIdWithComponentType |

### Frontend

| File | Action | Description |
|------|--------|-------------|
| `components/form/SearchableDropdown.vue` | Created | Dropdown with search for FK fields |
| `components/form/EditFieldRenderer.vue` | Created | Renders edit inputs by type |
| `views/GenericDetailView.vue` | Modified | Added edit mode, fixed plural→singular bug |
| `configs/details/componentCategory.detail.js` | Modified | Added editType to all fields |
| `configs/details/componentType.detail.js` | Modified | Added editType, searchable-select for categoryId |
| `views/GenericTableView.vue` | Modified | Edit button navigates to ?edit=true |
| `views/ComponentModelDetailView.vue` | Modified | Added edit mode with EditFieldRenderer |
| `configs/details/componentModels/switchModel.detail.js` | Modified | Added editType to all fields |
