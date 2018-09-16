package com.pe.azoth.dao;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

import javax.naming.NamingException;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.ArrayListHandler;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.pe.azoth.beans.Estado;

public class DaoEstadoImpl implements DaoEstado {
	private Conexion conexion;
	
	public DaoEstadoImpl() throws JsonParseException, JsonMappingException, IOException {	
		conexion = new Conexion();
	}
	
	/* (non-Javadoc)
	 * @see com.pe.azoth.dao.DaoEstado#listEstados()
	 */
	@Override
	public List<Estado> listEstados() throws SQLException, NamingException{
		
		try(Connection connection = this.conexion.getConnection()){
			
			return new QueryRunner()
					.query(connection,"SELECT * FROM estados", new ArrayListHandler())
					.stream()
					.map( rs -> new Estado(
							(Integer)rs[0],
							(String)rs[1]
					))
					.collect(Collectors.toList());
			
		}
		
	}
	
	/* (non-Javadoc)
	 * @see com.pe.azoth.dao.DaoEstado#listEstados(int)
	 */
	@Override
	public Estado getEstado (int id) throws SQLException, NamingException{
		
		try(Connection connection = this.conexion.getConnection()){
			return getEstado(id,connection);
		}
		
	}
	@Override
	public Estado getEstado (int id, Connection connection) throws SQLException, NamingException{
		
			
			List<Estado > lista = new QueryRunner()
					.query(connection,"SELECT * FROM estados WHERE id_estado = ? ", new ArrayListHandler(),id)
					.stream()
					.map( rs -> new Estado(
							(Integer)rs[0],
							(String)rs[1]
					))
					.collect(Collectors.toList()); 
			
			return (lista.size()==1?lista.get(0):null);
		
		
	}
}
