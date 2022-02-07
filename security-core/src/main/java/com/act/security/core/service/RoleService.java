package com.act.security.core.service;

import com.act.security.core.exceptions.PrivilegeNotFoundException;
import com.act.security.core.exceptions.RoleNotFoundException;
import com.act.security.core.model.Privilege;
import com.act.security.core.model.QRole;
import com.act.security.core.model.Role;
import com.act.security.core.model.StatutSecurityWorkflow;
import com.act.security.core.model.dto.role.RoleDto;
import com.act.security.core.model.dto.role.RoleFilterDto;
import com.act.security.core.model.dto.role.RoleFindDto;
import com.act.security.core.repo.PrivilegeRepo;
import com.act.core.infra.CustomPageable;
import com.act.core.exception.NotFoundException;
import com.act.security.core.repo.RoleRepo;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAQueryBase;
import com.querydsl.jpa.impl.JPAQuery;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.*;
import java.util.stream.Collectors;

import static com.act.core.util.AppUtils.has;


/**
 * @author DJIMGOU NKENNE DANY MARC 08/2020
 */
@Service
public class RoleService extends AbstractSecurityBdService<Role, RoleFindDto, RoleFilterDto> {
    @PersistenceContext
    EntityManager em;


    @Autowired
    RoleRepo repo;

    @Autowired
    SessionManager sessionManager;

    @Autowired
    PrivilegeRepo privilegeRepo;

    public RoleService() {
        super();
    }


    @Override
    public RoleRepo getRepo() {
        return repo;
    }

    @Override
    public Page<Role> findBy(RoleFilterDto filter) throws Exception {
        CustomPageable cpg = new CustomPageable(filter);
        if (cpg.getSort().isUnsorted()) {
            cpg.setSort(Sort.by(Sort.Order.asc("nom")));
        }
        Page<Role> page;
        String name = filter.getName();
        String desc = filter.getDescription();
        UUID parentId = filter.getParentId();

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

        if (!entity.isNew()) {
            oldEntity = searchById(entity.getId());
        }

        if (has(entity.getStatutCreation()) && entity.getStatutCreation().isValide()) {
            // recupération ancienne entité
            // sa dirty value avec la nouvelle donnée à enregistrer
            Role dirtyCopy = new Role();
            BeanUtils.copyProperties(entity, dirtyCopy, "id");
            dirtyCopy.setStatutCreation(StatutSecurityWorkflow.EN_ATTENTE_DE_VALIDATION);
            dirtyCopy.setDirty(Boolean.TRUE);
            repo.save(dirtyCopy);
            oldEntity.setStatutCreation(StatutSecurityWorkflow.EN_ATTENTE_DE_VALIDATION);
            oldEntity.setDirtyValueId(dirtyCopy.getId());
            oldEntity.updateCreateur(currentUserId());
            oldEntity.setAdminValidateurId(null);
            // je retourne la nouvelle valeur
            BeanUtils.copyProperties(oldEntity, entity, "id");


        } else {
            entity.updateCreateur(currentUserId());
            entity.setStatutCreation(StatutSecurityWorkflow.EN_ATTENTE_DE_VALIDATION);
            if (has(oldEntity)) {
                oldEntity.updateCreateur(currentUserId());
                oldEntity.setStatutCreation(StatutSecurityWorkflow.EN_ATTENTE_DE_VALIDATION);
                repo.save(oldEntity);
            }
//            }
        }
        return super.save(entity);
    }

    /**
     * * Validation entité:
     * * - Je recherche sa copie dirty je la met au statut validé
     * * - Je merge les propriétés de la copie dirty (sauf l'ID) dans l'entité
     * * - Je supprime la dirty
     *
     * @param entity                     entite
     * @param validateurId               id du validateur
     * @param commentaireAdminValidateur commentaire du validateur
     * @param id                         identifiant de l'entité
     */
    @Override
    public void validateEntity(Role entity, StatutSecurityWorkflow statutSecurityWorkflow, UUID validateurId, String commentaireAdminValidateur, UUID id) {
        repo.validateEntity(statutSecurityWorkflow, validateurId, commentaireAdminValidateur, entity.getId());
        entity = searchById(entity.getId());
        if (has(entity.getDirtyValueId())) {
            Role dirtyValue = searchById(entity.getDirtyValueId());

            if (statutSecurityWorkflow.isValide()) {
                dirtyValue.setStatutWorkflow(StatutSecurityWorkflow.VALIDE);
                BeanUtils.copyProperties(dirtyValue, entity, "id");
                entity.setPrivileges(dirtyValue.getPrivileges().stream().collect(Collectors.toSet()));
//                entity.setAdminCreateurId(null);
                entity.updateValidateur(currentUserId());
                entity.setDirty(null);
                entity.setDirtyValueId(null);
                repo.save(entity);
                dirtyValue.getPrivileges().clear();
                repo.delete(dirtyValue);
            }

        }
        if (statutSecurityWorkflow.isRejeter()) {
            entity.setDirty(null);
            entity.setDirtyValueId(null);
            entity.updateValidateur(currentUserId());

            /*
            if (has(entity.getDirtyValueId())) {
                Role dirtyValue = findById(entity.getDirtyValueId());
                dirtyValue.getPrivileges().clear();
                entity.setStatutCreation(StatutSecurityWorkflow.VALIDE);
                repo.delete(dirtyValue);
            }

            repo.save(entity);*/
        }

    }

    @Override
    public void delete(Role entity) throws RoleNotFoundException {
        if (has(entity.getDirtyValueId()) && existsById(entity.getDirtyValueId())) {
            Role oldEnt = searchById(entity.getDirtyValueId());
            oldEnt.getEnfants().clear();
            repo.delete(oldEnt);
        }
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