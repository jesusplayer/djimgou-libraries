package com.djimgou.tenantmanager.service;

import com.djimgou.core.exception.AppException;
import com.djimgou.core.exception.BadRequestException;
import com.djimgou.core.exception.ConflitException;
import com.djimgou.core.exception.NotFoundException;
import com.djimgou.core.export.DataExportParser;
import com.djimgou.core.infra.BaseFilterDto;
import com.djimgou.core.infra.CustomPageable;
import com.djimgou.core.service.AbstractDomainService;
import com.djimgou.tenantmanager.exceptions.ForbidenDeleteException;
import com.djimgou.tenantmanager.exceptions.PaysNotFoundException;
import com.djimgou.tenantmanager.model.Pays;
import com.djimgou.tenantmanager.model.QPays;
import com.djimgou.tenantmanager.model.Tenant;
import com.djimgou.tenantmanager.model.dto.pays.PaysDto;
import com.djimgou.tenantmanager.model.dto.pays.PaysFilterDto;
import com.djimgou.tenantmanager.model.dto.pays.PaysFindDto;
import com.djimgou.tenantmanager.repository.PaysRepo;
import com.djimgou.tenantmanager.repository.TenantRepo;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAQueryBase;
import com.querydsl.jpa.impl.JPAQuery;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.djimgou.core.util.AppUtils.has;

/**
 * @author djimgou
 */
@Getter
@Setter
@Service
public class PaysService extends AbstractDomainService<Pays, PaysFindDto, PaysFilterDto> {
    @Getter
    private PaysRepo repo;

    private TenantRepo tenantRepo;

    @PersistenceContext
    EntityManager em;
    private DataExportParser dataExportParser;
    @Autowired
    public PaysService(PaysRepo repo, TenantRepo tenantRepo,DataExportParser dataExportParser) {
        super(repo);
        this.repo = repo;
        this.tenantRepo = tenantRepo;
        this.dataExportParser=dataExportParser;
    }

    public Page<Pays> findBySearchText(String text, Pageable pg) {
        return repo.findBySearchText(text, pg);
    }

    @Transactional(/*propagation = Propagation.NESTED*/)
    public Pays savePays(UUID id, PaysDto paysDto) throws AppException, ConflitException {
        Pays pays = new Pays();
        if (has(id)) {
            pays = repo.findById(id).orElseThrow(PaysNotFoundException::new);
            if (pays.getReadonlyValue() != null && pays.getReadonlyValue()) {
                paysDto.setCode(pays.getCode());
            }
        } else {
            Optional<Pays> paysOld = repo.findOneByCode(paysDto.getCode());
            if (paysOld.isPresent()) {
                throw new ConflitException("Erreur un pays de même code existe déjà");
            }
            Optional<Pays> paysOld2 = repo.findOneByNom(paysDto.getNom());
            if (paysOld2.isPresent()) {
                throw new ConflitException("Erreur un pays de même Nom existe déjà");
            }
        }

        pays.fromDto(paysDto);
        return save(pays);
    }

    public Pays createPays(PaysDto paysDto) throws PaysNotFoundException, AppException, ConflitException {
        return savePays(null, paysDto);
    }

    @Transactional
    @Override
    public void deleteById(UUID paysId) throws AppException {

        Pays pays = repo.findById(paysId).orElseThrow(PaysNotFoundException::new);

        Page<Tenant> pg = tenantRepo.findByPaysId(paysId, Pageable.unpaged());
        if (pg.hasContent()) {
            throw new ForbidenDeleteException("Impossible de supprimer ce pays car " + pg.getTotalElements() + " centre(s) dépend(ent) de lui(" +
                    pg.getContent().stream().map(Tenant::getNom).collect(Collectors.joining(","))
                    + ")"
            );
        }
        if (pays.getReadonlyValue() != null && pays.getReadonlyValue()) {
            throw new AppException("La suppression de ce pays n'est pas autorisée, car il est très utile pour le bon fonctionnement du système");
        }
        try {
            repo.deleteById(paysId);
        } catch (DataIntegrityViolationException e) {
            throw new AppException("Impossible de supprimer ce pays car, il est utilisé par un objet du système ");
        }
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

    public List<List<?>> exporter() {
        return dataExportParser.parse(repo.exporter());
    }
}
