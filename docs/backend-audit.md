# SwitchScope backend — аудит слоёв (model / DAO / service / web / mapper / DTO)

Дата: 2026-08-23, ветка `dev`, коммит `bea4605`.

**Метод.** `./mvnw -DskipTests compile` (успешно, 26 `*MapperImpl`), анализ **сгенерированного** кода MapStruct,
сверка с Liquibase DDL, декомпиляция `spring-data-jpa-3.5.1.jar`, целевые проверки конфигурации.
Приложение не запускалось; `./mvnw verify` не запускался намеренно — он работает по живой dev-БД.

**Пометки:**
- **[код]** — проверено по исходникам, сгенерированным исходникам, DDL или байт-коду библиотеки.
- **[?]** — обоснованный вывод, не подтверждённый прогоном.

Коды HTTP-ответов выведены из `RestExceptionHandler.HTTP_STATUS_MAP` и типа исключения — путь кода
проверен, но ни один запрос не выполнялся.

---

## 0. Сводка

Слоёная схема (Entity → Repository → Service → Controller + MapStruct) выдержана и читается.
Но между слоями есть четыре системных разрыва, из-за которых **бо́льшая часть write-путей API нерабочая**,
и один разрыв в безопасности.

| Область | Оценка |
|---|---|
| Model (JPA) | рабочая, но перегружена EAGER и бизнес-логикой; несколько логических ошибок |
| Repository (DAO) | `BaseRepository.delete` не запускается вообще; generic-удаление не типобезопасно |
| Service | шаблон `save(detached)` ломает update; дублирование `getAll` / `getAllAsDto` |
| Mapper / DTO | **главный источник дефектов**: `unmappedTargetPolicy=IGNORE` молча теряет все FK |
| Web | нет `@Valid`, абстрактные DTO в `@RequestBody`, `@PreAuthorize` не активирован |

Ниже — оценка работоспособности CRUD по группам эндпоинтов.

| Группа | GET | POST | PUT | DELETE |
|---|---|---|---|---|
| Каталоги на `ComponentBaseRepository` (categories, statuses, natures, installable-types, installation-statuses, location-types) | ✅ | ✅ | ✅ | ✅ |
| `catalogs/component-types` | ✅ | ❌ п.1.2 | ✅ | ✅ |
| `catalogs/component-models` | ✅ | ❌ п.1.2 | ✅ | ❌ п.1.6 |
| `locations` | ✅ | ❌ п.1.2 | ❌ п.1.3 | ❌ п.1.6 |
| `installations` | ✅ | ❌ п.1.2 | ❌ п.1.3 | ❌ п.1.6 |
| Конкретные компоненты (switches, routers, access-points, racks, cable-runs, connectors, patch-panels) | ✅ | ❌ п.1.2 | ❌ п.1.3 | ❌ п.1.6 |
| `components` (полиморфный) | ✅ | ❌ п.1.4 | ✅ | ❌ п.1.6 |
| `devices`, `ports` (полиморфные) | ✅ | ❌ п.1.4 | ❌ п.1.4 | ❌ п.1.6 |

---

## 1. КРИТИЧНО

### 1.1 Method security выключена — все `@PreAuthorize` мертвы **[код]**

`config/SecurityConfig.java:26` — только `@EnableWebSecurity`. `@EnableMethodSecurity` в `src/main` **отсутствует**;
он есть **только в тестах**:
- `src/test/java/net/switchscope/AbstractCatalogControllerTest.java:205`
- `src/test/java/net/switchscope/AbstractCrudControllerTest.java:133`

Spring Boot не включает method security автоматически. Следствие: `@PreAuthorize("hasRole('ADMIN')")`
в `web/AbstractCatalogController.java:57,77,101` и во всех 7 каталожных контроллерах — **no-op**.
`SecurityConfig` разрешает `/api/**` любому аутентифицированному, `hasRole(ADMIN)` стоит только на `/api/admin/**`,
которого ни один контроллер не обслуживает.

**Итог: любой пользователь с ролью USER может создавать, менять и удалять записи всех каталогов.**
Тесты этого не ловят именно потому, что включают method security у себя.

Фикс: `@EnableMethodSecurity` на `SecurityConfig`.

### 1.2 Мапперы молча теряют все FK → POST не работает **[код]**

