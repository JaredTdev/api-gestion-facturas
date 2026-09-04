package com.api.gestion.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;

import com.api.gestion.security.jwt.JwtFilter;

@Configuration 
@EnableWebSecurity 
public class SecurityConfig {

    @Autowired 
    private CustomerDetailsService customerDetailsService;

    @Autowired 
    private JwtFilter jwtFilter;

    @Bean 
    public PasswordEncoder passwordEncoder() {
        //return NoOpPasswordEncoder.getInstance();
        return new BCryptPasswordEncoder();
    }

    protected SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) {
        httpSecurity.cors().configurationSource(request -> CorsConfiguration.app)
    }

    @Bean 
    public AuthenticationManager AuthenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception{
        return authenticationConfiguration.getAuthenticationManager();
    }
    
}
