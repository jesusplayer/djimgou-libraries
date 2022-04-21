package com.djimgou.tenantmanager.repository;

import com.djimgou.tenantmanager.model.Tenant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

public interface TenantRepo extends JpaRepository<Tenant, UUID>, QuerydslPredicateExecutor<Tenant> {
    @Transactional
    Optional<Tenant> findByExternalId(String agenceId);

    @Query("SELECT d FROM Tenant d WHERE " +
            "LOWER(d.code) LIKE LOWER(CONCAT('%',:searchText, '%')) OR " +
            "LOWER(d.ville) LIKE LOWER(CONCAT('%',:searchText, '%')) OR " +
            "LOWER(d.nom) LIKE LOWER(CONCAT('%',:searchText, '%')) ORDER BY d.nom")
    Page<Tenant> findBySearchText(@Param("searchText") String searchText, Pageable pageRequest);


    Page<Tenant> findByCodeContaining(String code, Pageable pageRequest);


    Page<Tenant> findByNomContaining(String nom, Pageable pageRequest);

    Page<Tenant> findAll(Specification<Tenant> spec, Pageable pageRequest);
    /**
     * long countByDept(String deptName);
     * long countBySalaryGreaterThanEqual(int salary);
     * long countByNameEndingWith(String endString);
     * long countByNameLike(String likeString);
     */
}
