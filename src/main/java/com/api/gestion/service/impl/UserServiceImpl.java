package com.api.gestion.service.impl;

import java.util.Map;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.api.gestion.constantes.FacturaConstantes;
import com.api.gestion.dao.UserDAO;
import com.api.gestion.pojo.User;
import com.api.gestion.service.UserService;
import com.api.gestion.util.FacturaUtils;

import lombok.extern.slf4j.Slf4j;


@Slf4j
@Service
public class UserServiceImpl implements UserService {
	
	@Autowired
	private UserDAO userDAO;

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

	private boolean validateSignUpMap(Map<String, String> requestMap) {
		if (requestMap.containsKey("nombre") && requestMap.containsKey("numeroDeContacto") &&
				requestMap.containsKey("email") && requestMap.containsKey("password")) {
			return true;
		} 
		return false;
	}
	
	private User getUserFromMap(Map<String, String> requestMap) {
		User user = new User();
		user.setNombre(requestMap.get("nombre"));
		user.setNumeroDeContacto(requestMap.get("numeroDeContacto"));
		user.setEmail(requestMap.get("email"));
		user.setPassword(requestMap.get("password"));
		user.setStatus("false");
		user.setRole("user");
		return user;
	}
	

}
