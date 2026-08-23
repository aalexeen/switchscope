# SwitchScope backend — исправление критичных находок 1–6

Ветка `dev`, база — коммит `bea4605`. 68 файлов изменено, 2 добавлено (+1148 / −634).
Не закоммичено.

**Что проверено:** `./mvnw -DskipTests test-compile` — успешно; анализ сгенерированного кода MapStruct
до и после правок; отдельная проверка Jackson-round-trip на реальных классах сборки (10 сценариев,
все PASS); 15 DB-независимых тестов — BUILD SUCCESS, 0 падений.

**Чего эти 15 тестов НЕ покрывают.** `AbstractCrudControllerTest` и `CrudSmokeControllerTest` — это
`@WebMvcTest(ComponentController.class)`, а `ComponentController` **не** наследует
`AbstractCrudController`; `AbstractCatalogControllerTest` целится в тестовый `TestEntityController`.
То есть переписанный базовый контроллер и все девять его наследников тестами не покрыты вовсе.
«15 тестов проходят» означает только «ничего из ранее покрытого не сломалось».

**Что НЕ проверено:** приложение не запускалось, `./mvnw verify` не запускался — он ходит в живую dev-БД.
Ни один HTTP-запрос не выполнялся.

---

## 1. Method security включена

`config/SecurityConfig.java` — добавлен `@EnableMethodSecurity`.

`@PreAuthorize("hasRole('ADMIN')")` в 7 каталожных контроллерах и в `AbstractCatalogController`
перестал быть no-op. Аннотация до этого присутствовала только в тестовых конфигурациях
(`AbstractCatalogControllerTest:205`, `AbstractCrudControllerTest:133`), из-за чего тесты проходили,
а продакшен пускал USER на запись в каталоги.

### ⚠️ Это изменение поведения, а не только закрытая дыра

Пока `@PreAuthorize` был мёртв, аккаунты с ролью USER **успешно писали в каталоги** — и фронтенд на это
рассчитывает: в `frontend/src/router/index.js` 46 маршрутов объявлены как `roles: ['USER', 'ADMIN']`,
включая все `/catalog/*`. После правки те же действия вернут 403.

Это и есть задуманное поведение (`AbstractCatalogController` всегда декларировал ADMIN-only на запись),
но для пользователей это выглядит как «приложение перестало давать редактировать справочники».
Нужно либо выдать нужным людям роль ADMIN, либо сузить `roles` во фронтенд-маршрутах до `['ADMIN']`
для операций записи.

**Проверено:** компиляция; наличие аннотации в main-конфигурации; наличие USER в 46 маршрутах фронтенда.
**Не проверено:** фактический 403 для USER — требует запуска.

---

## 2. Мапперы больше не теряют FK — POST работает

Введён `service/component/ComponentReferenceResolver` (новый): разрешает id → сущность для
`componentType`, `componentStatus`, `componentNature`, `installation`, `parentComponent`,
плюс типоспецифичные ссылки на каталог моделей.

Правило одно для create и update: **id есть в DTO → ссылка заменяется, id нет → ссылка не трогается.**
Так частичный update не обнуляет то, о чём клиент не просил. Обязательные ссылки проверяются в конце —
неполный create даёт 422 `componentTypeId is required`, а не нарушение NOT NULL.

Типобезопасность: `applyModelReference` / `applyComponentReference` проверяют подтип через
`Class.isInstance` и бросают `IllegalRequestDataException`, а не `ClassCastException` —
при Single Table Inheritance любой id резолвится, поэтому подтип надо проверять руками.

Разрешение FK добавлено в:

| Сервис | Разрешаемые ссылки |
|---|---|
| `NetworkSwitchService` | общие + `switchModelId` |
| `RouterService`, `AccessPointService` | общие |
| `RackService` | общие + `rackTypeId` |
| `CableRunService` | общие + `cableModelId`, `startLocationId`, `endLocationId` |
| `ConnectorService` | общие + `connectorModelId`, `cableRunId`, `portId` |
| `PatchPanelService` | общие + `patchPanelModelId` |
| `ComponentService` (полиморфный) | общие + модель по конкретному типу |
| `LocationService` | `typeId` (обязателен), `parentLocationId` |
| `InstallationService` | `locationId`, `installedItemTypeId`, `statusId`, `componentId`, `installedItemId` |
| `PortService` | `deviceId` (обязателен), `connectorId` |
| `ComponentTypeService` | `categoryId` (обязателен) |
| `ComponentModelService` | `componentTypeId` (обязателен) |

