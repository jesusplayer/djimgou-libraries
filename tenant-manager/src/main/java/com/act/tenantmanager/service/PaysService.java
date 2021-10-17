package com.act.tenantmanager.service;

import com.act.core.service.AbstractDomainService;
import com.act.tenantmanager.exceptions.PaysNotFoundException;
import com.act.tenantmanager.model.Pays;
import com.act.tenantmanager.model.QPays;
import com.act.tenantmanager.model.dto.pays.PaysDto;
import com.act.tenantmanager.model.dto.pays.PaysFilterDto;
import com.act.tenantmanager.model.dto.pays.PaysFindDto;
import com.act.core.infra.CustomPageable;
import com.act.tenantmanager.repository.PaysRepo;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAQueryBase;
import com.querydsl.jpa.impl.JPAQuery;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.act.core.util.AppUtils.has;

/**
 * @author djimgou
 */
@Service
public class PaysService extends AbstractDomainService<Pays, PaysFindDto, PaysFilterDto> {
    @Autowired
    PaysRepo repo;

    @PersistenceContext
    EntityManager em;


    public PaysService() {
        super();
    }

    @Override
    public PaysRepo getRepo() {
        return repo;
    }


    public Page<Pays> findBySearchText(String text, Pageable pg) {
        Page<Pays> page = repo.findBySearchText(text, pg);
        return page;
    }

    @Transactional(/*propagation = Propagation.NESTED*/)
    public Pays savePays(UUID id, PaysDto regionDto) throws PaysNotFoundException {
        Pays etage = new Pays();
        if (has(id)) {
            etage = repo.findById(id).orElseThrow(PaysNotFoundException::new);
        }
        etage.fromDto(regionDto);
        return save(etage);
    }

    @SneakyThrows
    public Pays createPays(PaysDto paysDto) {
        return savePays(null, paysDto);
    }

    @Override
    public Page<Pays> searchPageable(PaysFindDto findDto) {
        if (!has(findDto.getSearchKeys())) {
            findDto.setSearchKeys(new String[]{"code", "nom"});
        }
        return super.searchPageable(findDto);
    }

    public Page<Pays> findBy(PaysFilterDto filter) throws Exception {
        CustomPageable cpg = new CustomPageable(filter);
        if (cpg.getSort().isUnsorted()) {
            cpg.setSort(Sort.by(Sort.Order.asc("nom")));
        }
        Page<Pays> page;

        String name = filter.getNom();
        String code = filter.getCode();

        QPays qDevise = QPays.pays;

        JPAQuery query = new JPAQuery(em);
        JPAQueryBase exp2 = query.from(qDevise);
        List<BooleanExpression> expressionList = new ArrayList<>();
        if (has(name)) {
            expressionList.add(qDevise.nom.containsIgnoreCase(name));
        }
        if (has(code)) {
            expressionList.add(qDevise.code.containsIgnoreCase(code));
        }


        BooleanExpression exp = expressionList.stream().reduce(null, (old, newE) -> has(old) ? old.and(newE) : newE);

        exp2.where(exp).orderBy(qDevise.nom.asc());

        if (has(exp)) {
            page = repo.findAll(exp, cpg);
        } else {
            page = repo.findAll(cpg);
        }
        return page;
    }

    @Transactional
    public Page<Pays> searchPageable2(PaysFindDto filter) throws Exception {
        CustomPageable cpg = new CustomPageable(filter);
        if (cpg.getSort().isUnsorted()) {
            cpg.setSort(Sort.by(Sort.Order.asc("nom")));
        }
        Page<Pays> page;


        QPays qDevise = QPays.pays;

        JPAQuery query = new JPAQuery(em);
        JPAQueryBase exp2 = query.from(qDevise);
        List<BooleanExpression> expressionList = new ArrayList<>();
        expressionList.add(qDevise.nom.containsIgnoreCase(filter.getSearchText()));
        expressionList.add(qDevise.code.containsIgnoreCase(filter.getSearchText()));


        BooleanExpression exp = expressionList.stream().reduce(null, (old, newE) -> has(old) ? old.or(newE) : newE);

        exp2.where(exp).orderBy(qDevise.nom.asc());

        if (has(exp)) {
            page = repo.findAll(exp, cpg);
        } else {
            page = repo.findAll(cpg);
        }
        return page;
    }

}
