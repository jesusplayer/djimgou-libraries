package com.djimgou.core.coolvalidation.processors;

import com.djimgou.core.coolvalidation.annotations.Validations;
import com.djimgou.core.coolvalidation.annotations.Unique;
import com.djimgou.core.coolvalidation.exception.CoolValidationException;
import com.djimgou.core.util.AppUtils;
import com.djimgou.core.util.EntityRepository;
import com.djimgou.core.util.exception.NotManagedEntityException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Objects;

@Component
public class ValidationParserImpl implements ValidationParser {
    private EntityRepository er;

    @Autowired
    public ValidationParserImpl(EntityRepository er) {
        this.er = er;
    }

    public ValidationParserImpl(EntityManager em) {
        this(new EntityRepository(em));
        this.er.loadEntity();
    }

    @Transactional
    @Override
    public <T, ID> void validate(T entity) throws CoolValidationException {
        if (entity != null && entity.getClass().isAnnotationPresent(Validations.class)) {
            List<Field> fields = AppUtils.getFields(entity.getClass(), field -> field.isAnnotationPresent(Unique.class));
            Object id;
            String idName;
            try {
                idName = er.getIdKey(entity.getClass());
                id = AppUtils.getDeepProperty(entity, idName);
            } catch (NotManagedEntityException e) {

                throw new CoolValidationException(e.getMessage());
            }
            boolean isCreation = id == null;
            for (Field field : fields) {
                Unique an = field.getAnnotation(Unique.class);
                boolean exist = er.existBy(entity, field, an.ignoreCase());
                String message = an.message().isEmpty() ? String.format("The value of property %s is already exists in the database. it should be unique", field.getName()) : an.message();
                if (exist) {
                    if (isCreation) {
                        if (an.createMsg().length > 0) {
                            message = String.join("\n", an.createMsg());
                        }
                        throw new CoolValidationException(message);
                    } else {
                        List<?> oldEntity = er.findBy(entity, field, an.ignoreCase());
                        if (oldEntity != null && !oldEntity.isEmpty()) {
                            boolean isSame = oldEntity.stream().allMatch(e -> {
                                Object id2 = AppUtils.getDeepProperty(e, idName);
                                return Objects.equals(id2, id);
                            });
                            if (!isSame) {
                                if (an.updateMsg().length > 0) {
                                    message = String.join("\n", an.updateMsg());
                                }
                                // instruction très importante pour éviter dans le cadre d'une transaction
                                // d'effectuer une modification dans la BD si l'entité a été modifiée avant par le dev
                                // Spring automatiquement va essayer de mettre à jour la BD même si la methode save du repo n'a pas été appelée
                                this.er.getEm().detach(entity);
                                throw new CoolValidationException(message);
                            }
                        }

                    }

                }

            }
        }

    }
}
