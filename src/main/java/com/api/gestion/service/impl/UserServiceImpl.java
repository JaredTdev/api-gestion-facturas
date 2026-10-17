package com.api.gestion.service.impl;

import java.util.Map;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.api.gestion.constantes.FacturaConstantes;
import com.api.gestion.dao.UserDAO;
import com.api.gestion.pojo.User;
import com.api.gestion.security.CustomerDetailsService;
import com.api.gestion.security.jwt.JwtUtil;
import com.api.gestion.service.UserService;
import com.api.gestion.util.FacturaUtils;

import lombok.extern.slf4j.Slf4j;


@Slf4j
@Service
public class UserServiceImpl implements UserService {
	
	@Autowired
	private UserDAO userDAO;

	@Autowired 
	private  AuthenticationManager authenticationManager;

	@Autowired 
	private CustomerDetailsService customerDetailsService;

	@Autowired 
	private JwtUtil jwtUtil;

	@Autowired
    private PasswordEncoder passwordEncoder;

	@Override
	public ResponseEntity<String> signUp(Map<String, String> requestMap) {
		log.info("Registro interno de un usuario {}", requestMap);; // Para que imprima valosres del Map
		try {
			// Una vez que ya se haya validado que se tengan valores ahora se va a validad si este usuario ya existe.
			// para validar que no exista un usuario con un email registrado
			if(validateSignUpMap(requestMap)) {
				User user = userDAO.findByEmail(requestMap.get("email"));
				//Si no existe ences ahora si vamos a guardar
				if(Objects.isNull(user)) {
					userDAO.save(getUserFromMap(requestMap));
					return FacturaUtils.getResponseEntity("Usuario registradoo con exito", HttpStatus.CREATED);
				} else{
					return FacturaUtils.getResponseEntity("El usuario con este email ya existe", HttpStatus.BAD_REQUEST);
				}
			} 
			// Si la data es incorrecta
			else{
				return FacturaUtils.getResponseEntity(FacturaConstantes.INVALID_DATA, HttpStatus.BAD_REQUEST);
			}
		} catch (Exception exception) {
			// TODO: handle exception
			exception.printStackTrace();
		}
		return FacturaUtils.getResponseEntity(FacturaConstantes.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@Override
	public ResponseEntity<String> login(Map<String, String> requestMap) {
        log.info("Dentro del proceso de login para el email: {}", requestMap.get("email"));
        try {
            // 1. Spring Security valida el correo y la contraseña
            Authentication authentication = authenticationManager.authenticate(
              new UsernamePasswordAuthenticationToken(requestMap.get("email"), requestMap.get("password"))
            );

            // 2. Si las credenciales coinciden perfectamente
            if(authentication.isAuthenticated()) {
                
                // Buscamos el usuario síncronamente en la BD usando tu DAO
                User user = userDAO.findByEmail(requestMap.get("email"));

                if (Objects.nonNull(user)) {
                    // Validamos si la cuenta está aprobada
                    if(user.getStatus().equalsIgnoreCase("true")) {
                        
                        // Generamos el token inyectando el rol dinámico obtenido de la BD ("admin" o "user")
                        String token = jwtUtil.generateToken(user.getEmail(), user.getRole());
                        
                        return new ResponseEntity<String>("{\"token\":\"" + token + "\"}", HttpStatus.OK);
                    } else {
                        return new ResponseEntity<String>("{\"mensaje\":\"Espera la aprobación del administrador\"}", HttpStatus.BAD_REQUEST);
                    }
                }
            }
        } catch (Exception exception) {
            log.error("Fallo de autenticación para {}: ", requestMap.get("email"), exception);
        }
        return new ResponseEntity<String>("{\"mensaje\":\"Credenciales incorrectas\"}", HttpStatus.BAD_REQUEST);
    }

	private boolean validateSignUpMap(Map<String, String> requestMap) {
		return requestMap.containsKey("nombre") && requestMap.containsKey("numeroDeContacto") &&
			   requestMap.containsKey("email") && requestMap.containsKey("password");
	}
	
	private User getUserFromMap(Map<String, String> requestMap){
        User user = new User();
        user.setNombre(requestMap.get("nombre"));
        user.setNumeroDeContacto(requestMap.get("numeroDeContacto"));
        user.setEmail(requestMap.get("email"));
        
        // Encripta la contraseña usando BCrypt de forma obligatoria
        user.setPassword(passwordEncoder.encode(requestMap.get("password")));
        user.setStatus("false"); 
        
        if (requestMap.containsKey("role")) {
            user.setRole(requestMap.get("role"));
        } else {
            user.setRole("user");
        }
        return user;
    }
}
