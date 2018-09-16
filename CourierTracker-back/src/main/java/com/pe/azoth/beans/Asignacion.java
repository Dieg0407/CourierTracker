package com.pe.azoth.beans;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class Asignacion {
	public Asignacion(String correoUsuario, String codProducto, int nroProducto, Integer estado) {
		super();
		this.correoUsuario = correoUsuario;
		this.nroProducto = nroProducto;
		this.estado = estado;
		this.codProducto = codProducto;
	}
	
	public Asignacion() {}
	
	
	private String correoUsuario;
	private String codProducto;
	private int nroProducto;
	private Integer estado;
	
	@JsonProperty("correo_usuario")
	public String getCorreoUsuario() {
		return correoUsuario;
	}
	public void setCorreoUsuario(String usuario) {
		this.correoUsuario = usuario;
	}
	@JsonProperty("estado")
	public Integer getEstado() {
		return estado;
	}
	public void setEstado(Integer estado) {
		this.estado = estado;
	}
	@JsonProperty("nro_producto")
	public int getNroProducto() {
		return nroProducto;
	}
	public void setNroProducto(int producto) {
		this.nroProducto = producto;
	}
	@JsonProperty("codigo_producto")
	public String getCodProducto(){
		return this.codProducto;
	}
	
	public void setCodProducto(String codProducto){
		this.codProducto = codProducto;
	}
	
}
