package com.djimgou.core.cooldto.exception;

import java.lang.reflect.Field;

public class DtoBadPropertyValueException extends DtoMappingException {
    public DtoBadPropertyValueException(String message) {
        super(message);
    }

    public DtoBadPropertyValueException(String anotation, Field field, Object entity) {
        super("La propriété " + field.getName() +
                " ne doit pas être nulle car elle utilise l'annotation " + anotation+
                ". Elle doit  contenir l'identifiant de l'entité " +entity.getClass().getName()+
                " qui doit être chargée en BD automatiquement. "+
                ". Contactez le support"
        );
    }
}
