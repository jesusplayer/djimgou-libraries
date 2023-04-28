package com.djimgou.security.core.repo;

import com.djimgou.security.core.model.Privilege;
import com.djimgou.security.core.model.views.IPrivilegeDto;
import com.djimgou.security.core.model.views.IPrivilegeExport;
import com.djimgou.security.core.model.views.PrivilegeListview;
import com.djimgou.security.core.model.views.RoleListview;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional
    void deleteByUrl(String url);

    @Transactional
    void deleteByCode(String url);

    @Transactional
    void deleteByName(String url);

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
            "v.id as id," +
            "v.code AS code, " +
            "v.name AS name, " +
            "v.description AS description, " +
            "v.parent.id AS parentId, " +
            "v.parent.name AS nameParent, " +
            "v.deleted AS deleted, " +
            "v.readonlyValue AS readonlyValue, " +
            "v.httpMethod AS httpMethod " +
            "FROM Privilege v " +
            "LEFT join v.parent parent" +
            "")
    Page<PrivilegeListview> listView(Pageable pageable);


    @Query("SELECT " +
            "v.id as id," +
            "v.code AS code, " +
            "v.name AS name, " +
            "v.url AS url, " +
            "v.httpMethod AS httpMethod " +
            "FROM Privilege v " +
            "")
    List<IPrivilegeDto> listViewAll();

    @Query("SELECT " +
            "v.code AS code, " +
            "v.name AS name, " +
            "v.url AS url, " +
            "v.description AS description, " +
            "v.httpMethod AS httpMethod " +
            "FROM Privilege v " +
            "")
    List<IPrivilegeExport> exporter();

    boolean existsByUrl(String url);

    boolean existsByDescription(String description);

    @Transactional
    void deleteByDescription(String description);

    boolean existsByCode(String code);
}
