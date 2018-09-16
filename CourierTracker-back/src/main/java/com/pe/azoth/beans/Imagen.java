package com.pe.azoth.beans;

import java.io.Serializable;
import java.util.Arrays;

public class Imagen implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4549751150568575854L;
	private int id;
	private String codigo;
	private int numero;
	private byte[] imagen;

	public Imagen(){}

	public int getId(){return this.id;}
	public String getCodigo(){return this.codigo;}
	public int getNumero(){return this.numero;}
	public byte[] getImagen(){return this.imagen;}

	public void setId(int id ){
		this.id = id;
	}
	public void setCodigo(String codigo){
		this.codigo = codigo;
	}
	public void setNumero(int numero){
		this.numero = numero;
	}
	public void setImagen(byte[] imagen){
		this.imagen = imagen;
	}

	@Override
	public String toString() {
		return "Imagen [id=" + id + ", codigo=" + codigo + ", numero=" + numero + ", imagen=" + Arrays.toString(imagen)
				+ "]";
	}
	
//	
}