`mapper/MapStructConfig.java` задаёт `unmappedTargetPolicy = ReportingPolicy.IGNORE`, поэтому MapStruct
не сообщает о непроставленных полях. Проверка сгенерированного кода
(`target/generated-sources/annotations/**/*MapperImpl.java`) — какие FK-идентификаторы DTO не доходят до сущности в `toEntity`:

| Mapper | Потеряно в `toEntity` |
|---|---|
| `LocationMapperImpl` | `typeId`, `parentLocationId`, `childLocationIds` |
| `ComponentTypeMapperImpl` | `categoryId` |
| `*ModelMapperImpl` (7 шт.) | `componentTypeId` |
| `NetworkSwitchMapperImpl` | `switchModelId`, `componentStatusId`, `componentTypeId`, `componentNatureId`, `installationId`, `parentComponentId` |
| `RackMapperImpl` | `rackTypeId` + те же 5 |
| `CableRunMapperImpl` | `cableModelId`, `startLocationId`, `endLocationId`, `locationIds`, `connectorIds` + те же 5 |
| `ConnectorMapperImpl` | `connectorModelId`, `cableRunId`, `portId` + те же 5 |
| `PatchPanelMapperImpl` | `patchPanelModelId`, `cableRunIds` + те же 5 |
| `Router/AccessPointMapperImpl` | те же 5 |
| `InstallationMapperImpl` | `locationId`, `componentId`, `installedItemTypeId`, `statusId` |
| `LocationTypeMapperImpl` | `allowedChildTypeIds`, `allowedParentTypeIds` |
| `Ethernet/FiberPortMapperImpl` | `deviceId`, `connectorId` |

В интерфейсах это оформлено как `@Mapping(target = "type", ignore = true) // Set via service`
(`mapper/location/LocationMapper.java:53`), **но сервисы этого не делают**:

- `service/location/LocationService.java:88` — `create()` = `repository.save(entity)`, `type` не подставляется.
- `service/component/ComponentService.java:198` — `createAndReturnDto()` = `save(entity)`, без `handleFkChanges`.
- То же в `NetworkSwitchService`, `RouterService`, `AccessPointService`, `RackService`, `CableRunService`,
  `ConnectorService`, `PatchPanelService`, `InstallationService`, `ComponentModelService`, `ComponentTypeService`.

Соответствующие колонки — NOT NULL по DDL **[код]**:
- `init/50-components.yaml:76,81` — `component_status_id`, `component_type_id`
- `init/21-locations.yaml` — `location_type_id` (`Location.type` — `nullable = false`, `model/location/Location.java:28`)
- `init/42-installations.yaml:37,52,59,66` — `location_id`, `installable_type_id`, `installed_item_id`, `status_id`
- `init/30-component-models-catalog.yaml:71` — `component_type_id`

**Итог: POST на `/api/locations`, `/api/devices/switches`, `/api/devices/routers`, `/api/devices/access-points`,
`/api/racks`, `/api/cable-runs`, `/api/connectors`, `/api/patch-panels`, `/api/installations`,
`/api/catalogs/component-types`, `/api/catalogs/component-models` завершается нарушением NOT NULL**
(`DataIntegrityViolationException` → `DATA_CONFLICT` → 409).

Единственное место, где FK разрешаются корректно, — `ComponentService.handleFkChanges()`
(`service/component/ComponentService.java:262`), и оно вызывается **только** из `updateWithPolicyValidation`,
причём покрывает лишь `componentType` / `componentStatus` / `componentNature` — не `parentComponentId`,
`installationId` и не type-specific модели (`switchModelId`, `rackTypeId`, …).

### 1.3 `save(detached)` в update = merge, затирающий всё **[код]**

Шаблон, повторённый ~12 раз:

```java
// service/component/device/NetworkSwitchService.java:86
public NetworkSwitchTo updateAndReturnDto(UUID id, NetworkSwitch entity) {
    repository.getExisted(id);      // результат выбрасывается
    entity.setId(id);
    NetworkSwitch saved = repository.save(entity);   // merge detached
```

`entity` приходит из `mapper.toEntity(dto)` (`web/component/device/NetworkSwitchController.java:92`) — это
**новый detached-объект**, у которого все FK из п.1.2 = `null`. `save()` для не-нового id вызывает `em.merge()`,
который копирует **всё** состояние, включая `null`, поверх управляемой сущности.

