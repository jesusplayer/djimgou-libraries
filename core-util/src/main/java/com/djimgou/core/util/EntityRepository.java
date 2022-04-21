package com.djimgou.core.util;

import lombok.Getter;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.Type;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.djimgou.core.util.AppUtils.has;

@Component
@Getter
public class EntityRepository {
    @PersistenceContext
    private EntityManager em;
    private Map<Class<?>, ? extends EntityType<?>> entityMap;

    @PostConstruct
    private void loadEntity() {
        if (!AppUtils.has(entityMap)) {
            entityMap = em.getMetamodel().getEntities()
                    .stream().collect(Collectors.toMap(Type::getJavaType, Function.identity()));
        }
    }

    public boolean isManagedEntity(Class targetEntityClass) {
        return entityMap.containsKey(targetEntityClass);
    }
}
