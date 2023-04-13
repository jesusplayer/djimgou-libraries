package com.djimgou.security.core.service;

import com.djimgou.core.exception.AppException;
import com.djimgou.core.exception.ConflitException;
import com.djimgou.core.exception.NotFoundException;
import com.djimgou.core.export.DataExportParser;
import com.djimgou.core.infra.BaseFilterDto;
import com.djimgou.core.infra.CustomPageable;
import com.djimgou.security.core.exceptions.PrivilegeNotFoundException;
import com.djimgou.security.core.exceptions.ReadOnlyException;
import com.djimgou.security.core.model.Privilege;
import com.djimgou.security.core.model.QPrivilege;
import com.djimgou.security.core.model.Role;
import com.djimgou.security.core.model.dto.privilege.PrivilegeDto;
import com.djimgou.security.core.model.dto.privilege.PrivilegeFilterDto;
import com.djimgou.security.core.model.dto.privilege.PrivilegeFindDto;
import com.djimgou.security.core.repo.PrivilegeRepo;
import com.djimgou.security.core.repo.RoleRepo;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAQueryBase;
import com.querydsl.jpa.impl.JPAQuery;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static com.djimgou.core.util.AppUtils2.has;

/**
 * @author DJIMGOU NKENNE DANY MARC 08/2020
 */
@Log4j2
@Service
public class PrivilegeService extends AbstractSecurityBdService<Privilege, PrivilegeFindDto, PrivilegeFilterDto> {
    private PrivilegeRepo repo;

    @PersistenceContext
    EntityManager em;
    private RoleRepo roleRepo;
    private DataExportParser dataExportParser;

    @Autowired
    public PrivilegeService(PrivilegeRepo repo, RoleRepo roleRepo, DataExportParser dataExportParser) {
        super(repo);
        this.repo = repo;
        this.roleRepo = roleRepo;
        this.dataExportParser = dataExportParser;
    }

    public List<List<?>> exporter() {
        List<List<?>> er = dataExportParser.parse(repo.exporter());
        return er;
    }

    @Transactional
    public Page<Privilege> searchPageable2(PrivilegeFindDto filter) throws Exception {
        CustomPageable cpg = new CustomPageable(filter);
        if (cpg.getSort().isUnsorted()) {
            cpg.setSort(Sort.by(Sort.Order.asc("name")));
        }
        Page<Privilege> page;
        String txt = filter.getSearchText();

        QPrivilege qPrivilege = QPrivilege.privilege;

        JPAQuery query = new JPAQuery(em);
        JPAQueryBase exp2 = query.from(qPrivilege);
        List<BooleanExpression> expressionList = new ArrayList<>();
        expressionList.add(qPrivilege.name.containsIgnoreCase(txt));
        expressionList.add(qPrivilege.code.containsIgnoreCase(txt));
        expressionList.add(qPrivilege.parent.name.containsIgnoreCase(txt));
        expressionList.add(qPrivilege.parent.code.containsIgnoreCase(txt));

        BooleanExpression exp = expressionList.stream().reduce(null, (old, newE) -> has(old) ? old.or(newE) : newE);

        exp2.where(exp);//.orderBy(qPrivilege.name.asc());

        if (has(txt)) {
            page = repo.findBySearchText(txt, cpg);
            //if (has(exp)) {
            // page = repo.findAll(exp, cpg);
        } else {
            page = repo.findAll(cpg);
        }
        return page;
    }

    @Override
    public Page<Privilege> advancedSearchBy(BaseFilterDto baseFilter) throws Exception {
        return null;
    }

    @Transactional
    @Override
    public Page<Privilege> findBy(PrivilegeFilterDto baseFilter) throws Exception {
        CustomPageable cpg = new CustomPageable(baseFilter);
        if (cpg.getSort().isUnsorted()) {
            cpg.setSort(Sort.by(Sort.Order.asc("name")));
        }
        Page<Privilege> page;
        String name = baseFilter.getName();
        String code = baseFilter.getCode();
        UUID parentId = baseFilter.getParentId();

        QPrivilege qDevise = QPrivilege.privilege;

        JPAQuery query = new JPAQuery(em);
        JPAQueryBase exp2 = query.from(qDevise);
        List<BooleanExpression> expressionList = new ArrayList<>();
        if (has(name)) {
            expressionList.add(qDevise.name.containsIgnoreCase(name));
        }
        if (has(code)) {
            expressionList.add(qDevise.code.containsIgnoreCase(code));
        }
        if (has(parentId)) {
            expressionList.add(qDevise.parent.id.eq(parentId));
        }


        BooleanExpression exp = expressionList.stream().reduce(null, (old, newE) -> has(old) ? old.and(newE) : newE);

        exp2.where(exp).orderBy(qDevise.name.asc());

        if (has(exp)) {
            page = repo.findAll(exp, cpg);
        } else {
            page = repo.findAll(cpg);
        }
        return page;
    }

