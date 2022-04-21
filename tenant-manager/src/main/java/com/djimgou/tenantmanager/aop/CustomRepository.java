package com.djimgou.tenantmanager.aop;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

/**
 * Utilisé uniquement pour les repos qui dépendent du tenant
 * @param <T>
 * @param <ID>
 */
@NoRepositoryBean
public interface CustomRepository<T, ID> extends JpaRepository<T, ID> {
    //https://stackoverflow.com/questions/45169783/hibernate-filter-is-not-applied-for-findone-crud-operation
   /* default Optional<T> findById(ID id) {
        throw new RuntimeException();
    }

    Optional<T> getById(ID id);*/
}
