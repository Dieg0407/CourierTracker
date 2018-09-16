package com.pe.azoth.dao;


import java.sql.SQLException;
import java.util.List;
import javax.naming.NamingException;
import com.pe.azoth.beans.Imagen;

public interface DaoImagen {

	int deleteImagen(int id) throws SQLException, NamingException;

	List<Imagen> listImagenes(String codigo, int numero) throws SQLException, NamingException;

	void addImagen(Imagen imagen) throws SQLException, NamingException;

	Imagen getImagen(int id) throws SQLException, NamingException;
}