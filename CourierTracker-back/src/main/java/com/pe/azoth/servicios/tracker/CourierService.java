package com.pe.azoth.servicios.tracker;


import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.naming.NamingException;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.pe.azoth.beans.Asignacion;
import com.pe.azoth.beans.Cliente;
import com.pe.azoth.beans.Producto;
import com.pe.azoth.beans.Usuario;
import com.pe.azoth.dao.Conexion;
import com.pe.azoth.dao.DaoAsignacion;
import com.pe.azoth.dao.DaoAsignacionImpl;
import com.pe.azoth.dao.DaoCliente;
import com.pe.azoth.dao.DaoClienteImpl;
import com.pe.azoth.dao.DaoEstado;
import com.pe.azoth.dao.DaoEstadoImpl;
import com.pe.azoth.dao.DaoProducto;
import com.pe.azoth.dao.DaoProductoImpl;
import com.pe.azoth.dao.DaoUsuario;
import com.pe.azoth.dao.DaoUsuarioImpl;
import com.pe.azoth.modelo.JWTManager;

@Path("/")
public class CourierService {
	
	@POST
	@Path("/listProductos")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public List<Producto> listProductos(
		@HeaderParam("token") @DefaultValue("") String jwt) throws JsonProcessingException{
		
		JWTManager jwtManager = new JWTManager();
		try {
			Usuario usr = jwtManager.parseJWT(jwt);
			if(usr != null){
				
				DaoProducto daoProducto = new DaoProductoImpl();
				DaoCliente daoCliente  = new DaoClienteImpl();
				DaoEstado daoEstado = new DaoEstadoImpl();

				List<Producto> productos = daoProducto.listProductos();
				
				for(Producto p : productos) {
					//CAMBIOS CON EL MODELO
					//p.setOrigen(daoLocalidad.getLocalidad(p.getOrigen().getId()));
					//p.setDestino(daoLocalidad.getLocalidad(p.getDestino().getId()));
					p.setEnvio(daoCliente.getCliente(p.getEnvio().getId()));
					p.setRecepcion(daoCliente.getCliente(p.getRecepcion().getId()));
					p.setEstado(daoEstado.getEstado(p.getEstado().getId()));
				}
				
				return productos;
			}
			else
				throw new WebApplicationException(
						this.exception(Response.Status.UNAUTHORIZED, "ERROR DE AUTENTICACIÓN")
				);
		} 
		catch (IOException | SQLException | NamingException e) {
			e.printStackTrace(System.err);
			throw new WebApplicationException(
					this.exception(Response.Status.INTERNAL_SERVER_ERROR, e.getMessage())
			);
		}
	}
	
