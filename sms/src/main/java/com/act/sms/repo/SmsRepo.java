package com.act.sms.repo;

import com.act.sms.model.Sms;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

/**
 * @author DJIMGOU NKENNE DANY MARC 03/2022
 */
public interface SmsRepo extends JpaRepository<Sms, UUID>, QuerydslPredicateExecutor<Sms> {
    @Query("SELECT d FROM Sms d WHERE " +
            //"(d.dirty IS NULL OR d.dirty=FALSE) AND " +
            "(LOWER(d.to) LIKE LOWER(CONCAT('%',:searchText, '%'))) OR " +
            //"(LOWER(d.parent.code) LIKE LOWER(CONCAT('%',:searchText, '%'))) OR " +
            //"(LOWER(d.parent.name) LIKE LOWER(CONCAT('%',:searchText, '%'))) OR " +
            "(LOWER(d.text) LIKE LOWER(CONCAT('%',:searchText, '%')))")
    Page<Sms> findBySearchText(@Param("searchText") String searchText, Pageable pageRequest);


    Page<Sms> findAll(Specification<Sms> spec, Pageable pageRequest);
}
