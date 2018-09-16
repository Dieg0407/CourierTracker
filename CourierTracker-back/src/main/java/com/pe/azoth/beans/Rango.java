package com.pe.azoth.beans;

public class Rango {
	public Rango() {}
	
	
	public Rango(int id, String descripcion) {
		super();
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
