package com.djimgou.core.cooldto.processors;

import com.djimgou.core.cooldto.exception.DtoMappingException;
import com.djimgou.core.cooldto.exception.DtoTargetEntityNotFound;
import com.djimgou.core.cooldto.annotations.DtoField;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.lang.reflect.Field;
import java.util.List;

import static com.djimgou.core.util.AppUtils.*;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
public class DtoFieldProcessor implements DtoAnotationProcessor<DtoField> {
    Object dto;
    Object entity;
    List<Field> fields;
    private DtoClassProcessor parent;

    public DtoFieldProcessor(DtoClassProcessor parentProc) {
        this.parent = parentProc;
    }

    @Override
    public void init(Object dto, Object entity) {
        this.dto = dto;
        this.entity = entity;
        this.init();
    }

    @Override
    public final void dtoToEntity() throws DtoMappingException {
        for (Field field : fields) {
            DtoField an = field.getAnnotation(DtoField.class);
            String targetField = has(an.value()) ? an.value()[0] : field.getName();
            Object dtoFieldValue = null;
            try {
                dtoFieldValue = field.get(dto);
            } catch (IllegalAccessException e) {
                throw new DtoMappingException(e.getMessage());
            }
            if (hasField(entity, targetField)) {
                setDeepProperty(entity, targetField, dtoFieldValue);
            } else {
                throw new DtoTargetEntityNotFound("La propriété " + targetField +
                        " N'existe pas dans " + entity.getClass().getName());
            }
        }
    }
}
