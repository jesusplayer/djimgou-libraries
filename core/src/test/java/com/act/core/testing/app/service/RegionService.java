package com.act.core.testing.app.service;

import com.act.core.infra.CustomPageable;
import com.act.core.service.AbstractDomainServiceV2;
import com.act.core.testing.app.model.Region;
import com.act.core.testing.app.model.dto.region.RegionDetailDto;
import com.act.core.testing.app.model.dto.region.RegionDto;
import com.act.core.testing.app.model.dto.region.RegionFilterDto;
import com.act.core.testing.app.model.dto.region.RegionFindDto;
import com.act.core.testing.app.repository.RegionRepo;
import com.act.core.util.AppUtils;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAQueryBase;
import com.querydsl.jpa.impl.JPAQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.act.core.util.AppUtils.has;

/**
 * @author djimgou
 */
@Service
public class RegionService extends AbstractDomainServiceV2<Region, RegionFindDto, RegionFilterDto, RegionDto, RegionDetailDto> {
    private RegionRepo repo;

    public RegionService(RegionRepo repo) {
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
    public Page<Region> findBy(RegionFilterDto filter) throws Exception {
        CustomPageable cpg = new CustomPageable(filter);
        if (cpg.getSort().isUnsorted()) {
            cpg.setSort(Sort.by(Sort.Order.asc("nom")));
        }
        return repo.findAll(cpg);
    }

}
