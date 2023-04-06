package com.djimgou.security.core.repo;

import com.djimgou.security.core.model.Privilege;
import com.djimgou.security.core.model.views.IPrivilegeExport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;

import java.util.List;
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

    /*@Query("SELECT d FROM Privilege d WHERE " +
            "LOWER(d.name) LIKE LOWER(CONCAT('%',:nom, '%')) ")*/
    Optional<Privilege> findByName(String nom);

   /* @Query("SELECT d FROM Privilege d WHERE " +
            "LOWER(d.code) LIKE LOWER(CONCAT('%',:code, '%')) ")*/
    Optional<Privilege> findByCode(String code);

    /**
     * Liste des enfants d'un privilège
     *
     * @param parentId
     * @return
     */
    List<Privilege> findByParentId(UUID parentId);

    List<Privilege> findByParentCode(String codePrivParent);

    Page<Privilege> findAll(Specification<Privilege> spec, Pageable pageRequest);


    @Query("SELECT " +
            "v.code AS code, " +
            "v.name AS name, " +
            "v.url AS url, " +
            "v.description AS description " +
            "FROM Privilege v " +
            "")
    List<IPrivilegeExport> exporter();
}
