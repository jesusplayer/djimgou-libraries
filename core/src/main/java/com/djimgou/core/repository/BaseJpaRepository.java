package com.djimgou.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface BaseJpaRepository<ENTITY, ENTITY_ID> extends JpaRepository<ENTITY, ENTITY_ID>, QuerydslPredicateExecutor<ENTITY> {
}