	//OBTENER LOS REGISTROS
	@POST
	@Path("/getProductos")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public List<Producto> getTable(
		@HeaderParam("token") @DefaultValue("") String jwt) throws JsonProcessingException{
		
		JWTManager jwtManager = new JWTManager();
		try {
			Usuario usr = jwtManager.parseJWT(jwt);
			if(usr != null){
				
				try(Connection connection = new Conexion().getConnection()){
					
					DaoProducto daoProducto = new DaoProductoImpl();
					DaoAsignacion daoAsignacion = new DaoAsignacionImpl();
					DaoCliente daoCliente  = new DaoClienteImpl();
					DaoEstado daoEstado = new DaoEstadoImpl();
					
					List<String> temp = daoAsignacion.listAsignaciones(usr.getCorreo(),connection)
					.stream()
					.map(as -> as.getCodProducto()+"-"+as.getNroProducto())
					.collect(Collectors.toList());
					

					String[] array = new String[temp.size()];
					array = temp.toArray(array);
					List<Producto> productos = daoProducto.listProductos(connection,array);
					
					for(Producto p : productos) {
						//CAMBIOS CON EL MODELO
						//p.setOrigen(daoLocalidad.getLocalidad(p.getOrigen().getId()));
						//p.setDestino(daoLocalidad.getLocalidad(p.getDestino().getId()));
						p.setEnvio(daoCliente.getCliente(p.getEnvio().getId(),connection));
						p.setRecepcion(daoCliente.getCliente(p.getRecepcion().getId(),connection));
						p.setEstado(daoEstado.getEstado(p.getEstado().getId(),connection));
					}
					
					return productos;
				}
			}
			else
				throw new WebApplicationException(
						this.exception(Response.Status.UNAUTHORIZED, "ERROR DE AUTENTICACIÓN")
				);
		} 
		catch (IOException | SQLException | NamingException e) {
			e.printStackTrace(System.err);
			throw new WebApplicationException(
					this.exception(Response.Status.INTERNAL_SERVER_ERROR, e.getMessage())
			);
		}
	}
	@POST
	@Path("/getProductoCliente")
	@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public Producto getProductoCliente(
		String jsonObject) throws JsonProcessingException{
		ObjectMapper mapper = new ObjectMapper();
		try(Connection connection = new Conexion().getConnection()){

			String[] str = mapper.readTree(jsonObject).get("codigo_numero").textValue().split("-");
			String codigo = str[0];
			int numero = Integer.valueOf(str[1]);
			DaoProducto daoProducto = new DaoProductoImpl();
			DaoCliente daoCliente = new DaoClienteImpl();
			DaoEstado daoEstado = new DaoEstadoImpl();

			Producto temp = daoProducto.getProducto(numero,codigo,connection);

			if(temp != null){
				temp.setEnvio(daoCliente.getCliente(temp.getEnvio().getId(),connection));
				temp.setRecepcion(daoCliente.getCliente(temp.getRecepcion().getId(),connection));
				temp.setEstado(daoEstado.getEstado(temp.getEstado().getId(),connection));
				return temp;
			} 
			else 
				throw this.exception(Response.Status.NOT_FOUND, 
							"No se encontro codigo-numero : " + codigo+"-"+numero);
		}
		catch(IOException | SQLException | NamingException e){
			e.printStackTrace();
			throw this.exception(Response.Status.INTERNAL_SERVER_ERROR, e.getMessage());
		}

	}

	@POST
	@Path("/getProducto")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public Producto getProducto(
			@HeaderParam("token") @DefaultValue("") String jwt, 
			@FormParam("numero") @DefaultValue("-1") int numero,
			@FormParam("codigo") @DefaultValue("") String codigo) throws JsonProcessingException {
		
		try {
			JWTManager jwtManager = new JWTManager();
			Usuario usr = jwtManager.parseJWT(jwt);
			if(usr != null) {
				DaoProducto daoProducto = new DaoProductoImpl();
				DaoAsignacion daoAsignacion = new DaoAsignacionImpl();
				//DaoLocalidad daoLocalidad = new DaoLocalidadImpl();
				DaoCliente daoCliente  = new DaoClienteImpl();
				
				Asignacion a = daoAsignacion.getAsignacion(numero,codigo, usr.getCorreo());
				if(a != null) {
					Producto p = daoProducto.getProducto(numero, codigo);
					//p.setOrigen(daoLocalidad.getLocalidad(p.getOrigen().getId()));
					//p.setDestino(daoLocalidad.getLocalidad(p.getDestino().getId()));
					p.setEnvio(daoCliente.getCliente(p.getEnvio().getId()));
					p.setRecepcion(daoCliente.getCliente(p.getRecepcion().getId()));
					return p;
				}
					
				else
					throw this.exception(Response.Status.UNAUTHORIZED, 
							"EL USUARIO NO TIENE ASIGNACION UN PRODUCTO CON EL CODIGO+ID: "+codigo+"-"+numero);
			}
			else
				throw this.exception(Response.Status.UNAUTHORIZED, "ERROR DE AUTENTICACIÓN");
			
		}
		catch (IOException | SQLException | NamingException e) {
			e.printStackTrace(System.err);
			throw new WebApplicationException(
					this.exception(Response.Status.INTERNAL_SERVER_ERROR, e.getMessage())
			);
		}	
	}
	@POST
	@Path("/updateProducto")
	@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public Response updateProducto(
			@HeaderParam("token") @DefaultValue("") String jwt,
			Producto producto) throws JsonProcessingException {
		
		try {
			JWTManager jwtManager = new JWTManager();
			Usuario usr = jwtManager.parseJWT(jwt);
			if(usr != null) {
				DaoAsignacion daoAsignacion =  new DaoAsignacionImpl();
				DaoProducto daoProducto =  new DaoProductoImpl();
				
				Asignacion a = daoAsignacion.getAsignacion(producto.getNumero(),
						producto.getCodigo(),usr.getCorreo());
				
				if( a != null )
					if(daoProducto.updateProducto(producto) == 1)
						return Response.status(Response.Status.ACCEPTED).entity("{\"state\":\"success\"}").build();
				
				return Response.notModified().entity("{\"state\":\"notModified\"}").build();
			}
			else
				throw this.exception(Response.Status.UNAUTHORIZED, "ERROR DE AUTENTICACIÓN");
		}
		catch (IOException | SQLException | NamingException e) {
			e.printStackTrace(System.err);
			throw new WebApplicationException(
					this.exception(Response.Status.INTERNAL_SERVER_ERROR, e.getMessage())
			);
		}
	}
	
