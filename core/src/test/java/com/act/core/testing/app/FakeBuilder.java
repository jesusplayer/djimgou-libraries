/*
 * Copyright (c) 2022. Créé par DJIMGOU NKENNE Dany
 */

package com.act.core.testing.app;

import com.github.javafaker.Faker;
import org.assertj.core.util.DateUtil;
import org.junit.platform.commons.util.ReflectionUtils;
import org.springframework.test.util.ReflectionTestUtils;

import java.lang.reflect.Field;
import java.util.*;

import static org.springframework.test.util.ReflectionTestUtils.setField;

public class FakeBuilder {
    public static Faker faker = new Faker(new Locale("fr"));

    public static <T> T fake(Class<T> classe) {
        T object = ReflectionUtils.newInstance(classe);
        fake(object);
        return object;
    }

    public static void fake(Object object) {
        List<Field> var2 = ReflectionUtils.findFields(object.getClass(), field -> true, ReflectionUtils.HierarchyTraversalMode.TOP_DOWN);

        for (Field m : var2) {
            final String name = m.getName();

            Field f = AppTestUtils.getField(object, name).orElse(null);
            if (f.getType().isEnum()) {

            }
            final String typeName = f.getType().getName();
            if (Objects.equals(typeName, UUID.class.getName())) {
                if (!name.equals("id")) {

                }
            }
            if (Objects.equals(typeName, String.class.getName())) {
                if (name.startsWith("nom") || name.startsWith("nam")) {
                    ReflectionTestUtils.setField(object, name, faker.name().lastName());
                } else if (name.startsWith("prenom") || name.startsWith("lastN")) {
                    ReflectionTestUtils.setField(object, name, faker.name().firstName());
                } else if (name.contains("email")) {
                    ReflectionTestUtils.setField(object, name, faker.internet().emailAddress());
                } else if (name.contains("code")) {
                    ReflectionTestUtils.setField(object, name, faker.code().asin());
                } else {
                    ReflectionTestUtils.setField(object, name, faker.artist().name());
                }
            }
            if (Objects.equals(typeName, Double.class.getName()) || (Objects.equals(typeName, "double"))) {
                ReflectionTestUtils.setField(object, name, faker.number().randomDouble(3, 10, Integer.MAX_VALUE));
            }
            if (Objects.equals(typeName, Long.class.getName()) || (Objects.equals(typeName, "long"))) {
                ReflectionTestUtils.setField(object, name, faker.number().numberBetween(10L, Long.MAX_VALUE));
            }
            if (Objects.equals(typeName, Integer.class.getName()) || (Objects.equals(typeName, "int"))) {
                ReflectionTestUtils.setField(object, name, faker.number().numberBetween(2, 100000000));
            }
            if (Objects.equals(typeName, Date.class.getName())) {
                ReflectionTestUtils.setField(object, name, DateUtil.now());
            }
        }
    }

}
