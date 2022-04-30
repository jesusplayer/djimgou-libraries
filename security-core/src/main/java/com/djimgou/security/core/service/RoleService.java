package com.djimgou.security.core.service;

import com.djimgou.core.exception.NotFoundException;
import com.djimgou.core.infra.CustomPageable;
import com.djimgou.security.core.exceptions.PrivilegeNotFoundException;
import com.djimgou.security.core.exceptions.RoleNotFoundException;
import com.djimgou.security.core.model.Privilege;
import com.djimgou.security.core.model.QRole;
import com.djimgou.security.core.model.Role;
import com.djimgou.security.core.model.dto.role.RoleDto;
import com.djimgou.security.core.model.dto.role.RoleFilterDto;
import com.djimgou.security.core.model.dto.role.RoleFindDto;
import com.djimgou.security.core.repo.PrivilegeRepo;
import com.djimgou.security.core.repo.RoleRepo;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAQueryBase;
import com.querydsl.jpa.impl.JPAQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.*;
import java.util.stream.Collectors;

import static com.djimgou.core.util.AppUtils.has;


/**
 * @author DJIMGOU NKENNE DANY MARC 08/2020
 */
@Service
public class RoleService extends AbstractSecurityBdService<Role, RoleFindDto, RoleFilterDto> {
    @PersistenceContext
    EntityManager em;


    private RoleRepo repo;

    private SessionManager sessionManager;

    private PrivilegeRepo privilegeRepo;

    public RoleService(RoleRepo repo, SessionManager sessionManager, PrivilegeRepo privilegeRepo) {
        super(repo);
        this.repo = repo;
        this.sessionManager = sessionManager;
        this.privilegeRepo = privilegeRepo;
    }

    @Transactional
    @Override
    public Page<Role> findBy(RoleFilterDto baseFilter) throws Exception {
        CustomPageable cpg = new CustomPageable(baseFilter);
        if (cpg.getSort().isUnsorted()) {
            cpg.setSort(Sort.by(Sort.Order.asc("nom")));
        }
        Page<Role> page;
        String name = baseFilter.getName();
        String desc = baseFilter.getDescription();
        UUID parentId = baseFilter.getParentId();

        QRole qRole = QRole.role;

        JPAQuery query = new JPAQuery(em);
        JPAQueryBase exp2 = query.from(qRole);
        List<BooleanExpression> expressionList = new ArrayList<>();
        if (has(name)) {
            expressionList.add(qRole.name.containsIgnoreCase(name));
        }
        if (has(desc)) {
            expressionList.add(qRole.description.containsIgnoreCase(desc));
        }
        if (has(parentId)) {
            expressionList.add(qRole.parent.id.eq(parentId));
        }


        BooleanExpression exp = expressionList.stream().reduce(null, (old, newE) -> has(old) ? old.and(newE) : newE);

        exp2.where(exp).orderBy(qRole.name.asc());

        if (has(exp)) {
            page = repo.findAll(exp, cpg);
        } else {
            page = repo.findAll(cpg);
        }
        return page;
    }

    public void updateChildren(Role role, RoleDto dto) {
        Set<Privilege> toRemove = new HashSet<>();
        Set<Privilege> toAdd = new HashSet<>();
        if (has(role.getPrivileges())) {
            role.getPrivileges().stream()
                    .filter(privilege -> !dto.getPrivileges().contains(privilege))
                    .forEach(toRemove::add);
        }
        if (has(dto.getPrivileges())) {
            Role finalRole = role;
            dto.setPrivileges(
                    dto.getPrivileges().stream()
                            .map(privilege -> privilegeRepo.findById(privilege.getId()).orElse(null))
                            .collect(Collectors.toSet())
            );
            dto.getPrivileges().stream()
                    .filter(privilege -> !finalRole.getPrivileges().contains(privilege))
                    .forEach(toAdd::add);
        }

        if (!toRemove.isEmpty()) {
            toRemove.forEach(role.getPrivileges()::remove);
        }
        if (!toAdd.isEmpty()) {
            if (role.getPrivileges() == null) {
                role.setPrivileges(toAdd);
            } else {
                role.getPrivileges().addAll(toAdd);
            }
        }
    }

