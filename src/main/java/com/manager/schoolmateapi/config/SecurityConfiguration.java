package com.manager.schoolmateapi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.session.HttpSessionEventPublisher;

import com.manager.schoolmateapi.users.services.MyUserDetailsService;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfiguration {
	
    private final MyUserDetailsService userDetailsService;

	public SecurityConfiguration(MyUserDetailsService userDetailsService) {
		this.userDetailsService = userDetailsService;
	}

    @Bean
    HttpSessionEventPublisher httpSessionEventPublisher() {
        return new HttpSessionEventPublisher();
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        //To configure
        http.csrf().disable()
            .authorizeHttpRequests()
            .requestMatchers(HttpMethod.POST, "/users").permitAll()
            .anyRequest().authenticated()
            .and()
            .sessionManagement(session -> session
                .maximumSessions(1)
                .sessionRegistry(sessionRegistry())
                .maxSessionsPreventsLogin(true))
            .userDetailsService(userDetailsService)
            .logout(logout -> logout
            .logoutUrl("/logout")
            .invalidateHttpSession(true)
            .deleteCookies("JSESSIONID")
            .logoutSuccessHandler((request, response, authentication) -> {
                response.setContentType("application/json");
                response.getWriter().write("{\"message\": \"You are logged out!\"}");
            }))
            .exceptionHandling()
            .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED));

        JSONLoginConfigurer<HttpSecurity> configurer = new JSONLoginConfigurer<>();
        configurer.setBuilder(http);
        http.apply(configurer
                            .loginPage("/login")
                            .successHandler((request, response, authentication) -> {
                                response.setContentType("application/json");
                                response.getWriter().write("{\"message\": \"Logged in successfully\"}");
                            })
                            .failureHandler((request, response, exception) -> {
                                response.setContentType("application/json");
                                if (exception instanceof BadCredentialsException) {
                                    response.setStatus(HttpStatus.BAD_REQUEST.value());
                                    response.getWriter().write("{\"message\": \"Incorrect username or password\"}");
                                } else {
                                    response.getWriter().write("{\"message\": \"Authentication failed\"}");
                                }
                            })
                            .permitAll());

        return http.build();
    }

    @Bean
    SessionRegistry sessionRegistry() {
        return new SessionRegistryImpl();
    }
    
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    
    
}
