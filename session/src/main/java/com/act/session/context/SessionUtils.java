package com.act.session.context;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.extern.log4j.Log4j2;

import javax.servlet.http.HttpSession;

@Log4j2
public class SessionUtils {
    public static String toJson(Object object) throws InvalidSessionDataSerialisationException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        //objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        try {
            objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new InvalidSessionDataSerialisationException();
        }
        return null;
    }
    public static void setAttribute(HttpSession session, String key, Object object) throws InvalidSessionDataSerialisationException{
        session.setAttribute(key, toJson(object));
    }
    public static Object getAttribute(HttpSession session, String key,  Class classe) throws InvalidSessionDataDeSerialisationException {
        String o = (String) session.getAttribute(key);
        return fromJson(o, classe);
    }
    public static Object fromJson(String content, Class classe) throws InvalidSessionDataDeSerialisationException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        //objectMapper.enable(SerializationFeature.INDENT_OUTPUT);

        try {
            return objectMapper.readValue(content, classe);
        } catch (JsonProcessingException e) {
            InvalidSessionDataDeSerialisationException ex = new InvalidSessionDataDeSerialisationException();
            log.error(ex.getMessage(), e);
            throw ex;
        }

    }
}
