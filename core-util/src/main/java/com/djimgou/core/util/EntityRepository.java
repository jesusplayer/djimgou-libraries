package com.djimgou.core.util;

import com.djimgou.core.util.exception.NotManagedEntityException;
import lombok.Getter;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.Type;
import java.util.Map;
import java.util.Optional;
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

    public <T, ID> T find(Class<T> classe, ID id) {
        return em.find(classe, id);
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
