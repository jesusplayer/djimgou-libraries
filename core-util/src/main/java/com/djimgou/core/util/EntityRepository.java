package com.djimgou.core.util;

import com.djimgou.core.util.exception.NotManagedEntityException;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.persistence.*;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.Type;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.djimgou.core.util.AppUtils.getField;
import static com.djimgou.core.util.AppUtils.has;

@Component
@Getter@Setter
public class EntityRepository {
    @PersistenceContext
    private EntityManager em;
    private Map<Class<?>, ? extends EntityType<?>> entityMap;

    public EntityRepository(EntityManager em) {
        this.em = em;
    }

    @PostConstruct
    public void loadEntity() {
        if (!AppUtils.has(entityMap)) {
            entityMap = em.getMetamodel().getEntities()
                    .stream().collect(Collectors.toMap(Type::getJavaType, Function.identity()));
        }
    }

    public <T, ID> T find(Class<T> classe, ID id) {
        return em.find(classe, id);
    }

    public <T> List<?> findBy(T object, Field field, boolean ignoreCase) {
        Object o = null;

        field.setAccessible(true);
        try {
            o = field.get(object);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        final String key = "t." + field.getName();
        final String leftCond = ignoreCase ? " LOWER(" + key + ")" : key;
        final String rightCond = ignoreCase ? "LOWER(:val)" : ":val";
        final String queryStr = String.format("SELECT t FROM %s t WHERE %s = %s ",
                object.getClass().getSimpleName(), leftCond, rightCond
        );

        final TypedQuery<?> query = em.createQuery(queryStr, object.getClass());
        query.setFlushMode(FlushModeType.COMMIT);
        return query
                .setParameter("val", o).getResultList();
    }

    //@Transactional
    public <T> boolean existBy(T object, Field field, boolean ignoreCase) {
        Object o = null;
        try {
            o = field.get(object);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        final String key = "t." + field.getName();
        final String leftCond = ignoreCase ? " LOWER(" + key + ")" : key;
        final String rightCond = ignoreCase ? "LOWER(:val)" : ":val";
        final String queryStr = String.format("SELECT count(t) FROM %s t WHERE %s = %s ",
                object.getClass().getSimpleName(), leftCond, rightCond
        );
        long count = 0;
        try {
            final Query query = em.createQuery(queryStr);
            query.setFlushMode(FlushModeType.COMMIT);
            count = (Long) query
                    .setParameter("val", o).getSingleResult();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return count > 0;
    }

    @Transactional
    public <T> boolean existBy(T object, Field field) {
        return existBy(object, field, false);
    }

    public <T, ID> Optional<T> findById(Class<T> classe, ID id) {
        T r = find(classe, id);
        return Optional.ofNullable(r);
    }

    @Transactional
    public <T, ID> void deleteById(Class<T> classe, ID id) throws Exception {
        T r = find(classe, id);
        if (!has(r)) {
            throw new Exception("Impossible de supprimer l'objet inexistant " + classe.getSimpleName() + "#" + id);
        }
        em.remove(r);
    }

    public Type getIdType(Class entityClass) throws NotManagedEntityException {
        checkManagedEntity(entityClass);
        return entityMap.get(entityClass).getIdType();
    }

    public String getIdKey(Class entityClass) throws NotManagedEntityException {
        checkManagedEntity(entityClass);
        return entityMap.get(entityClass).getId(entityMap.get(entityClass).getIdType().getJavaType()).getName();
    }

    public boolean isManagedEntity(Class targetEntityClass) {
        return entityMap.containsKey(targetEntityClass);
    }

    public void checkManagedEntity(Class targetEntityClass) throws NotManagedEntityException {
        boolean r = entityMap.containsKey(targetEntityClass);
        if (!r) {
            throw new NotManagedEntityException(targetEntityClass);
        }
    }

    @Transactional
    public <T> T save(T entity) {
        em.persist(entity);
        return entity;
    }
}
