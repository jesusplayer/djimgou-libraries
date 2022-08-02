package com.djimgou.tenantmanager.service;

import com.djimgou.core.infra.BaseFilterDto;
import com.djimgou.core.infra.CustomPageable;
import com.djimgou.core.service.AbstractDomainService;
import com.djimgou.tenantmanager.exceptions.PaysNotFoundException;
import com.djimgou.tenantmanager.model.Pays;
import com.djimgou.tenantmanager.model.QPays;
import com.djimgou.tenantmanager.model.dto.pays.PaysDto;
import com.djimgou.tenantmanager.model.dto.pays.PaysFilterDto;
import com.djimgou.tenantmanager.model.dto.pays.PaysFindDto;
import com.djimgou.tenantmanager.repository.PaysRepo;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAQueryBase;
import com.querydsl.jpa.impl.JPAQuery;
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

import static com.djimgou.core.util.AppUtils.has;

/**
 * @author djimgou
 */
@Service
public class PaysService extends AbstractDomainService<Pays, PaysFindDto, PaysFilterDto> {
    private PaysRepo repo;

    @PersistenceContext
    EntityManager em;

    @Autowired
    public PaysService(PaysRepo repo) {
        super(repo);
        this.repo = repo;
    }

    public Page<Pays> findBySearchText(String text, Pageable pg) {
        return repo.findBySearchText(text, pg);
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

    public Pays createPays(PaysDto paysDto) throws PaysNotFoundException {
        return savePays(null, paysDto);
    }

    @Transactional
    @Override
    public Page<Pays> searchPageable(PaysFindDto findDto) {
        if (!has(findDto.getSearchKeys())) {
            findDto.setSearchKeys(new String[]{"code", "nom"});
        }
        return super.searchPageable(findDto);
    }

    @Override
    public Page<Pays> advancedSearchBy(BaseFilterDto baseFilter) throws Exception {
        return null;
    }

    @Transactional
    public Page<Pays> findBy(PaysFilterDto baseFilter) throws Exception {
        CustomPageable cpg = new CustomPageable(baseFilter);
        if (cpg.getSort().isUnsorted()) {
            cpg.setSort(Sort.by(Sort.Order.asc("nom")));
        }
        Page<Pays> page;

        String name = baseFilter.getNom();
        String code = baseFilter.getCode();

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
