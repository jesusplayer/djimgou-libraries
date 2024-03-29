package com.djimgou.tenantmanager.repository;

import com.djimgou.tenantmanager.model.Pays;
import com.djimgou.tenantmanager.views.IPaysExport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PaysRepo extends JpaRepository<Pays, UUID>, QuerydslPredicateExecutor<Pays> {
    @Query("SELECT d FROM Pays d WHERE " +
            "LOWER(d.nom) LIKE LOWER(CONCAT('%',:searchText, '%')) OR " +
            "LOWER(d.code) LIKE LOWER(CONCAT('%',:searchText, '%')) ORDER BY d.nom")
    Page<Pays> findBySearchText(@Param("searchText") String searchText, Pageable pageRequest);


    Page<Pays> findByCodeContaining(String code, Pageable pageRequest);

    @Query("SELECT d FROM Pays d WHERE " +
            "LOWER(d.code) LIKE LOWER(CONCAT('%',:code, '%')) ")
    Optional<Pays> findOneByCode(String code);

    @Query("SELECT d FROM Pays d WHERE " +
            "LOWER(d.nom) LIKE LOWER(CONCAT('%',:nomPays, '%')) ")
    Optional<Pays> findOneByNom(String nomPays);

    boolean existsByCodeOrNom(String codePays, String nomPays);

    boolean existsByCode(String codePays);

    Page<Pays> findByNomContaining(String nom, Pageable pageRequest);

    Page<Pays> findAll(Specification<Pays> spec, Pageable pageRequest);

    @Query("SELECT " +
            "v.code AS code, " +
            "v.nom AS nom " +
            "FROM Pays v " +
            "")
    List<IPaysExport> exporter();
    /**
     * long countByDept(String deptName);
     * long countBySalaryGreaterThanEqual(int salary);
     * long countByNameEndingWith(String endString);
     * long countByNameLike(String likeString);
     */
}