Исходы **не** совмещаются — какой именно наступит, определяется первым же NOT NULL-полем:

- **PUT падает целиком, БД не меняется** (нарушение NOT NULL → откат транзакции):
  switches, routers, access-points, racks, cable-runs, connectors, patch-panels
  (`component_type_id`, `component_status_id`), locations (`location_type_id`),
  installations (`location_id`, `installable_type_id`, `installed_item_id`, `status_id`),
  component-models (`component_type_id`).
- **Молчаливая потеря данных** — только там, где все сброшенные FK nullable.
  Среди перечисленных таких сущностей нет: у каждой есть хотя бы один NOT NULL FK.
  Однако вместе с NOT NULL-полями обнуляются и nullable — `componentNature`, `parentComponent`,
  `installation`, `switchModel`, `rackType`, `cableModel` и т. д., поэтому при исправлении **только**
  NOT NULL-полей (без остальных) сценарий сразу перейдёт в разряд тихой потери данных. **[?]**

Тот же код и в `CrudService.update()` у тех же сервисов, и в `LocationService.updateAndReturnDto:79`,
`InstallationService.update:46`, `ComponentModelService.update:55`.

Корректный шаблон в проекте уже есть — `UpdatableCrudService.updateFromDto()` (`ComponentCategoryService:82`,
`ComponentTypeService:103`): загрузить managed-сущность → `mapper.updateFromTo(existing, dto)` → `save`.
Он применён только к каталогам.

### 1.4 Абстрактные DTO в `@RequestBody` **[код]**

`@JsonTypeInfo` / `@JsonSubTypes` в проекте **отсутствуют** (grep по `to/` и `model/` — пусто).
При этом абстрактные типы стоят прямо в `@RequestBody`:

- `web/component/ComponentController.java:83` — `create(@RequestBody ComponentTo to)`; `ComponentTo` абстрактный (`to/component/ComponentTo.java:24`)
- `web/component/device/DeviceController.java:60,67` — `DeviceTo` абстрактный
- `web/port/PortController.java:49,57` — `PortTo` абстрактный (`to/port/PortTo.java:25`)

Jackson не может инстанцировать абстрактный класс → `InvalidDefinitionException`.
`RestExceptionHandler.HTTP_STATUS_MAP` его не содержит → `APP_ERROR` → 500.

Логика ниже по стеку это подтверждает: `ComponentController.mapToEntity()` определяет тип через
`to.getClass().getSimpleName().contains("...")` — то есть автор рассчитывал получить конкретный подкласс,
которого Jackson дать не может.

Работающим остаётся только `PUT /api/components/{id}` — он принимает `@RequestBody String jsonPayload`
и определяет DTO-класс по сущности из БД (`ComponentController.java:97-119`). Это единственный
полностью рабочий write-путь в подсистеме компонентов.

### 1.5 Клиент может задать `id` при создании и перезаписать чужую строку **[код]**

`BaseTo` (`to/BaseTo.java`) — `@Data`, сеттеры на `id` / `createdAt` / `updatedAt` публичные.
`@Schema(accessMode = READ_ONLY)` влияет только на Swagger, не на Jackson.
Все 25 сгенерированных `toEntity` (кроме `UserMapperImpl`) содержат `entity.setId(to.getId())`.
Ни один сервис, кроме `UserService.create` (`Assert.isNull(user.getId())`) и `ProfileController.register`
(`checkNew`), не проверяет новизну; далее `repository.save(entity)` с непустым id = `merge` → перезапись.

Для компонентов / локаций / установок это **не эксплуатируется**: merge упирается в NOT NULL из п.1.2
и откатывается. Эксплуатируется ровно там, где `toEntity` заполняет все NOT NULL-колонки — то есть
на каталогах без FK:

`/api/catalogs/component-categories`, `component-natures`, `component-statuses`,
`installable-types`, `installation-statuses`, `location-types`.

**В связке с п.1.1 это даёт: любой пользователь с ролью USER может отправить POST на каталожный
эндпоинт с `id` существующей записи и перезаписать её** — включая системные записи, помеченные
`systemType = true`.

Фикс: `@Mapping(target = "id", ignore = true)` в `toEntity` либо `ValidationUtil.checkNew()` в сервисах.

### 1.6 `BaseRepository.delete` не выполняется — DELETE не работает нигде вне каталогов **[код]**

