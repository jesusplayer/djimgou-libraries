package com.act.tenantmanager.repository;

import com.act.tenantmanager.model.Pays;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface PaysRepo extends JpaRepository<Pays, UUID>, QuerydslPredicateExecutor<Pays> {
    @Query("SELECT d FROM Pays d WHERE " +
            "LOWER(d.nom) LIKE LOWER(CONCAT('%',:searchText, '%')) OR " +
            "LOWER(d.code) LIKE LOWER(CONCAT('%',:searchText, '%')) ORDER BY d.nom")
    Page<Pays> findBySearchText(@Param("searchText") String searchText, Pageable pageRequest);


    Page<Pays> findByCodeContaining(String code, Pageable pageRequest);


    Page<Pays> findByNomContaining(String nom, Pageable pageRequest);

    Page<Pays> findAll(Specification<Pays> spec, Pageable pageRequest);
    
    /**
     * long countByDept(String deptName);
     * long countBySalaryGreaterThanEqual(int salary);
     * long countByNameEndingWith(String endString);
     * long countByNameLike(String likeString);
     */
}
