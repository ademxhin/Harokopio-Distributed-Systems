package dit.hua.gr.greenride.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Security configuration for the GreenRide application.
 */
@Configuration
@EnableMethodSecurity // enables @PreAuthorize on services/controllers
public class SecurityConfig {

    // @future: API Security (stateless - JWT based)

    /**
     * UI security chain for all requests (stateful - cookie based).
     */
    @Bean
    @Order(2)
    public SecurityFilterChain uiChain(final HttpSecurity http) throws Exception {
        http
                // This chain applies to all incoming requests.
                .securityMatcher("/**")
                .authorizeHttpRequests(auth -> auth
                        // Public pages
                        .requestMatchers("/", "/login", "/register").permitAll()
                        // Private pages (require authentication)
                        .requestMatchers("/profile", "/logout").authenticated()
                        // Everything else is currently allowed
                        .anyRequest().permitAll()
                )
                // Form login configuration
                .formLogin(form -> form
                        .loginPage("/login")               // custom login page (login.html)
                        .loginProcessingUrl("/login")      // POST target handled by Spring Security
                        .defaultSuccessUrl("/profile", true)
                        .failureUrl("/login?error")
                        .permitAll()
                )
                // Logout configuration
                .logout(logout -> logout
                        .logoutUrl("/logout")              // POST target handled by Spring Security
                        .logoutSuccessUrl("/login?logout")
                        .deleteCookies("JSESSIONID")
                        .invalidateHttpSession(true)
                        .permitAll()
                )
                // Disable HTTP Basic authentication (we only use form login).
                .httpBasic(basic -> {});

        return http.build();
    }

    /**
     * Password encoder used to hash user passwords.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * AuthenticationManager used by Spring Security for authentication.
     */
    @Bean
    public AuthenticationManager authenticationManager(final AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}