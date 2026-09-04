package com.api.gestion.security.jwt;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.api.gestion.security.CustomerDetailsService;

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

                    // para vaidar token tenga las mismas credenciales que en userdetails.
                    if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                        UserDetails userDetails = customerDetailsService.loadUserByUsername(username);
                        if (jwtUtil.validateToken(token, userDetails)) {
                            UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = 
                                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                            new WebAuthenticationDetailsSource().buildDetails(request);
                            SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                        }
                    }
                    filterChain.doFilter(request, response);
                }
        throw new UnsupportedOperationException("Unimplemented method 'doFilterInternal'");
    }
    /* Metodo para verificar si el usuario es admin
     */
    public boolean isAdmin() {
        return "admin".equalsIgnoreCase((String) claims.get("role"));
    }

    // Metodo para validar si es usuario
    public  boolean isUser() {
        return "user".equalsIgnoreCase((String) claims.get("role"));
    }

    // Metodo para revisar el tipo de usuario actual
    public String getCurrentUSer() {
        return username;
    }
    
}
