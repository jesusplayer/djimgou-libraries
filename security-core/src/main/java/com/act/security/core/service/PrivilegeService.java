package com.act.security.core.service;

import com.act.security.core.exceptions.PrivilegeNotFoundException;
import com.act.security.core.model.Privilege;
import com.act.security.core.model.QPrivilege;
import com.act.security.core.model.dto.privilege.PrivilegeDto;
import com.act.security.core.model.dto.privilege.PrivilegeFilterDto;
import com.act.security.core.model.dto.privilege.PrivilegeFindDto;
import com.act.core.infra.CustomPageable;
import com.act.core.exception.NotFoundException;
import com.act.security.core.model.StatutSecurityWorkflow;
import com.act.security.core.repo.PrivilegeRepo;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAQueryBase;
import com.querydsl.jpa.impl.JPAQuery;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static com.act.core.util.AppUtils.has;

/**
 * @author DJIMGOU NKENNE DANY MARC 08/2020
 */
@Log4j2
@Service
public class PrivilegeService extends AbstractSecurityBdService<Privilege, PrivilegeFindDto, PrivilegeFilterDto> {
    @Autowired
    PrivilegeRepo repo;
    @PersistenceContext
    EntityManager em;

    public PrivilegeService() {
        super();
    }

    @Override
    public PrivilegeRepo getRepo() {
        return repo;
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
    public Page<Privilege> findBy(PrivilegeFilterDto filter) throws Exception {
        CustomPageable cpg = new CustomPageable(filter);
        if (cpg.getSort().isUnsorted()) {
            cpg.setSort(Sort.by(Sort.Order.asc("nom")));
        }
        Page<Privilege> page;
        String name = filter.getName();
        String code = filter.getCode();
        UUID parentId = filter.getParentId();

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

    public Privilege savePrivilege(UUID id, PrivilegeDto dto) throws PrivilegeNotFoundException, NotFoundException {
        Privilege priv = new Privilege();
        if (has(id)) {
            priv = repo.findById(id).orElseThrow(PrivilegeNotFoundException::new);
        }
        priv.fromDto(dto);
        if (has(dto.getParentId())) {
            Privilege parent = repo.findById(dto.getParentId()).orElseThrow(() -> new NotFoundException("ce Privilège parent est inexistant"));
            priv.setParent(parent);
        }
        return save(priv);
    }

    public Privilege createPrivilege(PrivilegeDto dto) throws PrivilegeNotFoundException, NotFoundException {
        return savePrivilege(null, dto);
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
        repo.findById(id).orElseThrow(PrivilegeNotFoundException::new);
        repo.deleteById(id);
    }

    @Transactional
    @Override
    public Privilege save(Privilege entity, Privilege oldEntity) {
        if (!entity.isNew()) {
            oldEntity = searchById(entity.getId());
        }

        // Modification d'une entité qui était valide. elle passe en dirty
        if (has(entity.getStatutCreation()) && entity.getStatutCreation().isValide()) {
            // recupération ancienne entité
            // sa dirty value avec la nouvelle donnée à enregistrer
            Privilege dirtyCopy = new Privilege();
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
        }
        return this.save(entity);
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
    public void validateEntity(Privilege entity, StatutSecurityWorkflow statutSecurityWorkflow, UUID validateurId, String commentaireAdminValidateur, UUID id) {
        repo.validateEntity(statutSecurityWorkflow, validateurId, commentaireAdminValidateur, entity.getId());
        entity = searchById(entity.getId());
        if (has(entity.getDirtyValueId())) {
            Privilege dirtyValue = searchById(entity.getDirtyValueId());

            if (statutSecurityWorkflow.isValide()) {
                dirtyValue.setStatutWorkflow(StatutSecurityWorkflow.VALIDE);
                Set<Privilege> children = dirtyValue.getEnfants().stream()
                        .collect(Collectors.toSet());

                dirtyValue.getEnfants().clear();
                entity.getEnfants().clear();

                BeanUtils.copyProperties(dirtyValue, entity, "id", "enfants");

                entity.getEnfants().addAll(children);

                entity.updateValidateur(currentUserId());
//                entity.setAdminCreateurId(null);
                entity.setDirty(null);
                entity.setDirtyValueId(null);
                repo.save(entity);

                if (has(children)) {
                    // Si la dirty value a des enfants, on se rassure que ces enfants sont retirés
                    // et après on pourra supprimer la dirty value sans risque d'avoir une erreur de contrainte BD
                    dirtyValue = repo.save(dirtyValue);
                }
                try {
                    repo.delete(dirtyValue);
                } catch (Exception e) {
                }
            }

        }
        /*if (statutSecurityWorkflow.isRejeter()) {
            entity.setDirty(null);
            entity.setDirtyValueId(null);

            if (has(entity.getDirtyValueId())) {
                Authority dirtyValue = findById(entity.getDirtyValueId());
                dirtyValue.getPrivileges().clear();
                entity.setStatutCreation(StatutSecurityWorkflow.VALIDE);
                repo.delete(dirtyValue);
            }

            repo.save(entity);
        }*/

    }

}