```java
// repository/BaseRepository.java:19
@Transactional @Modifying
@Query("DELETE FROM #{#entityName} e WHERE e.id=:id")
UUID delete(UUID id);
```

Spring Data JPA допускает у `@Modifying`-запроса только `void` / `int` / `Integer` / `boolean` / `Boolean`.
Проверено по байт-коду `spring-data-jpa-3.5.1.jar`: конструктор
`JpaQueryExecution$ModifyingExecution` вычисляет `isAssignable(returnType, Void)` и
`isAssignable(returnType, Integer)` и вызывает `Assert.isTrue(isInt || isVoid, ...)`.
`UUID` не проходит → `IllegalArgumentException`.

`AbstractJpaQuery.execution` — поле типа `Lazy` (проверено там же), поэтому **исключение возникает
не на старте контекста, а при первом вызове**. Приложение поднимается нормально; падает первый же DELETE.
`IllegalArgumentException` есть в `HTTP_STATUS_MAP` → `BAD_DATA` → **422**, то есть ошибка
маскируется под «неверные данные».

Затронуто (всё, что унаследовано от `BaseRepository`):
`LocationRepository`, `InstallationRepository`, `ComponentRepository`, `DeviceRepository`,
`ConnectivityRepository`, `HousingRepository`, `ComponentModelRepository`, `PortRepository`,
`UserRepository` — то есть `DELETE` на locations, installations, components, devices, switches,
routers, access-points, racks, cable-runs, connectors, patch-panels, ports, component-models,
а также `DELETE /api/profile` и `UserService.delete`.

**Не затронуто:** каталоги на `ComponentBaseRepository` — там `deleteExisted` использует
`int deleteByIdCustom(UUID)` (`repository/component/ComponentBaseRepository.java:70`), корректный тип.
Именно поэтому дефект не всплыл: тесты и фронтенд удаляют преимущественно каталожные записи.

Фикс: сменить тип возврата на `int` и сравнивать с `0` (как уже сделано в `ComponentBaseRepository`).

---

## 2. ВЫСОКО

### 2.1 Bean Validation на DTO не работает — нет `@Valid` **[код]**

42 из 44 `@RequestBody` объявлены без `@Valid` (исключения — `ProfileController:47,58`).
`@Validated` в пакете `web` нет вовсе.

Следствие: `@NotNull`, `@Size`, `@NoHtml`, `@Email` на TO не проверяются. Валидация Hibernate на flush
(`ValidationEventListener`) частично спасает — но она проверяет **сущность**, а не DTO, и отдаёт
`ConstraintViolationException` при flush → 500/409 вместо 400/422 с полем-нарушителем.

Так, `@NoHtml` на DTO не срабатывает нигде — защита от HTML-инъекции держится только на аннотациях сущностей.

Расхождения ограничений, которые из-за этого никак не ловятся:
- `NamedTo.description` `@Size(max = 1024)` (`to/NamedTo.java:26`) vs `NamedEntity.description` `@Size(max = 512)` (`model/NamedEntity.java:30`).
- `ComponentTo.manufacturer` без `@NotNull`, при этом `components.manufacturer` NOT NULL (`init/50-components.yaml:56`).
- `ComponentTo.serialNumber` без `@NotNull`, при этом `serial_number` NOT NULL.

### 2.2 Полиморфные сервисы: небезопасный каст **[код]**

`ConnectivityRepository` и `HousingRepository` типизированы как `BaseRepository<Component>` — без сужения по подтипу.

```java
// service/component/connectivity/CableRunService.java:33
CableRun cableRun = (CableRun) repository.getExisted(id);
```

`GET /api/cable-runs/{id}` с id стойки → `ClassCastException` → 500 вместо 404.
То же в `ConnectorService:33`, `PatchPanelService:33`, `RackService:33`,
`NetworkSwitchService:33`, `RouterService:33`, `AccessPointService:33`.

Проверки типа нет и в `delete`. Сейчас это скрыто дефектом п.1.6, но **после его исправления**
`BaseRepository.delete` c `#{#entityName}` = `Component` станет «удалить любой компонент без фильтра
по дискриминатору»: `DELETE /api/racks/{id}` удалит коммутатор, если передать его id.
`DeviceRepository` типизирован как `BaseRepository<Device>` — там удаление ограничено поддеревом
Device, но `DELETE /api/devices/switches/{router-id}` всё равно удалит роутер. **[?]** — вывод из
семантики JPQL-полиморфизма, прогоном не подтверждён (см. п.1.6).

