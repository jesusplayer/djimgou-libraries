package com.djimgou.core.cooldto.processors;

import com.djimgou.core.cooldto.annotations.Dto;
import com.djimgou.core.cooldto.exception.DtoMappingException;
import com.djimgou.core.cooldto.exception.DtoNoDtoClassAnotationProvidedException;
import com.djimgou.core.cooldto.exception.DtoTargetClassMissMatchException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.BeanUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.djimgou.core.util.AppUtils.has;


@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
public class DtoClassProcessor implements IDtoAnotationProcessor {
    Object dto;
    Object entity;
    List<IDtoAnotationProcessor> processors;
    Class targetClass;

    public DtoClassProcessor(Object dto, Object entity) {
        init(dto, entity);
        this.processors = new ArrayList<>();
    }

    @Override
    public void init(Object dto, Object entity) {
        this.dto = dto;
        this.entity = entity;
    }

    @Override
    public List<Field> getFields() {
        return this.processors.stream().flatMap(processor -> processor.getFields().stream())
                .filter(field -> !ReflectionUtils.isPublicStaticFinal(field))
                .collect(Collectors.toList());
    }

    public void add(IDtoAnotationProcessor processor) {
        this.processors.add(processor);
    }

    public Dto getAnnotation() {
        if (dto.getClass().isAnnotationPresent(Dto.class)) {
            return dto.getClass().getAnnotation(Dto.class);
        }
        return null;
    }

    public void checkErrors() throws DtoTargetClassMissMatchException, DtoNoDtoClassAnotationProvidedException {
        if (dto.getClass().isAnnotationPresent(Dto.class)) {
            Dto dtoEntity = dto.getClass().getAnnotation(Dto.class);
            final Class[] targetClasses = dtoEntity.value();
            boolean hasTargetC = has(targetClasses);
            Class target = hasTargetC ? targetClasses[0] : null;

            if (hasTargetC) {
                if (has(entity)) {
                    if (!Objects.equals(target, entity.getClass())) {
                        throw new DtoTargetClassMissMatchException(entity, target);
                    }
                }
            } else {
                if (!has(entity)) {
                    throw new DtoTargetClassMissMatchException("Impossible de d'extraire le Dto" +
                            dto +
                            ", car la classe et l'objet cible sont nulles ");
                } else {
                    target = entity.getClass();
                }
            }
            setTargetClass(target);
        } else {
            throw new DtoNoDtoClassAnotationProvidedException(dto);
        }
    }

    @Override
    public void dtoToEntity() throws DtoMappingException {
        checkErrors();
        processors.forEach(processor -> processor.init(dto, entity));
        extractData();
    }

    private void extractData() throws DtoMappingException {
        List<Field> toIgnoreField = new ArrayList<>();
        for (IDtoAnotationProcessor processor : processors) {
            toIgnoreField.addAll(processor.getFields());
        }
        List<String> toIgnore = toIgnoreField.stream().map(Field::getName).collect(Collectors.toList());

        BeanUtils.copyProperties(dto, entity, toIgnore.toArray(new String[0]));

        DtoIdProcessor idProc = (DtoIdProcessor) processors.stream().filter(iDtoAnotationProcessor ->
                iDtoAnotationProcessor instanceof DtoIdProcessor
        ).findFirst().get();

        if (idProc.isReadOnlyStrategy()) {
            // si la stratégie est un Id readonly alors on execute le processeur
            // et on s'arrête
            idProc.dtoToEntity();
            return;
        }

        for (IDtoAnotationProcessor processor : processors) {
            processor.dtoToEntity();
        }
    }


}
