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
            if (has(list)) {
                if (list instanceof Collection) {
                    Class targetEntityClass = getDeepPropertyType(entity, targetField);
                    if (has(targetEntityClass)) {
                        // Si l'objet n'existe pas dans entity on le crée automatiquement
                        Collection targetObjValue = (Collection) getDeepProperty(entity, targetField);
                        Collection dtoColectionValue;
                        Class factory = ArrayList.class;
                        Collection toAdd = new ArrayList();
                        Collection toRemove = new ArrayList();
                        Collection toUpdate = new ArrayList();
                        Supplier suplierFactory = ArrayList::new;
                        if (list instanceof Set) {
                            factory = HashSet.class;
                            suplierFactory = HashSet::new;
                            toAdd = new HashSet();
                            toRemove = new HashSet();
                            toUpdate = new HashSet();
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
                            targetObjValue = (Collection) dtoColectionValue.stream().map(o -> {
                                Object cc = BeanUtils.instantiateClass(finalTargetInnerClass);
                                //BeanUtils.copyProperties(o, cc);
                                return cc;
                            }).collect(Collectors.toCollection(suplierFactory));

                            if (targetObjValue != null) {
                                final String idKeyName = entityRepo.getIdKey(finalTargetInnerClass);

                                Map idsTargetMap = (Map) targetObjValue.stream()
                                        .collect(Collectors.toMap(o -> getDeepProperty(o, idKeyName), Function.identity())
                                );

                                for (Object dtoObj : dtoColectionValue) {
                                    if (!idsTargetMap.containsKey(dtoObj)) {
                                        toAdd.add(dtoObj);
                                    }else{
                                        toUpdate.add(dtoObj);
                                    }
                                }

                                for (Object key : idsTargetMap.keySet()) {
                                    if(!dtoColectionValue.contains(key)){
                                        toRemove.add(key);
                                    }
                                }

                            }


                        }
                        //ManyToMany
                        // if( strategy = ADD_NEW
                        // if( strategy = ADD_NEW && an.persist()
                        // if( strategy = DELETE_ORPHANS
                        // if( strategy = DELETE_ORPHANS && an.persist()

                        //serialize(dtoColectionValue, targetObjValue);

                    }
                } else {
                    throw new DtoBadPropertyTypeException("@DtoFieldCollectionId", field, null, "Un objet possédant l'anotation @DtoFieldCollection");
                }

            }
        }
    }
}