### 2.3 Bulk-delete обходит `cascade` / `orphanRemoval` **[код]**

`BaseRepository.delete` — JPQL bulk DELETE, минующий persistence context. При этом объявлено:
- `Component.childComponents` — `cascade = ALL, orphanRemoval = true` (`model/component/Component.java:86`)
- `Location.childLocations` — `cascade = ALL, orphanRemoval = true` (`model/location/Location.java:42`)

В БД FK — `ON DELETE SET NULL` (`init/50-components.yaml:168`, `init/21-locations.yaml:152`).

Как и п.2.2, сейчас замаскировано п.1.6. После починки типа возврата удаление здания **не удалит**
его этажи (как обещает `orphanRemoval`), а молча сделает их корневыми локациями — расхождение
JPA-модели и фактического поведения нужно устранять вместе с п.1.6, а не после.

### 2.4 `AccessDeniedException` импортирован не из того пакета **[код]**

`config/RestExceptionHandler.java:33` — `import java.nio.file.AccessDeniedException;`
Spring Security бросает `org.springframework.security.access.AccessDeniedException`, которого в
`HTTP_STATUS_MAP` нет → 500 вместо 403. `AuthorizationDeniedException` (строка 64) замаплен,
но он относится к method security, которая сейчас выключена (п.1.1).

### 2.5 Регистрация недоступна анонимному пользователю **[код]**

`ProfileController.register` — `POST /api/profile` (`web/user/ProfileController.java:45`).
`SecurityConfig:72` требует аутентификации для всего `/api/**`. Закомментированное исключение
(`SecurityConfig:70`) ссылается на несуществующий `/api/auth/register`.
Зарегистрироваться может только уже вошедший пользователь.

Дополнительно: `register` вызывает `repository.prepareAndSave` напрямую, минуя
`UserService.create` → нет проверки уникальности e-mail, вместо 409 будет
`DataIntegrityViolationException`.

---

## 3. СРЕДНЕ

### 3.1 `ValidationUtil.assureIdConsistent` сравнивает UUID через `!=` **[код]**

```java
// validation/ValidationUtil.java:23
} else if (bean.getId() != id) {
```
Сравнение ссылок вместо `equals`. `PUT /api/profile` с непустым `id` в теле
(`ProfileController:60`) всегда даёт 422 `must has id=...`.

### 3.2 `Installation.isValidLocationInstallation()` всегда false для housed-установок **[код]**

```java
// model/installation/Installation.java:166
if (component != null && !component.canContainComponent(null)) { // TODO: resolve actual component
```
`canContainComponent(null)` возвращает `false` по определению (`Component.java:273`), значит любая
установка внутри housing-компонента считается невалидной. TODO в коде это признаёт.

### 3.3 `Installation.fitsInLocation()` — NPE при `rackPosition == null` **[код]**

```java
// model/installation/Installation.java:212
if (rackUnitHeight != null && location.isRackLike()) {
    return (rackPosition + rackUnitHeight - 1) <= location.getTotalRackUnits();
```
`rackPosition` — `Integer` и nullable (`init/42-installations.yaml`). Распаковка null → NPE.
Также `canChangeStatusTo(null)` (строка 238) → NPE на `targetStatus.getCode()`.

### 3.4 `Component.canHoldOtherComponents()` инвертирован **[код]**

```java
// model/component/Component.java:132
public boolean canHoldOtherComponents() {
    return componentType == null || !componentType.isCanContainComponents();
}
```
Метод возвращает `true`, когда компонент **не может** содержать другие.
Оба вызова (`Component.java:273,281`) используют его как guard `if (…) return false`, поэтому
`canContainComponent` / `canContainComponentType` работают корректно. Это дефект именования
публичного API, а не текущий баг — но метод public и будет неверно понят при следующем использовании.

### 3.5 EAGER-повсюду **[код]** (архитектурное)

`docs/architecture.md` предписывает `@ManyToOne(fetch = LAZY)`, фактически:
- `Component` — 3 EAGER `@ManyToOne` (`componentStatus`, `componentType`, `componentNature`)
- `Installation` — EAGER `location`, `status`
- `ComponentTypeEntity` — EAGER `category` + 3 EAGER `@ElementCollection`
- `NetworkSwitch.switchModel` — EAGER
- `User.roles` — EAGER

