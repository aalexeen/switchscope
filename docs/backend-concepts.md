# SwitchScope — три концепта

1. Дискриминатор в POST: практика и альтернативы
2. Конфигурируемые права доступа (концепт)
3. Обнуление полей через PUT: практика и решение

Часть 1 доведена до реализации. По части 2 реализован Этап 1 — схема, seed, аннотации и
`PermissionRegistry` в режиме audit-only; принуждения ещё нет, состав работ и что именно
сделано — в `TODO.md`, раздел 1.1. Часть 3 остаётся проектной.
Источники: OpenAPI/AsyncAPI discriminator,
Microsoft REST API Guidelines (PR #433 «Add guidelines for polymorphism»), Google AIP-133,
RFC 7386 (JSON Merge Patch), документация Spring Security (Authorization Architecture,
Method Security), OpenAPITools/jackson-databind-nullable, Baeldung «Absent vs Null in Jackson».

---

# 1. Дискриминатор в POST — нормальная практика?

## Короткий ответ

**Да, это стандартная практика.** Но для компонентов и устройств в SwitchScope есть вариант изящнее:
дискриминатор вообще не спрашивать у клиента, а выводить его из `componentTypeId`. Реализовано.

## Почему это стандарт

Дискриминатор в теле — не костыль, а первоклассный элемент спецификаций:

- **OpenAPI 3.x** имеет ключевое слово `discriminator` (`propertyName` + `mapping`) именно для
  выбора конкретной схемы внутри `oneOf`. То есть формат заранее рассчитан на такой POST.
- **Microsoft REST API Guidelines**, PR #433:
  «**DO** define a discriminator field indicating the kind of the resource and include any
  kind-specific fields in the body», плюс «**Kind in polymorphic type should be immutable**» и
  «**YOU SHOULD NOT** allow an update (patch) to change the kind field».
  Второе — ровно то, что уже реализовано: PUT берёт дискриминатор из сохранённой сущности,
  а не из payload. Так что текущий дизайн PUT задокументирован как рекомендуемый.
- Аргумент против отдельных эндпоинтов (StackExchange, обсуждение подписок): дробление коллекции
  по значению одного её атрибута протаскивает деталь реализации сервера наружу и ломает
  инвалидацию кэша по RFC 7234 — POST должен идти в тот же URI, из которого читают GET.

Альтернативы, которые обычно предлагают, и почему они не лучше:

| Альтернатива | Почему не подходит здесь |
|---|---|
| Content-negotiation: `Content-Type: application/vnd.switchscope.network-switch+json` | Пуристский REST, но инструментальная поддержка почти нулевая; Swagger UI, axios-обёртки и генераторы клиентов этого не умеют |
| Выводить тип из `componentTypeId` | **Выбрано для компонентов и устройств** — см. ниже. Требует чтения справочника при связывании, но он и так загружается резолвером FK. Не универсально: у `PortTo` нет `componentTypeId` |
| Угадывать тип по набору полей («есть `garden_size` → это House») | Bump.sh прямо называет это «weird (and costly)»; ломается при пересечении полей |
| Отдельный эндпоинт на подтип | Годится — см. ниже, но не универсально |

## Что изящнее конкретно в SwitchScope

> **Пересмотрено после уточнения:** полиморфный create будет использоваться из UI, поэтому вариант
> «убрать `POST /api/components` и `POST /api/devices`» снят. Ниже — решение, которое реализовано
> (коммит `2d14ec3`).

Ключевой факт, который решает вопрос. Каталог `component_types` содержит **15 кодов**, а Java-классов
всего **7**:

```
есть класс:  NETWORK_SWITCH ROUTER ACCESS_POINT CABLE_RUN CONNECTOR PATCH_PANEL RACK
нет класса:  FIREWALL PDU UPS SFP LOAD_BALANCER CABLE_MANAGER WALL_CABINET FIBER_PATCH_PANEL
```

Это не дефект, а тот самый «type-driven behavior» из docs/architecture.md: каталог описывает и типы, для которых
пока нет отдельной сущности.

При этом `@InstallableComponent(code = ...)` на семи классах **совпадает** и с `@DiscriminatorValue`,
и с колонкой `code` в каталоге — проверено механически (все семь строк идентичны, множества кодов
и имён в `@JsonSubTypes` совпадают).

**Отсюда проблема явного дискриминатора от клиента:** ничто не мешает прислать
`componentClass: NETWORK_SWITCH` вместе с `componentTypeId` от `FIREWALL`. Получится строка
`NetworkSwitch`, у которой тип говорит «межсетевой экран». Валидацией этого не поймать —
оба поля по отдельности корректны.

### Решение: сервер выводит дискриминатор из `componentTypeId`

```
POST /api/components
{ "componentTypeId": "…", "name": "sw-1", "maxPorts": 48 }
        ↓
componentTypeId → ComponentTypeEntity.code → это и есть дискриминатор
        ↓
подставляется в дерево → treeToValue(ComponentTo.class) → NetworkSwitchTo
```

Клиент дискриминатор не шлёт вовсе. `componentTypeId` он обязан прислать в любом случае —
это NOT NULL FK. Механизм тот же, что уже работал на PUT (пиннинг), только источник — справочник,
а не сохранённая строка:

| | Откуда берётся тип |
|---|---|
| POST | из `componentTypeId` (справочник) |
| PUT | из сохранённой сущности |

Клиент **может** прислать `componentClass` явно — тогда он **сверяется**, а не принимается на веру:
несовпадение → 422 с объяснением.

Что это даёт:
- рассогласование «класс ↔ тип» становится структурно невозможным;
- поле, которое клиенту негде взять кроме как захардкодив таблицу, исчезает из контракта;
- неимплементированный тип даёт понятную 422 («тип FIREWALL не имеет реализации, доступны: …»),
  а не падение десериализации;
- дополнительного запроса в БД нет — `ComponentType` всё равно грузится резолвером FK.

Реализация: `web/component/ComponentPayloadReader` — один класс на оба контроллера.
Bean-валидация вызывается там же вручную, потому что `@Valid` не применяется к сырому телу.

### Чтобы generic-форма знала, что предлагать

`ComponentTypeTo` получил два read-only поля:

```json
{ "code": "NETWORK_SWITCH", "componentClass": "NETWORK_SWITCH", "implemented": true }
{ "code": "FIREWALL",       "componentClass": null,             "implemented": false }
```

Оба заполняются из того же реестра, из которого сервер выводит дискриминатор, — разъехаться
они не могут. Конфигурационный фронтенд получает: список типов → отфильтровать по `implemented`
→ пользователь выбирает → по `componentClass` выбирается конфиг формы → POST с `componentTypeId`.

### Что осталось с явным дискриминатором

**Порты** (`portType`) — выводить не из чего: `PortTo` не имеет `componentTypeId`, а Ethernet/Fiber —
это осознанный выбор пользователя, а не следствие другого поля.

**Модели компонентов** (`discriminatorType`) — у `ComponentModelTo` есть `componentTypeId`, но
отображение `NETWORK_SWITCH → SWITCH_MODEL` строкой не выводится и аннотации-связки у подклассов
`ComponentModel` нет. Заводить её ради одного поля не стоит; выбор «это модель коммутатора» —
тоже осознанное действие пользователя.

## Мелочь про именование

Microsoft рекомендует единое имя `kind`. У нас три разных: `componentClass`, `portType`,
`discriminatorType`. Унификация была бы правильнее, но `portType` и `discriminatorType` уже
присутствуют в существующем контракте, и переименование — breaking change ради косметики.
Предложение: оставить, но задокументировать в OpenAPI через `@Schema(description=...)`
с перечислением допустимых значений.

---

# 2. Концепт: конфигурируемые права доступа

## Требование

Каждая операция API — отдельный «пункт», который в конфигурации либо разрешён, либо запрещён.
Без пересборки и передеплоя.

## Почему нынешняя модель этого не даёт

- `Role` — enum из двух значений (`USER`, `ADMIN`), зашитый в код, реализующий `GrantedAuthority`.
- Права зашиты в аннотации: `@PreAuthorize("hasRole('ADMIN')")`. Единственная «конфигурация» —
  выдать пользователю ADMIN, то есть выдать всё сразу.
- Гранулярность нулевая: нельзя разрешить редактировать типы компонентов, но запретить удалять
  локации.
- `frontend/src/utils/roles.js` выглядит как попытка это решить, но:
  **он никем не импортируется** (проверено grep'ом по всему `frontend/src`) и **падает при загрузке**
  — `ROLE_PERMISSIONS` ссылается сам на себя внутри собственного инициализатора
  (`...ROLE_PERMISSIONS[ROLES.USER]` на строке 51), что даёт
  `ReferenceError: Cannot access 'ROLE_PERMISSIONS' before initialization` (проверено запуском).
  Список прав в нём — про MAC-адреса и пользователей, наследие другого проекта.
  **Рекомендация: удалить, а не расширять.**

## Модель

Четыре сущности. Три уже вписываются в существующий каталожный паттерн проекта.

```
User ──< user_roles >── RoleEntity ──< role_permissions >── PermissionEntity
```

### 2.1 `PermissionEntity` — «что можно сделать»

Наследует `BaseCodedEntity` (как все справочники проекта) → бесплатно получает
`code`, `displayName`, `description`, `active`, `sortOrder`, а на фронтенде — таблицу
через существующий `GenericTableView` почти без кода.

Формат кода: **`<домен>.<ресурс>:<действие>`**

```
catalog.component-type:read        catalog.component-type:create
catalog.component-type:update      catalog.component-type:delete
catalog.component-model:update
component.network-switch:create    component.network-switch:delete
component:read
location:create                    location:delete
installation:update
port:create
user:read                          user:update
system.permission:update
```

Домены: `catalog`, `component`, `location`, `installation`, `port`, `user`, `system`.
Действия: `read`, `create`, `update`, `delete` + особые там, где нужно
(`nullify` — см. часть 3, `export`, `assign-role`).

Дополнительные поля сверх `BaseCodedEntity`: `domain`, `resource`, `action` — денормализация
ради группировки в UI и ради построения матрицы.

### 2.2 `RoleEntity` — «должностная функция»

Не enum. `code`, `displayName`, `description`, `systemRole` (нельзя удалить), `active`.
Seed: `ADMIN`, `USER`. Дальше добавляются данными: `CATALOG_EDITOR`, `AUDITOR`, `NOC_OPERATOR`.

Почему не enum: enum — это код, а требование — конфигурация.

Spring Security на этот счёт однозначен (Method Security → «Favor Granting Authorities Over
Complicated SpEL Expressions», Authorization Architecture): роли — широкие категории,
authorities — конкретные действия; более 10 ролей = «role explosion», признак что действия
моделируются ролями.

### 2.3 `role_permissions` — **это и есть конфигурация**

Строка есть → разрешено. Строки нет → запрещено. Всё.
Изменяется через админский API/UI, без передеплоя. Именно этот пункт закрывает требование.

### 2.4 `user_roles` — уже существует

Мигрирует с `VARCHAR(32)` со значением enum на FK на `roles.id`.
Changeset миграции: создать `roles`, засеять `ADMIN`/`USER`, добавить `role_id`,
проставить по строковому значению, удалить старую колонку.

## Как это включается в Spring Security

`UserDetailsService` формирует authorities из двух источников:

```
ROLE_ADMIN, ROLE_CATALOG_EDITOR          ← роли, с префиксом
catalog.component-type:update            ← права, без префикса
component.network-switch:delete
...
```

Проверка в контроллерах — по **правам**, не по ролям:

```java
@PreAuthorize("hasAuthority('catalog.component-type:update')")
```

Почему `hasAuthority`, а не `hasRole` + `RoleHierarchy`:
`RoleHierarchy` применяется **только** к `hasRole`, не к `hasAuthority` — это описано в документации
Spring Security и является классическим источником многочасовой отладки при смешении двух стилей.
Иерархия у нас задаётся данными (какие права входят в роль), а не второй скрытой системой.
Один стиль на весь код.

`hasRole` остаётся только для по-настоящему грубых границ (`/api/admin/**`).

### Читаемость: своя мета-аннотация

```java
@RequiresPermission("catalog.component-type:update")
public ComponentTypeTo update(...)
```

Мета-аннотация над `@PreAuthorize` (Spring Security это поддерживает штатно). Даёт две вещи:
читаемость и — главное — **возможность просканировать код на старте**.

## Верифицируемость — центральная часть концепта

Таблица «роль × право» бесполезна, если нельзя проверить, что она покрывает все операции.
Ровно это и произошло с `@PreAuthorize`: аннотации были, а механизм — нет, и никто не заметил.

В проекте уже есть подходящий прецедент — `InstallableComponentRegistry`: сканирует аннотации,
сверяет с кодами в БД и на старте пишет расхождения в обе стороны. Повторить его для прав:

**`PermissionRegistry`** на старте сканирует все `@RequestMapping`-методы всех контроллеров и
сверяет с таблицей `permissions`:

| Расхождение | Смысл | Реакция |
|---|---|---|
| Код есть, в БД нет | Операция неконфигурируема — админ физически не может её выдать | **ERROR**, fail-fast |
| В БД есть, в коде нет | Мёртвая строка конфигурации, вводит админа в заблуждение | WARN |
| **Метод-эндпоинт без `@RequiresPermission`** | **Ровно тот случай, что жил в проекте всю его жизнь** | **ERROR** (по флагу — WARN на переходный период) |

Третья строка — самая важная. Документация Spring Security говорит прямо:
*«when you use annotation-based Method Security, then unannotated methods are not secured.
To protect against this, declare a catch-all authorization rule in your HttpSecurity instance.»*

Поэтому две страховки, а не одна:
1. **Fail-closed на уровне запросов**: в `HttpSecurity` для `/api/**` — `denyAll()` по умолчанию,
   с явными исключениями. Забытая аннотация тогда закрывает доступ, а не открывает.
2. **Стартовый отчёт** реестра — список незаанотированных эндпоинтов в логе.

Плюс машиночитаемая проверка снаружи:

```
GET /api/admin/permissions/matrix
→ { roles: [...], permissions: [...], grants: [[roleCode, permissionCode], ...],
    unannotatedEndpoints: [...], orphanPermissions: [...] }
```

Это и есть «каждый пункт отражён в конфигурации» в форме, которую можно проверить,
задиффить между средами и положить в CI.

## Кэширование — не оптимизация, а обязательное условие

Аутентификация stateless HTTP Basic → `loadUserByUsername` выполняется **на каждый запрос**.
Сейчас это один запрос (users + EAGER roles). С правами станет три таблицы на каждый HTTP-вызов.

- `@Cacheable("userAuthorities")` по email — `@EnableCaching` в проекте уже есть (`AppConfig`).
- Инвалидация: полный evict кэша при изменении `user_roles`, `role_permissions`, `permissions`
  и при отключении пользователя. Изменения редки, точечная инвалидация не нужна.
- TTL (5 мин) как страховка от пропущенного evict.
- Важно: кэшировать **authorities**, а не сущность `User` — иначе в кэш попадает пароль и
  detached-граф Hibernate.

## Совместимость и радиус изменений

| Что | Как затрагивается | Решение |
|---|---|---|
| `Role` enum (`implements GrantedAuthority`) | Удаляется | `RoleEntity` + seed-строки `ADMIN`/`USER` |
| `AuthUser` — `super(..., user.getRoles())` | Ждёт `Collection<GrantedAuthority>` | Собирать роли + права |
| `LoginResponseTo` — `Set<Role> roles` | Читается фронтендом | **Добавить** `permissions: [...]`, `roles` оставить строками — фронтенд не ломается |
| `SecurityConfig` — `hasRole(Role.ADMIN.name())` | Ссылка на enum | Строковая константа |
| `useAuth.js`, `services/auth.js`, `UserAccount.vue` | Читают `user.roles` | Не трогаются (аддитивное изменение) |
| `frontend/utils/roles.js` | Мёртв и сломан | **Удалить**, заменить на `usePermissions()` поверх `permissions` из `/api/auth/check` |
| `router/index.js` — `meta.roles: ['USER','ADMIN']` в 46 маршрутах | Грубая гранулярность | `meta.permission: 'catalog.component-type:read'` |
| Кнопки в `CellActions.vue` | Показываются всем | `v-if="can('catalog.component-type:delete')"` |

Про фронтенд отдельно: гейт на клиенте — **удобство, а не защита**. Он прячет кнопки, которых
всё равно нет прав нажать; авторизация остаётся серверной.

## Этапы

**0.** Закоммитить текущие 68 файлов (фиксы 1–6) — иначе следующий диф нечитаем.

**1. Схема и наблюдение, без принуждения.**
Таблицы + seed + `@RequiresPermission` на всех эндпоинтах + `PermissionRegistry` в режиме
**audit-only**: пишет отчёт, ничего не блокирует. Даёт полную картину покрытия без риска сломать
работающих пользователей. `@PreAuthorize` пока не меняется.

**2. Включение принуждения по доменам.**
Начать с `catalog` (там же и была дыра), затем `component`, `location`, `installation`, `port`,
`user`. На каждом шаге — сверка отчёта реестра.

**3. Фронтенд.**
`/api/auth/check` отдаёт `permissions`; `usePermissions()`; маршруты и кнопки на права;
`utils/roles.js` удалить.

**4. Уборка.**
Enum `Role` удалить, `hasRole` оставить только на `/api/admin/**`, включить `denyAll()` по умолчанию.

## Что осознанно НЕ входит

- **ABAC / row-level** (владение объектом, права в пределах локации или площадки). Это следующий
  уровень: Spring даёт `PermissionEvaluator` и `hasPermission(#id, 'Component', 'WRITE')`.
  Проектировать сейчас — преждевременно; модель выше этому не мешает и является его основанием.
- **Внешний движок политик (OPA и т. п.)**. Для одного приложения с одной БД — избыточно.
  `AuthorizationManager` позволяет подключить его позже без переделки контроллеров.
- **Иерархия ролей** (`RoleHierarchy`). Вторая система наследования поверх `role_permissions`
  создаст ровно ту путаницу, о которой предупреждает документация. Наследование — данными:
  роль-наследник получает копию прав родителя.

---

# 3. Обнуление полей через PUT

## Текущее состояние

После правки фикса 3 (`NullValuePropertyMappingStrategy.IGNORE`) PUT игнорирует **и** отсутствующее
поле, **и** явный `null`. Это лучше прежнего поведения (обнулялось всё, чего не было в payload,
без всяких проверок), но `UpdatePolicyValidator` теперь проверяет право обнулить поле, а применить
это некому — слой прав повисает в воздухе.

Корень: после стандартной десериализации Jackson в POJO **отсутствующее поле и явный `null`
неразличимы** — оба дают `null`.

## Варианты и почему они отпадают

| | Как | Вердикт |
|---|---|---|
| **A. `JsonNullable<T>`** (`jackson-databind-nullable`) | Обёртка на каждое обнуляемое поле; трёхзначность `undefined` / `of(null)` / `of(v)` | Технически самый правильный, есть поддержка MapStruct и Bean Validation через `ValueExtractor`. **Но**: ~30 DTO, сотни полей; и проект-обёртка официально **ищет мейнтейнеров** (баннер в README). Цена не оправдана |
| **B. `Optional<T>` в полях** + `Jdk8Module` | Дёшево, работает | Авторы `jackson-databind-nullable` прямо возражают: «Beans shouldn't have `Optional` fields — `Optional` was designed to be used only as method return value», и «`Optional` should never be null», а здесь именно null и означает «поле отсутствовало» |
| **C. `ObjectReader.readerForUpdating(entity)`** | Jackson пишет прямо в существующий объект; нативная merge-семантика | **Опасно**: пишет в сущность напрямую и **обходит все `@Mapping(ignore = true)`** в мапперах — то есть возвращает ровно те дефекты, которые закрыли фиксы 2 и 3 |
| **D. `presentFields` + рефлексия** | Читать тело как дерево, запомнить имена присутствующих полей, применить явные null отдельно | **Рекомендуется**: половина уже написана и работает |

## Рекомендация: вариант D + унификация чтения запроса

### Обязательное условие

Сейчас сырой JSON видят только 11 контроллеров (`ComponentController`, `DeviceController`,
`PortController`, `ComponentModelController` и 7 каталожных). Девять наследников
`AbstractCrudController` принимают типизированный DTO и никакого `presentFields` не имеют.

**Половинчатая поддержка хуже нынешнего единообразного IGNORE**: `/api/components/{id}` умел бы
обнулять поле, а `/api/devices/switches/{id}` для той же сущности — нет. Разная семантика на
разных путях к одному объекту — генератор багов.

Значит `AbstractCrudController.update` тоже должен читать сырое тело. Шаблон уже написан трижды
(`ComponentController`, `PortController`, `DeviceController`); четвёртый раз в базовом классе делает
все девять наследников единообразными бесплатно.

### Конвейер обновления

```
raw JSON body
  ↓ objectMapper.readTree                     → ObjectNode
  ↓ пин дискриминатора из БД                  (только для полиморфных)
  ↓ treeToValue(dtoClass)                     → DTO (значения)
  ↓ presentFields = имена полей верхнего уровня
  ↓ mapper.updateFromTo(entity, dto)          IGNORE: применяет только non-null
  ↓ UpdatePolicyValidator.validate(...)       можно ли обнулять — уже реализовано
  ↓ NullFieldApplier.apply(entity, dtoClass, presentFields)   ← новое, ~40 строк
  ↓ resolver.applyReferences(entity, dto, presentFields)      ← FK тоже по presentFields
  ↓ save + mapToDto (в той же транзакции)
```

### `NullFieldApplier`

Для каждого поля, которое **присутствует в JSON** и равно `null`, и чьё обнуление **разрешено
политикой**, ставит `null` в сущность через Spring `BeanWrapper`.

Заготовка уже есть: `FieldAccessMetadataCache` умеет обходить иерархию классов DTO и кэшировать
метаданные полей. Расширяется до отображения «поле DTO → свойство сущности»; имена совпадают,
кроме FK (`componentTypeId` → `componentType`) — это отображение и так знает резолвер из фикса 2.

Защиты:
- поля с `FieldAccessLevel.REQUIRED` и `READ_ONLY` не обнуляются никогда (уже в валидаторе);
- поля, соответствующие NOT NULL-колонкам, — тоже (проверка резолвера остаётся последней линией);
- обнуление FK (`componentNatureId: null` → отвязать) обрабатывается резолвером, а не апплаером.

### Что при этом получает слой прав

`UpdatePolicy` начинает работать по назначению: ADMIN может обнулить `ADMIN_NULLABLE`-поле,
USER — только `USER_WRITABLE`, `REQUIRED` не обнуляет никто. Плюс естественная стыковка с частью 2:
право `<ресурс>:nullify` как отдельный конфигурируемый пункт.

## Оговорка о семантике HTTP

Строго по спецификации «null означает удалить значение» — это **PATCH** с
`application/merge-patch+json` (RFC 7386), а не PUT. PUT формально означает полную замену
представления.

То есть предлагаемый PUT ведёт себя как merge-patch. Это осознанное отклонение, и оно
распространено. Менять глагол ради чистоты сейчас — ломать фронтенд без выгоды.

Если строгость понадобится, миграция очевидна и обратно совместима:
`PATCH /api/...` с `application/merge-patch+json` получает описанную семантику,
PUT становится полной заменой, оба сосуществуют. Конвейер выше при этом не меняется —
меняется только источник маршрута.

---

# Сводка рекомендаций

| Вопрос | Рекомендация |
|---|---|
| 1. Дискриминатор в POST | Практика стандартная (OpenAPI `discriminator`, MS API Guidelines). Для компонентов и устройств **дискриминатор не нужен вообще**: сервер выводит его из `componentTypeId`, а `componentClass` от клиента только сверяется. Это заодно делает невозможным рассогласование «класс ↔ тип». Для `ports` и `component-models` — оставить явным, выводить не из чего. **Реализовано, коммит `2d14ec3`** |
| 2. Права доступа | `PermissionEntity` (каталог) + `RoleEntity` + `role_permissions` = конфигурация. Проверка через `hasAuthority('<домен>.<ресурс>:<действие>')`, не `hasRole`. Ключевое — `PermissionRegistry` со стартовой сверкой кода и БД в обе стороны + отчёт о незаанотированных эндпоинтах + `denyAll()` по умолчанию. Кэш authorities обязателен с первого дня (stateless Basic = загрузка на каждый запрос). Внедрять поэтапно, начиная с audit-only |
| 3. Обнуление через PUT | Вариант D: `presentFields` + `NullFieldApplier`, **при условии** что `AbstractCrudController.update` тоже перейдёт на чтение сырого тела — иначе семантика разъедется по эндпоинтам. `JsonNullable` отклонён по цене, `readerForUpdating` — потому что обходит `ignore = true` в мапперах |

## Один вопрос к вам

В рабочем дереве **68 незакоммиченных файлов** от фиксов 1–6. Любая из этих трёх работ поверх них
даст диф, который невозможно ревьюить. Коммитить фиксы 1–6 отдельным коммитом до начала?
