package com.act.core.exception;

import com.act.core.model.BaseBdEntity;

import java.lang.reflect.Field;

public class DtoTargetClassMissMatchException extends DtoMappingException {
    public DtoTargetClassMissMatchException(String message) {
        super(message);
    }

    public DtoTargetClassMissMatchException(Object entity, Class targetClass) {
        super("Erreur de definition, entité " + entity.getClass().getName() +
                " Différent de target=" + targetClass.getName());
    }
}
