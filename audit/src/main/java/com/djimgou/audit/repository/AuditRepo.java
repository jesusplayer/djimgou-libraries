package com.djimgou.audit.repository;

import com.djimgou.audit.model.Audit;
import com.djimgou.audit.model.AuditAction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.UUID;
/**
 * @author DJIMGOU NKENNE DANY MARC 08/2020
 */
public interface AuditRepo extends JpaRepository<Audit, UUID>, QuerydslPredicateExecutor<Audit> {
    @Query("SELECT d FROM Audit d WHERE " +
            "LOWER(d.nomEntite) LIKE LOWER(CONCAT('%',:searchText, '%')) OR " +
            "LOWER(d.action) LIKE LOWER(CONCAT('%',:searchText, '%')) OR " +
            "LOWER(d.action) LIKE LOWER(CONCAT('%',:searchText, '%'))")
    Page<Audit> findBySearchText(@Param("searchText") String searchText, Pageable pageRequest);

    @Query("SELECT s FROM Audit s WHERE " +
            "s.date BETWEEN :startDate AND :endDate")
    Page<Audit> findByDate(@Param("startDate") Date startDate, @Param("endDate") Date endDate, Pageable pageRequest);

    @Query("SELECT s FROM Audit s WHERE s.utilisateurId=:utilisateurId AND " +
            "(s.date BETWEEN :startDate AND :endDate)")
    Page<Audit> findByUtilisateurIdDate(@Param("utilisateurId") UUID utilisateurId, @Param("startDate") Date startDate, @Param("endDate") Date endDate, Pageable pageRequest);

    Page<Audit> findByUtilisateurId(UUID utilisateurId, Pageable pageRequest);

    @Query("SELECT s FROM Audit s WHERE s.action=:action AND " +
            "(s.date BETWEEN :startDate AND :endDate)")
    Page<Audit> findByActionDate(@Param("action") AuditAction action, @Param("startDate") Date startDate, @Param("endDate") Date endDate, Pageable pageRequest);

    Page<Audit> findByAction(UUID utilisateurId, Pageable pageRequest);

    Page<Audit> findAll(Specification<Audit> spec, Pageable pageRequest);
}
