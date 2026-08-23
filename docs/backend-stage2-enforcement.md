# Этап 2: чем принуждать права

> **Статус: реализовано целиком**, включая промотирование. Всё, что ниже помечено как гипотеза
> или предположение, к моменту реализации проверено — см. последний раздел. Приложение идёт в
> режиме `SHADOW` с `enforce-domains: [catalog, location, installation, port, component]`;
> 56 тестов зелёные. Что дало каждое промотирование — в разделе «Промотирование: чем кончилось».
>
> Одна поправка к тексту ниже: падение старта завязано **не** на глобальный режим, как сказано в
> разделе про режимы, а на то, где находится находка. Иначе `SHADOW` + `enforce-domains: [catalog]`
> — ровно та конфигурация, которой заканчивается план, — отказывал бы всем в каталогах при чистом
> логе старта. Правило: `mode == ENFORCE` роняет старт на любой ошибке; промотированный домен —
> на ошибке внутри себя; незаанотированный эндпоинт домена не имеет и следует глобальному режиму,
> как и в рантайме.

Продолжение `docs/backend-concepts.md` (часть 2) и раздела 1.1 `TODO.md`. Этап 1 закрыт: аннотации
стоят, `PermissionRegistry` сканирует, отчёт чистый, таблицы засеяны, ничего не принуждается.
Здесь выбирается механизм принуждения и порядок его включения.

Версии, от которых всё зависит: Spring Boot 3.5.5, Spring Security **6.5.3**, Spring Framework
6.2.9, Java 21. API ниже сверено с этими jar-ами, а не по памяти.

---

## Короткий ответ

**Собственный `AuthorizationManager<MethodInvocation>`, опубликованный как `Advisor` через
`AuthorizationManagerBeforeMethodInterceptor` с pointcut по `@RequestMapping`.**

Не `@PreAuthorize`, не `HandlerInterceptor`, не резолвинг handler-а внутри фильтра.

Решающий довод — не удобство, а то, что этим паттерном Spring Security реализует **сам себя**.
`SecuredAuthorizationManager` (обработчик `@Secured`) устроен ровно так:

```java
Method method = methodInvocation.getMethod();
Object target = methodInvocation.getThis();
Class<?> targetClass = (target != null) ? target.getClass() : null;
MethodClassKey cacheKey = new MethodClassKey(method, targetClass);
```

`MethodClassKey(method, targetClass)` — это буквально `HandlerKey(beanType, method)`, который проект
уже изобрёл в `PermissionRegistry`, наткнувшись на девять наследников `AbstractCrudController`.
Ключ совпадает не случайно: это одна и та же задача — «один метод, разные конкретные классы», — и
фреймворк решает её так же.

---

## Почему не остальные три

| Вариант | Вердикт |
|---|---|
| `@PreAuthorize` на методе | Отпадает по причине из Этапа 1: одно выражение на девять наследников. Годится только для 11 контроллеров с собственными методами, то есть даёт ровно ту половинчатость, против которой написан концепт |
| Мета-аннотация над `@PreAuthorize` (template annotations, 6.4+) | Не решает: параметр берётся из атрибутов аннотации, а не из класса бина |
| `HandlerInterceptor.preHandle` | Работает, но выносит авторизацию **за пределы** Spring Security: нет `AuthorizationDeniedEvent`, нет observability, нет `AuthorizationEventPublisher`, и появляется второй параллельный слой решений. Покрывает только MVC-диспатч — ровно как и method security, то есть и этого преимущества нет. Плюс возня с ERROR/FORWARD-диспатчами |
| `AuthorizationManager<RequestAuthorizationContext>` в `authorizeHttpRequests` | Отпадает. `AuthorizationFilter` работает до `DispatcherServlet`, `HandlerMethod` ещё не выбран. Резолвить его самому запрещает javadoc `HandlerMappingIntrospector`: *«Use of this component incurs the performance overhead of mapping the request, and should not be repeated multiple times per request»*. К тому же `AuthorizationFilter` бежит на каждом диспатче, а не на каждом запросе |

