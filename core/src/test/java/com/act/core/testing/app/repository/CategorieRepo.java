package com.act.core.testing.app.repository;

import com.act.core.testing.app.model.Categorie;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface CategorieRepo extends JpaRepository<Categorie, UUID>{
    @Query("SELECT d FROM Categorie d WHERE " +
            "LOWER(d.code) LIKE LOWER(CONCAT('%',:searchText, '%')) OR " +
            "LOWER(d.nom) LIKE LOWER(CONCAT('%',:searchText, '%')) ORDER BY d.nom")
    Page<Categorie> findBySearchText(@Param("searchText") String searchText, Pageable pageRequest);


    Page<Categorie> findByCodeContaining(String code, Pageable pageRequest);


    Page<Categorie> findByNomContaining(String nom, Pageable pageRequest);

    Page<Categorie> findAll(Specification<Categorie> spec, Pageable pageRequest);
    /**
     * long countByDept(String deptName);
     * long countBySalaryGreaterThanEqual(int salary);
     * long countByNameEndingWith(String endString);
     * long countByNameLike(String likeString);
     */
}
