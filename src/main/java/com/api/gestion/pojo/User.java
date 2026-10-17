package com.api.gestion.pojo;

import java.io.Serializable;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@NamedQuery(name = "User.findByEmail", query = "select u from User u where u.email=:email")
@NamedQuery(name = "User.updateStatus", query = "update User u set u.status=:status where u.id=:id")
@NamedQuery(name = "User.getAllAdmins", query = "select u.email from User u where u.role='admin'")

@Entity
@DynamicUpdate
@DynamicInsert
@Table(name = "users")
public class User implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@Column(name = "nombre")
	private String nombre;
	
	@Column(name = "numeroDeContacto")
	private String numeroDeContacto;
	
	@Column(name = "email")
	private String email;
	
	@Column(name = "password")
	private String password;
	
	@Column(name = "status")
	private String status;
	
	@Column(name = "role")
	private String role;

	// Constructor vacío explícito requerido por Hibernate
	public User() {
	}

	// Constructor completo explícito
	public User(Integer id, String nombre, String numeroDeContacto, String email, String password, String status, String role) {
		this.id = id;
		this.nombre = nombre;
		this.numeroDeContacto = numeroDeContacto;
		this.email = email;
		this.password = password;
		this.status = status;
		this.role = role;
	}

	// --- GETTERS Y SETTERS TRADICIONALES (MÉTODOS REALES EN CÓDIGO) ---

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getNumeroDeContacto() {
		return numeroDeContacto;
	}

	public void setNumeroDeContacto(String numeroDeContacto) {
		this.numeroDeContacto = numeroDeContacto;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}
}
