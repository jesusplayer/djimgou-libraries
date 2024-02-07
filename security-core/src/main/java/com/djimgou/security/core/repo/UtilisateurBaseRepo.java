package com.djimgou.security.core.repo;

import com.djimgou.security.core.model.Role;
import com.djimgou.security.core.model.Utilisateur;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * @author DJIMGOU NKENNE DANY MARC 08/2020
 */
@NoRepositoryBean
public interface UtilisateurBaseRepo<T extends Utilisateur, I> extends JpaRepository<T, I>, QuerydslPredicateExecutor<T> {
    @Query("SELECT d FROM Utilisateur d WHERE " +
            //"(d.dirty IS NULL OR d.dirty=FALSE) AND " +
            "(" +
            "LOWER(d.username) LIKE LOWER(CONCAT('%',:searchText, '%')) OR " +
            "LOWER(d.nom) LIKE LOWER(CONCAT('%',:searchText, '%')) OR " +
            "LOWER(d.telephone) LIKE LOWER(CONCAT('%',:searchText, '%')) OR " +
            "LOWER(d.email) LIKE LOWER(CONCAT('%',:searchText, '%')) OR " +
            "LOWER(d.prenom) LIKE LOWER(CONCAT('%',:searchText, '%')) " +
            ")")
    Page<T> findBySearchText(@Param("searchText") String searchText, Pageable pageRequest);


    @Modifying
    @Query("UPDATE Utilisateur u SET " +
            "u.nom = :nom, " +
            "u.prenom = :prenom, " +
            "u.username = :username, " +
            "u.email = :email " +
            " where u.id = :userId")
    void updateProfil(
            @Param("nom") String nom,
            @Param("prenom") String prenom,
            @Param("username") String username,
            @Param("email") String email,
            @Param("userId") UUID userId);

    @Transactional
    @Modifying
    @Query("UPDATE Utilisateur u SET " +
            "u.nom = :#{#user.nom}, " +
            "u.prenom = :#{#user.prenom}, " +
            "u.username = :#{#user.username}, " +
            "u.email = :#{#user.email} " +
            " where u.id = :#{#user.id}")
    void updateProfil(@Param("user") T user);

    Optional<T> findByUsername(@Param("username") String username);

    Optional<T> findByUsernameAndIdNot(String username, UUID id);

    @Query("SELECT d FROM Utilisateur d WHERE d.username = :username")
    Optional<T> findValidatedUsername(@Param("username") String username);


    Optional<T> findOneByNomAndPrenom(String nom, String prenom);


    @Transactional
    Optional<T> findOneByEmail(String email);

    @Transactional
    Page<T> findByEnabledTrue(Pageable pageRequest);

    @Transactional
    Page<T> findByEnabledFalse(Pageable pageable);


    Page<T> findAll(Specification<T> spec, Pageable pageRequest);


    @Modifying
    @Query("UPDATE Utilisateur u SET u.enabled = ?1 where u.id = ?2")
    void validerUtilisateur(Boolean enabled, UUID id);

    @Modifying
    @Query("UPDATE Utilisateur u SET " +
            "u.password = :password, " +
            "u.isPasswordChangedByUser = :isPasswordChangedByUser " +
            "where u.id = :id")
    void changePassword(
            @Param("password") String passwordEncrypted,
            @Param("id") UUID id,
            @Param("isPasswordChangedByUser") Boolean isPasswordChangedByUser
    );

    @Modifying
    @Query("UPDATE Utilisateur u SET " +
            "u.username = :username " +
            "where u.id = :id")
    void changeUsername(
            @Param("id") UUID id,
            @Param("username") String username
    );


    @Query("SELECT d FROM Utilisateur d " +
            "inner join d.authorities role " +
            "where role.name in :rolesName")
    List<T> usersByRoles(@Param("rolesName") List<String> rolesName);
}
