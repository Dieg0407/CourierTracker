package com.pe.azoth.beans;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DataBaseInfo {
	
	private String connectionType;
	private DataBaseJNDI jndiResource;
	private DataBaseParams dataBaseParams;
	
	public DataBaseInfo() {}
	
	
	@JsonProperty("type-connection")
	public String getConnectionType() {
		return connectionType;
	}
	public void setConnectionType(String connectionType) {
		this.connectionType = connectionType;
	}
	@JsonProperty("server-connection")
	public DataBaseJNDI getJndiResource() {
		return jndiResource;
	}
	public void setJndiResource(DataBaseJNDI jndiResource) {
		this.jndiResource = jndiResource;
	}
	@JsonProperty("program-connection")
	public DataBaseParams getDataBaseParams() {
		return dataBaseParams;
	}
	public void setDataBaseParams(DataBaseParams dataBaseParams) {
		this.dataBaseParams = dataBaseParams;
	}
	
	
}
