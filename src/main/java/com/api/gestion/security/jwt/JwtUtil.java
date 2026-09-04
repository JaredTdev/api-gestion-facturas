package com.api.gestion.security.jwt;

import java.security.Key;
import java.security.SecureRandom;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.security.core.userdetails.UserDetails;
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
        return extractClaims(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaims(token, Claims::getExpiration);
    }

    /* 
    Este metodo verifica y extrae todos los reclamos del JWT. Utiliza la clave secreta para
    verificar la firma del token y luego extrae los reclamos del payload.
     */
    public <T> T extractClaims(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(secret.getEncoded()))
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public String generateToken(String username, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", role);
        return createToken(claims, username);
    }

    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuedAt(new Date(System.currentTimeMillis() + 100 * 60 * 60 * 10))
                .signWith(Keys.hmacShaKeyFor(secret.getEncoded())).compact();
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
    
}
