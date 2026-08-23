package net.switchscope.config;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.switchscope.model.Role;
import net.switchscope.model.User;
import net.switchscope.repository.UserRepository;
import net.switchscope.service.security.RolePermissionCatalog;
import net.switchscope.web.AuthUser;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@Slf4j
@AllArgsConstructor
public class SecurityConfig {
    public static final PasswordEncoder PASSWORD_ENCODER = PasswordEncoderFactories.createDelegatingPasswordEncoder();

    private final UserRepository userRepository;
    private final RestAuthenticationEntryPoint authenticationEntryPoint;
    private final RolePermissionCatalog rolePermissionCatalog;

    @Bean
    PasswordEncoder passwordEncoder() {
        return PASSWORD_ENCODER;
    }

    @Bean
    UserDetailsService userDetailsService() {
        return email -> {
            log.debug("Authenticating '{}'", email);
            Optional<User> optionalUser = userRepository.findByEmailIgnoreCase(email);
            User user = optionalUser.orElseThrow(
                    () -> new UsernameNotFoundException("User '" + email + "' was not found"));
            return new AuthUser(user, authoritiesOf(user));
        };
    }

    /**
     * Roles as {@code ROLE_*}, plus every permission code those roles grant.
     * <p>
     * The role half is what {@code /api/admin/**} still checks with {@code hasRole}; the permission
     * half is what the method-security advisor checks with an exact authority match. The grants
     * come from {@link RolePermissionCatalog}, which holds them for a minute - without that, the
     * stateless Basic authentication would read {@code roles} and {@code role_permissions} on every
     * single request, since there is no session to remember them in.
     * <p>
     * The role name is the join key: {@link Role#ADMIN} to the {@code roles} row coded
     * {@code ADMIN}. That is deliberate duplication for now - the enum column is still the
     * authoritative one and only stage 4 drops it.
     */
    private List<GrantedAuthority> authoritiesOf(User user) {
        Map<String, Set<String>> grants = rolePermissionCatalog.byRole();
        List<GrantedAuthority> authorities = new ArrayList<>(user.getRoles());
        user.getRoles().stream()
                .map(Role::name)
                .map(code -> grants.getOrDefault(code, Set.of()))
                .flatMap(Set::stream)
                .distinct()
                .map(SimpleGrantedAuthority::new)
                .forEach(authorities::add);
        return authorities;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.cors(cors -> cors.configurationSource(request -> {
                var corsConfig = new org.springframework.web.cors.CorsConfiguration();
                corsConfig.setAllowedOriginPatterns(List.of(
                        "http://localhost:*",        // Any port on localhost
                        "http://192.168.*.*:*",     // Any IP in your network
                        "http://127.0.0.1:*"        // Loopback)); // Allow frontend
                ));
                corsConfig.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS")); // Allowed methods
                corsConfig.setAllowedHeaders(List.of("Authorization", "Content-Type", "X-Requested-With", "Accept")); // Allowed headers
                corsConfig.setAllowCredentials(true); // Important: Allow credentials
                corsConfig.setMaxAge(3600L); // Cache preflight for 1 hour
                return corsConfig;
            }))
            .authorizeHttpRequests(ac ->
                    ac.requestMatchers(HttpMethod.OPTIONS, "/**").permitAll() // Allow all OPTIONS requests (CORS preflight)
                      .requestMatchers("/favicon.ico").permitAll()
                      .requestMatchers("/", "/v3/api-docs", "/v3/api-docs/**", "/swagger-ui.html", "/swagger-ui/**").permitAll()
                      // Future: registration endpoint (currently no controller exists)
                      // .requestMatchers(HttpMethod.POST, "/api/auth/register").anonymous()
                      .requestMatchers("/api/admin/**").hasRole(Role.ADMIN.name()) // Admin-only endpoints
                      .requestMatchers("/api/**").authenticated()) // All other API endpoints require authentication
            .httpBasic(hbc -> hbc.authenticationEntryPoint(authenticationEntryPoint))
            .sessionManagement(smc -> smc.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .csrf(AbstractHttpConfigurer::disable);
        return http.build();
    }
}