Отдельно: Spring Security 7 планирует `@AuthorizeRequestMapping`
([spring-security#16250](https://github.com/spring-projects/spring-security/issues/16250)) —
фреймворк идёт туда же. Выбранный вариант окажется на пути миграции, а не поперёк него.

---

## Блокирующее условие: bridge-методы

**Это единственное, от чего зависит, заработает ли механизм вообще, и это проверено на
скомпилированных классах, а не выведено.**

`javap -p target/classes .../RackController`:

```
public java.util.List<...RackTo> getAll();
public ...RackTo get(java.util.UUID);
public net.switchscope.to.BaseTo get(java.util.UUID);   <-- bridge
```

`RackController` переопределяет `get(UUID)` с сужённым типом возврата (`RackTo` вместо стёртого
`BaseTo`), и компилятор порождает **синтетический bridge-метод**: на эту операцию в классе
физически два `java.lang.reflect.Method`.

Важно, что это **не единообразно**. Тот же `RackController` не объявляет `create`/`update`/`delete`
вовсе — они наследуются как есть, одним `Method` из `AbstractCrudController`. То есть у девяти
наследников соседствуют две разные ситуации с идентичностью метода, и какая где — зависит от
подкласса.

Реестр это уже переживает: `specificMethod` идёт через
`AopUtils.getMostSpecificMethod`, а тот внутри зовёт `BridgeMethodResolver.findBridgedMethod`.
Принуждение обязано нормализовать ключ **той же самой функцией**:

```java
Class<?> targetClass = AopProxyUtils.ultimateTargetClass(mi.getThis());
Method method = AopUtils.getMostSpecificMethod(mi.getMethod(), targetClass);
```

Если этого не сделать, `getRequiredCode` вернёт `null` на девяти контроллерах, сработает
fail-closed — и **каждый каталожный маршрут отдаст 403 пользователю, у которого право есть**.
Отладка такого сообщения занимает день.

Отсюда первый шаг Этапа 2 — не машинерия выката, а **различающий тест**. Одного эндпоинта мало:
нужны обе формы, иначе тест зелёный, а половина маршрутов сломана.

| Форма | Пример | Что проверяет |
|---|---|---|
| переопределён, bridge есть | `GET /api/racks/{id}` | нормализацию bridge-метода |
| унаследован как есть | `POST /api/racks` | композицию «ресурс подкласса + действие базового метода» |
| собственный метод контроллера | любой каталожный | что обычный случай не сломался |

Пользователь, у которого право **есть**, ожидание 200. Промах по ключу проявится мгновенно как 403.

---

## Вторая находка: гонка на старте

`PermissionRegistry.audit()` висит на `@EventListener(ApplicationReadyEvent.class)`. Веб-сервер в
Spring Boot стартует раньше — в `finishRefresh()` через `WebServerStartStopLifecycle`. Значит есть
окно, в котором сервер уже принимает запросы, а `codeByHandler` ещё пустая:

- если пустой ключ трактуется как deny — окно тотальных 403;
- если как allow — **окно полностью открытого API**.

Второе и есть дыра, ради которой всё затевалось.

Лечение — разделить скан и сверку с БД:

- **скан** (только `RequestMappingHandlerMapping`, без БД) → `SmartInitializingSingleton`. Он
  отрабатывает внутри `finishBeanFactoryInitialization`, то есть до старта сервера, и уже после
  `afterPropertiesSet()` самого `RequestMappingHandlerMapping`. Исходная причина «не
  `@PostConstruct`, мэппинг строится из тех же бинов» этим снимается;
- **сверка с БД и отчёт** могут остаться на `ApplicationReadyEvent`;
- менеджер дополнительно фейлится **закрыто**, если карта пуста, — на случай, если порядок
  когда-нибудь изменится.

---

## Третья находка: `null` — три разных состояния

`getRequiredCode` возвращает `null` для `@AuthenticatedOnly`, для забытой аннотации и (по п. выше)
для непостроенной карты. Принуждение обязано различать их, а булев `null` этого не умеет.

Заменить `String getRequiredCode(...)` на перечислимое решение — по сути `EndpointPermission.Status`,
который уже есть:

```java
sealed interface Requirement {
    record Permission(String code) implements Requirement {}   // проверить authority
    record AuthenticatedOnly() implements Requirement {}       // пропустить (401 уже отдал фильтр)
    record Unknown() implements Requirement {}                 // ЗАКРЫТЬ, а не открыть
}
```

Тогда «аннотацию забыли» нельзя спутать с «прав не требуется» — не по дисциплине, а по типу.
Старую сигнатуру оставить нельзя: она и есть та терпимая двусмысленность, из которой выросла
прежняя дыра.

---

## Четвёртая находка: проксируемость надо проверять

Method security работает через AOP-прокси. `final` класс контроллера, `final` или `private` метод —
и совет **молча** не применится. Тихая дыра ровно того сорта, ради борьбы с которым написан реестр.

Поэтому в `PermissionRegistry` добавляется четвёртая находка рядом с тремя нынешними: для каждого
guarded-эндпоинта проверить, что класс не `final`, метод `public` и не `final`. В `ENFORCE` —
ERROR, роняющий старт.

Это же снимает риск того, что pointcut заставит проксировать все контроллеры (сейчас проксируются
только восемь каталожных с `@PreAuthorize`): если что-то не проксируется, старт об этом скажет,
а не промолчит.

---

## Код

```java
@Configuration
@EnableWebSecurity
@EnableMethodSecurity          // prePost оставить ВКЛЮЧЁННЫМ: @PreAuthorize нужен на переходный период
public class PermissionEnforcementConfig {

    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    static Advisor requiresPermissionAdvisor(PermissionRegistry registry, PermissionPolicy policy) {
        Pointcut pointcut = Pointcuts.union(
                new AnnotationMatchingPointcut(null, RequestMapping.class, true),  // метод, вкл. мета
                new AnnotationMatchingPointcut(RequestMapping.class, true));       // класс
        var interceptor = new AuthorizationManagerBeforeMethodInterceptor(
                pointcut, new RequiresPermissionAuthorizationManager(registry, policy));
        interceptor.setOrder(AuthorizationInterceptorsOrder.PRE_AUTHORIZE.getOrder() - 1);
        return interceptor;
    }
}
```

`AnnotationMatchingPointcut(null, RequestMapping.class, true)` — третий аргумент `checkInherited`
ловит `@GetMapping`/`@PostMapping` как мета-аннотации `@RequestMapping` и наследование от
`AbstractCrudController`. Union класс+метод покрывает оба способа объявления. Конструкторы сверены с
`spring-aop-6.2.9`.

`static` у `@Bean` и `@Role(ROLE_INFRASTRUCTURE)` — обязательны: advisor создаётся очень рано, без
этого Spring будет ругаться на преждевременную инициализацию.

Порядок `PRE_AUTHORIZE - 1` ставит новый чек **перед** `@PreAuthorize`. На переходный период оба
действуют как **И** (оба должны пропустить), то есть эффект — более узкий из двух. Это безопасная
сторона; обратная конструкция (`legacyAllow || newAllow`) консервирует более широкое право и в
руководствах по миграции авторизации прямо названа антипаттерном.

Менеджер:

```java
public AuthorizationDecision check(Supplier<Authentication> auth, MethodInvocation mi) {
    Class<?> targetClass = AopProxyUtils.ultimateTargetClass(mi.getThis());
    Method method = AopUtils.getMostSpecificMethod(mi.getMethod(), targetClass);  // bridge!
    Requirement req = registry.requirementOf(targetClass, method);
    return switch (req) {
        case AuthenticatedOnly ignored -> null;                       // воздержаться
        case Unknown ignored           -> policy.decideUnknown(...);  // ЗАКРЫТЬ (в SHADOW — залогировать)
        case Permission(String code)   -> policy.decide(code, auth, targetClass, method);
    };
}
```

`AuthorizationManager` в 6.5.3 объявляет абстрактным `check(...)`, а `authorize(...)` — default.
Реализуем `check`. Отказ прилетает как `AccessDeniedException` / `AuthorizationDeniedException` —
оба уже отмаплены на 403 в `RestExceptionHandler:62,64`, форму ошибки трогать не нужно.

---

## Режимы: AUDIT → SHADOW → ENFORCE

В TODO два режима. Нужен третий, и он — не украшение: **shadow / dry-run** это то, что индустрия
ставит между «проверили покрытие» и «начали блокировать» (Istio `istio.io/dry-run`, Oso Migrate,
Fairvisor `would_reject`; общий рефрен руководств по миграции авторизации — *shadow mode first,
hard cutover without shadow means production traffic is your test suite*).

Нынешний `AUDIT` — **статический**: он проверяет покрытие (есть ли у операции право), но не
раздачу (есть ли право у того, кто ходит). На вопрос «кто и куда реально ходит» стартовый скан
ответить не может в принципе.

| Режим | Что делает |
|---|---|
| `AUDIT` | как сейчас: отчёт на старте, на запросах ничего |
| `SHADOW` | решение вычисляется на **каждом** запросе, `would-deny` пишется в лог со стабильным маркером, **ничего не блокируется** |
| `ENFORCE` | блокирует |

Плюс свойство `switchscope.security.permission.enforce-domains` — список доменов; домен вне списка
ведёт себя как `SHADOW`. Это даёт и постепенное включение («начиная с `catalog`»), и **откат без
пересборки**, если что-то поехало.

```yaml
switchscope:
  security:
    permission:
      mode: SHADOW
      enforce-domains: [catalog]
```

Честная оговорка: продакшна у проекта нет вовсе — приложение в разработке, dev-БД содержит только
тестовые данные, учёток две (`admin@gmail.com`, `user@gmail.com`), живого трафика нет. Поэтому
SHADOW здесь — не исследование трафика, а страховка и способ увидеть отказ раньше, чем на него
наткнёшься. Развилку про USER он не решает; она решается ниже, статическим фактом.

Из того же следует, что цена ошибки при промотировании домена низкая: никого не заблокируешь,
откат — строка в конфиге, а базу в крайнем случае можно пересоздать. Осторожность плана продиктована
не риском для данных, а тем, что тихо сломанная авторизация (см. bridge-методы) отлаживается долго и
выглядит как чужая проблема.

---

## Authorities и кэш

`UserDetailsService` начинает выдавать `ROLE_ADMIN`/`ROLE_USER` (нужны для `/api/admin/**`) **плюс**
коды прав как `SimpleGrantedAuthority`. Проверка — `hasAuthority`, не `hasRole`; смешивать нельзя,
`RoleHierarchy` применяется только ко второму.

Перед этим надо было убедиться, что форму `getAuthorities()` можно менять безнаказанно. Проверено:

- в `src/main/java` **никто** не читает `getAuthorities()` напрямую;
- `@AuthenticationPrincipal AuthUser` в `AuthController`/`ProfileController` используется только как
  идентичность (id, e-mail);
- слой полевых прав — `UpdatePolicyResolver:22-23` — резолвит политику через
  `AuthUtil.safeGet().hasRole(Role.ADMIN)`, то есть через **enum**, читаемый из `User.roles`, а не
  через authorities. `AdminUpdatePolicy` / `UserUpdatePolicy` выбираются им же.

Значит добавление кодов прав в authorities не переадресует ни одного решения слоя полевых прав —
Этап 1.2 (обнуление через PUT) от этого не страдает.

**Но это же означает связь, которую надо записать для Этапа 4:** удаление enum `Role` ломает не
только `SecurityConfig`, `User`, `AuthUser.hasRole` и `LoginResponseTo`, как записано в TODO, но и
`UpdatePolicyResolver` вместе с обеими политиками. В TODO слой полевых прав в списке зависимостей
enum-а не значится.

### Ловушка, которую надо обезвредить до кэша

В `AppConfig` стоит `@EnableCaching`, в `pom.xml` есть `spring-boot-starter-data-redis`, а
провайдера кэша (`caffeine`, `spring-boot-starter-cache`) — **нет**. Порядок автоконфигурации Boot
(`CacheType`, сверено с `spring-boot-autoconfigure-3.5.5`):

```
GENERIC, JCACHE, HAZELCAST, COUCHBASE, INFINISPAN, REDIS, CACHE2K, CAFFEINE, SIMPLE
```

`REDIS` идёт **раньше** `CAFFEINE` и `SIMPLE`, а `RedisCacheConfiguration` матчится по наличию
`RedisConnectionFactory`, который автоконфигурация redis-стартера создаст. При этом Redis в проекте
не поднят — в `application.yaml:71` его health-check отключён именно поэтому. Сейчас мина не
взорвалась ровно потому, что `@Cacheable` в проекте не используется **ни разу**.

Значит: **первый же `@Cacheable` уйдёт в `RedisCacheManager` и упадёт на подключении.** Перед
кэшированием — либо `caffeine` + `spring.cache.type: caffeine`, либо явный бин `CacheManager`.
Это блокирующее условие, а не деталь. (Вывод сделан из порядка автоконфигурации и условий в jar-е;
эмпирически стартом приложения не подтверждался.)

### Кэшировать надо не то, что написано в TODO

TODO говорит «кэшировать authorities, не сущность `User`». Направление верное — в кэш не должен
попасть пароль, — но объект выбран неудачно. Кэш «пользователь → authorities» плодит записи на
пользователя, требует адресной инвалидации и протухает при смене ролей пользователя.

**Лучше кэшировать `роль → набор кодов прав`:**

- это ~2 роли × 81 право, одна структура на всё приложение;
- ни PII, ни bcrypt-хеша (в отличие от `CachingUserDetailsService`/`@Cacheable` на
  `UserDetailsService`, которые требуют `eraseCredentials(false)`, то есть хранения хеша пароля —
  с Redis на classpath это означало бы хеши в Redis);
- инвалидируется целиком одним движением, и это ровно та таблица, которую правит администратор;
- роли пользователя читаются свежими на каждый запрос — они и так читаются сегодня
  (`user_role` — `@ElementCollection(fetch = EAGER)`), **лишних таблиц на запрос не добавляется
  вовсе**.

Фактически это второй реестр рядом с `PermissionRegistry`, что укладывается в архитектуру проекта.

**И главное про кэш:** всё требование «настраивается без пересборки» проверяется одним действием —
`UPDATE` строки в `role_permissions` должен подействовать. Кэш без инвалидации это требование
отменяет. Поэтому: короткий TTL **и** явный сброс при любой записи в
`roles`/`permissions`/`role_permissions`/`user_role`.

---

## Развилка USER × каталоги — закрывается статическим фактом

TODO оставляет выбор: закрыть каталоги для USER и убрать их из фронтенд-маршрутов, или выдать USER
`catalog.*:create/update/delete` строкой в `role_permissions`.

Проверено во фронтенде:

- `frontend/src/router/index.js`: 50 маршрутов с `meta.roles`; **`['ADMIN']` только у четырёх** —
  `/users/allusers`, `/users/allusers/:id`, `/users/add`, `/users/edit/:id`. Все **16 каталожных**
  маршрутов (`/catalog/component-types`, `/catalog/location-types`, … включая `/:id`-детали) —
  `['USER', 'ADMIN']`;
- `CellActions.vue` не гейтит кнопки ни по роли, ни по правам — кнопки редактирования и удаления
  показываются всем.

То есть фронтенд **намеренно** даёт USER редактировать каталоги, и делал это до `be4f97f`, пока
метод-секьюрити не была включена. Сегодняшние 403 — это регрессия, а не политика.

Из этого следует неприятное про seed: `fill/05` заморозил «сегодняшнее поведение», но сегодняшнее
поведение — это и есть та самая регрессия. Снимок сделан на день позже, чем нужно.

**Рекомендация: выдать USER `catalog.*:create/update/delete`** — строкой в `role_permissions`, без
пересборки. Это восстанавливает намерение фронтенда и заодно первый раз проверяет заявленную
конфигурируемость на живом примере. Обратный выбор (оставить закрытым) — продуктовое решение, и
тогда работать надо во фронтенде: 16 маршрутов и кнопки в `CellActions.vue`, а не одна строка в БД.

---

## Тесты: радиус невелик, но одно ломается по построению

Всего 13 тестовых файлов; аутентификацию используют **три**:
`AbstractCrudControllerTest`, `AbstractCatalogControllerTest`, `CrudSmokeControllerTest` — 16
вызовов `httpBasic(...)`.

Пользователи тестов **не те**, что в dev-БД: у каждого из трёх файлов свой вложенный
`TestSecurityConfig` с собственным `InMemoryUserDetailsManager`:

```java
User.withUsername("user").password("{noop}password").roles("USER").build(),
User.withUsername("admin").password("{noop}password").roles("ADMIN").build()
```

То есть тесты не ходят в `user@gmail.com` / `admin@gmail.com` и не зависят от раздачи в БД. Правка
механическая — `.roles(...)` → `.authorities(...)` с нужными кодами, три файла, по одному месту в
каждом. Общей тестовой конфигурации нет; заводить её сейчас или продублировать список прав трижды —
решать по месту, но три копии списка из 81 кода дублировать нельзя, нужна одна константа.

Отдельно: `TestEntityController` (`@Profile("mvc-test")`) наследует **`AbstractCatalogController`** —
тот самый мёртвый в проде класс — и не несёт `@RequiresPermission`. Pointcut по `@RequestMapping`
его матчит, deny-by-default закрывает, `AbstractCatalogControllerTest` падает. Развилка, которую
надо выбрать **сейчас**, а не наткнуться на неё:

- либо Этап 2 проставляет аннотации на `TestEntityController`;
- либо из ПРИОРИТЕТА 2 вытаскивается вперёд удаление `AbstractCatalogController` вместе с
  `TestEntityController` и `AbstractCatalogControllerTest` (или их перевод на
  `AbstractCrudController`).

Второе честнее: этот класс всё равно помечен на удаление, а Этап 2 — последний момент, когда его
ещё дёшево не тащить дальше.

---

## Порядок работ

Каждый шаг безопасен сам по себе и откатывается свойством.

| | Шаг | Почему здесь |
|---|---|---|
| 2.0 | **Различающий тест на ключ**: MockMvc, наследник `AbstractCrudController` + собственный контроллер, пользователь с правом → 200 | Bridge-методы. Всё остальное построено на совпадении ключа |
| 2.1 | Гонка старта: скан → `SmartInitializingSingleton`; `getRequiredCode` → перечислимое решение; fail-closed на пустой карте | Иначе окно открытого API при каждом рестарте |
| 2.2 | Четвёртая находка реестра — проксируемость | Иначе метод-секьюрити промахнётся молча |
| 2.3 | Явный `CacheManager` (caffeine) **до** любого `@Cacheable`; кэш `роль → права` + инвалидация; `UserDetailsService` выдаёт `ROLE_*` + коды | Ловушка Redis; и `UPDATE` обязан подействовать |
| 2.4 | Advisor + менеджер; режим `SHADOW`, `enforce-domains` пуст | Ничего не блокирует, всё логируется |
| 2.5 | Решить `TestEntityController` / `AbstractCatalogController`; перевести три тестовых файла на authorities | Ломается по построению |
| 2.6 | Выдать USER `catalog.*:create/update/delete` строками в `role_permissions` | Пока `catalog` в SHADOW, это **no-op**: ничего не меняет, но появляется в shadow-логе. И откатывается отдельно от всего остального |
| 2.7 | Убедиться, что в shadow-логе нет `would-deny` для USER на `catalog` | Единственная дешёвая проверка перед необратимым шагом |
| 2.8 | `enforce-domains: [catalog]`; **тем же коммитом** снять `@PreAuthorize("hasRole('ADMIN')")` с восьми каталожных контроллеров | Два чека на одном маршруте не должны жить дольше одного коммита |
| 2.9 | Остальные домены по одному; `hasRole` остаётся только на `/api/admin/**` | |

Этап 4 закрывается этим же кодом **в той части, ради которой он был задуман**: pointcut матчит и
незаанотированные `@RequestMapping`-методы, `Unknown` их закрывает. То есть забытая аннотация под
`/api/**` теперь закрывает, а не открывает, и отдельный `denyAll()` для этого не нужен.

Но формулировка «`denyAll()` для `/api/**`» шире, чем то, что делает method security, и разницу
надо назвать. Метод-секьюрити принципиально не покрывает точки входа, которые не являются
`@RequestMapping`-методами (эндпоинты actuator, сервлеты, статика).

Проверено, чем это закрыто сегодня: в `SecurityConfig.authorizeHttpRequests` нет терминального
правила `anyRequest()` — есть `permitAll` для `/`, api-docs, swagger, favicon и OPTIONS,
`hasRole(ADMIN)` для `/api/admin/**` и `authenticated()` для `/api/**`. В Spring Security 6.5.3
несовпавший запрос **отклоняется**, а не пропускается: в
`RequestMatcherDelegatingAuthorizationManager` зашита строка
`"Denying request since did not find matching RequestMatcher"` (в 5.x на этом месте было
«Abstaining», то есть разрешение). Значит дыры сегодня нет и `/actuator/**` закрыт.

Вывод для Этапа 4: **явный `anyRequest().denyAll()` всё равно стоит дописать** — не потому, что
сейчас открыто, а потому, что сейчас это закрыто умолчанием версии фреймворка, а не выраженным
намерением проекта.

---

## Что осознанно не делается

- Не резолвим handler в фильтре — javadoc `HandlerMappingIntrospector` прямо против.
- Не пишем 45 пустых переопределений ради аннотации.
- Не оставляем `hasRole` и `hasAuthority` на одном маршруте дольше одного коммита.
- Не делаем `legacyAllow || newAllow` — только «оба должны пропустить».
- ABAC / row-level, OPA, `RoleHierarchy` — по-прежнему вне объёма.

---

## Что проверено, а что нет

Проверено при проектировании — на коде и на jar-ах, не по памяти:

- bridge-методы в наследниках `AbstractCrudController` — `javap -p target/classes`;
- API Spring Security 6.5.3: `AuthorizationManager.check` абстрактен, `authorize` — default;
  `AuthorizationManagerBeforeMethodInterceptor(Pointcut, AuthorizationManager<MethodInvocation>)`;
  `AuthorizationInterceptorsOrder.PRE_AUTHORIZE`; конструкторы `AnnotationMatchingPointcut` и
  `Pointcuts.union` в `spring-aop-6.2.9`;
- несовпавший запрос в 6.5.3 отклоняется — строка `"Denying request since did not find matching
  RequestMatcher"` в `RequestMatcherDelegatingAuthorizationManager`;
- порядок `CacheType` в `spring-boot-autoconfigure-3.5.5`: `REDIS` раньше `CAFFEINE` и `SIMPLE`;
- слой полевых прав читает enum `Role`, а не authorities — `UpdatePolicyResolver:22-23`;
- тестовые пользователи — три отдельных `InMemoryUserDetailsManager`, не БД;
- фронтенд даёт USER все 16 каталожных маршрутов; `CellActions.vue` кнопки не гейтит.

Проверено при реализации — то, что раньше было выведено, а не увидено:

- **совпадение ключа.** `PermissionEnforcementTest`, 7 тестов, все зелёные: переопределённый метод
  с bridge (`GET /api/housing/racks/{id}`), чисто унаследованный (`DELETE /api/housing/racks/{id}`)
  и контроллер с собственным методом — каждый пропускает держателя права и отказывает остальным.
  Это тот самый различающий тест, ради которого он стоял первым шагом;
- **гонка на старте закрыта.** В логе `Permission audit […]` — строка 154, `Tomcat started on port
  8090` — строка 156. Раньше отчёт печатался после старта сервера;
- **ловушка Redis.** Отчёт автоконфигурации (`--debug`): `CaffeineCacheConfiguration matched`,
  `RedisCacheConfiguration` — `unknown cache type`. Провайдер выбран намеренно, а не по порядку;
- **pointcut по `@RequestMapping` проксирует все контроллеры без побочных эффектов.** Приложение
  поднимается, 109 эндпоинтов, 0 непроксируемых; живые запросы с `@PathVariable UUID` работают:
  USER читает стойки и каталоги (200), ADMIN читает матрицу (200), USER её не читает (403), USER не
  удаляет тип компонента (403 — от старой `@PreAuthorize`, как и задумано на переходный период);
- **раздача совпала с трафиком:** ни одного `PERMISSION-SHADOW-DENY` в логе;
- **раздача USER на каталоги применена:** в dev-БД по 8 строк на `read/create/update/delete`;
- **`SHADOW` и `enforce-domains` работают:** `PermissionShadowModeTest` — непромотированный домен
  не отказывает никому, промотированный отказывает и пропускает держателя права;
- **кэш действительно перехватывается:** 6 аутентифицированных запросов подряд → **один**
  `Loaded grants` в логе (`ADMIN=81, USER=80`). Без этого утверждение «лишних таблиц на запрос
  ноль» держалось бы только на том, что `@Cacheable` написан;
- **каждая ветка решения, включая недостижимые сквозь HTTP:** `PermissionDecisionTest`, 15 тестов.
  Незаанотированный эндпоинт под `ENFORCE` не доживает до рантайма — реестр роняет старт раньше, —
  поэтому его отказ и сам этот сторож проверяются в изоляции. Там же: exempt-ветка не закрывает
  `/api/auth/**` (её отказ означал бы, что войти не может никто), роль не подменяет право, пустой
  набор authorities не эквивалентен доступу.

Не проверено:

- поведение под нагрузкой: чего стоит `AopUtils.getMostSpecificMethod` на запрос. Если окажется
  заметным — кэшировать по `MethodClassKey`, ровно как это делает `SecuredAuthorizationManager`;
- **обнаружение** непроксируемого эндпоинта. Находок ноль, то есть проверено, что ничего не
  нашлось, а не что механизм находит. Дешёвый способ — временно сделать один контроллер `final`
  и убедиться, что старт под `ENFORCE` падает;
- глобальный `mode: ENFORCE`. Все домены, кроме `system`, промотированы поимённо, но сам флаг
  ещё стоит в `SHADOW`; отличаются они только тем, как обрабатывается эндпоинт **без** кода права
  (доменa у него нет, поэтому промотирование его не касается) и когда падает старт.

## Промотирование: чем кончилось

Шаг 2.9 плана («остальные домены по одному») выполнен. Порядок и эффект:

| Домен | Прав | Что изменилось в ответах API |
|---|---|---|
| `catalog` | 32 | **Да.** Тем же коммитом снята `@PreAuthorize("hasRole('ADMIN')")` с восьми контроллеров (24 аннотации) — USER снова правит справочники, как и предполагал фронтенд |
| `location`, `installation`, `port` | 12 | Нет. Старой аннотации там не было, а обе роли держат все эти права |
| `component` | 36 | Нет, по той же причине |

`component` оставлен напоследок не по осторожности к данным, а по охвату: домен определяется как
часть кода до первой точки, поэтому одно слово в списке включает сразу девять контроллеров —
`ComponentController`, `DeviceController` и семь из девяти наследников `AbstractCrudController`
(оставшиеся два — `location` и `installation`). Это 36 прав из 81 и ровно те маршруты, на которых
живут bridge-методы.

Живая проверка после `component` (профиль `local`, порт 8090, тестовые учётки
`user@gmail.com` / `admin@gmail.com`):

- USER читает `/api/components`, `/api/housing/racks`, `/api/devices/{routers,switches,access-points}`,
  `/api/connectivity/{connectors,patch-panels,cable-runs}`, `/api/devices` — везде 200;
- `GET` и `DELETE` на несуществующий id стоек и роутеров, `DELETE` коннектора и компонента —
  **404, а не 403**: право пропустило, запрос дошёл до сервиса. Это и есть проба на bridge-метод
  (`get` переопределён с сужением типа) и на чисто наследуемый метод (`delete` объявлен один раз
  на девять наследников);
- ADMIN читает матрицу — 200, аноним — 401, `PERMISSION-SHADOW-DENY` в логе — ноль;
- отчёт реестра: `mode=SHADOW, enforced domains=[catalog, location, installation, port, component]`,
  109 эндпоинтов — 101 под правом, 8 вне модели, 0 без аннотации, 0 непроксируемых; 81 право,
  0 отсутствующих в БД, 0 мёртвых строк.

**Что промотирование действительно кусается — проверено удалением строки, а не рассуждением.**
Из `role_permissions` убрана одна строка (`User` × `component.rack:delete`); тот же
`DELETE /api/housing/racks/<absent>` под USER сменил 404 на **403** через ~25 с (TTL кэша
`роль → права` — 60 с), а после возврата строки вернулся к 404 через ~60 с. Это единственная
проба, отличающая «право проверяется» от «право у всех есть, поэтому всё равно 200».

Остался один домен — `system`: одно право (`system.permission:read`) на `/api/admin/**`, который
`SecurityConfig` и так закрывает по роли. Он покрывается не отдельным промотированием, а
переводом самого `mode` в `ENFORCE`.


## Найдено попутно, вне объёма

`HttpMessageNotReadableException` отдаётся как **500**, а не 400: `POST /api/housing/racks` с телом
`{}` возвращает `{"title":"Application error","status":500,"detail":"Exception InvalidTypeIdException"}`.
К правам отношения не имеет, но всплыло из-за них — разбор аргументов идёт до проверки прав, так что
такой запрос до авторизации не доходит вообще (из-за чего пробы на запись в тесте пришлось сделать
`DELETE`, а не `POST`). Записано в ПРИОРИТЕТ 2 `TODO.md`.
