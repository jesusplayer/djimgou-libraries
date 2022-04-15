package com.act.core.exception;

import com.act.core.model.BaseBdEntity;
import com.act.core.model.IEntityDto;

import java.lang.reflect.Field;

public class DtoNoDtoClassAnotationProvidedException extends DtoMappingException {
    public DtoNoDtoClassAnotationProvidedException(String message) {
        super(message);
    }

    public DtoNoDtoClassAnotationProvidedException(Object dto) {
        super("Aucune Annotation @DtoClass définie pour " +
                dto.getClass().getSimpleName() + ". Contactez le support"
        );
    }
}
