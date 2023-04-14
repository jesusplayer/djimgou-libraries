

package com.djimgou.core.annotations;

import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;



import java.lang.annotation.*;

/**
 * Annotation qui permet de faire une suppression logique d'une entité
 * @Entity
 * @FilterDef(name = "deleteFilter",
 *         parameters = {@ParamDef(name = "discriminator", type = "boolean")},
 *         defaultCondition = "deleted = :discriminator"
 * )
 * public class LogicalDeleEntity {
 *     @Column()
 *     String nom;
 *
 *     Boolean deleted;
 * }
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface LogicalDelete {
    /**
     * valeur du nom de la propriété de l'objet qui servira de discriminateur. cette propriété doit être de type Boolean ou un int
     *
     * @return
     */
    String value();

}
