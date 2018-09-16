package com.pe.azoth.dao;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.naming.NamingException;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.ArrayListHandler;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.pe.azoth.beans.Cliente;
import com.pe.azoth.beans.Estado;
import com.pe.azoth.beans.Producto;

public class DaoProductoImpl implements DaoProducto {
	
	
	private Conexion conexion;
	
	public DaoProductoImpl() throws JsonParseException, JsonMappingException, IOException {	
		conexion = new Conexion();
	}
	
	/* (non-Javadoc)
	 * @see com.pe.azoth.dao.DaoProducto#listProductos()
	 */
	@Override
	public List<Producto> listProductos() throws SQLException, NamingException{
		try(Connection connection = this.conexion.getConnection()){
			
			return new QueryRunner()
					.query(connection,
							"SELECT codigo,numero,descripcion,direccion,origen,destino,"
							+ "cliente_envio,cliente_recepcion,id_estado, fec_creacion "
							+ "FROM productos "
							+ "WHERE id_estado != -1 "
							+ "ORDER BY id_estado,codigo,fec_creacion ", 
							new ArrayListHandler())
					.stream()
					.map( rs -> new Producto(
							(String)rs[0],//CODIGO
							(Integer)rs[1],//NUMERO
							(String)rs[2],//DESCRIPCION
							(String)rs[3],//DIRECCION
							(String)rs[4],//ORIGEN
							(String)rs[5],//DESTINO
							new Cliente ((Integer)rs[6]),//CLIENTE ENVIO
							new Cliente ((Integer)rs[7]),//CLIENTE RECEPCION
							new Estado ((Integer)rs[8]),//Estado,
							(Timestamp)rs[9] //FECHA CREACION
							))
					.collect(Collectors.toList());
		}
		
	}

	/* (non-Javadoc)
	 * @see com.pe.azoth.dao.DaoProducto#getProducto(java.lang.String)
	 */
	@Override
	public List<Producto> listProductos(String codigo)  throws SQLException, NamingException{
		try(Connection connection = this.conexion.getConnection()){
			return new QueryRunner()
					.query(connection, 
							"SELECT codigo,numero,descripcion,direccion,origen,"
							+ "destino,cliente_envio,cliente_recepcion,id_estado,fec_creacion "
							+ "FROM productos "
							+ "WHERE codigo = ? AND id_estado != -1 "
							+ "ORDER BY id_estado, codigo, fec_creacion ", 
							new ArrayListHandler(),
							codigo)
					.stream()
					.map( rs -> new Producto(
							(String)rs[0],//CODIGO
							(Integer)rs[1],//NUMERO
							(String)rs[2],//DESCRIPCION
							(String)rs[3],//DIRECCION
							(String)rs[4],//ORIGEN
							(String)rs[5],//DESTINO
							new Cliente ((Integer)rs[6]),//CLIENTE ENVIO
							new Cliente ((Integer)rs[7]),//CLIENTE RECEPCION
							new Estado ((Integer)rs[8]),//Estado,
							(Timestamp)rs[9] //FECHA CREACION
							))
					.collect(Collectors.toList());
			
		}
	}
	


	/* (non-Javadoc)
	 * @see com.pe.azoth.dao.DaoProducto#listProductos(java.lang.Integer)
	 */
	@Override
	public List<Producto> listProductos(String... codigos) throws SQLException, NamingException{
		try(Connection connection = this.conexion.getConnection()){
			if(codigos.length == 0)
				return Arrays.asList();
			else {
				
				String sql = "(";
				for(String x : codigos) {
					sql += ("'"+x+"'" +",");
				}
				sql = sql.substring(0,sql.length()-1)+") ";
				

				return new QueryRunner()
						.query(connection,
								"SELECT codigo,numero,descripcion,direccion,origen,destino,cliente_envio,"
								+ "cliente_recepcion,id_estado, fec_creacion "
								+ "FROM productos "
								+ "WHERE CONCAT(codigo,'-',numero) IN " + sql 
								+ "ORDER BY id_estado,codigo,fec_creacion ", 
								new ArrayListHandler())
						.stream()
						.map( rs -> new Producto(
								(String)rs[0],//CODIGO
								(Integer)rs[1],//NUMERO
								(String)rs[2],//DESCRIPCION
								(String)rs[3],//DIRECCION
								(String)rs[4],//ORIGEN
								(String)rs[5],//DESTINO
								new Cliente ((Integer)rs[6]),//CLIENTE ENVIO
								new Cliente ((Integer)rs[7]),//CLIENTE RECEPCION
								new Estado ((Integer)rs[8]),//Estado,
								(Timestamp)rs[9] //FECHA CREACION
								))
						.collect(Collectors.toList());
			}
		}
	}
	/* (non-Javadoc)
	 * @see com.pe.azoth.dao.DaoProducto#getProducto(java.lang.Integer)
	 */
	@Override
	public Producto getProducto(Integer numero, String codigo) throws SQLException, NamingException{
		
		try(Connection connection = this.conexion.getConnection()){
			return getProducto(numero,codigo,connection);
		}
	}

