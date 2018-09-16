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
import com.pe.azoth.beans.Rango;

public class DaoRangoImpl implements DaoRango {
	private Conexion conexion;
	
	public DaoRangoImpl() throws JsonParseException, JsonMappingException, IOException {	
		conexion = new Conexion();
	}
	
	/* (non-Javadoc)
	 * @see com.pe.azoth.dao.DaoRango#listRangos()
	 */
	@Override
	public List<Rango> listRangos() throws SQLException, NamingException{
		
		try(Connection connection = this.conexion.getConnection()){
			
			return new QueryRunner()
					.query(connection,"SELECT * FROM departamentos", new ArrayListHandler())
					.stream()
					.map( rs -> new Rango(
							(Integer)rs[0],
							(String)rs[1]
					))
					.collect(Collectors.toList());
			
		}
		
	}
	
	/* (non-Javadoc)
	 * @see com.pe.azoth.dao.DaoRango#listRangos(int)
	 */
	@Override
	public Rango listRangos(int id) throws SQLException, NamingException{
		
		try(Connection connection = this.conexion.getConnection()){
			
			List<Rango> lista = new QueryRunner()
					.query(connection,"SELECT * FROM departamentos WHERE id_departamento = ? ", new ArrayListHandler(),id)
					.stream()
					.map( rs -> new Rango(
							(Integer)rs[0],
							(String)rs[1]
					))
					.collect(Collectors.toList()); 
			
			return (lista.size()==1?lista.get(0):null);
		}
	}
}
