package com.djimgou.core.cooldto.processors;

import com.djimgou.core.cooldto.annotations.DtoCollection;
import com.djimgou.core.cooldto.annotations.DtoCollectionId;
import com.djimgou.core.cooldto.exception.DtoBadPropertyTypeException;
import com.djimgou.core.cooldto.exception.DtoMappingException;
import com.djimgou.core.cooldto.exception.DtoTargetEntityNotFound;
import com.djimgou.core.util.AppUtils;
import com.djimgou.core.util.EntityRepository;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.BeanUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static com.djimgou.core.util.AppUtils.*;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
public class DtoCollectionIdProcessor implements DtoAnotationProcessor<DtoCollectionId> {
    Object dto;
    Object entity;
    List<Field> fields;

    private EntityRepository entityRepo;
    private DtoClassProcessor parent;

    public DtoCollectionIdProcessor(EntityRepository entityRepo, DtoClassProcessor parentProc) {
        this.entityRepo = entityRepo;
        this.parent = parentProc;
    }

    @Override
    public void init(Object dto, Object entity) {
        this.dto = dto;
        this.entity = entity;
        this.init();
    }

    @SneakyThrows
    @Override
    public final void dtoToEntity() throws DtoMappingException {
        for (Field field : fields) {

            DtoCollectionId an = field.getAnnotation(DtoCollectionId.class);
            String targetField = has(an.value()) ? an.value()[0] : field.getName();

            Object list = null;
            try {
                list = field.get(dto);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            if (list != null && list instanceof Collection && !((Collection)list).isEmpty()) {
                Class targetEntityClass = getDeepPropertyType(entity, targetField);
                final String entityIdName = entityRepo.getIdKey(getEntity().getClass());
                Object entityId = getDeepProperty(entity, entityIdName);
                boolean isNew = true;
                if (entityId != null) {
                    isNew = !entityRepo.findById(getEntity().getClass(), entityId).isPresent();
                }

                if (has(targetEntityClass)) {
                    // Si l'objet n'existe pas dans entity on le crée automatiquement
                    Collection targetObjValue = (Collection) getDeepProperty(entity, targetField);
                    Collection dtoColectionValue;
                    Class factory = ArrayList.class;
                    Collection toAdd = new ArrayList();
                    Collection idDtoEntities = new ArrayList();
                    Collection toRemove = new ArrayList();
                    Collection toKeep = new ArrayList();
                    Supplier suplierFactory = ArrayList::new;
                    if (list instanceof Set) {
                        factory = HashSet.class;
                        suplierFactory = HashSet::new;
                        toAdd = new HashSet();
                        toRemove = new HashSet();
                        toKeep = new HashSet();
                        idDtoEntities = new HashSet();
                    }

                    if (hasField(entity, targetField) && !has(targetObjValue)) {
                        targetObjValue = (Collection) BeanUtils.instantiateClass(factory);
                        setDeepProperty(entity, targetField, targetObjValue);
                    }
                    try {
                        dtoColectionValue = (Collection) field.get(dto);
                    } catch (IllegalAccessException e) {
                        throw new DtoMappingException(e.getMessage());
                    }
                    List<Field> targetInnerField = AppUtils.getFields(entity.getClass(), field1 -> field1.getName().equals(targetField));
                    Class targetInnerClass = null;
                    final Type[] actualTypeArgument = ((ParameterizedType) targetInnerField.get(0).getGenericType()).getActualTypeArguments();
                    if (has(actualTypeArgument)) {
                        targetInnerClass = (Class) actualTypeArgument[0];
                        if (!entityRepo.isManagedEntity(targetInnerClass)) {
                            throw new DtoTargetEntityNotFound("Cette entité " +
                                    (has(targetInnerClass) ? targetInnerClass.getSimpleName() : "") + " n'est pas persistante vérifiez si elle est annoté @Entity"
                            );
                        }
                    } else {

                        throw new DtoBadPropertyTypeException("@DtoFieldCollectionId: La collection " + entity.getClass()
                                + "." + targetField + " doit posséder une entité persistante comme paramètre générique de la forme Collection<Entite>");
                    }

                    if (/*targetObjValue.isEmpty() &&*/ !dtoColectionValue.isEmpty()) {
                        Class finalTargetInnerClass = targetInnerClass;
                        final String idKeyName = entityRepo.getIdKey(finalTargetInnerClass);

                        //targetObjValue = Arrays.asList(idDtoEntities.toArray());

                        loadByDtoIds(dtoColectionValue, idDtoEntities, finalTargetInnerClass);

                        if (targetObjValue != null) {
                            Method mAddAll = Collection.class.getMethod("addAll", Collection.class);

                            if (isNew) { // lors de la création de l'entité
                                mAddAll.invoke(getDeepProperty(entity, targetField), idDtoEntities);
                            } else {
                                Map idsTargetMap = (Map) targetObjValue.stream()
                                        .collect(Collectors.toMap(o -> getDeepProperty(o, idKeyName), Function.identity())
                                        );

                                Map idsSourceMap = (Map) idDtoEntities.stream()
                                        .collect(Collectors.toMap(o -> getDeepProperty(o, idKeyName), Function.identity())
                                        );
                                toAdd.clear();
                                for (Object dtoObj : dtoColectionValue) {
                                    if (!idsTargetMap.containsKey(dtoObj)) {
                                        toAdd.add(idsSourceMap.get(dtoObj));
                                    } else {
                                        toKeep.add(idsSourceMap.get(dtoObj));
                                    }
                                }

                                for (Object key : idsTargetMap.keySet()) {
                                    if (!idsSourceMap.containsKey(key)) {
                                        toRemove.add(idsTargetMap.get(key));
                                    }
                                }

                                Method mClear = Collection.class.getMethod("clear");
                                mClear.invoke(getDeepProperty(entity, targetField));

                                toAdd.addAll(toKeep);
                                mAddAll.invoke(getDeepProperty(entity, targetField), toAdd);

                            }


                        } else {
                            setDeepProperty(entity, targetField, idDtoEntities);
                        }


                    } else {
                        setDeepProperty(entity, targetField, suplierFactory.get());
                    }
                    //ManyToMany
                    // if( strategy = ADD_NEW
                    // if( strategy = ADD_NEW && an.persist()
                    // if( strategy = DELETE_ORPHANS
                    // if( strategy = DELETE_ORPHANS && an.persist()

                    //serialize(dtoColectionValue, targetObjValue);

                }
            } else {
                Object objArr = getDeepProperty(entity, targetField);
                if (objArr != null) {
                    if (objArr instanceof Collection) {
                        Method mClear = Collection.class.getMethod("clear");
                        mClear.invoke(getDeepProperty(entity, targetField));
                    } else {
                        throw new DtoBadPropertyTypeException("@DtoFieldCollectionId", field, null, "La propriété cible " + targetField +
                                " n'est pas de même type");
                    }

                }
            }
        }
    }

    private void loadByDtoIds(Collection dtoColectionValue, Collection idDtoEntities, Class finalTargetInnerClass) throws DtoTargetEntityNotFound {
        for (Object dtoId : dtoColectionValue) {
            Optional<Object> obToAdd = entityRepo.findById(finalTargetInnerClass, dtoId);
            idDtoEntities.add(obToAdd.orElseThrow(() -> new DtoTargetEntityNotFound("Cette entité " +
                    (has(finalTargetInnerClass) ? finalTargetInnerClass.getSimpleName() : "") + "#" + dtoId + " n'existe pas dans la base de donnée"
            )));
        }
    }
}
