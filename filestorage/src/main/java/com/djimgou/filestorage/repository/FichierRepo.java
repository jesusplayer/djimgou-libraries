package com.djimgou.filestorage.repository;

import com.djimgou.core.repository.BaseJpaRepository;
import com.djimgou.filestorage.model.Fichier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface FichierRepo extends BaseJpaRepository<Fichier, UUID> {
    @Query("SELECT d FROM Fichier d WHERE " +
            "LOWER(d.nom) LIKE LOWER(CONCAT('%',:searchText, '%')) OR " +
            "LOWER(d.fichier1) LIKE LOWER(CONCAT('%',:searchText, '%'))")
    Page<Fichier> findBySearchText(@Param("searchText") String searchText, Pageable pageRequest);


    @Modifying
    void deleteByCustomData(String customData);

    List<Fichier> findByCustomData(String customData);

    Page<Fichier> findByNomContaining(String code, Pageable pageRequest);

    Page<Fichier> findAll(Specification<Fichier> spec, Pageable pageRequest);
    /**
     * long countByDept(String deptName);
     * long countBySalaryGreaterThanEqual(int salary);
     * long countByNameEndingWith(String endString);
     * long countByNameLike(String likeString);
     */
}