    public Privilege savePrivilege(UUID id, PrivilegeDto dto) throws PrivilegeNotFoundException, NotFoundException, ReadOnlyException, ConflitException {
        Privilege priv = new Privilege();
        if (has(id)) {
            priv = repo.findById(id).orElseThrow(PrivilegeNotFoundException::new);
            dto.setCode(priv.getCode());
            chackreadOnly(priv);
        } else {
            Optional<Privilege> opt = repo.findByCode(dto.getCode());
            if (opt.isPresent()) {
                throw new ConflitException("Un privilège du même code existe déjà");
            }
            Optional<Privilege> opt2 = repo.findByName(dto.getCode());
            if (opt2.isPresent()) {
                throw new ConflitException("Un privilège du même nom existe déjà");
            }
        }
        priv.fromDto(dto);
        if (has(dto.getParentId())) {
            Privilege parent = repo.findById(dto.getParentId()).orElseThrow(() -> new NotFoundException("ce Privilège parent est inexistant"));
            priv.setParent(parent);
        }
        return save(priv);
    }

    public Privilege createPrivilege(PrivilegeDto dto) throws PrivilegeNotFoundException, NotFoundException, ReadOnlyException, ConflitException {
        return savePrivilege(null, dto);
    }

    public void chackreadOnly(Privilege priv) throws ReadOnlyException {
        if (priv.getReadonlyValue() != null && priv.getReadonlyValue()) {
            throw new ReadOnlyException();
        }
    }

    /**
     * Pour enregister un privillège on parcours ses enfants pour le définir comme parent
     *
     * @param entity entite à enregistrer
     * @return nouvelle valeur enregistrée
     */
    @Transactional
    @Override
    public Privilege save(Privilege entity) {
        try {
            AtomicReference<Privilege> parent = new AtomicReference<>(entity);
            if (entity.isNew()) {
                parent.set(repo.save(entity));
            }

            if (has(parent.get().getEnfants())) {
                final Set<Privilege> collect = parent.get().getEnfants().stream().map(e -> {
                    Privilege newP = e;
                    if (!newP.isNew()) {
                        newP = repo.getOne(newP.getId());
                    }
                    newP.setParent(parent.get());
                    return repo.save(newP);
                }).collect(Collectors.toSet());
                parent.get().getEnfants().clear();
                parent.get().getEnfants().addAll(collect);
            }
            return repo.save(parent.get());
        } catch (Exception e) {
            log.error("Erreur d'enregistrement du privilège", e);
        }
        return null;
    }

    @Override
    public void deleteById(UUID id) throws PrivilegeNotFoundException {
        Privilege p = repo.findById(id).orElseThrow(PrivilegeNotFoundException::new);
        repo.deleteById(id);
    }

    @Transactional
    public void deletePrivilegeById(UUID id) throws AppException {
        Privilege privilege = repo.findById(id).orElseThrow(PrivilegeNotFoundException::new);
        List<Privilege> enfants = repo.findByParentId(privilege.getId());

        if (has(privilege.getEnfants())) {
            throw new AppException("Impossible de supprimer ce privilège car il contient " + enfants.size() + " privilèges(s) fils(" + enfants.stream().map(Privilege::getCode).collect(Collectors.joining(",")) + ")");
        }

        final List<Role> roles = roleRepo.findByPrivilegesIdIn(Arrays.asList(privilege.getId()));

        if (has(roles)) {
            throw new AppException("Impossible de supprimer ce privilège car " + roles.size() + " rôles(s) l'utilise(nt) (" + roles.stream().map(Role::getName).collect(Collectors.joining(",")) + ")");
        }

        if (has(privilege.getEnfants())) {
            privilege.getEnfants().clear();
        }

        repo.delete(privilege);
        //afterDataDeleted(privilege);
    }

    @Transactional
    @Override
    public Privilege save(Privilege entity, Privilege oldEntity) {
        return this.save(entity);
    }
}
