package com.pe.azoth.dao;


import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Blob;
import javax.sql.rowset.serial.SerialBlob;
import java.sql.ResultSet;

import java.util.List;
import java.util.stream.Collectors;

import javax.naming.NamingException;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.ArrayListHandler;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.pe.azoth.beans.Imagen;

public class DaoImagenImpl implements DaoImagen{

	private Conexion conexion;
	
	public DaoImagenImpl() throws JsonParseException, JsonMappingException, IOException {
		this.conexion = new Conexion();
	}

	@Override
	public int deleteImagen(int id)  throws SQLException, NamingException{
		try(Connection connection = conexion.getConnection()){
			try(PreparedStatement pst = connection.prepareStatement("DELETE FROM imagenes WHERE identificador = ?")){
				pst.setInt(1,id);
				return pst.executeUpdate();
			}
		}
	}

	@Override
	public void addImagen(Imagen imagen)  throws SQLException, NamingException{
		try(Connection connection = conexion.getConnection()){
			try(PreparedStatement pst = connection.prepareStatement(
				"INSERT INTO imagenes SET codigo = ?, numero = ?, imagen = ?")){
				pst.setString(1,imagen.getCodigo());
				pst.setInt(2,imagen.getNumero());
				pst.setBlob(3,new SerialBlob(imagen.getImagen()));
				pst.executeUpdate();
			}

		}
	}

	@Override
	public Imagen getImagen (int id)  throws SQLException, NamingException{
		try(Connection connection = conexion.getConnection()){
			try(PreparedStatement pst = connection.prepareStatement("SELECT * FROM imagenes WHERE identificador = ?")){
				pst.setInt(1,id);
				try(ResultSet rs = pst.executeQuery()){
					if(rs.next()){
						
						Imagen img = new Imagen();
						img.setId(id);
						img.setCodigo(rs.getString("codigo"));
						img.setNumero(rs.getInt("numero"));

						Blob tmp = rs.getBlob("image");
						img.setImagen(tmp.getBytes(1,(int)tmp.length()));
						tmp.free();

						return img;
					}
					else
						return null;

				}
			}
		}
	}

	@Override
	public List<Imagen> listImagenes(String codigo, int numero)  throws SQLException, NamingException{
		try(Connection connection = conexion.getConnection()){
			return new QueryRunner()
					.query(connection, 
							"SELECT identificador, codigo,numero,image FROM imagenes WHERE codigo = ? AND numero = ?",
							new ArrayListHandler(),
							codigo,numero)
					.stream()
					.map( rs -> resultSetToImagen(rs))
					.collect(Collectors.toList());
		}
	}

	private Imagen resultSetToImagen(Object[] rs) {
		try{		
			Imagen img = new Imagen();
			img.setId((Integer)rs[0]);
			img.setCodigo((String)rs[1]);
			img.setNumero((Integer)rs[2]);

			Blob tmp = (Blob)rs[3];
			img.setImagen(tmp.getBytes(1,(int)tmp.length()));
			tmp.free();
			

			return img;
		}
		catch(SQLException e){
			return new Imagen();
		}
	}

}