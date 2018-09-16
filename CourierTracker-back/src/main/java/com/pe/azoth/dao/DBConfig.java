package com.pe.azoth.dao;

import java.io.IOException;
import java.io.InputStreamReader;


import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pe.azoth.beans.DataBaseInfo;

public class DBConfig {
	private DataBaseInfo data;
	
	public DBConfig() throws JsonParseException, JsonMappingException, IOException {
		
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		InputStreamReader in = new InputStreamReader(classLoader.getResourceAsStream("db-config.json"),"utf-8");
		ObjectMapper object = new ObjectMapper();
		this.setData(object.readValue(in, DataBaseInfo.class));
		
	}
	public DataBaseInfo getData() {
		return data;
	}
	public void setData(DataBaseInfo data) {
		this.data = data;
	}
}
