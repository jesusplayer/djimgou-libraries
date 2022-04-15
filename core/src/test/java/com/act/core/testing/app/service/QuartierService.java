package com.act.core.testing.app.service;

import com.act.core.infra.CustomPageable;
import com.act.core.service.AbstractDomainServiceV2;
import com.act.core.testing.app.model.Quartier;
import com.act.core.testing.app.model.dto.quartier.QuartierDetailDto;
import com.act.core.testing.app.model.dto.quartier.QuartierDto;
import com.act.core.testing.app.model.dto.quartier.QuartierFilterDto;
import com.act.core.testing.app.model.dto.quartier.QuartierFindDto;
import com.act.core.testing.app.repository.QuartierRepo;
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
public class QuartierService extends AbstractDomainServiceV2<Quartier, QuartierFindDto, QuartierFilterDto, QuartierDto, QuartierDetailDto> {
    private QuartierRepo repo;

    public QuartierService(QuartierRepo repo) {
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
    public Page<Quartier> findBy(QuartierFilterDto filter) throws Exception {
        CustomPageable cpg = new CustomPageable(filter);
        if (cpg.getSort().isUnsorted()) {
            cpg.setSort(Sort.by(Sort.Order.asc("nom")));
        }
        return repo.findAll(cpg);
    }
}