	@POST
	@Path("/updateProductoConsola")
	@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public Response updateProductoConsola(
			@HeaderParam("token") @DefaultValue("") String jwt,
			Producto producto) throws JsonProcessingException {
		
		try {
			JWTManager jwtManager = new JWTManager();
			Usuario usr = jwtManager.parseJWT(jwt);
			if(usr != null) {
				//DaoAsignacion daoAsignacion =  new DaoAsignacionImpl();
				//if( a != null )
				if(update(producto) == 1)
					return Response.status(Response.Status.OK).build();
				
				return Response.notModified().entity("{\"message\":\"no se pudo anular el producto\"}").build();
			}
			else
				throw this.exception(Response.Status.UNAUTHORIZED, "ERROR DE AUTENTICACIÓN");
		}
		catch (IOException | SQLException | NamingException e) {
			e.printStackTrace(System.err);
			throw new WebApplicationException(
					this.exception(Response.Status.INTERNAL_SERVER_ERROR, e.getMessage())
			);
		}
	}
	
	private int update (Producto producto) throws IOException, NamingException, SQLException {
		DaoProducto daoProducto = new DaoProductoImpl();
		DaoCliente daoCliente = new DaoClienteImpl();
		Connection connection = null;
		try{
			connection = new Conexion().getConnection();
			
			connection.setAutoCommit(false);
			
			Cliente cliente = daoCliente.getCliente(producto.getEnvio().getDni(), connection);

			if(cliente == null)
				producto.getEnvio().setId( 
					daoCliente.insertCliente(producto.getEnvio(), connection)
				);
			else{
				producto.getEnvio().setId(cliente.getId());
				daoCliente.updateCliente(producto.getEnvio(), connection);
			}

			cliente = daoCliente.getCliente(producto.getRecepcion().getDni(), connection);
			if(daoCliente.getCliente(producto.getRecepcion().getDni()) == null)
				producto.getRecepcion().setId( 
					daoCliente.insertCliente(producto.getRecepcion(), connection)
				);
			else{
				producto.getRecepcion().setId(cliente.getId());
				daoCliente.updateCliente(producto.getRecepcion(), connection);
			}
			
			connection.commit();

			int ret = daoProducto.updateProducto(producto, connection);
			connection.commit();
			connection.close();
			
			return ret;
		}
		catch(SQLException e) {
			try { connection.rollback(); connection.close(); } catch(Exception ex) {}
			throw e;
		}
	}
	
	@POST
	@Path("/insertProducto")
	@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public Response insertProducto(
			@HeaderParam("token") @DefaultValue("") String jwt,
			Producto producto) throws JsonProcessingException {
		try {
			JWTManager jwtManager = new JWTManager();
			Usuario usr = jwtManager.parseJWT(jwt);
			if(usr != null) {
				try {
					int ret = insert(producto);
					return Response.ok().entity("{\"codigo\":\""+producto.getCodigo()+"-"+ret+"\"}").build();
				}
				catch(IOException | SQLException | NamingException e) {
					e.printStackTrace(System.err);
					throw this.exception(Response.Status.INTERNAL_SERVER_ERROR, e.getMessage());
				}
			}
			else {
				throw this.exception(Response.Status.UNAUTHORIZED, "ERROR DE AUTENTICACIÓN");
			}
		}
		catch (IOException | SQLException | NamingException e) {
			e.printStackTrace(System.err);
			throw this.exception(Response.Status.INTERNAL_SERVER_ERROR, e.getMessage());
		}
	}
	
