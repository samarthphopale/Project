package com.exim.v1.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // 1. Disable CSRF so testing tools like Swagger can make POST requests
            .csrf(csrf -> csrf.disable()) 
            .authorizeHttpRequests(auth -> auth
                // 2. Allow public access to Swagger core documentation interfaces
                .requestMatchers(
                    "/v3/api-docs/**",
                    "/swagger-ui/**",
                    "/swagger-ui.html"
                ).permitAll()
                
                // 3. FIX: Allow public testing of your functional application API endpoints
                .requestMatchers(
                    "/trades/**",
                    "/eximAI/**"
                ).permitAll()
                
                // All other endpoints not explicitly listed above still require a login
                .anyRequest().authenticated()
            )
            // Use standard browser form login controls for everything else
            .formLogin(form -> form.permitAll());

        return http.build();
    }
}
