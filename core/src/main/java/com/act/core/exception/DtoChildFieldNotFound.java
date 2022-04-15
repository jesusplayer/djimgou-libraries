package com.act.core.exception;

import com.act.core.model.BaseBdEntity;
import com.act.core.model.IEntityDto;

import java.lang.reflect.Field;

public class DtoChildFieldNotFound extends DtoMappingException {
    public DtoChildFieldNotFound(String message) {
        super(message);
    }

    public DtoChildFieldNotFound(IEntityDto dto, BaseBdEntity entity, Field field) {
        super("Aucune correspondance pour " +
                dto.getClass().getSimpleName() + "." + field.getName()
                + " Dans " + entity.getClass().getSimpleName()+ ". Contactez le support"
        );
    }
}
