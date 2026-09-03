package com.api.gestion.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.api.gestion.pojo.User;

@Repository
public interface UserDAO extends JpaRepository<User, Integer>{
	
	User findByEmail(@Param(("email")) String email);

}
