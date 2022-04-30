package com.djimgou.core.cooldto.processors;

import com.djimgou.core.cooldto.annotations.DtoFkId;
import com.djimgou.core.cooldto.exception.DtoBadPropertyValueException;
import com.djimgou.core.cooldto.exception.DtoMappingException;
import com.djimgou.core.cooldto.exception.DtoTargetEntityNotFound;
import com.djimgou.core.util.EntityRepository;
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
public class DtoFkIdProcessor implements DtoAnotationProcessor<DtoFkId> {
    private Object dto;
    private Object entity;
    private List<Field> fields;

    private EntityRepository entityRepo;

    private DtoClassProcessor parent;

    public DtoFkIdProcessor(EntityRepository entityRepo, DtoClassProcessor parentProc) {
        this.entityRepo = entityRepo;
        this.parent = parentProc;
    }

    /**
     *
     * @param dto
     * @param entity
     */
    @Override
    public void init(Object dto, Object entity) {
        this.dto = dto;
        this.entity = entity;
        this.init();
    }

    @Override
    public final void dtoToEntity() throws DtoMappingException {
        for (Field field : fields) {
            DtoFkId an = field.getAnnotation(DtoFkId.class);
            String targetField = has(an.value()) ? an.value()[0] : field.getName();

            Class targetEntityClass = getDeepPropertyType(entity, targetField);
            Object dtoFieldValue;
            try {
                dtoFieldValue = field.get(dto);
            } catch (IllegalAccessException e) {
                throw new DtoMappingException(e.getMessage());
            }
            if (entityRepo.isManagedEntity(targetEntityClass)) {
                if (has(dtoFieldValue)) {
                    Object targetEntity = entityRepo.getEm().find(targetEntityClass, dtoFieldValue);
                    setDeepProperty(entity, targetField, targetEntity);
                } else {
                    if (an.nullable()) {
                        setDeepProperty(entity, targetField, null);
                    } else {
                        throw new DtoBadPropertyValueException("@DtoFieldFkId", field, entity);
                    }
                }
            } else {
                throw new DtoTargetEntityNotFound("Cette entité " +
                        (has(targetEntityClass) ? targetEntityClass.getSimpleName() : "") + " n'est pas persistante vérifiez si " + targetField + " existe"
                );
            }
        }
    }
}