    public Role saveRole(UUID id, RoleDto dto) throws RoleNotFoundException, NotFoundException {
        Role role = new Role();
        if (has(id)) {
            role = repo.findById(id).orElseThrow(RoleNotFoundException::new);
            updateChildren(role, dto);
        }
        role.fromDto(dto, "privileges", "enfants");

        if (has(dto.getParentId())) {
            Role parent = repo.findById(dto.getParentId()).orElseThrow(() -> new RoleNotFoundException("ce Rôle parent est inexistant"));
            role.setParent(parent);
        }
        Role r = save(role);
        afterDataSaved(r);
        return r;
    }

    @Transactional
    public Role addPrivilege(UUID roleId, UUID privilegeId) throws NotFoundException {
        Role role = findById(roleId).orElseThrow(RoleNotFoundException::new);
        Privilege priv = privilegeRepo.findById(privilegeId).orElseThrow(PrivilegeNotFoundException::new);
        role.getPrivileges().add(priv);
        return save(role);
    }

    @Transactional
    public Role addPrivileges(UUID roleId, List<UUID> privilegeIds) throws NotFoundException {
        Role role = findById(roleId).orElseThrow(RoleNotFoundException::new);
        List<Privilege> privileges = new ArrayList<>();
        for (UUID uuid : privilegeIds) {
            Privilege privilege = privilegeRepo.findById(uuid).orElseThrow(PrivilegeNotFoundException::new);
            privileges.add(privilege);
        }
        role.getPrivileges().addAll(privileges);
        return role;
    }

    public Role createRole(RoleDto dto) throws RoleNotFoundException, NotFoundException {
        return saveRole(null, dto);
    }

    /**
     * 1. Je liste uniquement les entités qui sont validés
     * <p>
     * 2. Avant tout changement sur une entité VALIDE:
     * - Je recupère son ancienne donnée(en BD ou avant de save)
     * - Je modifie sa dirty value avec la nouvelle donnée à enregistrer
     * - Je copie les information de l'entité résultante
     * <p>
     * 3. Modification d'une entité
     * - Je charge la dirty value et j'effectue les modifs dessus
     * <p>
     * Validation entité:
     * - Je recherche sa copie je la met au statut validé
     * - Je merge les propriétés de la copie(sauf l'ID) dans l'entité
     * - Je supprime la dirty
     *
     * @param entity    objet à enregistrer
     * @param oldEntity ancienne valeur de l'objet à enregistrer
     */
    @Transactional
    @Override
    public Role save(Role entity, Role oldEntity) {
        // Modification d'une entité qui était valide. elle passe en dirty
        return super.save(entity);
    }

    @Override
    public void delete(Role entity) throws RoleNotFoundException {
        entity.getEnfants().clear();
        repo.delete(entity);
        afterDataDeleted(entity);
    }

    @Override
    public void deleteById(UUID id) throws RoleNotFoundException {
        Role role = repo.findById(id).orElseThrow(RoleNotFoundException::new);
        delete(role);
    }

    public void afterDataSaved(Role data) {
        sessionManager.authorityUpdated();
    }

    public void afterDataDeleted(Object data) {
        sessionManager.authorityUpdated();
    }

    @Transactional
    public Page<Role> searchPageable2(RoleFindDto filter) throws Exception {
        CustomPageable cpg = new CustomPageable(filter);
        if (cpg.getSort().isUnsorted()) {
            cpg.setSort(Sort.by(Sort.Order.asc("name")));
        }
        Page<Role> page;
        String txt = filter.getSearchText();

        QRole qPrivilege = QRole.role;

        JPAQuery query = new JPAQuery(em);
        JPAQueryBase exp2 = query.from(qPrivilege);
        List<BooleanExpression> expressionList = new ArrayList<>();
        expressionList.add(qPrivilege.name.containsIgnoreCase(txt));
        expressionList.add(qPrivilege.description.containsIgnoreCase(txt));
        expressionList.add(qPrivilege.parent.name.eq(txt));

        BooleanExpression exp = expressionList.stream().reduce(null, (old, newE) -> has(old) ? old.or(newE) : newE);

        exp2.where(exp);//.orderBy(qPrivilege.name.asc());

        if (has(txt)) {
            page = repo.findBySearchText(txt, cpg);

            // if (has(exp)) {
            //page = repo.findAll(exp, cpg);
        } else {
            page = repo.findAll(cpg);
        }
        return page;
    }

}
