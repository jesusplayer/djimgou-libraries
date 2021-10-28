package com.act.security.core.repo;

import com.act.security.core.model.ConfirmationToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
/**
 * @author DJIMGOU NKENNE DANY MARC 08/2020
 */
public interface ConfirmationTokenRepo extends JpaRepository<ConfirmationToken, UUID> {
    @Query("SELECT d FROM ConfirmationToken d WHERE " +
            "LOWER(d.confirmationToken) LIKE LOWER(CONCAT('%',:searchText, '%'))")
    Page<ConfirmationToken> findBySearchText(@Param("searchText") String searchText, Pageable pageRequest);

    @Transactional
    ConfirmationToken findByConfirmationToken(String confirmationToken);

    Page<ConfirmationToken> findAll(Specification<ConfirmationToken> spec, Pageable pageRequest);
}
