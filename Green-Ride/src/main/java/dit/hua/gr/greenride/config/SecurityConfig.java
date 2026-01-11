package dit.hua.gr.greenride.config;

import dit.hua.gr.greenride.core.security.JwtAuthenticationFilter;
import dit.hua.gr.greenride.web.rest.RestApiAccessDeniedHandler;
import dit.hua.gr.greenride.web.rest.RestApiAuthenticationEntryPoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    // ============================
    // API SECURITY CHAIN (JWT)
    // ============================
    @Bean
    @Order(1)
    public SecurityFilterChain apiChain(final HttpSecurity http,
                                        final JwtAuthenticationFilter jwtAuthenticationFilter,
                                        final RestApiAuthenticationEntryPoint restApiAuthenticationEntryPoint,
                                        final RestApiAccessDeniedHandler restApiAccessDeniedHandler) throws Exception {

        http
                .securityMatcher("/api/**", "/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html")

                .csrf(AbstractHttpConfigurer::disable)

                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .authorizeHttpRequests(auth -> auth
                        // Swagger / OpenAPI
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()

                        // PUBLIC AUTH ENDPOINTS
                        .requestMatchers("/api/auth/login", "/api/auth/register").permitAll()

                        // (keep if you need it)
                        .requestMatchers("/api/v1/auth/client-tokens").permitAll()

                        // Everything else under /api requires JWT
                        .requestMatchers("/api/**").authenticated()
                )

                .exceptionHandling(exh -> exh
                        .authenticationEntryPoint(restApiAuthenticationEntryPoint)
                        .accessDeniedHandler(restApiAccessDeniedHandler)
                )

                // JWT filter before UsernamePasswordAuthenticationFilter
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)

                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable);

        return http.build();
    }

    // ============================
    // UI SECURITY CHAIN (FORMS)
    // ============================
    @Bean
    @Order(2)
    public SecurityFilterChain uiChain(final HttpSecurity http) throws Exception {

        http
                .securityMatcher("/**")
                .authorizeHttpRequests(auth -> auth

                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()

                        .requestMatchers("/css/**", "/js/**", "/images/**", "/webjars/**").permitAll()

                        // Public pages
                        .requestMatchers("/", "/login", "/register", "/logged-out").permitAll()
                        .requestMatchers("/admin/login").permitAll()

                        // Admin & User restrictions
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/profile/**", "/rides/**").authenticated()

                        .anyRequest().permitAll()
                )

                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .successHandler((request, response, authentication) -> {

                            boolean isAdmin = authentication.getAuthorities().stream()
                                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

                            if (isAdmin) {
                                response.sendRedirect("/admin/dashboard");
                            } else {
                                response.sendRedirect("/profile?tab=home");
                            }
                        })
                        .failureUrl("/login?error")
                        .permitAll()
                )

                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/logged-out")
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                )

                .httpBasic(AbstractHttpConfigurer::disable);

        return http.build();
    }

    // ============================
    // PASSWORD ENCODER
    // ============================
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // ============================
    // AUTH MANAGER
    // ============================
    @Bean
    public AuthenticationManager authenticationManager(final AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}