package com.act.core.testing.app;

import org.junit.platform.commons.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static org.junit.platform.commons.util.ReflectionUtils.findFields;

public class AppTestUtils {
    /**
     * Retourne les fils distinct d'une entité
     *
     * @param classe
     * @param predicate
     * @return
     */
    public static List<Field> getFields(Class classe, Predicate<Field> predicate) {
        return Arrays.asList(classe.getDeclaredFields()).stream().filter(predicate::test)
                .collect(Collectors.toList());
    }

    public static Optional<Field> getField(Object object, String fieldName) {
        List<Field> m = ReflectionUtils.findFields(object.getClass(), field -> Objects.equals(fieldName, field.getName()), ReflectionUtils.HierarchyTraversalMode.TOP_DOWN);
        return m.stream().findFirst();
    }
}
