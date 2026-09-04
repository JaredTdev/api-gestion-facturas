package com.api.gestion.security.jwt;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/* Clase para poder validar el token generado, el usuario completo.
A detalle
 */
@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private CustomerDetailsService customerDetailsService;

    Claims claims = null;

    private String username = null;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
                if (request.getServletPath().matches("/user/login|/user/forgotPassword|/user/signup")) {
                    filterChain.doFilter(request, response);
                }
                else {
                    String authorizationHeader = request.getHeader("Authorization");
                    String token = null;

                    if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                        token = authorizationHeader.substring(7); // Es 7 por la cantidad de caracteres que esta usando "Bearer ".
                        username = jwtUtil.extractUsername(token);
                        claims = jwtUtil.extractAllClaims(token);
                    }
                }
        throw new UnsupportedOperationException("Unimplemented method 'doFilterInternal'");
    }
    
}
