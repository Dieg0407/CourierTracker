package com.pe.azoth.dao;

import java.sql.SQLException;
import java.sql.Connection;

import java.util.List;

import javax.naming.NamingException;

import com.pe.azoth.beans.Estado;

public interface DaoEstado {

	List<Estado> listEstados() throws SQLException, NamingException;

	Estado getEstado(int id) throws SQLException, NamingException;

	Estado getEstado(int id, Connection connection) throws SQLException, NamingException;
}