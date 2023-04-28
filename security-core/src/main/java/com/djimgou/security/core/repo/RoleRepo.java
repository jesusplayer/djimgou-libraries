package com.djimgou.security.core.repo;

import com.djimgou.security.core.model.Role;
import com.djimgou.security.core.model.views.IRoleExport;
import com.djimgou.security.core.model.views.RoleListview;
import com.djimgou.security.core.model.views.UtilisateurListview;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
public interface RoleRepo extends JpaRepository<Role, UUID>, QuerydslPredicateExecutor<Role> {
    @Query("SELECT d FROM Role d WHERE " +
            //"(d.dirty IS NULL OR d.dirty=FALSE) AND " +
            "(" +
            "LOWER(d.name) LIKE LOWER(CONCAT('%',:searchText, '%')) OR " +
            "LOWER(d.description) LIKE LOWER(CONCAT('%',:searchText, '%')) " +
            ")")
    Page<Role> findBySearchText(@Param("searchText") String searchText, Pageable pageRequest);

    /*@Query("SELECT d FROM Role d WHERE " +
            "LOWER(d.name) LIKE LOWER(CONCAT('%',:name, '%')) ")*/
    Role findByName(String name);

    List<Role> findByParentId(UUID parentId);

    List<Role> findByPrivilegesIdIn(List<UUID> privilegeIds);

    List<Role> findByPrivilegesCodeIn(List<String> privilegeCodes);

    /*@Query("SELECT d FROM Role d WHERE " +
            "LOWER(d.name) LIKE LOWER(CONCAT('%',:name, '%')) ")*/
    Optional<Role> findOneByName(String name);

    @Query("SELECT " +
            "v.id as id," +
            "v.name AS name, " +
            "v.description AS description, " +
            "v.parent.id AS parentId, " +
            "v.parent.name AS nameParent, " +
            "v.deleted AS deleted, " +
            "v.readonlyValue AS readonlyValue " +
            "FROM Role v " +
            "LEFT join v.parent parent" +
            "")
    Page<RoleListview> listView(Pageable pageable);

    @Transactional
    @Query("SELECT " +
            "v.name AS name, " +
            "v.description AS description, " +
            "parent.name AS nomParent " +
            "FROM Role v " +
            "LEFT join v.parent parent" +
            "")
    List<IRoleExport> exporter();

}
