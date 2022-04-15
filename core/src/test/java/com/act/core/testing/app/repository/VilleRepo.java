package com.act.core.testing.app.repository;

import com.act.core.testing.app.model.Ville;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface VilleRepo extends JpaRepository<Ville, UUID>{
    @Query("SELECT d FROM Ville d WHERE " +
            "LOWER(d.code) LIKE LOWER(CONCAT('%',:searchText, '%')) OR " +
            "LOWER(d.nom) LIKE LOWER(CONCAT('%',:searchText, '%')) ORDER BY d.nom")
    Page<Ville> findBySearchText(@Param("searchText") String searchText, Pageable pageRequest);


    Page<Ville> findByCodeContaining(String code, Pageable pageRequest);


    Page<Ville> findByNomContaining(String nom, Pageable pageRequest);

    Page<Ville> findAll(Specification<Ville> spec, Pageable pageRequest);
    /**
     * long countByDept(String deptName);
     * long countBySalaryGreaterThanEqual(int salary);
     * long countByNameEndingWith(String endString);
     * long countByNameLike(String likeString);
     */
}