	@Override
	public Producto getProducto(Integer numero, String codigo, Connection connection) throws SQLException, NamingException{
		
			List<Producto> temp = new QueryRunner()
					.query(connection,
							"SELECT codigo,numero,descripcion,direccion,origen,destino,cliente_envio,"
							+ "cliente_recepcion,id_estado, fec_creacion FROM productos "
							+ "WHERE id_estado != -1 AND codigo = ? AND numero = ? "
							+ "ORDER BY id_estado,codigo,fec_creacion ", 
						new ArrayListHandler(),
						codigo,numero)
					.stream()
					.map( rs -> new Producto(
							(String)rs[0],//CODIGO
							(Integer)rs[1],//NUMERO
							(String)rs[2],//DESCRIPCION
							(String)rs[3],//DIRECCION
							(String)rs[4],//ORIGEN
							(String)rs[5],//DESTINO
							new Cliente ((Integer)rs[6]),//CLIENTE ENVIO
							new Cliente ((Integer)rs[7]),//CLIENTE RECEPCION
							new Estado ((Integer)rs[8]),//Estado,
							(Timestamp)rs[9] //FECHA CREACION
							))
					.collect(Collectors.toList());
			if(temp.size() == 1)
				return temp.get(0);
			else
				return null;
		
	}
	
	/* (non-Javadoc)
	 * @see com.pe.azoth.dao.DaoProducto#insertProducto(com.pe.azoth.beans.Producto, java.sql.Connection)
	 */
	@Override
	public int insertProducto(Producto producto, Connection connection) throws SQLException{
		try(PreparedStatement pst = connection.prepareStatement(
				"INSERT INTO productos" + 
				"(numero,codigo,descripcion, direccion,origen,destino,"
				+ "cliente_envio,cliente_recepcion,id_estado) " + 
				"SELECT ( IF(MAX(numero) IS NULL,0,MAX(numero))+1 ), ?,?,?,?,?,?,?,? " + 
				"	FROM productos WHERE codigo = ? ;")){ 
			
			pst.setString(1, producto.getCodigo());
			pst.setString(2, producto.getDescripcion());
			pst.setString(3, producto.getDireccion());
			
			pst.setString(4, producto.getOrigen());
			pst.setString(5, producto.getDestino());
			
			pst.setInt(6, producto.getEnvio().getId());
			pst.setInt(7, producto.getRecepcion().getId());
			pst.setInt(8, producto.getEstado().getId());
			
			pst.setString(9,producto.getCodigo());
			
			pst.executeUpdate();

			return getNextIdProducto(producto.getCodigo(),connection)-1;
		}
	}
	/* (non-Javadoc)
	 * @see com.pe.azoth.dao.DaoProducto#insertProducto(com.pe.azoth.beans.Producto)
	 */
	@Override
	public int insertProducto(Producto producto) throws SQLException, NamingException {
		try(Connection connection = this.conexion.getConnection()){
			//producto.setId(this.getNextIdProducto(producto.getCodigo()));
			return this.insertProducto(producto,connection);
		}
	}
	
	/* (non-Javadoc)
	 * @see com.pe.azoth.dao.DaoProducto#updateProducto(com.pe.azoth.beans.Producto, java.sql.Connection)
	 */
	@Override
	public int updateProducto(Producto producto, Connection connection) throws SQLException {
		try(PreparedStatement pst = connection.prepareStatement(
				"UPDATE productos "
				+ "SET descripcion = ?,direccion = ?,origen = ?,destino = ?,cliente_envio = ?,"
				+ "cliente_recepcion = ?,id_estado = ? "
				+ "WHERE codigo = ? AND numero = ?;")){
			
			pst.setString(1, producto.getDescripcion());
			pst.setString(2, producto.getDireccion());
			pst.setString(3, producto.getOrigen());
			pst.setString(4, producto.getDestino());
			pst.setInt(5, producto.getEnvio().getId());
			pst.setInt(6, producto.getRecepcion().getId());
			pst.setInt(7, producto.getEstado().getId());
			pst.setString(8, producto.getCodigo());
			pst.setInt(9,producto.getNumero());
			
			return pst.executeUpdate();
		}
	}
	/* (non-Javadoc)
	 * @see com.pe.azoth.dao.DaoProducto#updateProducto(com.pe.azoth.beans.Producto)
	 */
	@Override
	public int updateProducto(Producto producto) throws SQLException, NamingException {
		try(Connection connection = conexion.getConnection()){
			return this.updateProducto(producto,connection);
		}
	}
	@Override
	public int getNextIdProducto(String codigo) throws SQLException, NamingException{
		try(Connection connection = conexion.getConnection()){
			try(PreparedStatement pst = connection.prepareStatement("select IFNULL(MAX(numero), 0) + 1  "
					+ "from productos where codigo = ?")){
				pst.setString(1, codigo);
				
				try(ResultSet rs = pst.executeQuery()){
					rs.next();
					return rs.getInt(1);
				}
				
			}
			
		}
	}
	
	public int getNextIdProducto(String codigo, Connection connection) throws SQLException{
		try(PreparedStatement pst = connection.prepareStatement("select IFNULL(MAX(numero), 0) + 1  "
				+ "from productos where codigo = ?")){
			pst.setString(1, codigo);
			
			try(ResultSet rs = pst.executeQuery()){
				rs.next();
				return rs.getInt(1);
			}
		}
	}
	
	@Override
	public List<String> getCodigos() throws SQLException, NamingException{
		try(Connection connection = conexion.getConnection()){
			return new QueryRunner()
					.query(connection, "SELECT distinct codigo FROM productos ", new ArrayListHandler())
					.stream()
					.map(e -> (String)e[0])
					.collect(Collectors.toList());
		}
	}
	
}