	private int insert(Producto producto) throws IOException, NamingException, SQLException {
		DaoProducto daoProducto = new DaoProductoImpl();
		DaoCliente daoCliente = new DaoClienteImpl();
		Connection connection = null;
		try{
			connection = new Conexion().getConnection();
			
			connection.setAutoCommit(false);
			
			Cliente cliente = daoCliente.getCliente(producto.getEnvio().getDni(), connection);

			if(cliente == null)
				producto.getEnvio().setId( 
					daoCliente.insertCliente(producto.getEnvio(), connection)
				);
			else{
				producto.getEnvio().setId(cliente.getId());
				daoCliente.updateCliente(producto.getEnvio(), connection);
			}

			cliente = daoCliente.getCliente(producto.getRecepcion().getDni(), connection);
			if(daoCliente.getCliente(producto.getRecepcion().getDni()) == null)
				producto.getRecepcion().setId( 
					daoCliente.insertCliente(producto.getRecepcion(), connection)
				);
			else{
				producto.getRecepcion().setId(cliente.getId());
				daoCliente.updateCliente(producto.getRecepcion(), connection);
			}
			
			connection.commit();
			
			int ret = daoProducto.insertProducto(producto, connection);
			

			connection.commit();
			connection.close();
			return ret;
		}
		catch(SQLException e) {
			try { connection.rollback(); connection.close();} catch(Exception ex) {}
			throw e;
		}
	}
	
	@POST
	@Path("/listUsuarios")
	@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public List<Usuario> listUsuarios(
			@HeaderParam("token") @DefaultValue("") String token) throws JsonProcessingException{
		
		try {
			JWTManager jwtManager = new JWTManager();
			
			if(jwtManager.parseJWT(token) != null) {
				DaoUsuario daoUsuarios = new DaoUsuarioImpl();
				return daoUsuarios.listUsuarios(
						"admin@local.ed" //NO SE PUEDE LISTAR EL USUARIO ADMINISTRADOR
				);
			}
			else
				throw this.exception(Response.Status.UNAUTHORIZED, "ERROR DE AUTENTICACIÓN");
		}
		catch (IOException | SQLException | NamingException e) {
			e.printStackTrace(System.err);
			throw this.exception(Response.Status.INTERNAL_SERVER_ERROR, e.getMessage());
		}
	}

	@POST
	@Path("/listUsuariosAsignacion")
	@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public Response listUsuarios(
			@HeaderParam("token") @DefaultValue("") String token,
			String jsonObject) throws JsonProcessingException{
		
		ObjectMapper mapper = new ObjectMapper();
		try {
			String codigo = mapper.readTree(jsonObject).get("codigo").textValue();
			int numero = mapper.readTree(jsonObject).get("numero").intValue();
			
			JWTManager jwtManager = new JWTManager();
			if(jwtManager.parseJWT(token) != null) {
				DaoUsuario daoUsuarios = new DaoUsuarioImpl();
				DaoAsignacion daoAsignaciones = new DaoAsignacionImpl();
				
				Map<String,Usuario> mapa = 
						daoUsuarios.listUsuarios(
						"admin@local.ed" //NO SE PUEDE LISTAR EL USUARIO ADMINISTRADOR
						)
						.stream()
						.filter(usr -> usr.getRango() == 2 || usr.getRango() == 3)
						
						.collect(
							Collectors.toMap(Usuario::getCorreo, u -> u)
						);
				
				List<String> usuariosAsignados = daoAsignaciones.listAsignaciones(numero, codigo)
				.stream()
				.map(a -> a.getCorreoUsuario())
				.collect(Collectors.toList());
				
				
				List<Usuario> asignados = new ArrayList<>();
				List<Usuario> sinAsignar = new ArrayList<>();
				
				for(String correo : usuariosAsignados) {
					if(mapa.get(correo) != null)
						asignados.add(mapa.get(correo));
					
					mapa.remove(correo);
				}
				for(String key : mapa.keySet()) {
					sinAsignar.add(mapa.get(key));
				}
				
				ObjectNode response = mapper.createObjectNode();
				ArrayNode arrA = mapper.valueToTree(asignados);
				ArrayNode arrS = mapper.valueToTree(sinAsignar);
				
				if(asignados.size() != 0)
					response.putArray("asignados").addAll(arrA);
				else
					response.putNull("asignados");
				
				if(sinAsignar.size() != 0)
					response.putArray("sin_asignar").addAll(arrS);
				else
					response.putNull("sin_asignar");
				
				return Response.ok().entity(response).build();
			}
			else
				throw this.exception(Response.Status.UNAUTHORIZED, "ERROR DE AUTENTICACIÓN");
		}
		catch (IOException | SQLException | NamingException  e) {
			e.printStackTrace(System.err);
			throw this.exception(Response.Status.INTERNAL_SERVER_ERROR, e.getMessage());
		}
	}
	
	
	@POST
	@Path("/getUsuario")
	@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public Usuario getUsuario(
			@HeaderParam("token") @DefaultValue("") String token,
			String jsonBody
			) throws JsonProcessingException{
		
		ObjectMapper mapper = new ObjectMapper();
		try {
			JWTManager jwtManager = new JWTManager();
			if(jwtManager.parseJWT(token) != null) {
				DaoUsuario daoUsuarios = new DaoUsuarioImpl();
				Usuario response =  daoUsuarios.getUsuario(
						mapper.readTree(jsonBody).get("correo").textValue()
				);
				if(response != null)
					return response;
				else
					throw this.exception(Response.Status.NOT_FOUND, "ID DEL USUARIO INVÁLIDA");
			}
			else
				throw this.exception(Response.Status.UNAUTHORIZED, "ERROR DE AUTENTICACIÓN");
		}
		catch (IOException | SQLException | NamingException  e) {
			e.printStackTrace(System.err);
			throw this.exception(Response.Status.INTERNAL_SERVER_ERROR, e.getMessage());
		}
		
	}
	
