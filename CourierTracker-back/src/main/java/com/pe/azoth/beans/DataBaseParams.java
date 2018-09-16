package com.pe.azoth.beans;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DataBaseParams {
	private String user;
	private String pass;
	private String ip;
	private String port;
	private String db;
	private String dbProvider;
	
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	public String getPass() {
		return pass;
	}
	public void setPass(String pass) {
		this.pass = pass;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public String getPort() {
		return port;
	}
	public void setPort(String port) {
		this.port = port;
	}
	public String getDb() {
		return db;
	}
	public void setDb(String db) {
		this.db = db;
	}
	@JsonProperty("db-provider")
	public String getDbProvider() {
		return dbProvider;
	}
	public void setDbProvider(String dbProvider) {
		this.dbProvider = dbProvider;
	}
	
}
