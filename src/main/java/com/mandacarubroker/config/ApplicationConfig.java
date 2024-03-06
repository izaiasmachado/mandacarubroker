package com.mandacarubroker.config;

import com.mandacarubroker.service.PasswordHashingService;
import com.mandacarubroker.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class ApplicationConfig {
    private final UserService userService;
    private final PasswordHashingService passwordHashingService;

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userService);
        authProvider.setPasswordEncoder(passwordHashingService);
        return authProvider;
    }

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();

        List<String> allowedOrigins = Arrays.asList(
                "http://localhost:3000",
                "https://mandacarubroker.com.br",
                "https://www.mandacarubroker.com.br",
                "https://test-front.mandacarubroker.com.br"
        );

        List<String> allowedMethods = Arrays.asList(
                "GET", "POST", "PUT", "DELETE", "OPTIONS",  "HEAD", "TRACE", "CONNECT"
        );

        allowedOrigins.forEach(config::addAllowedOrigin);
        allowedMethods.forEach(config::addAllowedMethod);
        config.addAllowedHeader("*");
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}