	@POST
	@Path("/updateUsuario")
	@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public Response updateUsuario(
			@HeaderParam("token") @DefaultValue("") String token,
			String jsonObject) throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
	
		try {
			Usuario usr = new JWTManager().parseJWT(token);
			if(usr != null){
				if(usr.getRango() == 1) {					
					DaoUsuario dao = new DaoUsuarioImpl();
					
					Usuario usuario = mapper.readValue(
							mapper.readTree(jsonObject).get("usuario").toString(), 
							Usuario.class);
					int res = dao.updateUsuario(
							usuario, 
							mapper.readTree(jsonObject).get("new_password").asBoolean()
					);
					
					if(res == 1)
						return Response.ok().build();
					else
						return Response.status(Response.Status.NOT_MODIFIED).build();
					
				}
				else
					throw this.exception(Response.Status.UNAUTHORIZED, "EL USUARIO NO TIENE EL RANGO DE ADMINISTRADOR");
			}
			else 
				throw this.exception(Response.Status.UNAUTHORIZED, "ERROR DE AUTENTICACIÓN");
			
			
		}
		catch (IOException | SQLException | NamingException  e) {
			e.printStackTrace(System.err);
			throw this.exception(Response.Status.INTERNAL_SERVER_ERROR, e.getMessage());
		}
	}
	
	@POST
	@Path("/insertUsuario")
	@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public Response insertUsuario(
			@HeaderParam("token") @DefaultValue("") String token,
			Usuario newUser) throws JsonProcessingException {
		try {
			Usuario usuario =  new JWTManager().parseJWT(token) ;
			if(usuario != null) {
				if(usuario.getRango() == 1) {
					
					DaoUsuario dao = new DaoUsuarioImpl();
					dao.insertUsuario(newUser);
					return Response.ok().build();
				}
				else
					throw this.exception(Response.Status.UNAUTHORIZED, "EL USUARIO NO TIENE EL RANGO DE ADMINISTRADOR");
			}
			else
				throw this.exception(Response.Status.UNAUTHORIZED, "ERROR DE AUTENTICACIÓN");
		}
		catch (IOException | SQLException | NamingException  e) {
			e.printStackTrace(System.err);
			throw this.exception(Response.Status.INTERNAL_SERVER_ERROR, e.getMessage());
		}
	}
	
	@POST
	@Path("/getCliente")
	@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public Cliente getCliente(@HeaderParam("token") @DefaultValue("") String token,
			String jsonObject) throws JsonProcessingException {
		try {
			if(new JWTManager().parseJWT(token) != null) {
				
				DaoCliente daoCliente = new DaoClienteImpl();
				JsonNode json = new ObjectMapper().readTree(jsonObject);
				
				return daoCliente.getCliente(json.get("dni").textValue());
			}
			else
				throw this.exception(Response.Status.UNAUTHORIZED, "ERROR DE AUTENTICACIÓN");
		}
		catch(IOException | SQLException | NamingException  e) {
			e.printStackTrace(System.err);
			throw this.exception(Response.Status.INTERNAL_SERVER_ERROR, e.getMessage());
		}
		
	}
	
	@POST
	@Path("/updateCliente")
	@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public Response updateCliente(
			@HeaderParam("token") @DefaultValue("") String token,
			Cliente cliente
			) throws JsonProcessingException{
		try {
			if(new JWTManager().parseJWT(token) != null) {
				
				DaoCliente daoCliente = new DaoClienteImpl();
				if( daoCliente.updateCliente(cliente) == 1)
					return Response.ok().build();
				else
					return Response.notModified().build();
				
			}
			else
				throw this.exception(Response.Status.UNAUTHORIZED, "ERROR DE AUTENTICACIÓN");
		}
		catch(IOException | SQLException | NamingException  e) {
			e.printStackTrace(System.err);
			throw this.exception(Response.Status.INTERNAL_SERVER_ERROR, e.getMessage());
		}
		
	}
	
	@POST
	@Path("/asignarUsuariosProducto")
	@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public Response asignarUsuariosProducto(
			@HeaderParam("token") @DefaultValue("") String token,
			String jsonObject) throws JsonProcessingException {
		
		ObjectMapper mapper = new ObjectMapper();
		try {
			if(new JWTManager().parseJWT(token) != null) {
				JsonNode raiz = mapper.readTree(jsonObject);
				
				JsonNode asignados = raiz.get("asignados");
				JsonNode sinAsignar = raiz.get("sin_asignar");
				
				asignar(asignados,sinAsignar,mapper);
				
				return Response.ok().build();
			}
			else
				throw this.exception(Response.Status.UNAUTHORIZED, "ERROR DE AUTENTICACIÓN");
		}
		catch(IOException | SQLException | NamingException  e) {
			e.printStackTrace(System.err);
			throw this.exception(Response.Status.INTERNAL_SERVER_ERROR, e.getMessage());
		}
		
	}
	
	private void asignar(JsonNode asignados, JsonNode sinAsignar, ObjectMapper mapper) throws SQLException, NamingException, IOException {
		Connection connection = null;
		try {
			connection = new Conexion().getConnection();
			
			String codProducto = null;
			int nroProducto = -1;
			
			DaoAsignacion daoAsignacion = new DaoAsignacionImpl();
			if(asignados != null) {
				for(JsonNode node : asignados) {
					Asignacion temporal = mapper.treeToValue(node, Asignacion.class);
					if(daoAsignacion.getAsignacion(temporal.getNroProducto(), temporal.getCodProducto(), temporal.getCorreoUsuario(), connection) == null)
						daoAsignacion.insertAsignacion(temporal,connection);
					
					codProducto = temporal.getCodProducto();
					nroProducto = temporal.getNroProducto();
				}
			}
			
			if(sinAsignar != null) {
				for(JsonNode node : sinAsignar) {
					Asignacion temporal = mapper.treeToValue(node, Asignacion.class);
					if(daoAsignacion.getAsignacion(temporal.getNroProducto(), temporal.getCodProducto(), temporal.getCorreoUsuario(), connection) != null)
						daoAsignacion.deleteAsignacion(temporal,connection);
					
					
					codProducto = temporal.getCodProducto();
					nroProducto = temporal.getNroProducto();
				}
			}
			
			if(codProducto != null) {
				if(daoAsignacion.getAsignacion(nroProducto, codProducto, "admin@local.ed", connection) == null)
					daoAsignacion.insertAsignacion(new Asignacion("admin@local.ed",codProducto,nroProducto,0));
			}
			
			connection.close();
		}
		catch(SQLException e) {
			try { connection.close(); } catch(Exception ex) {}
			throw e;
		}
	}
	
	private WebApplicationException exception(Response.Status status, String mensaje) throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode node = mapper.createObjectNode();
		node.put("message", mensaje);
		throw new WebApplicationException(
				Response.status(status)
				.entity(mapper.writeValueAsString(node))
				.type(MediaType.APPLICATION_JSON)
				.build()
		);
	}
	
	
}