Последние три коммита (`10d34b0`, `79f995a`, `d80a9d0`) — это лечение `LazyInitializationException`
сменой fetch-типа и ручными `Hibernate.initialize()` в сервисах. Причина при `open-in-view: false`
(`application.yaml:14`) — маппинг вне транзакции; корректное лечение — `JOIN FETCH` + маппинг
внутри `@Transactional`, что частично и сделано в `*AsDto`-методах. Сейчас в системе сосуществуют
три стратегии (EAGER, `Hibernate.initialize`, `JOIN FETCH`) — их стоит свести к одной.

Остаточный риск: `ComponentTypeController.getAll()` (`web/catalog/ComponentTypeController.java:57`)
маппит **после** закрытия транзакции. Сейчас не падает только потому, что в `ComponentTypeEntity`
всё EAGER. **[?]**

### 3.6 Дублирование связи Component ↔ Installation **[код]**

Три независимых способа связать компонент с установкой:
- `Component.installation` → `components.installation_id` (`Component.java:80`)
- `Installation.component` → `installations.housing_component_id` (`Installation.java:36`)
- `Installation.installedItemId` — нетипизированный `UUID` без FK (`Installation.java:47`)

Ни одна пара не связана через `mappedBy`, синхронизация не гарантирована,
`installedItemId` не проверяется на существование. `Installation.getInstalledItem()` бросает
`UnsupportedOperationException` (`Installation.java:377`), а обещанной реализации в
`InstallationService` нет.

### 3.7 `HasPortsImpl.getPorts()` возвращает копию **[код]**

```java
// model/component/device/HasPortsImpl.java:52
public List<Port> getPorts() { return new ArrayList<>(ports); }
```
Персистентность не ломается (доступ по полям — `@Access(FIELD)` в `BaseEntity`), но
`device.getPorts().add(port)` молча теряет данные, а `Hibernate.initialize(sw.getPorts())`
(`NetworkSwitchService:35`) инициализирует уже отсоединённую копию — работает лишь
как побочный эффект копирования.

### 3.8 `EncryptedStringConverter` **[код]**

- `private static String encryptionKey` заполняется через `@Value`-сеттер на Spring-бине; JPA-конвертер
  создаётся Hibernate'ом отдельно. Работает только благодаря `static` — и только если Spring-бин
  инициализирован раньше первой конвертации. Хрупкий порядок инициализации.
- Ключ выводится обрезанием/дополнением нулями UTF-8-байтов (`getKeyBytes()`), без KDF.
  Короткая парольная фраза даёт ключ с нулевым хвостом.
- Дефолт в `application.yaml:78` — `change-me-in-production-32chars`.

### 3.9 `UserRepository.findByEmailIgnoreCase` регистронезависим только наполовину **[код]**

```java
@Query("SELECT u FROM User u WHERE u.email = LOWER(:email)")
```
`LOWER` применён к параметру, но не к колонке. Работает лишь пока все e-mail в БД в нижнем регистре
(`prepareAndSave` это обеспечивает для новых, но не для CSV seed-данных). Имя метода вводит в заблуждение.

### 3.10 Логирование контроллеров подавлено **[код]**

`application.yaml:51` — `net.switchscope.backend: INFO`, а реальный пакет — `net.switchscope`.
При `root: WARN` все `log.info` в контроллерах и сервисах не пишутся.

### 3.11 `liquibase.clear-checksums: true` в основном профиле **[код]**

`application.yaml:46`. Сбрасывает контрольные суммы при каждом старте — изменение уже применённого
changeSet перестаёт диагностироваться. В `docs/architecture.md` при этом записано «never modify existing changesets».

### 3.12 Мелочи **[код]**

- `HasId.id()` (`HasId.java:19`) — `Assert.notNull(getId())`, затем `return getId() == null` → всегда `false`. Мёртвый и бессмысленный метод.
- `ErrorType.BAD_REQUEST` → `HttpStatus.UNPROCESSABLE_ENTITY` (422), а не 400 (`error/ErrorType.java`).
- `ComponentTypeService.updateFromDto:110` — `existing.getCategory().getId()` без null-проверки.
- `AbstractCatalogController.update` логирует `log.warn("does not implement UpdatableCrudService")` на каждый запрос для сервисов, которые его не реализуют, — это `ComponentStatusService` (`/api/catalogs/component-statuses`).
- `ComponentController.mapToEntity` / `DeviceController.mapToEntity` / `PortController.mapToEntity` определяют тип через `getSimpleName().contains(...)` — хрупко, ломается при переименовании DTO.
- `CrudService.getAll()/getById()` продублированы методами `getAllAsDto()/getByIdAsDto()`; контроллеры используют вторые, первые остаются мёртвым кодом в большинстве сервисов. `AbstractCrudController` при этом переопределён во всех наследниках целиком — базовый класс фактически не выполняет своей роли.

