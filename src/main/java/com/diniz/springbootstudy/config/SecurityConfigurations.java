package com.diniz.springbootstudy.config;

import com.diniz.springbootstudy.services.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

// ============================================================================
// SPRING SECURITY CONFIGURATION CLASS
// ============================================================================
// Central security configuration bean defining HTTP filters, route permissions,
// session management policies, and password encoders.
// ============================================================================


@Configuration  // Tells Spring this class contains application configuration and bean definitions used at startup
/*
 * Application starts
 *         │
 *         ▼
 * Spring scans classes
 *         │
 *         ▼
 * Finds @Configuration
 *         │
 *         ▼
 * Recognizes this class contains setup/configurations
 *         │
 *         ▼
 * Looks for methods annotated with @Bean
 *         │
 *         ▼
 * Executes these methods on startup
 *         │
 *         ▼
 * Registers the created objects in the Spring context
 */
/*
 * This is a good first stop. Before moving on to @EnableWebSecurity,
 * it's important that this concept is solid: @Configuration doesn't
 * execute security itself; it tells Spring that this class contains
 * the configuration needed to set up the application's security.
 */
@EnableWebSecurity
/*
 * @EnableWebSecurity
 *
 *        │
 *        ▼
 *
 * Spring Security starts
 *
 *        │
 *        ▼
 *
 * Creates security infrastructure
 *
 *        │
 *        ▼
 *
 * Creates security filters
 *
 *        │
 *        ▼
 *
 * Prepares the SecurityFilterChain
 *
 *        │
 *        ▼
 *
 * Intercepts HTTP requests
 */
/*
 * What does @EnableWebSecurity mean?
 * This annotation tells Spring:
 *
 * "Enable the web security module in this application."
 *
 * It acts like an ON switch for Spring Security.
 * Without it, Spring Security won't build the entire infrastructure
 * required to protect HTTP requests.
 */
/*
 * Think of it like a home alarm system:
 *
 * @EnableWebSecurity:
 * "Install and turn on the alarm system."
 *
 * SecurityFilterChain:
 * "Configure how the alarm should operate."
 *
 * For example:
 * - Which doors remain open?
 * - Who is allowed to enter?
 * - Which sensor/method to use?
 */

/*
* CONCLUSION OF BOTH ANNOTATIONS
 * SecurityConfigurations
 *
 *        │
 *        ├── @Configuration
 *        │        ↓
 *        │   Spring configuration class
 *        │
 *        └── @EnableWebSecurity
 *                 ↓
 *            Activates HTTP security
 */
