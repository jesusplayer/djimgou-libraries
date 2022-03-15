package com.act.tenantmanager.service;

import com.act.core.infra.CustomPageable;
import com.act.tenantmanager.exceptions.PaysNotFoundException;
import com.act.tenantmanager.exceptions.TenantNotFoundException;
import com.act.tenantmanager.model.Pays;
import com.act.tenantmanager.model.QTenant;
import com.act.tenantmanager.model.Tenant;
import com.act.tenantmanager.model.dto.tenant.TenantDto;
import com.act.tenantmanager.model.dto.tenant.TenantFilterDto;
import com.act.tenantmanager.model.dto.tenant.TenantFindDto;
import com.act.tenantmanager.repository.PaysRepo;
import com.act.tenantmanager.repository.TenantRepo;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAQueryBase;
import com.querydsl.jpa.impl.JPAQuery;
import lombok.SneakyThrows;
import org.hibernate.search.engine.search.query.SearchResult;
import org.hibernate.search.engine.search.query.dsl.SearchQueryOptionsStep;
import org.hibernate.search.mapper.orm.Search;
import org.hibernate.search.mapper.orm.search.loading.dsl.SearchLoadingOptionsStep;
import org.hibernate.search.mapper.orm.session.SearchSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

import static com.act.core.util.AppUtils.has;
import static com.act.core.util.AppUtils.toPage;

/**
 * @author djimgou
 */
@Service
public class TenantService {

    private TenantRepo repo;

    private PaysRepo etageRepo;

    @PersistenceContext
    private EntityManager em;

    @Autowired
    public TenantService(TenantRepo repo, PaysRepo etageRepo) {
        this.repo = repo;
        this.etageRepo = etageRepo;
    }

    public Page<Tenant> findBySearchText(String text, Pageable pg) {
        Page<Tenant> page = repo.findBySearchText(text, pg);
        return page;
    }

    @Transactional(/*propagation = Propagation.NESTED*/)
    public Tenant saveTenant(UUID id, TenantDto tenantDto) throws TenantNotFoundException, PaysNotFoundException {
        Tenant tenant = new Tenant();
        if (has(id)) {
            tenant = repo.findById(id).orElseThrow(TenantNotFoundException::new);
        }
        tenant.setCode(tenantDto.getCode());
        tenant.setNom(tenantDto.getNom());
        tenant.setVille(tenantDto.getVille());
        if (!(has(id) && Objects.equals(tenant.getPays().getId(), tenantDto.getPaysId()))) {
            Pays pays = etageRepo.findById(tenantDto.getPaysId()).orElseThrow(PaysNotFoundException::new);
            tenant.setPays(pays);
        }
        try{
            return repo.save(tenant);

        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    @SneakyThrows
    public Tenant createTenant(TenantDto tenantDto) {
        return saveTenant(null, tenantDto);
    }

    /**
     * Recherche avec pagination et filtre
     *
     * @param filter
     * @return
     * @throws Exception
     */
    public Page<Tenant> findBy(TenantFilterDto filter) throws Exception {
        CustomPageable cpg = new CustomPageable(filter);
        if (cpg.getSort().isUnsorted()) {
            cpg.setSort(Sort.by(Sort.Order.asc("nom")));
        }
        Page<Tenant> page;

        String name = filter.getNom();
        String code = filter.getCode();

        QTenant qDevise = QTenant.tenant;

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
    public SearchResult<Tenant> search(TenantFindDto findDto) {
        String[] fieldsSplit;
        final Class filterDtoClass = TenantFilterDto.class;
        final Class findDtoClass = TenantFindDto.class;

        if (has(findDto.getSearchKeys())) {
            fieldsSplit = findDto.getSearchKeys();
        } else {
            Set<Field> f = new HashSet<>();
            Collections.addAll(f, filterDtoClass.getDeclaredFields());
            Collections.addAll(f, findDtoClass.getDeclaredFields());
            String fields = f.stream().map(Field::getName)
                    .collect(Collectors.joining(","));
            fieldsSplit = fields.split(",");
        }
        SearchSession searchSession = Search.session(em);
        String[] finalFff = fieldsSplit;
        final SearchQueryOptionsStep<?, Tenant, SearchLoadingOptionsStep, ?, ?>
                query = searchSession.search(Tenant.class)
                .where(f -> f.match().fields(finalFff)
                        .matching(findDto.getSearchText()));
        SearchResult<Tenant> result;
        if (findDto.getSize() == 0) {
            result = query.fetchAll();
        } else {
            int offset = findDto.getPage() * findDto.getSize();
            result = query.fetch(offset, findDto.getSize());
        }
        return result;
    }

    public Optional<Tenant> findById(UUID tenantId) {
        return repo.findById(tenantId);
    }

    public void deleteById(UUID tenantId) {
        repo.deleteById(tenantId);
    }

    public List<Tenant> findAll() {
        return repo.findAll();
    }

    public Page<Tenant> findAll(Pageable pageable) {
        return repo.findAll(pageable);
    }

    public Page<Tenant> searchPageable(TenantFindDto findDto) {
        // long totalHitCount = result.total().hitCount();
        if (!has(findDto.getSearchKeys())) {
            findDto.setSearchKeys(new String[]{"code", "nom","ville"});
        }
        SearchResult<Tenant> res = search(findDto);
        Page<Tenant> p = toPage(new CustomPageable(findDto), res.hits(), (int) res.total().hitCount());
        return p;
    }
    @Transactional
    public Page<Tenant> searchPageable2(TenantFindDto filter) throws Exception {
        CustomPageable cpg = new CustomPageable(filter);
        if (cpg.getSort().isUnsorted()) {
            cpg.setSort(Sort.by(Sort.Order.asc("nom")));
        }
        Page<Tenant> page;


        QTenant qDevise = QTenant.tenant;

        JPAQuery query = new JPAQuery(em);
        JPAQueryBase exp2 = query.from(qDevise);
        List<BooleanExpression> expressionList = new ArrayList<>();
        expressionList.add(qDevise.nom.containsIgnoreCase(filter.getSearchText()));
        expressionList.add(qDevise.code.containsIgnoreCase(filter.getSearchText()));
        expressionList.add(qDevise.ville.containsIgnoreCase(filter.getSearchText()));
        expressionList.add(qDevise.pays.nom.containsIgnoreCase(filter.getSearchText()));
        expressionList.add(qDevise.pays.code.containsIgnoreCase(filter.getSearchText()));


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
