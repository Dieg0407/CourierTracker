package com.pe.azoth.servicios.tracker;

import java.io.IOException;
import java.sql.SQLException;

import javax.naming.NamingException;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.pe.azoth.beans.Imagen;
import com.pe.azoth.dao.DaoImagen;
import com.pe.azoth.dao.DaoImagenImpl;

@Path("/img")
public class ImageService{

	@POST
	@Path("/deleteImage")
	@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public Response deleteImage(String jsonRequest){
		ObjectMapper mapper = new ObjectMapper();
		
		try {
			JsonNode body = mapper.readTree(jsonRequest);
			int id = body.get("id").asInt();
			DaoImagen dao = new DaoImagenImpl();
			int resultados = dao.deleteImagen(id);
			
			if(resultados == 1)
				return Response.ok().build();
			else
				return Response.notModified().build();
		} 
		catch (JsonProcessingException e) {
			e.printStackTrace(System.err);
			throw this.exception(Response.Status.BAD_REQUEST, "Hubieron problemas procesando la petición al servidor");
		} 
		catch (IOException e) {
			e.printStackTrace(System.err);
			throw this.exception(Response.Status.BAD_REQUEST, "Hubieron problemas procesando la petición al servidor");
		} 
		catch (SQLException e) {
			e.printStackTrace(System.err);
			throw this.exception(Response.Status.BAD_REQUEST, "Hubo un error en la Base de Datos");
		} 
		catch (NamingException e) {
			e.printStackTrace(System.err);
			throw this.exception(Response.Status.BAD_REQUEST, 
					"Hubo un error en la configuración del servidor, Informar al proveedor");
		}
	}

	@GET
	@Path("/getImage")
	@Produces("image/jpg")
	public Response getImage(
		@QueryParam("id") int id){
		
		try {
			DaoImagen dao = new DaoImagenImpl();
			Imagen img = dao.getImagen(id);
			
			if(img == null)
				return Response.status(Response.Status.NOT_FOUND).build();
			else
				return Response
						.ok(img.getImagen(),"image/jpg")
						.header("Inline", "filename=\""+ 
								String.format("%s-%d-%d.jpg", img.getCodigo(),img.getNumero(),img.getId())+"\"")
						.build();
			
		} 
		catch (JsonProcessingException e) {
			e.printStackTrace(System.err);throw this.exception(Response.Status.BAD_REQUEST, 
					"Hubo un error en la configuración del servidor, Informar al proveedor");
		} 
		catch (IOException e) {
			e.printStackTrace(System.err);throw this.exception(Response.Status.BAD_REQUEST, 
					"Hubo un error en la configuración del servidor, Informar al proveedor");
		} 
		catch (SQLException e) {
			e.printStackTrace(System.err);
			throw this.exception(Response.Status.BAD_REQUEST, "Hubo un error en la Base de Datos");
		} 
		catch (NamingException e) {
			e.printStackTrace(System.err);
			throw this.exception(Response.Status.BAD_REQUEST, 
					"Hubo un error en la configuración del servidor, Informar al proveedor");
		}
	}

	@POST
	@Path("/putImage/{codigo}/{numero}")
	@Consumes({ "image/jpg", "image/png" })
	public Response putImage(byte[] img, 
		@PathParam("codigo") String codigo, 
		@PathParam("numero") int numero ){
		
		Imagen imagen = new Imagen();
		imagen.setCodigo(codigo);
		imagen.setNumero(numero);
		imagen.setImagen(img);
		
		DaoImagen dao = new DaoImagenImpl();
		return null;
	}

	@GET
	@Path("/getNumberImages")
	@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public Response getNumberImages(String jsonRequest){
		return null;
	}
	
	private WebApplicationException exception(Response.Status status, String mensaje) {
		
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode node = mapper.createObjectNode();
		node.put("message", mensaje);
		throw new WebApplicationException(
				Response.status(status)
				.entity(node)
				.type(MediaType.APPLICATION_JSON)
				.build()
		);
	}
}