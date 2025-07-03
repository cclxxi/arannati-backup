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
                        .ignoringRequestMatchers("/api/**") // Disable CSRF for all API endpoints
                )
                .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/api/admin-creation").permitAll() // Temporary endpoint for admin creation
                .requestMatchers("/api/catalog/**").permitAll() // Allow access to catalog API for all users
                .requestMatchers("/api/cart/count").permitAll() // Allow access to cart count for all users
                .requestMatchers("/api/cart/**").authenticated() // Require authentication for cart operations
                .requestMatchers("/api/wishlist/check/**").permitAll() // Allow access to wishlist check for all users
                .requestMatchers("/api/wishlist/**").authenticated() // Require authentication for wishlist operations
                .requestMatchers("/api/messages/unread-count").authenticated() // Require authentication for unread message count
                .requestMatchers("/api/messages/**").authenticated() // Require authentication for message operations
                .requestMatchers("/api/dashboard").permitAll() // Allow access to dashboard API for all users (guest view)
                .requestMatchers("/api/dashboard/**").authenticated() // Require authentication for dashboard operations
                .requestMatchers("/api/orders/shipping").permitAll() // Allow access to shipping calculation for all users
                .requestMatchers("/api/orders/**").authenticated() // Require authentication for order operations
                .requestMatchers("/api/materials/**").hasAnyRole("COSMETOLOGIST", "ADMIN") // Only COSMETOLOGIST and ADMIN can access material API endpoints
                .requestMatchers("/api/cosmetologist/**").hasAnyRole("COSMETOLOGIST", "ADMIN") // Only COSMETOLOGIST and ADMIN can access cosmetologist API endpoints
                .requestMatchers("/api/admin/**").hasRole("ADMIN") // Only ADMIN can access admin API endpoints
                .requestMatchers("/static/**").permitAll()
                .requestMatchers("/css/**", "/js/**", "/images/**").permitAll()
                .requestMatchers("/error").permitAll()
                .anyRequest().authenticated()
            )
            .httpBasic(httpBasic -> {}); // Use HTTP Basic authentication for API endpoints

        // We no longer explicitly set an AuthenticationProvider
        // Spring Security will automatically use our AuthUserDetailsService

        return http.build();
    }
}
