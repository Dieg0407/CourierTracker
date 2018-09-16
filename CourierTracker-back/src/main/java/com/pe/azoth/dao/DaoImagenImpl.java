package com.pe.azoth.dao;


import java.io.IOException;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.naming.NamingException;
import javax.sql.rowset.serial.SerialBlob;

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
			try(PreparedStatement pst = connection.prepareStatement("SELECT * FROM imagenes WHERE codigo = ? AND numero = ?")){
				pst.setString(1,codigo);
				pst.setInt(2,numero);	
				try(ResultSet rs = pst.executeQuery()){
					List<Imagen> lst = new ArrayList<>();
					while(rs.next()) {
						Imagen img = new Imagen();
						img.setId(rs.getInt("identificador"));
						img.setCodigo(rs.getString("codigo"));
						img.setNumero(rs.getInt("numero"));

						Blob tmp = rs.getBlob("image");
						img.setImagen(tmp.getBytes(1,(int)tmp.length()));
						tmp.free();
						
						lst.add(img);
					}
					return lst;
				}
			}
		}
	}


}