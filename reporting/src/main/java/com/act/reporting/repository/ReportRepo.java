package com.act.reporting.repository;

import com.act.reporting.model.Report;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface ReportRepo extends JpaRepository<Report, UUID>, QuerydslPredicateExecutor<Report> {
    @Query("SELECT d FROM Report d WHERE " +
            "LOWER(d.nom) LIKE LOWER(CONCAT('%',:searchText, '%')) OR " +
            "LOWER(d.fichier1) LIKE LOWER(CONCAT('%',:searchText, '%'))")
    Page<Report> findBySearchText(@Param("searchText") String searchText, Pageable pageRequest);


    Page<Report> findByNomContaining(String code, Pageable pageRequest);

    Page<Report> findAll(Specification<Report> spec, Pageable pageRequest);
    /**
     * long countByDept(String deptName);
     * long countBySalaryGreaterThanEqual(int salary);
     * long countByNameEndingWith(String endString);
     * long countByNameLike(String likeString);
     */
}
