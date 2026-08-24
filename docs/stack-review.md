# SwitchScope — ревизия стека и предложения по улучшению

Дата: 2026-08-23. Ветка `dev`, HEAD `134d8ae` («Write down why the catalog controllers cannot
simply inherit»). В рабочем дереве не закоммичены изменения в `TODO.md`,
`backend/src/main/resources/application.yaml`,
`backend/src/test/java/net/switchscope/security/permission/PermissionShadowModeTest.java`,
`docs/backend-stage2-enforcement.md` — их я не трогал.

**Метод.** Чтение живых файлов на HEAD: `backend/pom.xml`, `application.yaml`,
`frontend/package.json`, `SecurityConfig.java`, тестовое дерево целиком, `frontend/src/api`,
`frontend/src/services/auth.js`; целевые `grep` по `backend/src/main` на предмет фактического
использования объявленных зависимостей; сверка `@Mapper`-интерфейсов с `target/generated-sources`.

**Важно про предыдущие документы.** `docs/backend-audit.md` датирован коммитом `bea4605`;
коммит `be4f97f` («Fix six critical defects in the backend write path») закрыл его пункты 1.1–1.6.
Проверено на HEAD: `@EnableMethodSecurity` в `SecurityConfig.java:34` присутствует, резолверы
ссылок в сервисах на месте. **Ни одна находка ниже не перенесена из тех документов** — всё
перепроверено по коду.

**Чего я не делал.** Приложение не запускалось, `./mvnw test` и `./mvnw verify` не запускались,
ни одного HTTP-запроса не выполнено. Пометки: **[код]** — проверено по файлу или выводу `grep`;
**[?]** — вывод, не подтверждённый прогоном.

---

## 0. Сводка

Стек выбран хорошо и в целом современен: Boot 3.5.5 / Hibernate 7.2 / Java 21, Liquibase с
разделением DDL/DML, MapStruct с `componentModel=spring`, UUID v7, `open-in-view: false`,
конфигурируемые права с режимами AUDIT/SHADOW/ENFORCE. Слоёная схема выдержана, документация
(`docs/architecture.md`) точна и подробна.

Проблемы не в выборе технологий, а в трёх местах:

| Область | Оценка |
|---|---|
| Состав зависимостей | **7 из ~25 библиотек объявлены, но не используются ни строчкой** — включая три, которые тянут за собой обходные пути |
| Тесты | Testcontainers объявлен, настроен и **никуда не подключён**; `./mvnw verify` идёт в живую dev-БД; 16 тестовых файлов на 231 основной |
| Аутентификация | **пароль пользователя хранится в `localStorage` в открытом виде** и читается на каждый запрос |

Остальное — конфигурационная гигиена и расхождение README с реальностью.

---

## 1. Зависимости, которые не используются

Проверено `grep -rl` по `backend/src/main` **[код]**:

| Зависимость | Использований в `src/main` | Что это значит |
|---|---:|---|
| `spring-kafka` | **0** | плюс `spring-kafka-test` и `testcontainers:kafka` в тестах |
| `spring-boot-starter-data-redis` | **0** | единственное упоминание Redis во всём `src/main` — строка в `application.yaml`, которая **выключает его health-check** |
| `spring-boot-starter-websocket` | **0** | |
| `sshj` | **0** | |
| `expectit-core` | **0** | |
| `snmp4j` | **0** | слово «snmp» встречается только как *данные* — поля `snmpCommunity`/`snmpVersion` в `DeviceTo` и seed-CSV |
| `com.google.code.findbugs:annotations:3.0.1u2` | **0** | версия 2015 года |

Используются по назначению: `jsoup` (единственный потребитель — `validation/NoHtmlValidator`),
`mapstruct`, `lombok`, `caffeine`, `springdoc`, `micrometer-registry-prometheus`, `liquibase`.

### 1.1 Каскад Redis — самая дешёвая правка во всём репозитории

Redis не поднят и в коде не используется, но стартер лежит на classpath. Из-за этого понадобились
**два обходных пути**, оба задокументированы в самом коде:

