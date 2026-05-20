package com.sapir.smartvacationplanner.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import jakarta.servlet.http.HttpServletResponse;


import javax.sql.DataSource;

@Configuration
public class SecurityConfig {

    @Bean
    public UserDetailsManager userDetailsManager(DataSource dataSource) {

        JdbcUserDetailsManager manager = new JdbcUserDetailsManager(dataSource);

        // login by email instead of username

        manager.setUsersByUsernameQuery("""
                select email, password, active
                from users
                where email = ?
                """);

        
        // add ROLE_ prefix for Spring Security

        manager.setAuthoritiesByUsernameQuery("""
                select email, concat('ROLE_', role)
                from users
                where email = ?
                """);

        return manager;
    }



    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http.authorizeHttpRequests(configurer -> configurer

                // vacations
                .requestMatchers(HttpMethod.GET, "/api/v1/vacations/**")
                    .hasAnyRole("CUSTOMER", "ADMIN")

                .requestMatchers(HttpMethod.POST, "/api/v1/vacations/**")
                    .hasAnyRole("CUSTOMER", "ADMIN")

                .requestMatchers(HttpMethod.PUT, "/api/v1/vacations/**")
                    .hasAnyRole("CUSTOMER", "ADMIN")

                .requestMatchers(HttpMethod.PATCH, "/api/v1/vacations/**")
                    .hasAnyRole("CUSTOMER", "ADMIN")

                .requestMatchers(HttpMethod.DELETE, "/api/v1/vacations/**")
                    .hasRole("ADMIN")

                .anyRequest().authenticated()
        );

        // handle access denied exceptions

        http.exceptionHandling(exception -> exception
            .accessDeniedHandler((request, response, accessDeniedException) -> {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.setContentType("application/json");
    
                response.getWriter().write("""
                        {
                          "message": "You are not allowed to perform this action",
                          "status": 403
                        }
                        """);
            })
    );

        // basic auth for now


        http.httpBasic(Customizer.withDefaults());

        // disable csrf for REST API


        http.csrf(csrf -> csrf.disable());

        return http.build();
    }
}