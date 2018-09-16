package com.pe.azoth.dao;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

import javax.naming.NamingException;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.ArrayListHandler;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.pe.azoth.beans.Asignacion;

public class DaoAsignacionImpl implements DaoAsignacion {
	
	private Conexion conexion;
	
	public DaoAsignacionImpl() throws JsonParseException, JsonMappingException, IOException {
		this.conexion = new Conexion();
	}
	
	/* (non-Javadoc)
	 * @see com.pe.azoth.dao.DaoAsignacion#listAsignaciones()
	 */
	@Override
	public List<Asignacion> listAsignaciones() throws SQLException, NamingException{
		try(Connection connection = this.conexion.getConnection()){
			
			return new QueryRunner()
					.query(connection, "SELECT a.correo, a.codigo, a.numero, p.id_estado "
							+ "FROM asignaciones a "
							+ "INNER JOIN productos p ON (p.codigo = a.codigo AND p.numero = a.numero) "
							, new ArrayListHandler())
					.stream()
					.map(rs -> new Asignacion(
							(String)rs[0], //CORREO USUARIO
							(String)rs[1], //CODIGO PRODUCTO
							(Integer)rs[2],//NUMERO PRODUCTO
							(Integer)rs[3] //ESTADO DEL PRODUCTO
							))
					.collect(Collectors.toList());
			
		}
	}
	
	/* (non-Javadoc)
	 * @see com.pe.azoth.dao.DaoAsignacion#listAsignaciones(java.lang.String)
	 */
	@Override
	public List<Asignacion> listAsignaciones(String correoUsuario) throws SQLException, NamingException{
		try(Connection connection = this.conexion.getConnection()){
			
			return new QueryRunner()
					.query(connection, "SELECT a.correo, a.codigo, a.numero, p.id_estado  "
							+ "FROM asignaciones a "
							+ "INNER JOIN productos p ON (p.codigo = a.codigo AND p.numero = a.numero ) "
							+ "WHERE a.correo = ? ", new ArrayListHandler(),correoUsuario)
					.stream()
					.map(rs -> new Asignacion(
							(String)rs[0], //CORREO USUARIO
							(String)rs[1], //CODIGO PRODUCTO
							(Integer)rs[2],//NUMERO PRODUCTO
							(Integer)rs[3] //ESTADO DEL PRODUCTO
							))
					.collect(Collectors.toList());
			
		}
	}
	
	/* (non-Javadoc)
	 * @see com.pe.azoth.dao.DaoAsignacion#listAsignaciones(java.lang.Integer)
	 */
	@Override
	public List<Asignacion> listAsignaciones(Integer nroProducto, String codigoProducto) throws SQLException, NamingException{
		try(Connection connection = this.conexion.getConnection()){
			
			return new QueryRunner()
					.query(connection, "SELECT a.correo, a.codigo, a.numero, p.id_estado "
							+ "FROM asignaciones a "
							+ "INNER JOIN productos p ON (p.codigo = a.codigo AND p.numero = a.numero ) "
							+ "WHERE a.numero = ? AND a.codigo = ?", 
							new ArrayListHandler(),
							nroProducto ,codigoProducto)
					.stream()
					.map(rs -> new Asignacion(
							(String)rs[0], //CORREO USUARIO
							(String)rs[1], //CODIGO PRODUCTO
							(Integer)rs[2],//NUMERO PRODUCTO
							(Integer)rs[3] //ESTADO DEL PRODUCTO
							))
					.collect(Collectors.toList());
			
		}
	}
	/* (non-Javadoc)
	 * @see com.pe.azoth.dao.DaoAsignacion#getAsignacion(java.lang.Integer, java.lang.String)
	 */
	@Override
	public Asignacion getAsignacion(Integer nroProducto, String codigoProducto, String correoUsuario) throws SQLException, NamingException {
		
		try(Connection connection = this.conexion.getConnection()){
			
			List<Asignacion> temp =  new QueryRunner()
					.query(connection,"SELECT a.correo, a.codigo, a.numero, p.id_estado "
							+ "FROM asignaciones a "
							+ "INNER JOIN productos p ON (p.codigo = a.codigo AND p.numero = a.numero ) "
							+ "WHERE a.correo = ? AND p.codigo = ? AND p.numero = ?", 
							new ArrayListHandler(),
							correoUsuario,codigoProducto,nroProducto)
					.stream()
					.map(rs -> new Asignacion(
							(String)rs[0], //CORREO USUARIO
							(String)rs[1], //CODIGO PRODUCTO
							(Integer)rs[2],//NUMERO PRODUCTO
							(Integer)rs[3] //ESTADO DEL PRODUCTO
							))
					.collect(Collectors.toList());
			
			return (temp.size() == 1)?temp.get(0):null;
		}
	}
	@Override
	public Asignacion getAsignacion(Integer nroProducto, String codigoProducto, String correoUsuario,Connection connection) throws SQLException {
		
			List<Asignacion> temp =  new QueryRunner()
					.query(connection,"SELECT a.correo, a.codigo, a.numero, p.id_estado "
							+ "FROM asignaciones a "
							+ "INNER JOIN productos p ON (p.codigo = a.codigo AND p.numero = a.numero ) "
							+ "WHERE a.correo = ? AND p.codigo = ? AND p.numero = ?", 
							new ArrayListHandler(),
							correoUsuario,codigoProducto,nroProducto)
					.stream()
					.map(rs -> new Asignacion(
							(String)rs[0], //CORREO USUARIO
							(String)rs[1], //CODIGO PRODUCTO
							(Integer)rs[2],//NUMERO PRODUCTO
							(Integer)rs[3] //ESTADO DEL PRODUCTO
							))
					.collect(Collectors.toList());
			
			return (temp.size() == 1)?temp.get(0):null;
		
	}
	
	/* (non-Javadoc)
	 * @see com.pe.azoth.dao.DaoAsignacion#insertAsignacion(com.pe.azoth.beans.Asignacion, java.sql.Connection)
	 */
	@Override
	public int insertAsignacion (Asignacion asignacion, Connection connection) throws SQLException {
		try(PreparedStatement pst = connection.prepareStatement(
				"INSERT INTO asignaciones "+
				"(correo, codigo, numero) "+
				"VALUES (?,?,?) ")){
			
			pst.setString(1, asignacion.getCorreoUsuario());
			pst.setString(2, asignacion.getCodProducto());
			pst.setInt(3,asignacion.getNroProducto());
			return pst.executeUpdate();
		}
	}
	/* (non-Javadoc)
	 * @see com.pe.azoth.dao.DaoAsignacion#insertAsignacion(com.pe.azoth.beans.Asignacion)
	 */
	@Override
	public int insertAsignacion (Asignacion asignacion) throws SQLException, NamingException {
		try(Connection connection = this.conexion.getConnection()){
			return this.insertAsignacion(asignacion,connection);
		}
	}

	@Override
	public int deleteAsignacion(Asignacion asignacion, Connection connection) throws SQLException {
		try(PreparedStatement pst = connection.prepareStatement("DELETE FROM asignaciones "
				+ "WHERE correo = ? AND codigo = ? AND numero = ? ")){
			pst.setString(1, asignacion.getCorreoUsuario());
			pst.setString(2, asignacion.getCodProducto());
			pst.setInt(3, asignacion.getNroProducto());
			
			return pst.executeUpdate();
		}
	}

	@Override
	public int deleteAsignacion(Asignacion asignacion) throws SQLException, NamingException {
		try(Connection connection = this.conexion.getConnection()){
			return this.deleteAsignacion(asignacion,connection);
		}
	}
	
}