1. `pom.xml`, комментарий над `caffeine`: Boot выбирает провайдер кэша по порядку в `CacheType`,
   где `REDIS` идёт раньше `CAFFEINE`, поэтому первый же `@Cacheable` ушёл бы в
   `RedisCacheManager` и упал на коннекте. Отсюда прибитый гвоздями `spring.cache.type: caffeine`.
2. `application.yaml`: `management.health.redis.enabled: false` — иначе `/actuator/health`
   всегда DOWN.
3. Плюс в трёх `@SpringBootTest` прописан
   `spring.autoconfigure.exclude=…RedisAutoConfiguration,…RedisRepositoriesAutoConfiguration`.

**Удаление одной зависимости делает ненужными все три.** `spring.cache.type: caffeine` при этом
стоит оставить — но уже как осознанный выбор, а не как защиту от случайности.

> Если Redis/Kafka/WebSocket/SSH/SNMP — это запланированная функциональность (README обещает
> SNMP-discovery, SSH-конфигурирование и WebSocket-мониторинг), то правильный ход не «удалить
> навсегда», а **вернуть их вместе с первым кодом, который их использует**. Пока их нет, они
> удлиняют старт, раздувают образ и создают ровно такие ловушки, как выбор провайдера кэша.

### 1.2 `hibernate-platform` и `hibernate-core` разъехались **[код]**

```xml
<!-- dependencyManagement -->
<artifactId>hibernate-platform</artifactId> <version>7.1.1.Final</version>
<!-- dependencies -->
<artifactId>hibernate-core</artifactId>     <version>7.2.0.Final</version>
```

BOM `hibernate-platform` существует ровно для того, чтобы держать модули Hibernate в одной версии,
и здесь он переопределён для главного модуля. Любой другой модуль Hibernate, притянутый транзитивно,
приедет в версии 7.1.1 рядом с core 7.2.0. Нужно поднять платформу до 7.2.0 и убрать явную версию
у `hibernate-core`.

### 1.3 `<changeLogFile>` объявлен дважды **[код]**

В блоке `liquibase-maven-plugin` тег встречается два раза:
`src/main/resources/db/changelog/db.changelog-master.yaml`, а ниже —
`db/changelog/db.changelog-master.yaml` (вместе с `<searchPath>src/main/resources</searchPath>`).
Побеждает второй, первый — молчаливый мусор. То же и с `<verbose>true</verbose>`, он тоже дважды.

### 1.4 Остатки Spring Initializr **[код]**

`<url/>`, `<licenses><license/></licenses>`, `<developers><developer/></developers>`,
пустой `<scm>` с четырьмя пустыми тегами. Пустой `<license/>` без `<name>` — невалидная запись
для `mvn site` и для публикации; проект при этом лицензирован под BSL 1.1 (`LICENSE`), что стоило
бы туда и записать.

### 1.5 Пароли по умолчанию в `<properties>` **[код]**

```xml
<database.username>postgres</database.username>
<database.password>postgres</database.password>
```

Их переопределяет gitignored `backend/.mvn/maven.config`, так что настоящих кредов в репозитории
нет — **это проверено: `git ls-files` не содержит ни `.local.props`, ни `maven.config`,
ни `application-local.yaml`, ни `config.properties`; `.gitignore` закрывает их все.** Гигиена
секретов здесь заметно лучше среднего.

Но дефолт `postgres/postgres` означает, что забытый `maven.config` даёт не понятную ошибку
конфигурации, а попытку залогиниться суперпользователем. Лучше оставить свойства пустыми и
опереться на `<requireProperty>` в `maven-enforcer-plugin`.

---

## 2. Тесты — самое высоколевереджное место

### 2.1 Testcontainers настроен и не подключён **[код]**

`TestcontainersConfiguration` объявляет три контейнера (`postgres:18`, Kafka, Redis) с
`@ServiceConnection`. Единственное место, где он используется:

```java
// TestBackendApplication.java:8
SpringApplication.from(BackendApplication::main).with(TestcontainersConfiguration.class).run(args);
```

Это dev-раннер, а не тестовая инфраструктура. **Ни один тест его не импортирует** — проверено
`grep -rn 'TestcontainersConfiguration' src/test/java`. Более того, все три `@SpringBootTest`
явно ставят `spring.testcontainers.enabled=false`, то есть уходят в тот datasource, что
настроен, — в живую dev-БД на `localhost:5433/switchscope`. `TODO.md` это признаёт: «Своей БД
у тестов нет: `./mvnw verify` идёт в ту же dev-базу».