### 3.13 Проверено и дефектом **не является** **[код]**

Из 31 интерфейса в пакете `mapper` сгенерировано 26 `*MapperImpl`. Пять «пропущенных» —
`BaseMapper`, `ComponentMapper`, `DeviceMapper`, `ComponentModelMapper`, `DeviceModelMapper`, `PortMapper` —
не помечены `@Mapper` и являются generic-родителями (`ComponentMapper<E extends Component, T extends ComponentTo>`).
MapStruct для них impl не создаёт по замыслу, Spring-бинов они не требуют, и ни один из них
не внедряется в сервисы или контроллеры (`ComponentController`, `DeviceController`, `PortController`,
`ComponentModelController` внедряют только конкретные мапперы). Контекст поднимается.

---

## 4. Что работает корректно

- `BaseEntity`: UUID v7, `equals`/`hashCode` через `ProxyUtils.getUserClass` — правильная реализация для Hibernate-прокси.
- `UpdatableCrudService` + `updateFromDto` — верный шаблон частичного обновления; применён в `ComponentCategoryService`, `ComponentTypeService`, `ComponentNatureService`, `InstallableTypeService`, `InstallationStatusService`, `LocationTypeService`.
- `security/policy/*` — рабочий и аккуратный механизм ролевого контроля обнуления полей (`@FieldAccess` + `FieldAccessMetadataCache` + `UpdatePolicyValidator`), реально используется в 7 каталожных контроллерах и в `ComponentService` / `ComponentModelService`. Мёртвым кодом не является.
- `ComponentBaseRepository` — корректные типы возврата у `@Modifying`-запросов (`int`), в отличие от `BaseRepository` (п.1.6).
- `User.password` — `@JsonProperty(WRITE_ONLY)`, утечки пароля через `GET /api/profile` нет.
- `InstallableComponentRegistry` — сверка Java-аннотаций с кодами в БД на старте, с диагностикой расхождений.
- `RestExceptionHandler` — корректная работа с `ProblemDetail` (RFC 7807) и разворачиванием root cause.
- `PortController` и `DeviceController` — единственные, где update загружает managed-сущность перед `updateFromTo` (правильный шаблон), хотя их всё равно блокирует п.1.4.
- Liquibase-схема аккуратная: осмысленные `onDelete`, индексы на всех FK.

---

## 5. Рекомендованный порядок исправлений

1. `@EnableMethodSecurity` в `SecurityConfig` (п.1.1) — одна строка, закрывает дыру в авторизации.
2. `@Mapping(target = "id", ignore = true)` в `toEntity` (п.1.5) — вторая половина той же дыры.
3. `BaseRepository.delete` → тип возврата `int`, сравнение с `0` (п.1.6); одновременно решить
   вопрос типобезопасности удаления (п.2.2) и `orphanRemoval` (п.2.3), пока починка не сделала их живыми.
4. Заменить `unmappedTargetPolicy` на `WARN` и пересобрать — это подсветит компилятором весь объём п.1.2.
5. Ввести `@JsonTypeInfo`/`@JsonSubTypes` на `ComponentTo`, `DeviceTo`, `PortTo` **или** перевести
   `ComponentController`/`DeviceController`/`PortController` на приём сырого JSON, как это уже сделано
   в `PUT /api/components/{id}` (п.1.4).
6. Перевести все сервисы на `UpdatableCrudService.updateFromDto` и вынести разрешение FK
   (по образцу `ComponentService.handleFkChanges`) в общий метод, вызываемый и из `create`, и из `update` (п.1.2, 1.3).
7. `@Valid` на все `@RequestBody`; согласовать `@Size` DTO ↔ сущность ↔ DDL (п.2.1).
8. Исправить импорт `AccessDeniedException` (п.2.4).
