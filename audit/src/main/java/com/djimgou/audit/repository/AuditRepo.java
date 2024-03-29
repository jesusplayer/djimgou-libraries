package com.djimgou.audit.repository;

import com.djimgou.audit.model.Audit;
import com.djimgou.audit.model.AuditAction;
import com.djimgou.audit.views.AuditListView;
import com.djimgou.audit.views.IAuditExport;
import com.djimgou.core.repository.BaseJpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.UUID;
/**
 * @author DJIMGOU NKENNE DANY MARC 08/2020
 */
public interface AuditRepo extends BaseJpaRepository<Audit, UUID> {
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

    @Query("SELECT DISTINCT s.nomEntite FROM Audit s")
    List<String> findNomEntite();

    Page<Audit> findByAction(UUID utilisateurId, Pageable pageRequest);

    Page<Audit> findAll(Specification<Audit> spec, Pageable pageRequest);

    @Query("SELECT " +
            "v.id as id," +
            "v.username AS username, " +
            "v.action AS action, " +
            "v.date AS date, " +
            "v.nomEntite AS nomEntite " +
            "FROM Audit v " +
            "")
    Page<AuditListView> listView(Pageable pg);

    @Query("SELECT " +
            "v.username AS username, " +
            "v.action AS action, " +
            "v.date AS date, " +
            "v.nomEntite AS nomEntite, " +
            "v.data AS data " +
            "FROM Audit v " +
            "")
    List<IAuditExport> exporter();
}