Всё для правильного решения уже куплено и лежит: зависимость `testcontainers:postgresql`
объявлена, конфигурация написана. Нужен `@Import(TestcontainersConfiguration.class)` на
`AbstractContextTest` (и убрать `spring.testcontainers.enabled=false`). После этого:

- прогон перестаёт зависеть от состояния dev-базы и от того, накатаны ли миграции;
- `liquibase` на чистом контейнере становится **настоящей проверкой changelog-а** — сегодня
  changelog никогда не применяется с нуля в CI;
- побочные эффекты прогона перестают быть «нормой, а не аварией».

Заодно: контейнеры Kafka и Redis можно удалить — см. §1.

### 2.2 `@MockBean` устарел и не переживёт Boot 4 **[код]**

33 использования `org.springframework.boot.test.mock.mockito.MockBean` в `src/test`. Аннотация
объявлена deprecated в Boot 3.4 и удалена в Boot 4; замена — `@MockitoBean` из
`org.springframework.test.context.bean.override.mockito`. Механическая замена, но её надо сделать
до любого разговора о переходе на Boot 4.

Там же: `org.testcontainers.containers.KafkaContainer` — тоже deprecated в пользу
`org.testcontainers.kafka.*`. Если Kafka уходит (§1), вопрос снимается сам.

### 2.3 Обоснование 14 моков в `AbstractContextTest` больше не соответствует коду **[код]**

Класс объявляет:

> «The mappers no MapStruct implementation is generated for, mocked so that the full application
> context can start.»

На HEAD это не так. Проверка: 26 интерфейсов с `@Mapper(` в `src/main`, 26 файлов `*MapperImpl.java`
в `target/generated-sources` — множества совпадают полностью, ни одного `@Mapper` без реализации.
(Остальные 6 из 32 «мапперов» — generic-базы вроде `BaseMapper`/`DeviceMapper`, у которых импла
и не должно быть.)

Значит, четыре `@SpringBootTest` (`BackendApplicationTests`, `PermissionCoverageTest`,
`PermissionEnforcementTest`, `PermissionShadowModeTest`) подменяют моками 14 **рабочих** бинов.
Для тестов прав это безвредно — они не смотрят на тела ответов. Но комментарий объясняет то,
чего нет, и первый же тест, который начнёт проверять payload, получит `null` вместо DTO и
непонятную ошибку. **[?]** Скорее всего, весь `AbstractContextTest` можно удалить целиком —
это проверяется одним прогоном.

### 2.4 Покрытие

16 тестовых файлов на 231 основной (24 828 строк). Из них 6 — тестовые фикстуры
(`TestEntity`, `TestEntityTo`, `TestEntityController`, `TestBackendApplication`,
`TestcontainersConfiguration`, `AbstractContextTest`), то есть настоящих тестов десять, и пять
из них — про подсистему прав. `backend-fixes.md` честно пишет, что переписанный
`AbstractCrudController` и все девять его наследников **не покрыты вовсе**.

Подсистема прав при этом покрыта хорошо и содержательно — `PermissionEnforcementTest` с его
разбором bridge-методов написан на голову выше среднего. Проблема не в качестве тестов,
а в том, что 90% кода их не имеет.

---

## 3. Конфигурация

### 3.1 `clear-checksums: true` противоречит собственному правилу **[код]**

```yaml
spring.liquibase:
  change-log: classpath:db/changelog/db.changelog-master.yaml
  clear-checksums: true
```

Это выполняется **при каждом старте приложения** и стирает контрольные суммы всех применённых
changeset-ов. Тем самым отключается ровно та проверка, которая ловит нарушение правила из
`docs/architecture.md`: «Never modify existing changesets, create new ones». Правило остаётся
в документации, но перестаёт быть проверяемым.

Обычно этот флаг ставят разово, чтобы разобрать конкретный конфликт, и убирают. Если он нужен
постоянно — значит, changeset-ы правятся на месте, и это стоит признать явно, а не маскировать.

### 3.2 Отладочный вывод в профиле по умолчанию **[код]**

