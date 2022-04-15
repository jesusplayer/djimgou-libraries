package com.act.core.testing.app.service;

import com.act.core.infra.CustomPageable;
import com.act.core.service.AbstractDomainServiceV2;
import com.act.core.testing.app.model.Marque;
import com.act.core.testing.app.model.dto.marque.MarqueDetailDto;
import com.act.core.testing.app.model.dto.marque.MarqueDto;
import com.act.core.testing.app.model.dto.marque.MarqueFilterDto;
import com.act.core.testing.app.model.dto.marque.MarqueFindDto;
import com.act.core.testing.app.repository.MarqueRepo;
import com.act.core.util.AppUtils;
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
public class MarqueService extends AbstractDomainServiceV2<Marque, MarqueFindDto, MarqueFilterDto, MarqueDto, MarqueDetailDto> {
    private MarqueRepo repo;

    public MarqueService(MarqueRepo repo) {
        super(repo);
        this.repo = repo;
    }

    public Page<Marque> findBy(MarqueFilterDto filter) throws Exception {
        CustomPageable cpg = new CustomPageable(filter);
        if (cpg.getSort().isUnsorted()) {
            cpg.setSort(Sort.by(Sort.Order.asc("nom")));
        }
        return repo.findAll(cpg);
    }

}
