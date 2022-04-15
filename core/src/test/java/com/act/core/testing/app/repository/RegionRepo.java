package com.act.core.testing.app.repository;

import com.act.core.testing.app.model.Region;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface RegionRepo extends JpaRepository<Region, UUID> {
    @Query("SELECT d FROM Region d WHERE " +
            "LOWER(d.code) LIKE LOWER(CONCAT('%',:searchText, '%')) OR " +
            "LOWER(d.nom) LIKE LOWER(CONCAT('%',:searchText, '%')) ORDER BY d.nom")
    Page<Region> findBySearchText(@Param("searchText") String searchText, Pageable pageRequest);


    Page<Region> findByCodeContaining(String code, Pageable pageRequest);


    Page<Region> findByNomContaining(String nom, Pageable pageRequest);

    Page<Region> findAll(Specification<Region> spec, Pageable pageRequest);
    /**
     * long countByDept(String deptName);
     * long countBySalaryGreaterThanEqual(int salary);
     * long countByNameEndingWith(String endString);
     * long countByNameLike(String likeString);
     */
}
