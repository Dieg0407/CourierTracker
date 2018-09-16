package com.pe.azoth.modelo;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.Key;
import java.sql.SQLException;
import java.util.Base64;

import javax.crypto.spec.SecretKeySpec;
import javax.naming.NamingException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pe.azoth.beans.Usuario;
import com.pe.azoth.dao.DaoUsuario;
import com.pe.azoth.dao.DaoUsuarioImpl;

import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

public class JWTManager{
	
	public static void main(String[] args) throws Exception{
		JWTManager manager = new JWTManager();
		ObjectMapper mapper = new ObjectMapper();
		
		String jwt = manager.createJWT("admin", "administrador123456789");
		
		if(jwt != null) {
			Usuario usr = manager.parseJWT(jwt);
			
			System.out.println(mapper.writeValueAsString(usr));
		}
		else {
			System.out.println("Credenciales no validas");
		}
	}	
	
	//ESTO SE TIENE QUE PONER EN LA BD HASHEADO
	private static final String tokenKey = "ct-12344321-7894564512345adsadasdadsadasdxczcaxczcxzcsdacscsdcsdc";
		
	private Key key;
	public JWTManager() {
		try {
			SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
			byte[] apiKeySecretBytes;
			apiKeySecretBytes = tokenKey.getBytes("UTF-8");
			key = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());
			
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
	
	public String createJWT(String user, String pass) throws JsonParseException, JsonMappingException, IOException,
				NullPointerException, SQLException, NamingException  {
		
		DaoUsuario dao = new DaoUsuarioImpl();
		Usuario usr = dao.getUsuario(user, pass);
		
		if(usr != null) {
			
			JwtBuilder builder = Jwts.builder();
			//SETTING THE HEADERS
			builder.setHeaderParam("typ","JWT");
			//SETTING THE CLAIMS
			builder.setPayload(new ObjectMapper().writeValueAsString(usr));		
			
			builder.signWith(this.key);
		
			return builder.compact();
		}
		else
			return null;
		
	}
	
	public Usuario parseJWT(String jwtString) throws JsonParseException, JsonMappingException, IOException, SQLException, NamingException {
		
		JwtParser parser = Jwts.parser();
		parser.setSigningKey(key);
		
		if(parser.isSigned(jwtString)) {
			DaoUsuario dao = new DaoUsuarioImpl();
			
			ObjectMapper mapper = new ObjectMapper();
			String base64 = jwtString.split("\\.")[1];
			
			String usrJson = new String(Base64.getDecoder().decode(base64.getBytes("utf-8")),"utf-8");
			
			Usuario usr = mapper.readValue(usrJson , Usuario.class);
			
			if(dao.isActivo(usr.getCorreo()))
				return usr;
			else
				return null;
			
		}
		else 
			return null;
	}
	
}