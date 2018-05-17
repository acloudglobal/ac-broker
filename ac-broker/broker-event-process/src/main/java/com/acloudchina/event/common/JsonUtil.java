package com.acloudchina.event.common;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class JsonUtil {

	private JsonUtil() {
	}

	public static final ObjectMapper mapper = new ObjectMapper();

	public static byte[] encode(Object obj) {
		try {
			return mapper.writeValueAsBytes(obj);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			throw new RuntimeException("encode failed");
		}
	}

	public static <T> T decode(byte[] bytes, Class<T> cls) {
		try {
			return mapper.readValue(bytes, cls);
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("decode failed");
		}
	}
	
	public static String objectToJson(Object object){

        try {
            return mapper.writeValueAsString(object);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("objectToJson error");
        }
    }
	
	 public static <T> T jsonToObject(String str, Class<T> cls){
	        try {
	            return mapper.readValue(str, cls);
	        } catch (Exception e) {
	            e.printStackTrace();
	            throw new RuntimeException("jsonToObject error");
	        }
	    }
}
