package com.act.core.testing.app.repository;

import com.act.core.testing.app.model.Quartier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface QuartierRepo extends JpaRepository<Quartier, UUID>{
    @Query("SELECT d FROM Quartier d WHERE " +
            "LOWER(d.code) LIKE LOWER(CONCAT('%',:searchText, '%')) OR " +
            "LOWER(d.nom) LIKE LOWER(CONCAT('%',:searchText, '%')) ORDER BY d.nom")
    Page<Quartier> findBySearchText(@Param("searchText") String searchText, Pageable pageRequest);


    Page<Quartier> findByCodeContaining(String code, Pageable pageRequest);


    Page<Quartier> findByNomContaining(String nom, Pageable pageRequest);


    Page<Quartier> findAll(Specification<Quartier> spec, Pageable pageRequest);
    /**
     * long countByDept(String deptName);
     * long countBySalaryGreaterThanEqual(int salary);
     * long countByNameEndingWith(String endString);
     * long countByNameLike(String likeString);
     */
}
