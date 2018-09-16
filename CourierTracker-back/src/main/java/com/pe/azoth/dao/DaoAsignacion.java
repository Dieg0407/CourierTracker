package com.pe.azoth.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import javax.naming.NamingException;

import com.pe.azoth.beans.Asignacion;

public interface DaoAsignacion {

	/* (non-Javadoc)
	 * @see com.pe.azoth.dao.DaoAsignacion#listAsignaciones()
	 */
	List<Asignacion> listAsignaciones() throws SQLException, NamingException;

	/* (non-Javadoc)
	 * @see com.pe.azoth.dao.DaoAsignacion#listAsignaciones(java.lang.String)
	 */
	List<Asignacion> listAsignaciones(String correoUsuario) throws SQLException, NamingException;

	/* (non-Javadoc)
	 * @see com.pe.azoth.dao.DaoAsignacion#listAsignaciones(java.lang.Integer)
	 */
	List<Asignacion> listAsignaciones(Integer nroProducto, String codigoProducto) throws SQLException, NamingException;

	Asignacion getAsignacion(Integer nroProducto, String codigoProducto, String correoUsuario) throws SQLException, NamingException;
	
	Asignacion getAsignacion(Integer nroProducto, String codigoProducto, String correoUsuario, Connection connection) throws SQLException;

	/* (non-Javadoc)
	 * @see com.pe.azoth.dao.DaoAsignacion#insertAsignacion(com.pe.azoth.beans.Asignacion, java.sql.Connection)
	 */
	int insertAsignacion(Asignacion asignacion, Connection connection) throws SQLException;

	/* (non-Javadoc)
	 * @see com.pe.azoth.dao.DaoAsignacion#insertAsignacion(com.pe.azoth.beans.Asignacion)
	 */
	int insertAsignacion(Asignacion asignacion) throws SQLException, NamingException;

	int deleteAsignacion(Asignacion asignacion,Connection connection) throws SQLException;
	
	int deleteAsignacion(Asignacion asignacion) throws SQLException, NamingException;

}