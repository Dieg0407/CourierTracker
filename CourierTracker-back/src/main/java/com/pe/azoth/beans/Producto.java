package com.pe.azoth.beans;

import java.sql.Timestamp;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Producto {
	public Producto() {}
	
	public Producto(String codigo ,int numero,  String descripcion, String direccion, String origen, String destino, Cliente envio,
			Cliente recepcion, Estado estado,Timestamp fechaCreacion) {
		super();
		this.numero = numero;
		this.codigo = codigo;
		this.descripcion = descripcion;

		this.direccion = direccion;
		this.origen = origen;
		this.destino = destino;
		this.envio = envio;
		this.recepcion = recepcion;
		this.estado = estado;
		this.fechaCreacion = fechaCreacion;
	}
	

	private String codigo;
	private int numero;

	private String descripcion;
	private String direccion;
	private String origen;
	private String destino;
	
	private Cliente envio;
	private Cliente recepcion;
	
	private Estado estado;
	
	private Timestamp fechaCreacion;

	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	public int getNumero() {
		return numero;
	}

	public void setNumero(int numero) {
		this.numero = numero;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public String getDireccion() {
		return direccion;
	}

	public void setDireccion(String direccion) {
		this.direccion = direccion;
	}

	public String getOrigen() {
		return origen;
	}

	public void setOrigen(String origen) {
		this.origen = origen;
	}

	public String getDestino() {
		return destino;
	}

	public void setDestino(String destino) {
		this.destino = destino;
	}

	public Cliente getEnvio() {
		return envio;
	}

	public void setEnvio(Cliente envio) {
		this.envio = envio;
	}

	public Cliente getRecepcion() {
		return recepcion;
	}

	public void setRecepcion(Cliente recepcion) {
		this.recepcion = recepcion;
	}

	public Estado getEstado() {
		return estado;
	}

	public void setEstado(Estado estado) {
		this.estado = estado;
	}

	@JsonProperty("fecha_creacion")
	public Timestamp getFechaCreacion() {
		return fechaCreacion;
	}

	public void setFechaCreacion(Timestamp fecha_creacion) {
		this.fechaCreacion = fecha_creacion;
	}
	
	
	
	
}
