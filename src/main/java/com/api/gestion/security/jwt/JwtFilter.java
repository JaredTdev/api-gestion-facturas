package com.api.gestion.security.jwt;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
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

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) 
            throws ServletException, IOException {
        
        if (request.getServletPath().matches("/user/login|/user/forgotPassword|/user/signup")) {
            filterChain.doFilter(request, response);
            return;
        }

        String authorizationHeader = request.getHeader("Authorization");
        String token = null;
        String username = null;
        String role = null;

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            token = authorizationHeader.substring(7);
            username = jwtUtil.extractUsername(token);
            
            Claims claims = jwtUtil.extractAllClaims(token);
            if (claims != null) {
                role = (String) claims.get("role");
            }
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = customerDetailsService.loadUserByUsername(username);
            
            if (jwtUtil.validateToken(token, userDetails)) {
                
                String formatRole = role.startsWith("ROLE_") ? role.toUpperCase() : "ROLE_" + role.toUpperCase();
                List<SimpleGrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority(formatRole));

                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, authorities);
                
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        }
        
        filterChain.doFilter(request, response);
    }

    public boolean isAdmin() {
        if (SecurityContextHolder.getContext().getAuthentication() == null) return false;
        return SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }

    public boolean isUser() {
        if (SecurityContextHolder.getContext().getAuthentication() == null) return false;
        return SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_USER"));
    }

    public String getCurrentUser() {
        if (SecurityContextHolder.getContext().getAuthentication() == null) return null;
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }
}
