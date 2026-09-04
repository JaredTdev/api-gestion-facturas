package com.api.gestion.security.jwt;

import java.security.Key;
import java.security.SecureRandom;
import java.util.function.Function;

import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;

@Service
public class JwtUtil {

    // Clave secreta que nos servira como firma en el token
    private Key secret;

    /*
    Este metodo es anotado con @PostConstruct, lo que significa que se ejecutara despues de que la 
    instancia de la clase haya sido construida y todas las dependencias hayan sido inyectadas.
    En este metodo, se inicializa la clave secreta que se utilizara para firmar y verificar los 
    tokens JWT. Se genera una clave aleatoria utilizando SecureRandom() y se asigna a la variable secret.
     */
    @PostConstruct
    protected void init() {
        byte[] apiKeySecretBytes = new byte[64]; // 512 bits
        new SecureRandom().nextBytes(apiKeySecretBytes);
        secret = Keys.hmacShaKeyFor(apiKeySecretBytes);
    }




    public String extractUsername(String token) {
        return null;
    }

    public <T> T extractClaims(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public Claims extractAllClaims(String token) {
        return Jwts.parser().setSigningKey(secret).parsC
    }
    
}