```yaml
spring.jpa.show-sql: true
logging.level:
  org.hibernate.SQL: DEBUG
  org.hibernate.orm.jdbc.bind: TRACE
  org.springframework.cache: TRACE
```

`show-sql: true` **и** `org.hibernate.SQL: DEBUG` — это один и тот же SQL дважды, один раз мимо
логгера в `System.out`. `jdbc.bind: TRACE` печатает значения всех параметров, включая те, что
проходят через `EncryptedStringConverter`. Место всему этому — в `application-local.yaml`,
а не в базовом файле, который поедет в любое окружение.

### 3.3 Swagger UI на корне **[код]**

`springdoc.swagger-ui.path: /` плюс `permitAll` на `/` в `SecurityConfig`. Корень приложения —
неаутентифицированная страница документации API. Для dev нормально; как значение по умолчанию —
нет.

### 3.4 Jackson видит поля напрямую **[код]**

```yaml
spring.jackson:
  mapper.default-view-inclusion: true
  visibility: {field: any, getter: none, setter: none, is-getter: none}
```

`field: any` означает сериализацию **приватных полей**, минуя геттеры. Это обходит любую логику
в геттере и делает `@JsonIgnore` на геттере бесполезным. При наличии MapStruct и отдельных
`*To` DTO необходимости в этом быть не должно — вероятно, это наследие ранней стадии, когда
наружу отдавались сущности. **[?]** Проверяется отключением и прогоном.

### 3.5 `app.encryption.key` с рабочим значением по умолчанию **[код]**

```yaml
key: ${APP_ENCRYPTION_KEY:change-me-in-production-32chars}
```

Дефолт ровно 31 символ и приложение с ним стартует. Ключ шифрования лучше не иметь по умолчанию
вовсе — пусть старт падает с внятным сообщением, чем данные окажутся зашифрованы публично
известным ключом.

---

## 4. Аутентификация — здесь самая серьёзная находка

### 4.1 Пароль лежит в `localStorage` в открытом виде **[код]**

`frontend/src/services/auth.js:91-92`:

```javascript
localStorage.setItem("email", credentials.email);
localStorage.setItem("password", credentials.password);
```

`frontend/src/api/instance.js:21-28` читает их на каждом запросе и собирает заголовок:

```javascript
const password = localStorage.getItem("password");
const credentials = btoa(`${email}:${password}`);
return `Basic ${credentials}`;
```

Последствия:
- любой XSS уносит **сам пароль**, а не сессию: его нельзя отозвать, и он, как правило,
  переиспользован в других местах;
- пароль переживает закрытие браузера и остаётся на диске;
- он же виден в DevTools у любого, кто подошёл к незаблокированной машине.

Это прямое следствие связки «Basic + `SessionCreationPolicy.STATELESS`»: раз сессии нет,
креды нужны на каждом запросе, а значит их надо где-то держать. Правильное решение — не
«шифровать localStorage», а **убрать необходимость хранить пароль**: `POST /api/auth/login`,
возвращающий сессионную куку `HttpOnly` (или JWT в `HttpOnly`-куке). `spring-boot-starter-oauth2-resource-server`
уже в зависимостях — половина пути пройдена.

### 4.2 Basic поверх обычного HTTP **[код]**

`getBaseURL()` в `instance.js` жёстко строит `http://…:8090/api/`, и `SecurityConfig` не требует
HTTPS. Basic-заголовок — это base64, не шифрование; в открытом HTTP пароль идёт по сети как есть,
на каждом запросе.

### 4.3 CORS разрешает всю локальную сеть с кредами **[код]**

```java
corsConfig.setAllowedOriginPatterns(List.of(
        "http://localhost:*", "http://192.168.*.*:*", "http://127.0.0.1:*"));
corsConfig.setAllowCredentials(true);
```

Любая страница, отданная любым хостом сети 192.168.0.0/16 на любом порту, может делать
credentialed-запросы к API. Для домашней разработки это удобно, но это значение **по умолчанию,
в основном файле**, а не в dev-профиле. Список origin-ов должен приходить из конфигурации.

### 4.4 CSRF отключён

