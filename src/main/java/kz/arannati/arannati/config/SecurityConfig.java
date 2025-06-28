package kz.arannati.arannati.config;

import kz.arannati.arannati.service.impl.AuthUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/api/admin-creation") // Отключаем CSRF для маршрута создания админа
                )
                .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/api/admin-creation").permitAll() // Temporary endpoint for admin creation
                .requestMatchers("/auth/**").permitAll()
                .requestMatchers("/static/**").permitAll()
                .requestMatchers("/css/**", "/js/**", "/images/**").permitAll()
                .requestMatchers("/error").permitAll()
                .requestMatchers("/").permitAll() // Allow access to catalog (root page) for all users
                .requestMatchers("/product/**").permitAll() // Allow access to product details for all users
                .requestMatchers("/dashboard").permitAll() // Allow access to main dashboard for all users
                .requestMatchers("/dashboard/admin/**").hasRole("ADMIN") // Only ADMIN can access admin dashboard
                .requestMatchers("/admin/**").hasRole("ADMIN") // Only ADMIN can access admin pages
                .requestMatchers("/materials/**").hasAnyRole("COSMETOLOGIST", "ADMIN") // Only COSMETOLOGIST and ADMIN can access materials
                .requestMatchers("/special-offers/**").hasAnyRole("COSMETOLOGIST", "ADMIN") // Only COSMETOLOGIST and ADMIN can access special offers
                        .requestMatchers("/messages/**").authenticated()
                        .requestMatchers("/admin/messages/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/auth/login")
                .loginProcessingUrl("/auth/login")
                .usernameParameter("email")
                .defaultSuccessUrl("/dashboard", true)
                .failureUrl("/auth/login?error")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/auth/logout")
                .logoutSuccessUrl("/auth/login?logout")
                .invalidateHttpSession(true)
                .clearAuthentication(true)
                .permitAll()
            );

        // We no longer explicitly set an AuthenticationProvider
        // Spring Security will automatically use our AuthUserDetailsService

        return http.build();
    }
}