Добавлены защиты от самоссылки: `Location cannot be its own parent`, `Component cannot be its own parent`.

**Проверено:** компиляция, наличие вызова разрешения во всех create-путях.
**Не проверено:** успешный INSERT — требует БД.

---

## 3. Update больше не мержит detached-сущность

Введён интерфейс `service/DtoCrudService<E, T>` (новый) с `createFromDto(dto)` / `updateFromDto(id, dto)`,
возвращающими DTO. `AbstractCrudController` переписан на него: базовый контроллер больше **не** строит
сущность маппером и не отдаёт её сервису — раньше именно этот путь и был сломан, и
`InstallationController`, не переопределявший `create`/`update`, наследовал поломку напрямую.

Новый шаблон update: загрузить managed-сущность → `mapper.updateFromTo(existing, dto)` →
разрешить ссылки → `save`. Маппинг обратно в DTO — внутри той же транзакции
(`open-in-view: false`, поэтому иначе ловится `LazyInitializationException`).

### Второй слой той же проблемы: `updateFromTo` затирал скаляры

Загрузки managed-сущности было **недостаточно**. `MapStructConfig` не задавал
`nullValuePropertyMappingStrategy`, то есть действовал дефолт `SET_TO_NULL`, и сгенерированный
`updateFromTo` присваивал безусловно:

```java
entity.setName( to.getName() );        // было: PUT без name -> name = null
entity.setAddress( to.getAddress() );
```

Ассоциации при этом спасал `ignore = true`, а вот все скалярные поля обнулялись, если payload их
не содержал. В `MapStructConfig` добавлено
`nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE`.

**Проверено по сгенерированному коду:** после правки все `updateFromTo` присваивают под условием
`if (to.getX() != null)`; безусловное присваивание осталось ровно одно —
`UserMapperImpl.setEmail`, потому что оно задано через `expression`, а выражения этой стратегией
не управляются (в единственном вызывающем пути `email` обязателен по `@NotBlank`, так что NPE там нет).
`toEntity` стратегия не затрагивает — перепроверено на том же прогоне.

**Следствие — PUT стал частичным (merge), а не полной заменой.** Это согласуется с тем, как всегда вёл
себя `PUT /api/components/{id}`, с правилом «id нет в DTO → ссылка не трогается» из п.2 и с самим
замыслом слоя `UpdatePolicy`, который существует ради различения «поле отсутствует» и «поле явно = null».

**Ограничение, которое из этого следует:** явное обнуление поля (`"description": null`) теперь тоже
игнорируется маппером. `UpdatePolicyValidator` по-прежнему проверяет, *разрешено* ли обнуление, но
применить его некому. До правки было хуже — обнулялось всё подряд без всякой проверки — но полноценная
поддержка явного null (применять `presentFields` после маппера) осталась незакрытой задачей.

Переведены на `DtoCrudService`: `NetworkSwitchService`, `RouterService`, `AccessPointService`,
`RackService`, `CableRunService`, `ConnectorService`, `PatchPanelService`, `LocationService`,
`InstallationService`. Их контроллеры лишились собственных `create`/`update` — базовый класс теперь
делает правильную вещь.

В `InstallationService` из-за прежнего `SET_TO_NULL` был отдельный побочный эффект: `installedItemId`
маппером переносится, PUT без него обнулял поле, проверка обязательности затем бросала
`installedItemId is required` — 422 на payload, который этого поля вообще не упоминал. С `IGNORE` этого
не происходит.

Сущностные `create(E)` / `update(UUID, E)` в этих сервисах (плюс `ComponentService`, `DeviceService`,
`PortService`, `ComponentModelService`, `ComponentTypeService`) помечены `@Deprecated` и бросают
`UnsupportedOperationException` с указанием на DTO-метод. Интерфейс `CrudService` их требует, удалить
нельзя — но тихая порча данных заменена на громкий отказ, и следующий контроллер не сможет случайно
на них наткнуться.

**Проверено:** компиляция; 15 тестов web-слоя проходят (см. оговорку о покрытии выше); в сервисах не
осталось достижимого по HTTP `entity.setId(id); save(entity)` (см. «Осталось» ниже); все сгенерированные
`updateFromTo` присваивают под null-проверкой.
**Не проверено:** сохранение ассоциаций и скаляров при реальном PUT.

---

## 4. Абстрактные DTO десериализуются

Добавлена полиморфная привязка Jackson через **уже существующее** поле-дискриминатор
(`As.EXISTING_PROPERTY`), а не через синтетическое:

| Базовый DTO | Свойство | Значения |
|---|---|---|
| `ComponentTo` | `componentClass` (новое поле) | `NETWORK_SWITCH`, `ROUTER`, `ACCESS_POINT`, `CABLE_RUN`, `CONNECTOR`, `PATCH_PANEL`, `RACK` |
| `PortTo` | `portType` (уже было) | `ETHERNET`, `FIBER` |
| `ComponentModelTo` | `discriminatorType` (было, но никем не заполнялось) | `SWITCH_MODEL`, `ROUTER_MODEL`, … |

Значения совпадают с JPA `@DiscriminatorValue`. Мапперы заполняют дискриминатор на выходе
(`entity.getDiscriminatorValue()`); на `ComponentModel` этот геттер добавлен по образцу `Component`.

`ComponentModelTo` в исходном отчёте не фигурировал — при проверке выяснилось, что он тоже абстрактный
и `POST /api/catalogs/component-models` ломался ровно так же. Исправлен вместе с остальными.

**Важный побочный эффект и его нейтрализация.** `@JsonTypeInfo` на базовом классе действует и при чтении
в конкретный подтип, поэтому наивное добавление аннотации сломало бы единственный работавший write-путь
`PUT /api/components/{id}` (фронтенд его вызывает). Это подтверждено экспериментально:

```
FAIL  readValue(noDisc, NetworkSwitchTo.class) -> InvalidTypeIdException: missing type id property 'componentClass'
```

Поэтому все PUT-контроллеры (`ComponentController`, `DeviceController`, `PortController`,
`ComponentModelController`) читают тело как дерево и **проставляют дискриминатор из сохранённой
сущности**, а не берут его из payload. Это и обратно совместимо (payload может его не содержать),
и строже: клиент не может подменить тип существующей строки.

**Проверено экспериментально** (`ObjectMapper` на реальных классах сборки, без БД и без старта приложения):

```
PASS  POST  ComponentTo  <- componentClass
PASS  POST  DeviceTo     <- componentClass
PASS  POST  ComponentTo  <- RACK
PASS  POST  ComponentTo  <- unknown discriminator is rejected
PASS  PUT   NetworkSwitchTo <- pinned componentClass (payload omits it)
PASS  PUT   pinning overrides a spoofed componentClass
PASS  POST  PortTo       <- portType
PASS  PUT   EthernetPortTo <- pinned portType
PASS  POST  ComponentModelTo <- discriminatorType
PASS  PUT   SwitchModelTo <- pinned discriminatorType
      componentClass occurrences in serialized output: 1
```

### ⚠️ Требуется правка фронтенда

`POST` на полиморфные эндпоинты теперь **обязан** содержать дискриминатор:

- `POST /api/components`, `POST /api/devices` → `"componentClass": "NETWORK_SWITCH"` (и т. п.)
- `POST /api/ports` → `"portType": "ETHERNET"` / `"FIBER"`
- `POST /api/catalogs/component-models` → `"discriminatorType": "SWITCH_MODEL"` (и т. п.)

`PUT` и `GET` менять не нужно: PUT берёт тип из БД, а GET теперь дополнительно отдаёт поле
`componentClass` (для `PortTo`/`ComponentModelTo` поля уже существовали). Типизированные эндпоинты
(`/api/devices/switches`, `/api/housing/racks`, …) дискриминатора не требуют — там тип задан маршрутом.

---

## 5. `id` из запроса больше не перезаписывает чужую строку

`@Mapping(target = "id", ignore = true)` добавлен в `toEntity` всех 26 мапперов.

**Проверено по сгенерированному коду:** ни один из 26 `*MapperImpl.toEntity` больше не содержит
`entity.setId(to.getId())` (до правки — 25 из 26).

Это закрывает связку с п.1: раньше USER мог отправить POST на каталожный эндпоинт с `id`
существующей записи, `save()` выполнял merge и перезаписывал её, включая записи с `systemType = true`.

---

## 6. `DELETE` больше не падает с 422

`repository/BaseRepository.java`: `UUID delete(UUID)` → `int deleteByIdReturningCount(UUID)`,
`deleteExisted` сравнивает с `0`.

Spring Data JPA допускает у `@Modifying`-запроса только `void`/`int`/`Integer`/`boolean`/`Boolean`
(`JpaQueryExecution$ModifyingExecution` вызывает `Assert.isTrue(isInt || isVoid, ...)`), поэтому
`UUID` приводил к `IllegalArgumentException` при первом же вызове. Метод переименован, чтобы имя
не конфликтовало по смыслу с унаследованным `CrudRepository.delete(T)`; именование взято из
уже корректного `ComponentBaseRepository.deleteByIdCustom`.

