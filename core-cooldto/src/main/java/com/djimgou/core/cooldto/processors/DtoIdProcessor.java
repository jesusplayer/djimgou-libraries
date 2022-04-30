package com.djimgou.core.cooldto.processors;

import com.djimgou.core.cooldto.annotations.DtoFieldIdStrategyType;
import com.djimgou.core.cooldto.annotations.DtoId;
import com.djimgou.core.cooldto.exception.DtoBadPropertyValueException;
import com.djimgou.core.cooldto.exception.DtoFieldNotFoundException;
import com.djimgou.core.cooldto.exception.DtoMappingException;
import com.djimgou.core.cooldto.exception.DtoTargetEntityNotFound;
import com.djimgou.core.util.EntityRepository;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.BeanUtils;

import java.lang.reflect.Field;
import java.util.List;

import static com.djimgou.core.util.AppUtils.has;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
public class DtoIdProcessor implements DtoAnotationProcessor<DtoId> {
    private Object dto;
    private Object entity;
    private List<Field> fields;

    private EntityRepository entityRepo;

    private DtoClassProcessor parent;

    public DtoIdProcessor(EntityRepository entityRepo, DtoClassProcessor parentProc) {
        this.entityRepo = entityRepo;
        this.parent = parentProc;
    }

    /**
     * @param dto
     * @param entity
     */
    @Override
    public void init(Object dto, Object entity) {
        this.dto = dto;
        this.entity = entity;
        this.init();
    }

    public boolean isReadOnlyStrategy() {
        if (has(fields)) {
            DtoId an = fields.get(0).getAnnotation(DtoId.class);
            return has(an.strategy()) && DtoFieldIdStrategyType.READONLY.equals(an.strategy()[0]);
        }
        return false;
    }

    public boolean isUpdateStrategy() {
        if (has(fields)) {
            DtoId an = fields.get(0).getAnnotation(DtoId.class);
            return has(an.strategy()) && DtoFieldIdStrategyType.UPDATE.equals(an.strategy()[0]);
        }
        return false;
    }

    @Override
    public final void dtoToEntity() throws DtoMappingException {
        if (fields.size() > 1) {
            throw new DtoMappingException("Erreurs plusieurs propriétes sont annoté @DtoFieldId dans la classe " +
                    dto.getClass().getName() + " Il ne doit Avoir qu'une seule"
            );
        }
        for (Field field : fields) {
            DtoId an = field.getAnnotation(DtoId.class);

            String targetField = field.getName();

            Class targetEntityClass = parent.getTargetClass();

            Object dtoFieldValue;
            try {
                dtoFieldValue = field.get(dto);
            } catch (IllegalAccessException e) {
                throw new DtoMappingException(e.getMessage());
            }
            if (entityRepo.isManagedEntity(targetEntityClass)) {
                if (has(dtoFieldValue)) {
                    Object targetEntity = entityRepo.getEm().find(targetEntityClass, dtoFieldValue);
                    if (!has(targetEntity)) {
                        if (!an.nullable()) {
                            throw new DtoFieldNotFoundException(targetEntityClass.getSimpleName() + "#" + (dtoFieldValue) + " est inexistant");
                        }
                    } else {
                        BeanUtils.copyProperties(targetEntity, entity);
                        if (isUpdateStrategy()) {
                            BeanUtils.copyProperties(dto, entity);
                        }
                    }
                } else {
                    if (!an.nullable()) {
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
