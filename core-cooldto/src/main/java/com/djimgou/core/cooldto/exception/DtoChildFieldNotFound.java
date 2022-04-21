package com.djimgou.core.cooldto.exception;

import com.djimgou.core.cooldto.model.IEntityDto;

import java.lang.reflect.Field;

public class DtoChildFieldNotFound extends DtoMappingException {
    public DtoChildFieldNotFound(String message) {
        super(message);
    }

    public DtoChildFieldNotFound(IEntityDto dto, Object entity, Field field) {
        super("Aucune correspondance pour " +
                dto.getClass().getSimpleName() + "." + field.getName()
                + " Dans " + entity.getClass().getSimpleName()+ ". Contactez le support"
        );
    }
}