Для строго stateless-Basic это защитимо (браузер не подставляет Basic-заголовок сам). Но
`withCredentials: true` во фронтенде и `allowCredentials(true)` на бэкенде говорят о том, что
куки в системе всё-таки предполагаются. Как только появится сессионная кука (§4.1), CSRF
придётся включать обратно — стоит заложить это сразу.

---

## 5. Документация разошлась с кодом

`README.md` в разделе Technology Stack обещает:

> **Vue 3** … **Quasar Framework** … **Pinia** для state management … **D3.js** и **Vis.js** …
> **Chart.js** … **Redis** для кэша и сессий … **TimescaleDB**

Фактический `frontend/package.json`: `vue`, `vue-router`, `axios`, `tailwindcss`, `primeicons`,
`vue-spinner`, `vue-toastification`. **Ни Quasar, ни Pinia, ни D3, ни Vis.js, ни Chart.js.**
State management — синглтон-композаблы с ручным кэшем на 5 минут (это описано в
`docs/architecture.md` и работает, но это не Pinia).

`docs/architecture.md` при этом **точен** — расхождение только в README. Раз README первым видит
любой новый человек (и любой ИИ-агент), расхождение обходится дороже, чем кажется.

Мелочь там же: `json-server` числится в `dependencies`, хотя это инструмент для мока API — ему
место в `devDependencies`.

---

## 6. Что в проекте сделано хорошо и стоит сохранить

Это не вежливость — перечисленное ниже дороже всего, что написано выше, и его легко сломать
при рефакторинге.

1. **Подсистема прав (AUDIT → SHADOW → ENFORCE, промотирование по доменам).** Возможность
   включать принуждение по одному домену и откатывать удалением строки из списка, без пересборки, —
   это редко встречающийся уровень аккуратности. Живая проверка через удаление строки
   `role_permissions` вместо рассуждения о том, что механизм работает, — правильный способ
   доказывать такие вещи.
2. **`PermissionEnforcementTest`** и его разбор bridge-методов у generic-контроллеров. Ловушка
   настоящая и неочевидная, а комментарий объясняет *почему* ассерты сформулированы как
   «что угодно, кроме 403».
3. **`open-in-view: false`**, `default_batch_fetch_size: 20`, `ddl-auto: validate`. Три
   маленькие настройки, каждая из которых снимает целый класс проблем.
4. **Разделение Liquibase на `init/` (DDL) и `fill/` (DML) с CSV**, нумерация файлов,
   `relativeToChangelogFile: true` везде.
5. **UUID v7** вместо v4 — правильный выбор для PK с точки зрения локальности индекса.
6. **`validate_schema.py`** в корне: проверка живой схемы против Liquibase. Такой инструмент
   обычно пишут после первого инцидента, а не до.
7. **Гигиена секретов.** Все креды — в gitignored-файлах, в индексе git ничего нет, есть
   `*.example`-двойники. Проверено.
8. **Конфигурационная система таблиц во фронтенде.** Одна `GenericTableView.vue` на все таблицы,
   новая таблица = один конфиг-файл. Сокращение с ~2 840 до ~890 строк — заявлено в
   `docs/architecture.md`; сама схема из кода читается.

---

## 7. Предложения, по убыванию отношения пользы к затратам

| # | Что | Затраты | Почему именно это |
|---|---|---|---|
| 1 | **Подключить Testcontainers к тестам** (`@Import` вместо dev-раннера, убрать `spring.testcontainers.enabled=false`) | часы | Всё уже куплено. Даёт воспроизводимый прогон и, впервые, реальную проверку changelog-а с нуля |
| 2 | **Убрать неиспользуемые зависимости** (kafka, redis, websocket, sshj, snmp4j, expectit, findbugs) | часы | Снимает каскад обходных путей вокруг Redis; возвращать по одной вместе с кодом, который их использует |
| 3 | **Убрать пароль из `localStorage`** — сессионная кука `HttpOnly` вместо Basic-на-каждом-запросе | 1–2 дня | Единственная находка с настоящим импактом на безопасность |
| 4 | **Убрать `clear-checksums: true`** | минуты | Возвращает проверяемость правила «не править применённые changeset-ы» |
| 5 | **Вынести отладочные настройки в `application-local.yaml`** (`show-sql`, `hibernate.SQL`, `jdbc.bind`, swagger на `/`, CORS-паттерны) | часы | Базовый файл едет в любое окружение |
| 6 | **`@MockBean` → `@MockitoBean`**; проверить, нужен ли `AbstractContextTest` вообще | часы | Механически; блокирует переход на Boot 4 |
| 7 | **Выровнять `hibernate-platform` с `hibernate-core`**, убрать дубли `<changeLogFile>`, заполнить/удалить заглушки Initializr | час | Гигиена сборки |
| 8 | **Привести README в соответствие с `package.json`** | час | README читают первым, и он единственный неточный документ в репозитории |
| 9 | **Покрыть `AbstractCrudController` и девять наследников** | дни | Единственный крупный непокрытый кусок; после п.1 это уже можно делать по-настоящему |
| 10 | Убрать `postgres/postgres` из `<properties>`, добавить `maven-enforcer-plugin` | час | Забытый `maven.config` должен падать понятно, а не логиниться суперпользователем |

