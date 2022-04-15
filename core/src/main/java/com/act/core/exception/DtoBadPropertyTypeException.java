package com.act.core.exception;

import com.act.core.model.BaseBdEntity;

import java.lang.reflect.Field;

import static com.act.core.util.AppUtils.has;

public class DtoBadPropertyTypeException extends DtoMappingException {
    public DtoBadPropertyTypeException(String message) {
        super(message);
    }

    public DtoBadPropertyTypeException(String anotation, Field field, String type, String expectType) {
        super("L'anotation " + anotation +
                " ne doit pas être placé sur la propriété " + field.getName() +
                " Car elle est de type " + (has(type) ? type : field.getType())+
                "est attendu un type "+expectType
                + ". Contactez le support"
        );
    }
}
