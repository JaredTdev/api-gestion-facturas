package com.api.gestion.security;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.api.gestion.dao.UserDAO;
import com.api.gestion.pojo.User;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CustomerDetailsService implements UserDetailsService{
    
    //private final PasswordEncoder passwordEncoder;

    @Autowired
    private UserDAO userDAO;

    private User userDetail;

    //CustomerDetailsService(PasswordEncoder passwordEncoder) {
    //    this.passwordEncoder = passwordEncoder;
    //}

    // Buscar al usuario por email
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("Dentro de loadUserByUsername {}",username);
        //userDetail = userDAO.findByEmail(username);

        // Consulta fresca y local a la base de datos (Thread-Safe)
        User user = userDAO.findByEmail(username);

        if(!Objects.isNull(userDetail)){
            // Formateamos el rol (ej: "admin" -> "ROLE_ADMIN") para compatibilidad con Spring Security
            String formatRole = user.getRole().startsWith("ROLE_") ? user.getRole().toUpperCase() : "ROLE_" + user.getRole().toUpperCase();

            return new org.springframework.security.core.userdetails.User(
                userDetail.getEmail(),
                userDetail.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority(formatRole))
            );
        }
        else{
            throw new UsernameNotFoundException("Usuario no encontrado con el email: " + username);
        }
    }

    public User getUserDetail(){
        return userDetail;
    }
    
}
