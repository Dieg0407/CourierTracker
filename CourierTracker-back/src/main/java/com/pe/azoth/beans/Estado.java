package com.pe.azoth.beans;

import java.io.Serializable;

public class Estado implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4924432217614025578L;

	public Estado() {}
	
	public Estado(int id) {
		this.id = id;
	}
	public Estado(int id,String descripcion) {
		this.id = id;
		this.descripcion = descripcion;
	}
	private int id;
	private String descripcion;
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	
	
}
