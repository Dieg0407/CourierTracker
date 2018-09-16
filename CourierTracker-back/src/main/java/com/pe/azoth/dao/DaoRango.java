package com.pe.azoth.dao;

import java.sql.SQLException;
import java.util.List;

import javax.naming.NamingException;

import com.pe.azoth.beans.Rango;

public interface DaoRango {

	List<Rango> listRangos() throws SQLException, NamingException;

	Rango listRangos(int id) throws SQLException, NamingException;

}