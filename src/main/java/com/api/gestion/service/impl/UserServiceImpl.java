package com.api.gestion.service.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.api.gestion.dao.UserDAO;
import com.api.gestion.pojo.User;
import com.api.gestion.service.UserService;

import lombok.extern.slf4j.Slf4j;


@Slf4j
@Service
public class UserServiceImpl implements UserService {
	
	@Autowired
	private UserDAO userDAO;

	@Override
	public ResponseEntity<String> signUp(Map<String, String> requestMap) {
		return null;
	}
	
	private User getUserFromMap(Map<String, String> requestMap) {
		User user = new User();
		user.setNombre(requestMap.get("nombre"));
		user.setNumeroDeContacto(null);
	}
	

}
