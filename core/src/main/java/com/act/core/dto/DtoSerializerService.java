package com.act.core.dto;

import com.act.core.exception.*;
import com.act.core.model.BaseBdEntity;
import com.act.core.model.IEntityDto;
import com.act.core.util.AppUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.metamodel.EntityType;
import java.lang.reflect.Field;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.act.core.util.AppUtils.*;
import static com.act.core.util.AppUtils.getFields;

@Service
public class DtoSerializerService {
    @PersistenceContext
    EntityManager em;

    public static Map<Class<?>, ? extends EntityType<?>> entityMap;

    public void serialize(Object dto, Object entity) throws DtoMappingException {
        loadEntity();
        if (dto.getClass().isAnnotationPresent(DtoClass.class)) {
            DtoClass dtoEntity = dto.getClass().getAnnotation(DtoClass.class);
            final Class[] targetClasses = dtoEntity.value();
            boolean hasTargetC = has(targetClasses);
            final Class target = hasTargetC ? targetClasses[0] : null;
            if (!hasTargetC || Objects.equals(target, entity.getClass())) {

                List<Field> toIgnoreField = new ArrayList<>();
                final List<Field> toDtoFields = getFields(dto.getClass(), field ->
                        field.isAnnotationPresent(DtoField.class)
                );
                toIgnoreField.addAll(toDtoFields);

                final List<Field> toDtoDbFields = getFields(dto.getClass(), field ->
                        field.isAnnotationPresent(DtoFieldDb.class)
                );
                toIgnoreField.addAll(toDtoDbFields);

                final List<Field> toDtoFieldsEntities = getFields(dto.getClass(), field ->
                        field.isAnnotationPresent(DtoFieldEntity.class)
                );
                toIgnoreField.addAll(toDtoFieldsEntities);

                List<String> toIgnore = toIgnoreField.stream().map(Field::getName).collect(Collectors.toList());

                BeanUtils.copyProperties(dto, entity, toIgnore.toArray(new String[0]));

                extractDtoFields(dto, entity, toDtoFields);
                extractDtoFieldsDb(dto, entity, toDtoDbFields);
                extractDtoFieldsEntity(dto, entity, toDtoFieldsEntities);

            } else {
                throw new DtoTargetClassMissMatchException(entity, target);
            }

            //buildEntityDto(dto,entity);
        } else {
            throw new DtoNoDtoClassAnotationProvidedException(dto);
        }
    }

    public final void extractDtoFields(Object dto, Object entity, List<Field> toSerialize) throws DtoMappingException {
        for (Field field : toSerialize) {
            DtoField an = field.getAnnotation(DtoField.class);
            String targetField = has(an.targetKey()) ? an.targetKey()[0] : field.getName();
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

    public final void extractDtoFieldsDb(Object dto, Object entity, List<Field> toSerialize) throws DtoMappingException {
        for (Field field : toSerialize) {
            DtoFieldDb an = field.getAnnotation(DtoFieldDb.class);
            String targetField = has(an.targetKey()) ? an.targetKey()[0] : field.getName();
            Class targetEntityClass = getDeepPropertyType(entity, targetField);
            Object dtoFieldValue;
            try {
                dtoFieldValue = field.get(dto);
            } catch (IllegalAccessException e) {
                throw new DtoMappingException(e.getMessage());
            }
            if (entityMap.containsKey(targetEntityClass)) {
                if (has(dtoFieldValue)) {
                    Object targetEntity = em.find(targetEntityClass, dtoFieldValue);
                    setDeepProperty(entity, targetField, targetEntity);
                } else {
                    if (an.nullable()) {
                        setDeepProperty(entity, targetField, null);
                    } else {
                        throw new DtoBadPropertyValueException("@DtoFieldDb", field, entity);
                    }
                }
            } else {
                throw new DtoTargetEntityNotFound("Cette entité " +
                        (has(targetEntityClass) ? targetEntityClass.getSimpleName() : "") + " n'est pas persistante vérifiez si " + targetField + " existe"
                );
            }
        }
    }

    public final void extractDtoFieldsEntity(Object dto, Object entity, List<Field> toSerialize) throws DtoMappingException {
        for (Field field : toSerialize) {
            DtoFieldEntity an = field.getAnnotation(DtoFieldEntity.class);
            //Object targetEntity = getDeepProperty(entity, targetField);

            if (!field.getType().isArray()) {
                String targetField = has(an.targetKey()) ? an.targetKey()[0] : field.getName();
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
                    serialize(dtoFieldValue, targetObjValue);
                } else {
                    throw new DtoTargetEntityNotFound(dto, entity, field);
                }


            } else {
                throw new DtoBadPropertyTypeException("@DtoFieldEntity", field, null, "Un objet possédant l'anotation @DtoClass");
            }


        }
    }


    private void loadEntity() {
        if (!has(entityMap)) {
            entityMap = em.getMetamodel().getEntities()
                    .stream().collect(Collectors.toMap(o -> o.getJavaType(), Function.identity()));
        }
    }

    public final void injectReferencedField(UUID id, IEntityDto entityDto, BaseBdEntity entity, Predicate<Field> fieldFilter) throws NotFoundException, DtoChildFieldNotFound {
        if (!has(fieldFilter)) {
            fieldFilter = (t) -> true;
        }

        List<Field> dtoIdFields = getFields(
                entityDto.getClass(), fieldFilter.and(f -> Objects.equals(f.getType().getName(), UUID.class.getName())
                        && !Objects.equals("id", f.getName())
                        && f.getName().endsWith("Id")
                )

        );

        for (Field field : dtoIdFields) {
            UUID dtoId = (UUID) AppUtils.getField(field.getName(), entityDto);

            String objName1 = field.getName();
            String objName2 = field.getName().substring(0, field.getName().length() - 2);
            String objName3 = field.getName() + "Ob";

            String choosedKey = objName2;

            boolean childOb1 = AppUtils.hasField(entity.getClass(), objName1);
            boolean childOb2 = AppUtils.hasField(entity.getClass(), objName2);
            boolean childOb3 = AppUtils.hasField(entity.getClass(), objName3);
            // lordre est important
            if (childOb3) {
                choosedKey = objName3;
            } else {
                if (childOb1) {
                    choosedKey = objName1;
                }
            }

            if (childOb1 || childOb2 || childOb3) {
                Object childObj = AppUtils.getField(choosedKey, entity);
                String finalChoosedKey = choosedKey;

                UUID childObId = null;
                if (has(childObj)) {
                    childObId = (UUID) AppUtils.getField("id", childObj);
                }
                final boolean existChildId = has(id) && Objects.equals(childObId, dtoId);
                if (!existChildId) {
                    Field childObjField = getFields(entity.getClass(), field1 ->
                            Objects.equals(finalChoosedKey, field1.getName()))
                            .stream().findFirst().orElse(null);

                    Class key = childObjField.getType();
                    Object childDbValue = em.find(key, dtoId);
                    if (childDbValue == null) {
                        throw new NotFoundException(key.getSimpleName() + "#" +
                                id + " N'existe pas"
                        );
                    }
                    AppUtils.setField(choosedKey, entity, childDbValue);
                }
            } else {
                throw new DtoChildFieldNotFound(entityDto, entity, field);
            }

        }
    }
}