Пункты 1–2 стоит делать вместе: удаление Kafka и Redis упрощает `TestcontainersConfiguration`
до одного контейнера PostgreSQL, а он и есть тот, который нужен.

---

## 8. Замечание про Boot 4 / Java 25

Если встанет вопрос перехода (Boot 3.5.5 — это ни последняя, ни предпоследняя ветка; актуальны
4.0.x и 4.1.x), то в порядке возрастания стоимости:

1. `@MockBean` → `@MockitoBean` (§2.2) — **обязательно**, аннотация в Boot 4 удалена.
2. Spring Framework 7 и Servlet 6.1 (Tomcat 11) — смена базовых API.
3. Spring Security 7 — `SecurityFilterChain` DSL менялся.
4. Hibernate 7.2 уже стоит и старше того, что несёт Boot 3.5 по умолчанию, — этот шаг фактически
   уже сделан.

Главное препятствие — не API, а **десять настоящих тестов на 24 828 строк**. Переход на Boot 4
без покрытия — это замена набора зависимостей вслепую. Поэтому п.1 и п.9 из таблицы выше стоят
раньше любого разговора о версии Boot.

---

## Приписка: дерево сдвинулось, пока писалось это ревью

К моменту сохранения файла рабочее дерево ушло вперёд. В начале работы `git status` показывал
4 изменённых файла; в конце — **45 изменённых (+300 / −591) и четыре новых пути**:
`security/policy/EntityNullability.java`, `security/policy/NullFieldApplier.java`,
`web/payload/` и `src/test/java/net/switchscope/security/policy/`. Это, судя по коду,
раздел 1.2 из `TODO.md` — обнуление полей через PUT, введён тип `PartialUpdate<T>`,
переписаны 21 сервис и 22 контроллера.

**Что из ревью это НЕ затрагивает** (перепроверено после изменений):

- §1 целиком — состав зависимостей не менялся, `pom.xml` не в списке изменённых.
- §3 и §4 — `application.yaml` и фронтенд не в списке изменённых.
- §5 — README не менялся.
- Расхождение контрактов, о котором пишет коммит `134d8ae`, **сохраняется**:
  `UpdatableCrudService.updateFromDto` по-прежнему возвращает `E`, а
  `DtoCrudService.updateFromDto` — `T`, при одинаковом имени и одинаковом
  `PartialUpdate<T>` в аргументе. Шесть каталожных сервисов по-прежнему на `E`-варианте.

**Что нужно перепроверить перед использованием:**

- §2.3 — вывод про 26 `@Mapper` / 26 сгенерированных `*MapperImpl` сделан по
  `target/generated-sources`, который теперь старше `src`. Сам вывод («у всех мапперов
  реализация есть, поэтому обоснование 14 моков в `AbstractContextTest` неверно») почти
  наверняка устоял — мапперы не в списке изменённых, — но пересборкой это стоит подтвердить.
- `AbstractCrudControllerTest.java` изменён; §2.4 про покрытие могло сдвинуться.

Метод, а не только результат: любую находку отсюда стоит подтверждать `grep` по текущему
состоянию, а не цитировать этот файл — ровно та же оговорка, что сделана в шапке про
`backend-audit.md`.
