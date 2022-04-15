package com.act.core.testing.app.service;

import com.act.core.infra.CustomPageable;
import com.act.core.service.AbstractDomainServiceV2;
import com.act.core.testing.app.model.Categorie;
import com.act.core.util.AppUtils;
import com.act.core.testing.app.model.dto.categorie.CategorieDetailDto;
import com.act.core.testing.app.model.dto.categorie.CategorieDto;
import com.act.core.testing.app.model.dto.categorie.CategorieFilterDto;
import com.act.core.testing.app.model.dto.categorie.CategorieFindDto;
import com.act.core.testing.app.repository.CategorieRepo;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAQueryBase;
import com.querydsl.jpa.impl.JPAQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.act.core.util.AppUtils.has;


/**
 * @author djimgou
 */
@Service
public class CategorieService extends AbstractDomainServiceV2<Categorie, CategorieFindDto, CategorieFilterDto, CategorieDto, CategorieDetailDto> {
    private CategorieRepo repo;

    public CategorieService(CategorieRepo repo) {
        super(repo);
        this.repo = repo;
    }
    /**
     * Recherche avec pagination et filtre
     *
     * @param filter
     * @return
     * @throws Exception
     */
    public Page<Categorie> findBy(CategorieFilterDto filter) throws Exception {
        CustomPageable cpg = new CustomPageable(filter);
        if (cpg.getSort().isUnsorted()) {
            cpg.setSort(Sort.by(Sort.Order.asc("nom")));
        }

        return repo.findAll(cpg);
    }

}
