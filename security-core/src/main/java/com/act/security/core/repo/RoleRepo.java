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
;

}
