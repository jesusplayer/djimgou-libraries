package com.djimgou.security.core.repo;

import com.djimgou.security.core.model.Privilege;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

/**
 * @author DJIMGOU NKENNE DANY MARC 08/2020
 */
public interface PrivilegeRepo extends JpaRepository<Privilege, UUID>, QuerydslPredicateExecutor<Privilege> {
    @Query("SELECT d FROM Privilege d WHERE " +
            //"(d.dirty IS NULL OR d.dirty=FALSE) AND " +
            "(LOWER(d.code) LIKE LOWER(CONCAT('%',:searchText, '%'))) OR " +
            //"(LOWER(d.parent.code) LIKE LOWER(CONCAT('%',:searchText, '%'))) OR " +
            //"(LOWER(d.parent.name) LIKE LOWER(CONCAT('%',:searchText, '%'))) OR " +
            "(LOWER(d.name) LIKE LOWER(CONCAT('%',:searchText, '%')))")
    Page<Privilege> findBySearchText(@Param("searchText") String searchText, Pageable pageRequest);

    Optional<Privilege> findByName(String nom);

    Optional<Privilege> findByCode(String nom);

    Page<Privilege> findAll(Specification<Privilege> spec, Pageable pageRequest);
}
