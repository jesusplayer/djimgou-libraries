package com.djimgou.core.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Permet de décrire un champ d'export de données
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ColumnExport {
    /**
     * Le nom de la colonne a affichée
     *
     * @return
     */
    String value();

    /**
     * L'ordre d'affichage de la colonne
     * @return
     */
    int order() default 0;
}
