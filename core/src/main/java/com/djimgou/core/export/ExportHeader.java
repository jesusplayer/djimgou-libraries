package com.djimgou.core.export;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExportHeader {
    String text;
    Method method;
    Field field;

    public ExportHeader(String text, Method method) {
        this.text = text;
        this.method = method;
    }

    public ExportHeader(String text, Field field) {
        this.text = text;
        this.field = field;
    }

    public ExportHeader(String text) {
        this.text = text;
    }

    public Object peekValue(Object object) {
        try {

            if (method != null) {
                return method.invoke(object);

            }
            if (field != null) {
                field.setAccessible(true);
                return field.get(object);
            }
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }
}
