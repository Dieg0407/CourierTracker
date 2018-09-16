package com.pe.azoth.dao;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;


import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.pe.azoth.beans.DataBaseInfo;

public class Conexion {
	
	protected static DataBaseInfo config = null;
	
	public Conexion() throws JsonParseException, JsonMappingException, IOException  {
		if(Conexion.config == null)
			Conexion.config = new DBConfig().getData();
	}
	
	public DataBaseInfo getConfig(){
		return config;
	}
	public synchronized void reload() throws  JsonParseException, JsonMappingException, IOException {
		Conexion.config = new DBConfig().getData();
	}
	
	public Connection getConnection() throws SQLException, NamingException {
		
		if(Conexion.config.getConnectionType().equals("server")) {
			InitialContext ctx = new InitialContext();
			return ((DataSource)ctx.lookup(Conexion.config.getJndiResource().getJndi())).getConnection();
		}
		else {
			String cadena = "";
			switch(Conexion.config.getDataBaseParams().getDbProvider()) {
				case "postgre":
					DriverManager.registerDriver(new org.postgresql.Driver());
					cadena = "jdbc:postgresql://%s:%s/%s";
					break;
				case "mysql":
					DriverManager.registerDriver(new com.mysql.jdbc.Driver());
					cadena = "jdbc:mysql://%s:%s/%s?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";
					break;
				default:
					throw new IllegalArgumentException ("El proveedor de BD no "
							+ "es soportado por el sistema");
			}
			return DriverManager.getConnection(
					String.format(cadena, 
							Conexion.config.getDataBaseParams().getIp(),
							Conexion.config.getDataBaseParams().getPort(),
							Conexion.config.getDataBaseParams().getDb()) ,
							Conexion.config.getDataBaseParams().getUser(), 
							Conexion.config.getDataBaseParams().getPass());
		}
		
	}
	
}
