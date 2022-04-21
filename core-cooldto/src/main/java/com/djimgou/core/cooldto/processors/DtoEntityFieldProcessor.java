package com.djimgou.core.cooldto.processors;

import com.djimgou.core.cooldto.exception.DtoBadPropertyTypeException;
import com.djimgou.core.cooldto.exception.DtoMappingException;
import com.djimgou.core.cooldto.exception.DtoTargetEntityNotFound;
import com.djimgou.core.cooldto.annotations.DtoEntityField;
import com.djimgou.core.util.EntityRepository;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.BeanUtils;

import java.lang.reflect.Field;
import java.util.List;

import static com.djimgou.core.util.AppUtils.*;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
public class DtoEntityFieldProcessor implements DtoAnotationProcessor<DtoEntityField> {
    private Object dto;
    private Object entity;
    private List<Field> fields;
    private EntityRepository entityRepo;
    private DtoClassProcessor parent;

    public DtoEntityFieldProcessor(EntityRepository entityRepo, DtoClassProcessor parentProc) {
        this.entityRepo = entityRepo;
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
            DtoEntityField an = field.getAnnotation(DtoEntityField.class);
            //Object targetEntity = getDeepProperty(entity, targetField);

            if (!field.getType().isArray()) {
                String targetField = has(an.value()) ? an.value()[0] : field.getName();
                Class targetEntityClass = getDeepPropertyType(entity, targetField);
                if (has(targetEntityClass)) {
                    // Si l'objet n'existe pas dans entity on le crée automatiquement
                    Object targetObjValue = getDeepProperty(entity, targetField);
                    Object dtoFieldValue;
                    if (hasField(entity, targetField) && !has(targetObjValue)) {
                        targetObjValue = BeanUtils.instantiateClass(targetEntityClass);
                        setDeepProperty(entity, targetField, targetObjValue);
                    }
                    try {
                        dtoFieldValue = field.get(dto);
                    } catch (IllegalAccessException e) {
                        throw new DtoMappingException(e.getMessage());
                    }
                    // serialize(dtoFieldValue, targetObjValue);
                    DtoClassProcessor proc = DtoProcessorfactory.newInstance(dtoFieldValue, targetObjValue, entityRepo);
                    proc.dtoToEntity();
                } else {
                    throw new DtoTargetEntityNotFound(dto, entity, field);
                }


            } else {
                throw new DtoBadPropertyTypeException("@DtoFieldEntity", field, null, "Un objet possédant l'anotation @DtoClass");
            }


        }
    }
}
