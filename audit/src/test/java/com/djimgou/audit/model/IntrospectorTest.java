package com.djimgou.audit.model;


import com.djimgou.audit.annotations.MyAnnotationIntrospector;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class IntrospectorTest {

    @DisplayName("@IgnoreOnAudit: marche proprement")
    @Test
    public void testIgnore() {
        Person p = new Person("Djimgou", "dany", 25, "CM");
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        objectMapper.setAnnotationIntrospector(new MyAnnotationIntrospector());
        try {
            String str = objectMapper.writeValueAsString(p);
            Person p2 = objectMapper.readValue(str, Person.class);
            assertEquals("CM", p.getPays());
            assertEquals(0, p2.getAge());
            assertNull(p2.getNom());
            assertNull(p2.getPrenom());
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }
}
