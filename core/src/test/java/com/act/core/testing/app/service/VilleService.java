package com.act.core.testing.app.service;

import com.act.core.infra.CustomPageable;
import com.act.core.service.AbstractDomainServiceV2;
import com.act.core.testing.app.model.Ville;
import com.act.core.testing.app.model.dto.ville.VilleDetailDto;
import com.act.core.testing.app.model.dto.ville.VilleDto;
import com.act.core.testing.app.model.dto.ville.VilleFilterDto;
import com.act.core.testing.app.model.dto.ville.VilleFindDto;
import com.act.core.testing.app.repository.VilleRepo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * @author djimgou
 */
@Service
public class VilleService extends AbstractDomainServiceV2<Ville, VilleFindDto, VilleFilterDto, VilleDto, VilleDetailDto> {
    private VilleRepo repo;

    @PersistenceContext
    EntityManager em;

    public VilleService(VilleRepo repo) {
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
    @Override
    public Page<Ville> findBy(VilleFilterDto filter) throws Exception {
        CustomPageable cpg = new CustomPageable(filter);
        if (cpg.getSort().isUnsorted()) {
            cpg.setSort(Sort.by(Sort.Order.asc("nom")));
        }
        return repo.findAll(cpg);
    }


}
