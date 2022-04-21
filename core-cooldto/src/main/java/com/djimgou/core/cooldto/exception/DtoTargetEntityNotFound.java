package com.djimgou.core.cooldto.exception;

import java.lang.reflect.Field;

public class DtoTargetEntityNotFound extends DtoMappingException {
    public DtoTargetEntityNotFound(String message) {
        super(message);
    }

    public DtoTargetEntityNotFound(Object dto, Object entity, Field field) {
        super("Aucun targetEntity défini pour le serilizer" +
                dto.getClass().getSimpleName() + "." + field.getName()
                + " Dans " + entity.getClass().getSimpleName()+ ". Contactez le support"
        );
    }
}
