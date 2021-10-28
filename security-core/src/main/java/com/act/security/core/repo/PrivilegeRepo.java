package com.act.security.core.repo;

import com.act.security.core.model.Privilege;
import com.act.security.core.model.StatutSecurityWorkflow;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
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

    Page<Privilege> findByDirtyFalseOrDirtyNull(Pageable pageRequest);

    @Modifying
    @Query("UPDATE Privilege u SET " +
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
    @Query("UPDATE Privilege u SET " +
            "u.statutCreation = :statutCreation " +
            "where u.id = :id")
    void updateStatut(
            @Param("statutCreation") StatutSecurityWorkflow statutSecurityWorkflow,
            @Param("id") UUID id
    );
}
