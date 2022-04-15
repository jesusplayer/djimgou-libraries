package com.act.core.exception;

import java.lang.reflect.Field;

import static com.act.core.util.AppUtils.has;

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
