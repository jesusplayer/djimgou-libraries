package com.act.core.testing.app.repository;

import com.act.core.testing.app.model.Produit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

public interface ProduitRepo extends JpaRepository<Produit, UUID>{
    @Query("SELECT d FROM Produit d WHERE " +
            "LOWER(d.marque) LIKE LOWER(CONCAT('%',:searchText, '%'))  ")
    Page<Produit> findBySearchText(@Param("searchText") String searchText, Pageable pageRequest);

    @Modifying
    @Transactional
    @Query("UPDATE Produit p SET p.actif = :actif WHERE p.id = :id")
    void activer(@Param("id") UUID produiteId, @Param("actif") boolean actif);

    @Modifying
    @Transactional
    @Query("UPDATE Produit p SET p.commission = :commission WHERE p.id = :id")
    void modifierCommission(@Param("id") UUID produiteId, @Param("commission") Double commission);

    @Modifying
    @Transactional
    @Query("UPDATE Produit p SET p.actif = :actif WHERE p.id IN :ids")
    void activer(@Param("ids") UUID[] produiteIds, @Param("actif") boolean actif);

    @Modifying
    @Transactional
    @Query("UPDATE Produit p SET p.enLocation = :enLocation WHERE p.id = :id")
    void mettreEnlocation(@Param("id") UUID partenaireId, @Param("enLocation") boolean actif);

    Page<Produit> findAll(Specification<Produit> spec, Pageable pageRequest);

    //Page<Produit> findByIdAndImagesCustomData(UUID productId, String customData);

    Page<Produit> findByEnLocationTrue(Pageable pageable);
    /**
     * long countByDept(String deptName);
     * long countBySalaryGreaterThanEqual(int salary);
     * long countByNameEndingWith(String endString);
     * long countByNameLike(String likeString);
     */
}
