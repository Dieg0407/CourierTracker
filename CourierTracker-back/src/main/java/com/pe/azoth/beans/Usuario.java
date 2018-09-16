package com.pe.azoth.beans;

import java.io.Serializable;

public class Usuario implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5758207726143940780L;

	public Usuario(String correo, String pass, String nombres, String apellidos, String dni, int rango, Boolean activo) {
		super();
		this.correo = correo;
		this.pass = pass;
		this.nombres = nombres;
		this.apellidos = apellidos;
		this.dni = dni;
		this.rango = rango;
		this.setActivo(activo);
	}

	public Usuario() {}

	private String correo;
	private String pass;
	private String nombres;
	private String apellidos;
	private String dni;
	
	private int rango;
	private Boolean activo;

	public String getCorreo() {
		return correo;
	}

	public void setCorreo(String correo) {
		this.correo = correo;
	}

	public String getPass() {
		return pass;
	}

	public void setPass(String pass) {
		this.pass = pass;
	}

	public String getNombres() {
		return nombres;
	}

	public void setNombres(String nombres) {
		this.nombres = nombres;
	}

	public String getApellidos() {
		return apellidos;
	}

	public void setApellidos(String apellidos) {
		this.apellidos = apellidos;
	}

	public String getDni() {
		return dni;
	}

	public void setDni(String dni) {
		this.dni = dni;
	}

	
	public int getRango() {
		return rango;
	}

	public void setRango(int rango) {
		this.rango = rango;
	}

	public Boolean isActivo() {
		return activo;
	}

	public void setActivo(Boolean activo) {
		this.activo = activo;
	}
	
	
}