public class SecurityConfigurations {
  //--------------------------ATTRIBUTE CLASS FILTER------------------------------
    /*
     * private final JwtAuthenticationFilter jwtAuthenticationFilter;
     *
     * This line seems simple, but it involves crucial Spring concepts:
     *
     * - Dependency Injection (DI)
     * - Inversion of Control (IoC)
     * - Spring Bean management
     * - Responsibility of the JWT Filter
     *
     * Let's break it down step by step.
     */
    /*
     * 1. It creates an attribute inside the class:
     *
     *    private final JwtAuthenticationFilter jwtAuthenticationFilter;
     *
     *    Translating:
     *    "This SecurityConfigurations class needs access to an object of type JwtAuthenticationFilter."
     *    It is saying:
     *    "To configure security, I need to know my JWT filter."
     *
     * 2. What is JwtAuthenticationFilter?
     *    It is the custom class we created:
     *
     *    public class JwtAuthenticationFilter extends OncePerRequestFilter
     *
     *    It has a specific responsibility:
     *    Intercept every HTTP request and check if a valid JWT token exists.
     *
     *    The flow is:
     *
     *    Client (Postman/Browser)
     *            │
     *            │ Authorization: Bearer token
     *            ▼
     *    JwtAuthenticationFilter
     *            │
     *            │ Token valid?
     *            ▼
     *    SecurityContextHolder receives authenticated user
     *            │
     *            ▼
     *    Controller is executed
     *
     * 3. Why store it here?
     *    Because further down you have:
     *
     *    .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
     *
     *    In other words, this class needs to hand over the filter so Spring can place it
     *    inside the security chain. It needs a reference to this object.
     *    That is why we have:
     *
     *    private final JwtAuthenticationFilter jwtAuthenticationFilter;
     *
     * 4. Now the important detail: final
     *    Why not just:
     *
     *    private JwtAuthenticationFilter jwtAuthenticationFilter;
     *
     *    ?
     *    Because using `final`:
     *    You are saying: "Once this object is received, it will never be replaced."
     *
     *    Example:
     *    Allowed (once):
     *    this.jwtAuthenticationFilter = jwtAuthenticationFilter;
     *
     *    Not allowed later:
     *    jwtAuthenticationFilter = new AnotherFilter(); // Compilation Error!
     */
  /*
   * @Component
   * public class JwtAuthenticationFilter extends OncePerRequestFilter {
   *
   * So Spring does:
   *
   * Spring starts
   *
   *       │
   *       ▼
   *
   * Finds @Component
   *
   *       │
   *       ▼
   *
   * Creates JwtAuthenticationFilter
   *
   *       │
   *       ▼
   *
   * Stores it in the ApplicationContext
   *
   *       │
   *       ▼
   *
   * Delivers it to SecurityConfigurations
   */
  /*
   * public SecurityConfigurations(JwtAuthenticationFilter jwtAuthenticationFilter) {
   *     this.jwtAuthenticationFilter = jwtAuthenticationFilter;
   * }
   *
   * Means:
   *
   * "Spring, when creating my security configuration, automatically inject
   * the JWT filter you already created so that I can add it later to the filter chain."
   */
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    /**
     * Dependency injection via constructor.
     */
    /*
     * public SecurityConfigurations(JwtAuthenticationFilter jwtAuthenticationFilter) {
     *     this.jwtAuthenticationFilter = jwtAuthenticationFilter;
     * }
     *
     * It is right here that we see how this dependency enters the class.
     */
    /*
     * 2. But here is a detail...
     *    In your class:
     *
     *    public SecurityConfigurations(JwtAuthenticationFilter jwtAuthenticationFilter)
     *
     *    You are not doing:
     *
     *    new SecurityConfigurations(...)
     *
     *    Spring is the one doing it.
     *    So the flow is different.
     *
     * 3. What does Spring see?
     *    It finds:
     *
     *    @Configuration
     *    public class SecurityConfigurations
     *
     *    So it thinks:
     *    "I need to create a SecurityConfigurations object."
     *
     *    But it looks at the constructor:
     *
     *    public SecurityConfigurations(JwtAuthenticationFilter jwtAuthenticationFilter)
     *
     *    And realizes:
     *    "To create this class, I first need to have a JwtAuthenticationFilter."
     *
     * 4. So Spring looks for this object
     *    It searches in the ApplicationContext:
     *
     *    ApplicationContext
     *            │
     *            ├── JwtAuthenticationFilter ✅
     *            ├── UserService
     *            ├── Repository
     *            └── other Beans
     *
     *    It finds the filter.
     *    Then it internally performs something equivalent to:
     *
     *    JwtAuthenticationFilter filter =
     *            applicationContext.getBean(JwtAuthenticationFilter.class);
     *
     *    SecurityConfigurations config =
     *            new SecurityConfigurations(filter);
     */
    /*
     * Full flow so far:
     *
     * Spring starts
     *        │
     *        ▼
     * Finds JwtAuthenticationFilter
     *        │
     *        ▼
     * Creates the filter
     *        │
     *        ▼
     * Finds SecurityConfigurations
     *        │
     *        ▼
     * Analyzes the constructor
     *        │
     *        ▼
     * Delivers the JwtAuthenticationFilter
     *        │
     *        ▼
     * Executes:
     * this.jwtAuthenticationFilter = jwtAuthenticationFilter
     *        │
     *        ▼
     * Configuration ready
     */
    public SecurityConfigurations(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    /**
     * Configures the HTTP security filter chain and defines fine-grained access policies.
     *
     * Execution Flow:
     * Disable CSRF ──> Disable Frame Options (H2) ──> Set STATELESS Session ──> Define Authorization Rules ──> Add JWT Filter
     */
    /*
     * The next part will be the first major method:
     *
     * @Bean
     * public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception
     *
     * Here begins the heart of the Spring Security configuration.
     */
    /*
     * Let me study:
     *
     * @Bean
     * public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
     *
     * This line contains several important concepts:
     *
     * - @Bean
     * - SecurityFilterChain
     * - securityFilterChain
     * - HttpSecurity
     * - throws Exception
     *
     * Let's break it down step by step.
     *
     * 1. The @Bean annotation
     *
     *    @Bean
     *
     *    We have already seen that @Configuration transforms this class into a configuration class.
     *    Now, @Bean says:
     *
     *    "Spring, execute this method and register the returned object inside your ApplicationContext."
     *
     *    In other words, this is not an ordinary method.
     *    It is a creator of a Spring-managed object.
     *
     *    Normally in Java, it would look like this:
     *    SecurityFilterChain chain = securityFilterChain(http);
     *
     *    But with Spring:
     *    @Bean
     *    public SecurityFilterChain securityFilterChain(HttpSecurity http)
     *
     *    Spring itself calls this method.
     *
     * 2. What is SecurityFilterChain?
     *    This is the main concept.
     *    Spring Security works based on a chain of filters.
     *    A request does not go straight to the Controller.
     *    It passes through several filters first.
     *
     *    Example:
     *
     *    Client (Postman)
     *           │
     *           ▼
     *    -------------------------
     *    SecurityFilterChain
     *    -------------------------
     *           │
     *           ▼
     *    JWT Filter
     *           │
     *           ▼
     *    Authentication Filter
     *           │
     *           ▼
     *    Authorization Filter
     *           │
     *           ▼
     *    Controller
     *
     *    So:
     *    public SecurityFilterChain securityFilterChain(...)
     *
     *    Means:
     *    "This method will build and return the security chain used to protect my requests."
     *
     * 3. Who uses this object?
     *    Spring Security does.
     *    When the application starts:
     *
     *    @Bean SecurityFilterChain
     *            │
     *            ▼
     *    Spring registers it
     *            │
     *            ▼
     *    Spring Security uses it on every HTTP request
     *
     *    For example, when you perform:
     *    GET /orders01/10
     *
     *    The path is:
     *    Request
     *       │
     *       ▼
     *    SecurityFilterChain
     *       │
     *       ▼
     *    JwtAuthenticationFilter
     *       │
     *       ▼
     *    Checks authentication
     *       │
     *       ▼
     *    Checks authorization
     *       │
     *       ▼
     *    Controller
     *
     * 4. Now, the parameter:
     *    HttpSecurity http
     *
     *    This is another fundamental piece.
     *    Spring hands you an HttpSecurity object to configure.
     *    Think of it as the control panel for HTTP security.
     *    With it, you specify:
     *    - Disable CSRF
     *    - Use stateless session management
     *    - Allow public access to login
     *    - Require ADMIN role on specific routes
     *    - Add my custom JWT filter
     *
     *    Example:
     *    http
     *       .csrf(...)
     *       .sessionManagement(...)
     *       .authorizeHttpRequests(...)
     *
     *    You are configuring this object.
     *    It's like tuning a car:
     *    HttpSecurity is the dashboard.
     *    You adjust:
     *    Engine       → Authentication
     *    Doors        → Endpoints
     *    Alarms       → Filters
     *    Keys         → Permissions
     *
     * 5. Why is it received as a parameter?
     *    Because Spring creates this object for you beforehand.
     *    You never write:
     *    new HttpSecurity();
     *
     *    Spring delivers it ready:
     *    Spring
     *       │
     *       ▼
     *    Configured HttpSecurity
     *       │
     *       ▼
     *    Your method receives it
     *
     * 6. Now:
     *    throws Exception
     *
     *    Why is it there?
     *    Because some internal Spring Security methods can throw checked exceptions.
     *
     *    Example:
     *    http.someConfiguration() // can fail
     *
     *    So the method declares:
     *    throws Exception
     *
     *    Meaning:
     *    "If any internal configuration fails, allow the exception to propagate."
     *
     * Putting it all together:
     *
     * @Bean
     * public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
     *
     * Read it like this:
     * "Spring, execute this method during startup. It will receive your HTTP
     * configuration object, assemble my security filter chain, and register
     * that chain to protect all requests in the application."
     *
     * Visual Flow:
     * Application starts
     *        │
     *        ▼
     * Spring finds @Bean
     *        │
     *        ▼
     * Calls securityFilterChain()
     *        │
     *        ▼
     * Passes HttpSecurity
     *        │
     *        ▼
     * Method configures:
     *    - CSRF
     *    - Session Management
     *    - Permissions
     *    - JWT Filter
     *        │
     *        ▼
     * .build()
     *        │
     *        ▼
     * Returns SecurityFilterChain
     *        │
     *        ▼
     * Spring uses it on all requests
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        /*
         * Now let's enter the first line of the chain:
         *
         * return http
         *
         * It seems simple, but we need to understand the reason behind this programming style.
         *
         * The full method is:
         *
         * @Bean
         * public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
         *     return http
         *             .csrf(...)
         *             .headers(...)
         *             .sessionManagement(...)
         *             .authorizeHttpRequests(...)
         *             .build();
         * }
         */
        /*
         * 1. What is this `http`?
         *    This `http` is the object received by the method:
         *
         *    public SecurityFilterChain securityFilterChain(HttpSecurity http)
         *
         *    In other words, Spring handed us an `HttpSecurity` object.
         *    It is the object responsible for configuring HTTP security.
         *
         * 2. Why does it start with `return http`?
         *    Because all these methods:
         *
         *    .csrf().headers().sessionManagement().authorizeHttpRequests()
         *
         *    work on top of the same object.
         *    It is as if we were saying:
         *
         *    Take the HttpSecurity object
         *           │
         *           ▼
         *    Configure one thing
         *           │
         *           ▼
         *    Configure another thing
         *           │
         *           ▼
         *    Configure yet another thing
         *           │
         *           ▼
         *    In the end, transform it into a SecurityFilterChain
         *
         * 3. This style has a name
         *    This design pattern is called:
         *
         *    Fluent API
         *
         *    It occurs when methods return the object itself (or its configurator),
         *    allowing method calls to be chained together continuously.
         *
         *    Simple Java example:
         *
         *    StringBuilder builder = new StringBuilder();
         *    builder
         *        .append("Hello ")
         *        .append("Diniz")
         *        .append("!");
         *
         *    Each `.append()` returns the `StringBuilder` instance itself.
         *    So `builder.append()` becomes `builder.append().append().append();`
         *
         *    In Spring Security:
         *
         *    http
         *        .csrf(...)
         *        .headers(...)
         *        .sessionManagement(...)
         *
         *    follows the exact same idea.
         *
         * 4. What happens internally?
         *    Imagine:
         *
         *    http.csrf(...) -> returns HttpSecurity
         *
         *    So it becomes:
         *
         *    HttpSecurity
         *         │
         *         ▼
         *    csrf(...)
         *         │
         *         ▼
         *    returns updated HttpSecurity
         *         │
         *         ▼
         *    headers(...)
         *         │
         *         ▼
         *    returns updated HttpSecurity
         *         │
         *         ▼
         *    sessionManagement(...)
         *
         * 5. So the object is being modified in-place?
         *    Exactly.
         *    There are not multiple objects being created at each step.
         *    It is the same object:
         *
         *                    Same Object
         *                        │
         *                        ▼
         *    HttpSecurity ────────────────
         *
         *    Before:
         *    CSRF: Default
         *    Session: Default
         *    Filters: Default
         *
         *    After:
         *    CSRF: Disabled
         *    Session: STATELESS
         *    Filters: JWT added
         *
         * 6. And at the end?
         *    At the very end, we have:
         *
         *    .build();
         *
         *    This method takes all that accumulated configuration and creates a:
         *
         *    SecurityFilterChain
         *
         *    Flow:
         *    HttpSecurity
         *         │
         *         │ (accumulated configurations)
         *         ▼
         *    .build()
         *         │
         *         ▼
         *    SecurityFilterChain
         *
         *    That is why the method's return type is `SecurityFilterChain`.
         *
         * 7. Full flow of this line
         *
         *    return http
         *
         *    Means:
         *    "Take the HttpSecurity object that Spring delivered to me,
         *    apply all following configurations, and return the completed filter chain."
         */
        return http
                .csrf(csrf -> csrf.disable())
                /*
                 * A próxima linha é:
                 *
                 * .csrf(csrf -> csrf.disable())
                 *
                 * Vamos detalhar isso em partes:
                 *
                 * 1. O que é CSRF?
                 *    CSRF significa Cross-Site Request Forgery (Falsificação de Requisição Entre Sites).
                 *    É um ataque onde um site malicioso engana o navegador de um usuário autenticado
                 *    para enviar requisições não autorizadas para a sua aplicação web,
                 *    aproveitando-se de Cookies de sessão enviados automaticamente.
                 *
                 * 2. Por que desabilitar aqui?
                 *    - Aplicações web tradicionais (Stateful) usam Cookies para guardar sessão,
                 *      os quais o navegador envia automaticamente. Elas PRECISAM de proteção CSRF.
                 *    - APIs REST que usam JWT (Stateless) armazenam o token (ex: no LocalStorage/Header)
                 *      e o enviam explicitamente via cabeçalho `Authorization: Bearer <token>`.
                 *      Como o navegador NÃO envia cabeçalhos customizados automaticamente para outros sites,
                 *      APIs REST sem sessão ficam naturalmente imunes a ataques clássicos de CSRF.
                 *
                 * 3. Entendendo a sintaxe Lambda:
                 *    .csrf(csrf -> csrf.disable())
                 *
                 *    - `.csrf(...)` chama o método configurador de CSRF no `HttpSecurity`.
                 *    - `csrf ->` é uma expressão Lambda que recebe o objeto `CsrfConfigurer`.
                 *    - `csrf.disable()` desativa explicitamente a proteção CSRF.
                 *
                 *    Nota: No Spring Security 6+, você também pode escrever usando Method Reference:
                 *    .csrf(AbstractHttpConfigurer::disable)
                 *
                 * Fluxo:
                 * HttpSecurity
                 *      │
                 *      ▼
                 * .csrf(csrf -> csrf.disable())
                 *      │
                 *      ▼
                 * Proteção CSRF Desativada (Ideal para APIs REST Stateless com JWT)
                 */
                .headers(headers -> headers.frameOptions(frame -> frame.disable())) // Release H2 Console access
                /*
                 * Next line:
                 *
                 * .headers(headers -> headers.frameOptions(frame -> frame.disable()))
                 *
                 * Let's break this down step by step:
                 *
                 * 1. What are Frame Options?
                 *    `X-Frame-Options` is an HTTP response header that tells the browser
                 *    whether or not a webpage is allowed to render inside an <iframe>,
                 *    <frame>, <embed>, or <object>.
                 *
                 * 2. What attack does it prevent by default?
                 *    By default, Spring Security enables Clickjacking protection by setting
                 *    `X-Frame-Options: DENY`. Clickjacking is an attack where a malicious site
                 *    embeds your website inside an invisible iframe to trick users into clicking
                 *    buttons or links on your site unwittingly.
                 *
                 * 3. Why disable it?
                 *    - The most common reason in Spring Boot development is to enable the
                 *      embedded H2 Database Console (`/h2-console`), which relies heavily
                 *      on HTML <iframe> elements to render its dashboard.
                 *    - Another reason is if your application legitimately needs to be embedded
                 *      inside another website or portal using an iframe.
                 *
                 * 4. Lambda syntax breakdown:
                 *    - `.headers(...)` configures HTTP Security Response Headers.
                 *    - `headers.frameOptions(...)` specifically configures the `X-Frame-Options` header.
                 *    - `frame -> frame.disable()` completely turns off the restriction.
                 *
                 *    Note: A safer alternative if you only want to support the H2 Console is:
                 *    `frame -> frame.sameOrigin()` (which allows frames from the same origin while blocking external sites).
                 *
                 * Flow:
                 * HttpSecurity
                 *      │
                 *      ▼
                 * .headers(headers -> headers.frameOptions(frame -> frame.disable()))
                 *      │
                 *      ▼
                 * X-Frame-Options header disabled (Allows <iframe> embedding, e.g., for H2 Console)
                 */
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                /*
                 * Next line:
                 *
                 * .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                 *
                 * Let's break this down step by step:
                 *
                 * 1. What is Session Management in Spring Security?
                 *    By default, traditional web applications use HTTP Sessions (Stateful).
                 *    The server creates an `HttpSession` on the server side and sends a cookie
                 *    (e.g., JSESSIONID) to the client so that the user stays logged in between requests.
                 *
                 * 2. What does SessionCreationPolicy.STATELESS mean?
                 *    It tells Spring Security:
                 *    "Do NOT create an HTTP session, and NEVER use an existing HTTP session
                 *    to obtain the SecurityContext/Authentication."
                 *
                 * 3. Why use STATELESS for REST APIs with JWT?
                 *    - REST APIs should be stateless (each request contains all the information
                 *      needed to authenticate, e.g., the JWT in the `Authorization: Bearer` header).
                 *    - Eliminates server-side memory overhead for storing user sessions.
                 *    - Enables effortless horizontal scaling across multiple servers (load balancing),
                 *      since any server can validate a request independently without needing
                 *      shared session storage (like Redis).
                 *
                 * 4. Lambda syntax breakdown:
                 *    - `.sessionManagement(...)` configures the session management strategy.
                 *    - `session ->` receives the `SessionManagementConfigurer`.
                 *    - `session.sessionCreationPolicy(...)` sets the creation policy mode.
                 *
                 * Flow:
                 * HttpSecurity
                 *      │
                 *      ▼
                 * .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                 *      │
                 *      ▼
                 * Spring Security will never create or rely on HTTP Sessions (JSESSIONID)
                 */
                .authorizeHttpRequests(authorize -> authorize
                        /*
                         * 1. What does authorizeHttpRequests mean?
                         *    Translating:
                         *    authorizeHttpRequests -> "Authorize HTTP requests."
                         *
                         *    In other words:
                         *    Spring Security will evaluate every incoming request and ask:
                         *    - Can this URL be accessed?
                         *    - Who can access it?
                         *    - Does the user need to be authenticated?
                         *    - Does the user need the ADMIN role?
                         *
                         * 2. The `authorize` parameter
                         *    Notice:
                         *    authorize -> authorize
                         *
                         *    We have a Lambda expression.
                         *    The first `authorize ->`: receives the configuration object.
                         *    The second: `authorize.requestMatchers(...)` uses that object.
                         *
                         *    It could be written like this:
                         *    .authorizeHttpRequests(config -> {
                         *        config.requestMatchers(...);
                         *    })
                         *
                         *    But Spring prefers the chained fluent style.
                         *
                         * 3. What is this object?
                         *    This object is the authorization registry.
                         *    It provides methods such as:
                         *    - .requestMatchers()
                         *    - .hasAuthority()
                         *    - .authenticated()
                         *    - .permitAll()
                         *    - .anyRequest()
                         *
                         *    Each method call builds an authorization rule.
                         *
                         * 4. How to mentalize it?
                         *    Imagine a security guard at a building entrance holding a rule sheet:
                         *
                         *    DOOR / ENDPOINT          RULE
                         *    ──────────────────────────────────────────────────
                         *    Login                    Anyone (Public)
                         *    GET /products            Anyone (Public)
                         *    Create Product           ADMIN only
                         *    View Profile             Authenticated User
                         *    Delete User              ADMIN only
                         *
                         *    This sheet is exactly what this block defines.
                         *
                         * 5. Flow of a request:
                         *    Imagine: GET /products
                         *
                         *    Client
                         *       │
                         *       ▼
                         *    SecurityFilterChain
                         *       │
                         *       ▼
                         *    JwtAuthenticationFilter
                         *       │
                         *       ▼
                         *    authorizeHttpRequests
                         *       │
                         *       ▼
                         *    Searches for a matching rule for /products
                         *       │
                         *       ▼
                         *    Decides: permitAll? / authenticated? / ROLE_ADMIN?
                         *       │
                         *       ▼
                         *    Controller
                         *
                         * 6. Order of rules IS CRITICAL!
                         *    Spring Security evaluates rules from TOP TO BOTTOM.
                         *
                         *    In your code:
                         *    .requestMatchers(HttpMethod.GET, ApiRoutes.USERS_ME).authenticated()
                         *    .requestMatchers(HttpMethod.GET, ApiRoutes.USERS_WILDCARD).hasAuthority("ROLE_ADMIN")
                         *
                         *    Why does `/me` come BEFORE `/users/*`?
                         *    Because `/users/me` also matches the wildcard pattern `/users/*`.
                         *
                         *    If the ADMIN wildcard rule came first:
                         *    GET /users/* ──► Requires ROLE_ADMIN
                         *
                         *    A regular authenticated user trying to access `/users/me` would get:
                         *    403 Forbidden!
                         *
                         *    Rule of thumb:
                         *    More specific rules MUST come before general/wildcard rules.
                         *
                         * 7. The catch-all at the end:
                         *    .anyRequest().authenticated()
                         *
                         *    Means:
                         *    "Any endpoint that wasn't explicitly matched above requires an authenticated user."
                         *
                         *    Example:
                         *    You create a new endpoint: GET /reports
                         *    ...but forget to define a specific `.requestMatchers(...)` for it.
                         *
                         *    It falls through to:
                         *    /reports ──► anyRequest() ──► authenticated()
                         *
                         *    - Without valid JWT: 401 Unauthorized
                         *    - With valid JWT: Access Granted
                         *
                         * 8. Visualizing your authorization architecture:
                         *
                         *    authorizeHttpRequests
                         *           │
                         *           ├── Public Routes
                         *           │     └── permitAll()
                         *           │
                         *           ├── Authenticated User Routes
                         *           │     └── authenticated()
                         *           │
                         *           └── Administrator Routes
                         *                 └── hasAuthority("ROLE_ADMIN")
                         *
                         * 9. Crucial distinction: Authentication vs. Authorization
                         *
                         *    Authentication:
                         *    "WHO ARE YOU?"
                         *    Example (JWT payload):
                         *    email = admin@email.com
                         *    role = ADMIN
                         *
                         *    Authorization:
                         *    "WHAT ARE YOU ALLOWED TO DO?"
                         *    Example (ROLE_ADMIN permissions):
                         *    ROLE_ADMIN
                         *       ├── Create Product
                         *       ├── Delete User
                         *       └── View All Orders
                         *
                         *    Therefore:
                         *    - `.authenticated()` asks: "Is there a logged-in user?"
                         *    - `.hasAuthority("ROLE_ADMIN")` asks: "Does the logged-in user have admin privileges?"
                         */

                        // ===================================================
                        // 1. PUBLIC ENDPOINTS (No authentication required)
                        // ===================================================
                        .requestMatchers(HttpMethod.POST, ApiRoutes.AUTH_LOGIN).permitAll()
                        /*
                         * Next line:
                         *
                         * .requestMatchers(HttpMethod.GET, ApiRoutes.PRODUCTS_WILDCARD).permitAll()
                         *
                         * Here we see a crucial concept: URL Wildcards.
                         *
                         * 1. What we already know:
                         *    The structural pattern remains:
                         *
                         *    .requestMatchers(HTTP_METHOD, PATH).ACCESS_RULE()
                         *
                         *    So:
                         *    .requestMatchers(
                         *        HttpMethod.GET,
                         *        ApiRoutes.PRODUCTS_WILDCARD
                         *    ).permitAll()
                         *
                         *    Means:
                         *    "Allow public access for GET requests related to products."
                         *
                         * 2. What is HttpMethod.GET?
                         *    GET means: "Fetch/Retrieve data."
                         *    Examples:
                         *    - GET /products      -> List all products
                         *    - GET /products/10   -> Get product details for ID 10
                         *
                         *    Since GET requests are read-only operations, they are usually
                         *    public in an e-commerce platform so visitors can browse the store.
                         *
                         * 3. The new key concept: ApiRoutes.PRODUCTS_WILDCARD
                         *    The constant name contains WILDCARD, which means "coringa".
                         *    It is defined in your routes class as something like:
                         *
                         *    public static final String PRODUCTS_WILDCARD = "/products/**";
                         *
                         * 4. What does `/products/**` mean?
                         *    The double asterisk `**` means: "Any path and sub-path under this route."
                         *
                         *    If configured as `/products/**`, it matches:
                         *    - GET /products
                         *    - GET /products/1
                         *    - GET /products/1/details
                         *    - GET /products/category/electronics
                         *
                         * 5. Crucial difference between `*` and `**`:
                         *
                         *    Single Asterisk (*): Matches only ONE level deep.
                         *    Path: /products/*
                         *    - ✅ GET /products/10
                         *    - ❌ GET /products/10/details (Fails: 2 levels deep)
                         *
                         *    Double Asterisk (**): Matches ANY depth level.
                         *    Path: /products/**
                         *    - ✅ GET /products/10
                         *    - ✅ GET /products/10/details
                         *    - ✅ GET /products/category/books/java
                         *
                         * 6. Granular Security (Method-based isolation):
                         *    This rule ONLY permits GET requests.
                         *    It DOES NOT permit POST, PUT, or DELETE on `/products/**`.
                         *
                         *    Method Matrix:
                         *    ┌────────┬──────────────────────┬──────────────┐
                         *    │ Action │ Route / Method       │ Permission   │
                         *    ├────────┼──────────────────────┼──────────────┤
                         *    │ Read   │ GET  /products/**    │ permitAll()  │
                         *    │ Create │ POST /products/**    │ ROLE_ADMIN   │
                         *    │ Update │ PUT  /products/**    │ ROLE_ADMIN   │
                         *    │ Delete │ DELETE /products/**  │ ROLE_ADMIN   │
                         *    └────────┴──────────────────────┴──────────────┘
                         *
                         * Summary of this line:
                         * Means: "Anyone can browse the product catalog using GET requests without sending a JWT token."
                         *
                         * The following line:
                         * .requestMatchers(HttpMethod.GET, ApiRoutes.CATEGORIES_WILDCARD).permitAll()
                         *
                         * applies this exact same wildcard logic to product categories!
                         */
                        .requestMatchers(HttpMethod.POST, ApiRoutes.AUTH_FORGOT_PASSWORD).permitAll()
                        .requestMatchers(HttpMethod.POST, ApiRoutes.AUTH_RESET_PASSWORD).permitAll()
                        .requestMatchers(HttpMethod.POST, ApiRoutes.USERS_BASE).permitAll() // Public user self-registration
                        .requestMatchers(HttpMethod.GET, ApiRoutes.PRODUCTS_WILDCARD).permitAll() // Public product catalog showcase
                        .requestMatchers(HttpMethod.GET, ApiRoutes.CATEGORIES_WILDCARD).permitAll() // Public category catalog showcase
                        .requestMatchers(ApiRoutes.H2_CONSOLE).permitAll() // Access to H2 Database Web Console
                        .requestMatchers(ApiRoutes.TEST_RESET).permitAll() // Test environment database reset endpoint

                        // ===================================================
                        // 2. SPECIFIC PRIVATE ENDPOINTS (LOGGED USER PROFILE)
                        // CRITICAL: Evaluated before generic ADMIN boundaries to avoid path matching collision
                        // ===================================================
                        .requestMatchers(HttpMethod.GET, ApiRoutes.USERS_ME).authenticated()
                        .requestMatchers(HttpMethod.PUT, ApiRoutes.USERS_ME).authenticated()
                        .requestMatchers(HttpMethod.GET, ApiRoutes.ORDERS_BASE + "/me").authenticated() // User personal orders (/orders01/me)

                        // ===================================================
                        // 3. ADMIN RESTRICTED ENDPOINTS (Requires ROLE_ADMIN Authority)
                        // Real-world Rule: Full administrative control over users, catalogs & global orders
                        // ===================================================
                        .requestMatchers(ApiRoutes.ADMIN_BASE).hasAuthority("ROLE_ADMIN") // ProductAdminController (/admin/products)

                        // Product & Category Management Mutations (ADMIN Only)
                        .requestMatchers(HttpMethod.POST, ApiRoutes.PRODUCTS_WILDCARD).hasAuthority("ROLE_ADMIN")
                        .requestMatchers(HttpMethod.PUT, ApiRoutes.PRODUCTS_WILDCARD).hasAuthority("ROLE_ADMIN")
                        .requestMatchers(HttpMethod.DELETE, ApiRoutes.PRODUCTS_WILDCARD).hasAuthority("ROLE_ADMIN")

                        .requestMatchers(HttpMethod.POST, ApiRoutes.CATEGORIES_WILDCARD).hasAuthority("ROLE_ADMIN")
                        .requestMatchers(HttpMethod.PUT, ApiRoutes.CATEGORIES_WILDCARD).hasAuthority("ROLE_ADMIN")
                        .requestMatchers(HttpMethod.DELETE, ApiRoutes.CATEGORIES_WILDCARD).hasAuthority("ROLE_ADMIN")

                        // User Administration (ADMIN Only)
                        .requestMatchers(HttpMethod.GET, ApiRoutes.USERS_BASE).hasAuthority("ROLE_ADMIN") // List all system users
                        .requestMatchers(HttpMethod.GET, ApiRoutes.USERS_WILDCARD).hasAuthority("ROLE_ADMIN") // Find user by ID
                        .requestMatchers(HttpMethod.PUT, ApiRoutes.USERS_WILDCARD).hasAuthority("ROLE_ADMIN") // Update user by ID
                        .requestMatchers(HttpMethod.DELETE, ApiRoutes.USERS_WILDCARD).hasAuthority("ROLE_ADMIN") // Delete user by ID

                        // Order System-Wide Listing (ADMIN Only)
                        // Restricts global order list so regular clients cannot inspect company sales
                        .requestMatchers(HttpMethod.GET, ApiRoutes.ORDERS_BASE).hasAuthority("ROLE_ADMIN")

                        // ===================================================
                        // 4. PRIVATE ENDPOINTS (Requires valid Bearer JWT token)
                        // Allows logged clients to interact with specific order operations
                        // ===================================================
                        .requestMatchers(HttpMethod.GET, ApiRoutes.ORDERS_BASE + "/*").authenticated() // Detailed order view (/orders01/{id})
                        .requestMatchers(HttpMethod.POST, ApiRoutes.ORDERS_BASE).authenticated() // New order placement (/orders01)

                        // Any other unmapped request must be authenticated
                        .anyRequest().authenticated()
                        /*
                         * // Any other unmapped request must be authenticated
                         * .anyRequest().authenticated()
                         *
                         * It works as a final security fallback rule.
                         *
                         * 1. What does anyRequest() mean?
                         *    anyRequest() means:
                         *    "Any request that has not been matched by any previous rule."
                         *
                         *    In other words, everything that did not trigger a preceding .requestMatchers(...).
                         *
                         *    Example - imagine you have these rules:
                         *    .requestMatchers("/auth/login").permitAll()
                         *    .requestMatchers("/products/**").permitAll()
                         *    .requestMatchers("/users/me").authenticated()
                         *
                         *    Now someone requests:
                         *    GET /reports
                         *
                         *    Spring Security checks top-to-bottom:
                         *    - Matches /auth/login? ❌ No
                         *    - Matches /products/**? ❌ No
                         *    - Matches /users/me?   ❌ No
                         *
                         *    So it hits:
                         *    .anyRequest()
                         *
                         * 2. What does .authenticated() do?
                         *    It means: "Requires an authenticated user."
                         *
                         *    Flow required:
                         *    JWT sent in request header
                         *           │
                         *           ▼
                         *    JwtAuthenticationFilter validates token
                         *           │
                         *           ▼
                         *    Token valid -> SecurityContextHolder populated
                         *
                         *    - If unauthenticated -> Returns 401 Unauthorized
                         *    - If valid JWT present -> Allowed to reach Controller
                         *
                         * 3. Why does this catch-all rule exist?
                         *    For defense-in-depth security.
                         *
                         *    Suppose you create a new endpoint in a Controller:
                         *    @GetMapping("/reports")
                         *    public List<Report> getReports() { ... }
                         *
                         *    ...and you forget to write an explicit .requestMatchers("/reports") rule.
                         *
                         *    Without .anyRequest().authenticated(), unmapped endpoints might default
                         *    to open access.
                         *
                         *    With .anyRequest().authenticated():
                         *    GET /reports
                         *           │
                         *           ▼
                         *    No specific match above
                         *           │
                         *           ▼
                         *    anyRequest()
                         *           │
                         *           ▼
                         *    authenticated() ──► 401 if no valid JWT
                         *
                         * 4. Important distinction: authenticated != ADMIN
                         *    A common misconception is thinking .authenticated() grants admin permissions.
                         *    It does NOT. It simply means "logged in".
                         *
                         *    - Regular user with valid JWT ──► Allowed
                         *    - Admin user with valid JWT   ──► Allowed
                         *
                         *    Specific role restrictions require `.hasAuthority("ROLE_ADMIN")` or `.hasRole("ADMIN")`.
                         *
                         * 5. Placement in the configuration chain
                         *    `.anyRequest()` MUST ALWAYS be the final rule in the authorization block.
                         *
                         *    If placed at the top:
                         *    .anyRequest().authenticated() // ❌ Traps every single request!
                         *    .requestMatchers("/auth/login").permitAll() // Never reached!
                         *
                         *    Spring evaluates rules sequentially from top to bottom; the first match wins.
                         *
                         * 6. Summary of the full authorization chain flow:
                         *
                         *    POST /auth/login        ──► permitAll()
                         *    GET  /products/**       ──► permitAll()
                         *    GET  /users/me          ──► authenticated()
                         *    POST /products/**       ──► hasAuthority("ROLE_ADMIN")
                         *    ...
                         *    Any unmapped route     ──► anyRequest().authenticated()
                         */
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
        /*
         * Next lines:
         *
         * .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
         * .build();
         *
         * Let's break down these two final lines in detail:
         *
         * 1. Understanding .addFilterBefore(...)
         *    Spring Security works via a chain of filters executed sequentially on every HTTP request.
         *    By default, Spring Security uses `UsernamePasswordAuthenticationFilter` to handle traditional
         *    form-based logins (session & cookie authentication).
         *
         *    Since our API relies on Stateless JWT authentication, we need our custom filter
         *    (`jwtAuthenticationFilter`) to intercept the request BEFORE standard username/password
         *    authentication takes place.
         *
         *    Method parameters:
         *    - Parameter 1: `jwtAuthenticationFilter` (The custom filter instance we injected)
         *    - Parameter 2: `UsernamePasswordAuthenticationFilter.class` (The target reference position)
         *
         * 2. Why execute JWT verification BEFORE UsernamePasswordAuthenticationFilter?
         *    If a request carries an `Authorization: Bearer <token>` header:
         *    - `jwtAuthenticationFilter` runs FIRST.
         *    - It extracts and validates the token.
         *    - If valid, it creates an `Authentication` object and stores it inside Spring's `SecurityContextHolder`.
         *    - Subsequent security filters (and `authorizeHttpRequests`) recognize that the request is
         *      ALREADY authenticated, bypassing redundant login/form checks.
         *
         *    Filter Chain Order:
         *    Incoming HTTP Request
         *             │
         *             ▼
         *    [ CorsFilter ]
         *             │
         *             ▼
         *    [ jwtAuthenticationFilter ] ◄── (OUR CUSTOM FILTER ADDED HERE)
         *             │   • Extracts JWT token from Header
         *             │   • Validates signature & expiration
         *             │   • Sets user in SecurityContextHolder
         *             ▼
         *    [ UsernamePasswordAuthenticationFilter ]
         *             │
         *             ▼
         *    [ FilterSecurityInterceptor / AuthorizationFilter ]
         *             │   • Enforces .authorizeHttpRequests() rules
         *             ▼
         *    [ Controller Endpoint ]
         *
         * 3. Understanding .build();
         *    This is the final terminal step of the Fluent Builder pattern on `HttpSecurity`.
         *
         *    What happens inside `.build()`?
         *    - It collects all registered configurations (CSRF disabled, Stateless session policy,
         *      route rules, custom filters).
         *    - It validates and compiles these rules into a concrete `DefaultSecurityFilterChain` object.
         *    - That `SecurityFilterChain` instance is returned by the `@Bean` method and registered
         *      in Spring's `ApplicationContext`.
         *
         * Full Method Completion Flow:
         *
         * @Bean
         * public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
         *     return http
         *             .csrf(csrf -> csrf.disable())
         *             .headers(headers -> headers.frameOptions(frame -> frame.disable()))
         *             .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
         *             .authorizeHttpRequests(authorize -> authorize
         *                 .requestMatchers(HttpMethod.POST, ApiRoutes.LOGIN).permitAll()
         *                 .requestMatchers(HttpMethod.GET, ApiRoutes.PRODUCTS_WILDCARD).permitAll()
         *                 .anyRequest().authenticated()
         *             )
         *             .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
         *             .build(); // ◄── SecurityFilterChain built and returned to ApplicationContext
         * }
         */
    }

    /**
     * Exposes the AuthenticationManager bean used by AuthenticationController.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
    /*
     * @Bean
     * public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
     *     return authenticationConfiguration.getAuthenticationManager();
     * }
     *
     * Let's break down this crucial Spring Security configuration method step by step:
     *
     * 1. What is the AuthenticationManager?
     *    The `AuthenticationManager` is the core interface in Spring Security responsible
     *    for processing authentication requests.
     *
     *    Its primary method is:
     *    Authentication authenticate(Authentication authentication) throws AuthenticationException;
     *
     *    When a user attempts to log in (e.g., submitting email and password via POST /auth/login),
     *    it takes an unauthenticated `UsernamePasswordAuthenticationToken`, validates credentials
     *    (usually against a database via UserDetailsService and PasswordEncoder), and returns
     *    a fully populated, authenticated `Authentication` object if successful.
     *
     * 2. Why do we need to export it as a @Bean?
     *    In Spring Security 5 and older versions, `AuthenticationManager` was exposed by default.
     *    In modern Spring Security (6+), Spring does NOT expose `AuthenticationManager` as a Bean
     *    automatically.
     *
     *    By placing `@Bean` on this method, you explicitly register `AuthenticationManager` into
     *    Spring's ApplicationContext. This allows you to inject it into your AuthController or
     *    AuthService to perform manual authentication during login:
     *
     *    @RestController
     *    public class AuthController {
     *        private final AuthenticationManager authenticationManager; // ◄── Injected here!
     *
     *        @PostMapping("/login")
     *        public ResponseEntity<TokenResponse> login(@RequestBody LoginRequest dto) {
     *            var authToken = new UsernamePasswordAuthenticationToken(dto.email(), dto.password());
     *            var auth = authenticationManager.authenticate(authToken); // ◄── Used here!
     *            var token = tokenService.generateToken((User) auth.getPrincipal());
     *            return ResponseEntity.ok(new TokenResponse(token));
     *        }
     *    }
     *
     * 3. What is AuthenticationConfiguration parameter?
     *    `AuthenticationConfiguration` is a helper class provided by Spring Security that holds
     *    the global authentication configuration assembled by Spring Boot.
     *
     *    Instead of manually building an `AuthenticationManager` from scratch (setting up
     *    UserDetailsService, PasswordEncoder, and DaoAuthenticationProvider manually),
     *    Spring Security automatically injects `AuthenticationConfiguration` into your method parameter.
     *
     * 4. How does .getAuthenticationManager() work?
     *    `authenticationConfiguration.getAuthenticationManager()` retrieves the pre-configured,
     *    ready-to-use `AuthenticationManager` instance that Spring Security created behind
     *    the scenes based on your application's setup.
     *
     * 5. Why `throws Exception`?
     *    `getAuthenticationManager()` declares `throws Exception` because building or retrieving
     *    the underlying manager can throw checked exceptions if internal security components
     *    fail to initialize.
     *
     * Visual Flow:
     *
     * Application Startup
     *        │
     *        ▼
     * Spring finds @Bean authenticationManager(...)
     *        │
     *        ▼
     * Spring injects AuthenticationConfiguration
     *        │
     *        ▼
     * Calls authenticationConfiguration.getAuthenticationManager()
     *        │
     *        ▼
     * Registers AuthenticationManager in ApplicationContext
     *        │
     *        ▼
     * Injected into AuthController / AuthService for handling user login
     */
    /**
     * Password encoder bean using BCrypt hashing algorithm.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    /*
     * @Bean
     * public PasswordEncoder passwordEncoder() {
     *     return new BCryptPasswordEncoder();
     * }
     *
     * Let's break down this essential security method step by step:
     *
     * 1. What is PasswordEncoder?
     *    `PasswordEncoder` is a core Spring Security interface used to perform
     *    one-way hashing of raw passwords and to safely verify encoded passwords
     *    during authentication.
     *
     *    Its primary methods are:
     *    - String encode(CharSequence rawPassword);
     *      Converts a plain text password (e.g., "123456") into a secure hash.
     *    - boolean matches(CharSequence rawPassword, String encodedPassword);
     *      Checks if an incoming plain text password matches an existing stored hash.
     *
     * 2. Why NEVER store plain-text passwords?
     *    Storing passwords in plain text inside a database is a critical security vulnerability.
     *    If the database is leaked or breached, all user accounts are compromised immediately.
     *    `PasswordEncoder` ensures that only irreversible mathematical hashes are saved.
     *
     * 3. What is BCryptPasswordEncoder?
     *    `BCryptPasswordEncoder` implements the widely trusted **BCrypt** hashing algorithm.
     *
     *    Key characteristics of BCrypt:
     *    - **Salt generation:** BCrypt automatically generates a unique random "salt"
     *      (random bytes) for every single password before hashing it.
     *      Even if two users pick "password123", their resulting hashes will be entirely different!
     *    - **Protection against Rainbow Table attacks:** Because every hash uses a unique salt,
     *      precomputed dictionary/rainbow tables cannot be used to reverse the hashes.
     *    - **Key Stretching (Work Factor):** BCrypt is deliberately slow and computationally intensive.
     *      This makes brute-force dictionary attacks extremely slow and costly for attackers.
     *
     * 4. Why register it as a @Bean?
     *    Placing `@Bean` exposes `PasswordEncoder` to Spring's ApplicationContext.
     *    This allows Spring Security's `AuthenticationManager` and your custom application services
     *    to inject and share the exact same encoder:
     *
     *    a) During User Registration (UserService):
     *       String hashedPassword = passwordEncoder.encode(dto.password());
     *       user.setPassword(hashedPassword);
     *       repository.save(user);
     *
     *    b) During Login (AuthenticationManager / DaoAuthenticationProvider):
     *       Spring Security automatically calls:
     *       passwordEncoder.matches(loginPassword, userFromDb.getPassword());
     *
     * Visual Flow:
     *
     * Registration:
     * Plain Password ("secret123") ──► passwordEncoder.encode() ──► Hash ("$2a$10$e8W...") ──► Saved in DB
     *
     * Authentication (Login):
     * Raw Input ("secret123") + DB Hash ("$2a$10$e8W...") ──► passwordEncoder.matches() ──► true/false
     */
}

/*
 * CHAVE DE TESTE POSTMAN (VAULT)
 * badef917c56d1a3711a814389fdeeb39065e74981e06926569f35ee860944b2c
 */

/*
+-------------------+--------------------+--------------------+
| Recurso / Ação    | CLIENT (Cliente)   | ADMIN (Administrador)|
+-------------------+--------------------+--------------------+
| Ver Produtos      | ✅ Permitido       | ✅ Permitido       |
| Fazer Pedido      | ✅ Apenas o seu    | ✅ Permitido       |
| Ver Meus Pedidos  | ✅ (/orders01/me)  | ✅ Permitido       |
| Ver Todos Pedidos | ❌ 403 Forbidden   | ✅ (/orders01)     |
| Listar Usuários   | ❌ 403 Forbidden   | ✅ (/users)        |
| Deletar Usuários  | ❌ 403 Forbidden   | ✅ (/users/{id})   |
| Criar Produtos    | ❌ 403 Forbidden   | ✅ (/products)     |
+-------------------+--------------------+--------------------+
*/

/*
1. User (entidade)
        |
        | implements UserDetails
        |
        ▼

2. AuthorizationService
        |
        | implements UserDetailsService
        |
        ▼

3. AuthenticationController
        |
        | recebe email + senha
        |
        ▼

4. AuthenticationManager
        |
        ▼

5. PasswordEncoder
        |
        ▼

6. TokenService (JWT criação)
        |
        ▼

7. JwtAuthenticationFilter  ← agora
        |
        | valida o token recebido
        |
        ▼

8. SecurityConfigurations
        |
        | configura o filtro dentro da cadeia
 */



    //ESTUDO

    /*
    Você executa a aplicação

    ↓

    Spring Boot inicia

    ↓

    Lê todas as classes

    ↓

    Encontra @Configuration

    ↓

    Cria essa classe

    ↓

    Executa seus @Bean

    ↓

    Monta toda a segurança

    ↓

    A aplicação começa a aceitar requisições
     */