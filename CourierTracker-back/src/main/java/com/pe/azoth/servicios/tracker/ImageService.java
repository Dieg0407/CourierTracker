package com.pe.azoth.servicios.tracker;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

import javax.naming.NamingException;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
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
			throw this.exception(Response.Status.INTERNAL_SERVER_ERROR, "Hubo un error en la Base de Datos");
		} 
		catch (NamingException e) {
			e.printStackTrace(System.err);
			throw this.exception(Response.Status.INTERNAL_SERVER_ERROR, 
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
			throw this.exception(Response.Status.INTERNAL_SERVER_ERROR, "Hubo un error en la Base de Datos");
		} 
		catch (NamingException e) {
			e.printStackTrace(System.err);
			throw this.exception(Response.Status.INTERNAL_SERVER_ERROR, 
					"Hubo un error en la configuración del servidor, Informar al proveedor");
		}
	}

	@POST
	@Path("/putImage/{codigo}/{numero}")
	@Consumes(MediaType.MULTIPART_FORM_DATA+";boundary=" + "*****")
	public Response putImage(
		@PathParam("codigo") String codigo, 
		@PathParam("numero") int numero ,
		@DefaultValue("true") @FormDataParam("enabled") boolean enabled,
        @FormDataParam("file") InputStream uploadedInputStream,
        @FormDataParam("file") FormDataContentDisposition fileDetail) {
		
		
		System.err.println("ENTRO AL SERVICIO");
		
		try {
			ByteArrayOutputStream buffer = new ByteArrayOutputStream();

			int nRead;
			byte[] data = new byte[16384];

			while ((nRead = uploadedInputStream.read(data, 0, data.length)) != -1) {
			  buffer.write(data, 0, nRead);
			}

			buffer.flush();
			
			byte[] total = buffer.toByteArray();
			buffer.close();
			
			Imagen imagen = new Imagen();
			imagen.setCodigo(codigo);
			imagen.setNumero(numero);
			imagen.setImagen(total);
			
			DaoImagen dao = new DaoImagenImpl();
			dao.addImagen(imagen);
			return Response.ok().build();
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
			throw this.exception(Response.Status.INTERNAL_SERVER_ERROR, "Hubo un error en la Base de Datos");
		} 
		catch (NamingException e) {
			e.printStackTrace(System.err);
			throw this.exception(Response.Status.INTERNAL_SERVER_ERROR, 
					"Hubo un error en la configuración del servidor, Informar al proveedor");
		}
		
	}

	@GET
	@Path("/getNumberImages")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public Response getNumberImages(
			@QueryParam("codigo") String codigo,
			@QueryParam("numero") int numero ){
		ObjectMapper mapper = new ObjectMapper();
		try {

			DaoImagen dao = new DaoImagenImpl();
			List<Integer> ids = dao.listImagenes(codigo, numero)
					.stream()
					.map(img -> img.getId())
					.collect(Collectors.toList());
			
			ObjectNode response = mapper.createObjectNode();
			ArrayNode array = mapper.valueToTree(ids);
			response.putArray("ids").addAll(array);
			
			return Response.ok().entity(response).build();
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
			throw this.exception(Response.Status.INTERNAL_SERVER_ERROR, "Hubo un error en la Base de Datos");
		} 
		catch (NamingException e) {
			e.printStackTrace(System.err);
			throw this.exception(Response.Status.INTERNAL_SERVER_ERROR, 
					"Hubo un error en la configuración del servidor, Informar al proveedor");
		}
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