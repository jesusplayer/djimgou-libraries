package com.djimgou.core.cooldto.processors;


import com.djimgou.core.cooldto.annotations.DtoIgnore;
import com.djimgou.core.util.AppUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public interface DtoAnotationProcessor<T extends Annotation> extends IDtoAnotationProcessor {

    void setFields(java.util.List<Field> fields);

    DtoClassProcessor getParent();

    default Class getAnnotation() {
        Class an = getGenericParamTypeFromInterface(this.getClass(), 0);
        return an;
    }

    default void init() {
        Objects.requireNonNull(getAnnotation(), "La classe annotation ne doit pas être vide");
        Stream<Field> s = AppUtils.getFieldsAsStream(getDto().getClass(), field -> field.isAnnotationPresent(getAnnotation()) && !field.isAnnotationPresent(DtoIgnore.class));
        this.setFields(s.collect(Collectors.toList()));
    }

    default List<Type> getGenericParamTypeFromInterface(Class classe) {
        ((ParameterizedType) classe.getGenericInterfaces()[0]).getActualTypeArguments();
        List<Type> list = (List) Arrays.asList(classe.getGenericInterfaces()).stream().flatMap((type) -> {
            return Stream.of(((ParameterizedType) type).getActualTypeArguments());
        }).collect(Collectors.toList());
        return list;
    }

    default Class getGenericParamTypeFromInterface(Class classe, int pos) {
        List<Type> list = getGenericParamTypeFromInterface(classe);
        Class cc = null;
        if (!list.isEmpty()) {
            cc = (Class) list.get(pos);
        }

        return cc;
    }
}
