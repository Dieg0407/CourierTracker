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
import com.pe.azoth.beans.Cliente;

public class DaoClienteImpl implements DaoCliente {
	
	private Conexion conexion;
	
	public DaoClienteImpl() throws JsonParseException, JsonMappingException, IOException {
		this.conexion = new Conexion();
	}
	
	/* (non-Javadoc)
	 * @see com.pe.azoth.dao.DaoCliente#listClientes()
	 */
	@Override
	public List<Cliente> listClientes() throws SQLException, NamingException{
		try(Connection connection = this.conexion.getConnection()){
			return new QueryRunner()
					.query(connection, 
							"SELECT id_cliente,nombres,apellidos,dni,celular,correo "
							+ "FROM clientes",new ArrayListHandler())
					.stream()
					.map( rs -> new Cliente(
							(Integer)rs[0],//ID
							(String)rs[1],//NOMBRES
							(String)rs[2],//APELLIDOS
							(String)rs[3],//DNI
							(String)rs[4],//CELULAR
							(String)rs[5]//CORREO
							))
					.collect(Collectors.toList());
		}
	}
	
	/* (non-Javadoc)
	 * @see com.pe.azoth.dao.DaoCliente#listClientes(java.lang.String)
	 */
	@Override
	public List<Cliente> listClientes(String dni) throws SQLException, NamingException {
		try(Connection connection = this.conexion.getConnection()){
			return new QueryRunner()
					.query(connection, 
							"SELECT id_cliente,nombres,apellidos,dni,celular,correo "
							+ "FROM clientes WHERE dni = ? ",new ArrayListHandler(),dni)
					.stream()
					.map( rs -> new Cliente(
							(Integer)rs[0],//ID
							(String)rs[1],//NOMBRES
							(String)rs[2],//APELLIDOS
							(String)rs[3],//DNI
							(String)rs[4],//CELULAR
							(String)rs[5]//CORREO
							))
					.collect(Collectors.toList());
		}
	}
	
	/* (non-Javadoc)
	 * @see com.pe.azoth.dao.DaoCliente#listClientes(java.lang.Integer)
	 */
	@Override
	public Cliente getCliente(Integer idCliente) throws SQLException, NamingException{
		try(Connection connection = this.conexion.getConnection()){
			List<Cliente> lista = new QueryRunner()
					.query(connection, 
							"SELECT id_cliente,nombres,apellidos,dni,celular,correo "
							+ "FROM clientes WHERE id_cliente = ? ",new ArrayListHandler(),idCliente)
					.stream()
					.map( rs -> new Cliente(
							(Integer)rs[0],//ID
							(String)rs[1],//NOMBRES
							(String)rs[2],//APELLIDOS
							(String)rs[3],//DNI
							(String)rs[4],//CELULAR
							(String)rs[5]//CORREO
							))
					.collect(Collectors.toList());
			return (lista.size() == 1)?lista.get(0):null;
		}
	}

	@Override
	public Cliente getCliente(Integer idCliente, Connection connection) throws SQLException{
		
		List<Cliente> lista = new QueryRunner()
				.query(connection, 
						"SELECT id_cliente,nombres,apellidos,dni,celular,correo "
						+ "FROM clientes WHERE id_cliente = ? ",new ArrayListHandler(),idCliente)
				.stream()
				.map( rs -> new Cliente(
						(Integer)rs[0],//ID
						(String)rs[1],//NOMBRES
						(String)rs[2],//APELLIDOS
						(String)rs[3],//DNI
						(String)rs[4],//CELULAR
						(String)rs[5]//CORREO
						))
				.collect(Collectors.toList());
		return (lista.size() == 1)?lista.get(0):null;
		
	}

	@Override
	public Cliente getCliente(String dni) throws SQLException, NamingException {
		try(Connection connection = this.conexion.getConnection()){
			List<Cliente> lista = new QueryRunner()
					.query(connection, 
							"SELECT id_cliente,nombres,apellidos,dni,celular,correo "
							+ "FROM clientes WHERE dni = ? ",new ArrayListHandler(),dni)
					.stream()
					.map( rs -> new Cliente(
							(Integer)rs[0],//ID
							(String)rs[1],//NOMBRES
							(String)rs[2],//APELLIDOS
							(String)rs[3],//DNI
							(String)rs[4],//CELULAR
							(String)rs[5]//CORREO
							))
					.collect(Collectors.toList());
			return (lista.size() == 1)?lista.get(0):null;
		}
	}

	@Override
	public Cliente getCliente(String dni, Connection connection) throws SQLException {
		List<Cliente> lista = new QueryRunner()
				.query(connection, 
						"SELECT id_cliente,nombres,apellidos,dni,celular,correo "
					+ "FROM clientes WHERE dni = ? ",new ArrayListHandler(),dni)
				.stream()
				.map( rs -> new Cliente(
						(Integer)rs[0],//ID
						(String)rs[1],//NOMBRES
						(String)rs[2],//APELLIDOS
						(String)rs[3],//DNI
						(String)rs[4],//CELULAR
						(String)rs[5]//CORREO
						))
				.collect(Collectors.toList());
		return (lista.size() == 1)?lista.get(0):null;
	}


	
	/* (non-Javadoc)
	 * @see com.pe.azoth.dao.DaoCliente#insertCliente(com.pe.azoth.beans.Cliente, java.sql.Connection)
	 */
	@Override
	public int insertCliente(Cliente cliente, Connection connection) throws SQLException, NamingException{
		try(PreparedStatement pst = connection.prepareStatement(
				"INSERT INTO clientes "
				+ "(nombres,apellidos,dni,celular,correo) "
				+ "VALUES (?,?,?,?,?)")){
			
			pst.setString(1, cliente.getNombres());
			pst.setString(2, cliente.getApellidos());
			pst.setString(3, cliente.getDni());
			pst.setString(4, cliente.getCelular());
			pst.setString(5, cliente.getCorreo());
			
			pst.executeUpdate();

			connection.commit();

			return this.getCliente(cliente.getDni(),connection).getId();
		}
	}
	
	/* (non-Javadoc)
	 * @see com.pe.azoth.dao.DaoCliente#insertCliente(com.pe.azoth.beans.Cliente)
	 */
	@Override
	public int insertCliente(Cliente cliente) throws SQLException, NamingException{
		try(Connection connection = this.conexion.getConnection()){
			return this.insertCliente(cliente, connection);
		}
	}
	
	/* (non-Javadoc)
	 * @see com.pe.azoth.dao.DaoCliente#updateCliente(com.pe.azoth.beans.Cliente, java.sql.Connection)
	 */
	@Override
	public int updateCliente(Cliente cliente, Connection connection) throws SQLException, NamingException{
		try(PreparedStatement pst = connection.prepareStatement(
				"UPDATE clientes "+
				"SET nombres = ?, apellidos = ?, dni = ?, celular = ?, correo = ? "+
				"WHERE id_cliente = ? ")){
			pst.setString(1, cliente.getNombres());
			pst.setString(2, cliente.getApellidos());
			pst.setString(3, cliente.getDni());
			pst.setString(4, cliente.getCelular());
			pst.setString(5, cliente.getCorreo());
			pst.setInt(6, cliente.getId());
			
			return pst.executeUpdate();
		}
	}
	/* (non-Javadoc)
	 * @see com.pe.azoth.dao.DaoCliente#updateCliente(com.pe.azoth.beans.Cliente)
	 */
	@Override
	public int updateCliente(Cliente cliente) throws SQLException, NamingException{
		try(Connection connection = this.conexion.getConnection()){
			return this.updateCliente(cliente,connection);
		}
	}

	
}
