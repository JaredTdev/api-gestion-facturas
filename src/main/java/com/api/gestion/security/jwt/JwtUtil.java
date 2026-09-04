package com.api.gestion.security.jwt;

import org.springframework.stereotype.Service;

@Service
public class JwtUtil {

    // Clave secreta que nos servira como firma en el token
    private String secret = "sprinboot"; 
    
}
