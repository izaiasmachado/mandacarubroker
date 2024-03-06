package com.mandacarubroker.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static com.mandacarubroker.domain.user.Permission.STOCKS_UPDATE;
import static com.mandacarubroker.domain.user.Permission.STOCKS_CREATE;
import static com.mandacarubroker.domain.user.Permission.STOCKS_READ;
import static com.mandacarubroker.domain.user.Permission.STOCKS_DELETE;
import static com.mandacarubroker.domain.user.Permission.USER_CREATE;
import static com.mandacarubroker.domain.user.Permission.USER_READ;
import static com.mandacarubroker.domain.user.Permission.USER_UPDATE;
import static com.mandacarubroker.domain.user.Permission.USER_DELETE;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpMethod.PUT;
import static org.springframework.http.HttpMethod.DELETE;
import static org.springframework.security.config.Customizer.withDefaults;
import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfiguration {
    private static final String[] WHITE_LIST_URL = {
            "/",
            "/auth/login",
            "/auth/register",
            "/docs**",
            "/swagger-ui/**",
            "/v3/api-docs/**",
    };

    private final AuthenticationProvider authenticationProvider;
    private final JwtAuthFilter jwtAuthFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(final HttpSecurity http) throws Exception {
        http.cors(withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(STATELESS))
                .authorizeHttpRequests(req -> {
                    req.requestMatchers(WHITE_LIST_URL).permitAll();
                    req.requestMatchers(GET, "/stocks").hasAuthority(STOCKS_READ.getPermission());
                    req.requestMatchers(GET, "/stocks/**").hasAuthority(STOCKS_READ.getPermission());
                    req.requestMatchers(POST, "/stocks").hasAuthority(STOCKS_CREATE.getPermission());
                    req.requestMatchers(PUT, "/stocks/**").hasAuthority(STOCKS_UPDATE.getPermission());
                    req.requestMatchers(DELETE, "/stocks/**").hasAuthority(STOCKS_DELETE.getPermission());
                    req.requestMatchers(GET, "/users").hasAuthority(USER_READ.getPermission());
                    req.requestMatchers(GET, "/users/**").hasAuthority(USER_READ.getPermission());
                    req.requestMatchers(PUT, "/users/**").hasAuthority(USER_UPDATE.getPermission());
                    req.requestMatchers(DELETE, "/users/**").hasAuthority(USER_DELETE.getPermission());
                    req.requestMatchers(POST, "/users").hasAuthority(USER_CREATE.getPermission());
                    req.requestMatchers(GET, "/auth/me").authenticated();
                    req.anyRequest().authenticated();
                })
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