Затрагивало DELETE на locations, installations, components, devices, switches, routers,
access-points, racks, cable-runs, connectors, patch-panels, ports, component-models, `/api/profile`.

**Проверено:** компиляция; прямых вызовов `delete(UUID)` вне `deleteExisted` в коде нет.
**Не проверено:** фактическое удаление строки.

---

## Заодно (одна строка, та же поверхность)

`@Valid` добавлен **только на `create`** — в `AbstractCrudController`, `AbstractCatalogController`,
7 каталожных контроллерах, `ComponentController`, `PortController`, `DeviceController`,
`ComponentModelController`, `ComponentTypeController`.

На `update` он сознательно **не** ставится. `ComponentTo` несёт `@NotNull` на `componentStatusId` и
`componentTypeId`, `NamedTo.name` — `@NotBlank @Size(min = 2)`. С `@Valid` на PUT любой частичный
payload получал бы 400, то есть ветка «id нет в DTO → ссылка не трогается» из п.2 стала бы
недостижимой на самых горячих путях, а `PUT /api/components/{id}` (сырой JSON, без `@Valid`) продолжал
бы принимать частичные — два разных контракта для одной сущности. Выбран единый: **POST валидируется
полностью, PUT частичный.**

Это часть находки 2.1 исходного отчёта, а не 1–6.

Также снят NPE-риск в `ComponentTypeService.updateFromDto` (`existing.getCategory().getId()` без
null-проверки).

---

## Осталось (не входило в 1–6)

- **Расхождения `@Size`** DTO ↔ сущность ↔ DDL (`NamedTo.description` 1024 vs `NamedEntity` 512).
  Теперь, когда `@Valid` работает, они станут заметны: значение 513–1024 символов пройдёт валидацию
  DTO и упадёт на flush.
- **Коллекционные ассоциации** не разрешаются: `CableRunTo.locationIds` / `connectorIds`,
  `PatchPanelTo.cableRunIds`, `LocationTypeTo.allowedChildTypeIds` / `allowedParentTypeIds`,
  `ComponentCategoryTo.componentTypeIds`. Все они nullable и требуют двусторонней синхронизации —
  это отдельная задача, а не часть п.2.
- **`entity.setId(id); save(entity)` остался в 5 плоских каталогах** (`ComponentNatureService`,
  `ComponentStatusService`, `InstallableTypeService`, `InstallationStatusService`,
  `LocationTypeService`). Их мапперы не теряют ни одного FK, а по HTTP все пять идут через
  `updateFromDto`/`updateAndMapToDto` — сущностный `update` недостижим. Он всё ещё способен затереть
  `@ElementCollection properties`, если его кто-нибудь вызовет.
- **`AbstractCatalogController` — мёртвый код:** ни один контроллер его не наследует, все 7 каталожных
  реализованы отдельно ради `UpdatePolicy`. Ветка `instanceof UpdatableCrudService` с `log.warn`
  никогда не исполняется.
- **Явное обнуление полей через PUT не работает** (следствие `NullValuePropertyMappingStrategy.IGNORE`,
  см. п.3). Чтобы вернуть его без возврата к затиранию, нужно применять `presentFields` после маппера:
  поле, присутствующее в JSON со значением `null` и прошедшее `UpdatePolicyValidator`, обнулять явно.
- **Валидация на PUT отсутствует** (см. «Заодно»). Частичный payload сейчас не проверяется вообще;
  корректное решение — валидационные группы либо ручная проверка присутствующих полей.
- Находки 2.2–3.13 исходного отчёта (импорт `AccessDeniedException`, недоступная регистрация,
  `!=` на UUID в `ValidationUtil`, `orphanRemoval` vs bulk delete, NPE в `Installation`, EAGER-повсюду,
  `clear-checksums`, логирование) — не трогались.

## Что стоит прогнать перед мержем

1. `./mvnw verify` на **тестовой** БД (не dev — см. заметку о том, что тесты ходят в живую).
2. Ручной smoke: POST/PUT/DELETE по одному представителю каждой группы из таблицы п.2.
3. Правку фронтенда из п.4 — иначе POST на три полиморфных эндпоинта начнёт возвращать 400/422
   вместо прежних 500 (то есть ломается он не сильнее, чем был, но и не чинится).
