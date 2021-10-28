package com.act.security.core.repo;

import com.act.security.core.model.Role;
import com.act.security.core.model.StatutSecurityWorkflow;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

/**
 * @author DJIMGOU NKENNE DANY MARC 08/2020
 */
public interface RoleRepo extends JpaRepository<Role, UUID>, QuerydslPredicateExecutor<Role> {
    @Query("SELECT d FROM Role d WHERE " +
            //"(d.dirty IS NULL OR d.dirty=FALSE) AND " +
            "(" +
            "LOWER(d.name) LIKE LOWER(CONCAT('%',:searchText, '%')) OR " +
            "LOWER(d.description) LIKE LOWER(CONCAT('%',:searchText, '%')) " +
            ")")
    Page<Role> findBySearchText(@Param("searchText") String searchText, Pageable pageRequest);


    Role findByName(String authority);

    @Modifying
    @Query("UPDATE Role u SET " +
            "u.dirty = NULL, " +
            "u.dirtyValueId = NULL " +
            "where u.id = :id")
    void invalidate(@Param("id") UUID uuid);

    //    @Query("SELECT  d FROM Role d WHERE d.dirty=FALSE")
//    Page<Role> findAll(Specification<Role> spec, Pageable pageRequest);
    Page<Role> findByDirtyFalseOrDirtyNull(Pageable pageRequest);

    @Modifying
    @Query("UPDATE Role u SET " +
            "u.statutCreation = :statutCreation, " +
            "u.adminValidateurId = :validateurId, " +
            "u.commentaireAdminValidateur = :commentaireAdminValidateur " +
            "where u.id = :id")
    void validateEntity(
            @Param("statutCreation") StatutSecurityWorkflow statutSecurityWorkflow,
            @Param("validateurId") UUID validateurId,
            @Param("commentaireAdminValidateur") String commentaireAdminValidateur,
            @Param("id") UUID id
    );

    @Modifying
    @Query("UPDATE Role u SET " +
            "u.statutCreation = :statutCreation " +
            "where u.id = :id")
    void updateStatut(
            @Param("statutCreation") StatutSecurityWorkflow statutSecurityWorkflow,
            @Param("id") UUID id
    );
}
