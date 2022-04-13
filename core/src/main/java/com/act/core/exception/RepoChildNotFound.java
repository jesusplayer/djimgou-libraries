package com.act.core.exception;

import com.act.core.model.BaseBdEntity;
import com.act.core.model.IEntityDto;

import java.lang.reflect.Field;

public class RepoChildNotFound extends Exception {
    public RepoChildNotFound(String message) {
        super(message);
    }

    public RepoChildNotFound(IEntityDto dto, BaseBdEntity entity, Field field) {
        super("Aucun Repository fils défini pour prendre en charge l'enregistrement automatique de " +
                dto.getClass().getSimpleName() + "." + field.getName()
                + " Dans " + entity.getClass().getSimpleName() + ". Contactez le support"
        );
    }
}
