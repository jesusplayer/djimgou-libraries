package com.act.security.core.service;

import com.act.security.core.exceptions.BadConfirmPasswordException;
import com.act.security.core.exceptions.UnautorizedException;
import com.act.security.core.exceptions.UtilisateurNotFoundException;
import com.act.security.core.model.QUtilisateur;
import com.act.security.core.model.Role;
import com.act.security.core.model.StatutSecurityWorkflow;
import com.act.security.core.model.Utilisateur;
import com.act.security.core.model.dto.utilisateur.ModifierProfilDto;
import com.act.security.core.model.dto.utilisateur.UtilisateurDto;
import com.act.security.core.model.dto.utilisateur.UtilisateurFilterDto;
import com.act.security.core.model.dto.utilisateur.UtilisateurFindDto;
import com.act.core.infra.CustomPageable;
import com.act.core.exception.ConflitException;
import com.act.core.exception.NotFoundException;
import com.act.security.core.repo.RoleRepo;
import com.act.security.core.repo.UtilisateurBaseRepo;
import com.act.tenantmanager.exceptions.TenantNotFoundException;
import com.act.tenantmanager.model.Tenant;
import com.act.tenantmanager.repository.TenantRepo;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAQueryBase;
import com.querydsl.jpa.impl.JPAQuery;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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
@Service("appDefaultUtilisateurService")
public class UtilisateurBdService extends AbstractSecurityBdService<Utilisateur, UtilisateurFindDto, UtilisateurFilterDto> implements UtilisateurBdServiceBase<Utilisateur, UtilisateurFindDto, UtilisateurFilterDto, UtilisateurDto, ModifierProfilDto> {

    @Qualifier("appUtilisateurRepo")
    @Autowired(required = false)
    UtilisateurBaseRepo<Utilisateur, UUID> customRepo;

    @Qualifier("appDefaultUtilisateurRepo")
    @Autowired()
    UtilisateurBaseRepo<Utilisateur, UUID> repo;

    @Autowired
    TenantRepo tenantRepo;

    @Autowired
    RoleRepo roleRepo;

    @PersistenceContext
    EntityManager em;
/*
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;*/

    @Autowired
    SecuritySessionService sessionService;

    public UtilisateurBdService() {
        super();
    }

    public Utilisateur createUtilisateur(UtilisateurDto utilisateurDto) throws BadConfirmPasswordException, ConflitException, UtilisateurNotFoundException {
        return saveUtilisateur(null, utilisateurDto);
    }

    public Utilisateur createCompteUtilisateur(UtilisateurDto utilisateurDto) throws BadConfirmPasswordException, ConflitException, UtilisateurNotFoundException {
        return saveUtilisateur(null, utilisateurDto);
    }

    public void checkDuplicate(UUID id, ModifierProfilDto dto) throws ConflitException {
        final Optional<Utilisateur> oneByEmail = getRepo().findOneByEmail(dto.getEmail());
        if (oneByEmail.isPresent() && !Objects.equals(oneByEmail.get().getId(), id)) {
            throw new ConflitException("Un utilisateur avec cet email existe déjà");
        }

        final Optional<Utilisateur> oneByNomAndPrenom = getRepo().findOneByNomAndPrenom(dto.getNom(), dto.getPrenom());
        if (oneByNomAndPrenom.isPresent() && !Objects.equals(oneByNomAndPrenom.get().getId(), id)) {
            throw new ConflitException("Un utilisateur avec cet nom et prenom existe déjà");
        }
       /* final Boolean oneByTelephone = getRepo().existsByTelephone(dto.getTelephone());
        if (has(dto.getTelephone()) &&
                oneByTelephone.isPresent() && !Objects.equals(oneByTelephone.get().getId(), id)) {
            throw new ConflitException("Un utilisateur avec télephone existe déjà");
        }*/
    }

    public Utilisateur modifierProfil(ModifierProfilDto dto) throws UtilisateurNotFoundException, ConflitException, UnautorizedException {
        Optional<UUID> optid = sessionService.currentUserId();
        UUID id = optid.orElseThrow(UnautorizedException::new);
        Utilisateur user = getRepo().findById(id).orElseThrow(UtilisateurNotFoundException::new);
        checkDuplicate(id, dto);
        user.fromDto(dto);
        return save(user);
    }

    @Transactional
    public Utilisateur addTenant(UUID utilisateurId, UUID tenantId) throws UtilisateurNotFoundException, TenantNotFoundException {
        Utilisateur user = getRepo().findById(utilisateurId).orElseThrow(UtilisateurNotFoundException::new);
        Tenant tenant = tenantRepo.findById(tenantId).orElseThrow(TenantNotFoundException::new);
        if (user.getTenants() == null) {
            user.setTenants(new HashSet<>());
        }
        user.getTenants().add(tenant);
        return user;
    }

    public void updateRoleChildren(Utilisateur user, UtilisateurDto dto) {
        Set<Role> toRemove = new HashSet<>();
        Set<Role> toAdd = new HashSet<>();
        if (has(user.getAuthorities())) {
            user.getAuthorities().stream()
                    .filter(privilege -> !dto.getAuthorities().contains(privilege))
                    .forEach(toRemove::add);
        }
        if (has(dto.getAuthorities())) {
            Utilisateur finalUser = user;
            dto.setAuthorities(
                    dto.getAuthorities().stream()
                            .map(privilege -> roleRepo.findById(privilege.getId()).orElse(null))
                            .collect(Collectors.toSet())
            );
            dto.getAuthorities().stream()
                    .filter(privilege -> !finalUser.getAuthorities().contains(privilege))
                    .forEach(toAdd::add);
        }

        if (!toRemove.isEmpty()) {
            toRemove.forEach(user.getAuthorities()::remove);
        }
        if (!toAdd.isEmpty()) {
            if (user.getAuthorities() == null) {
                user.setAuthorities(toAdd);
            } else {
                user.getAuthorities().addAll(toAdd);
            }
        }
    }

    public void updateTenantChildren(Utilisateur user, UtilisateurDto dto) {
        Set<Tenant> toRemove = new HashSet<>();
        Set<Tenant> toAdd = new HashSet<>();
        if (has(user.getTenants())) {
            user.getTenants().stream()
                    .filter(privilege -> !dto.getTenants().contains(privilege))
                    .forEach(toRemove::add);
        }
        if (has(dto.getTenants())) {
            Utilisateur finalUser = user;
            dto.setTenants(
                    dto.getTenants().stream()
                            .map(privilege -> tenantRepo.findById(privilege.getId()).orElse(null))
                            .collect(Collectors.toSet())
            );
            dto.getTenants().stream()
                    .filter(privilege -> !finalUser.getTenants().contains(privilege))
                    .forEach(toAdd::add);
        }

        if (!toRemove.isEmpty()) {
            toRemove.forEach(user.getTenants()::remove);
        }
        if (!toAdd.isEmpty()) {
            if (user.getTenants() == null) {
                user.setTenants(toAdd);
            } else {
                user.getTenants().addAll(toAdd);
            }
        }
    }

    @Transactional
    public Utilisateur saveUtilisateur(UUID id, UtilisateurDto dto) throws UtilisateurNotFoundException, BadConfirmPasswordException, ConflitException {
        Utilisateur user = new Utilisateur();
        if (has(id)) {
            user = getRepo().findById(id).orElseThrow(UtilisateurNotFoundException::new);
            updateRoleChildren(user, dto);
            updateTenantChildren(user, dto);
        } else {
            if (!has(dto.getPasswordConfirm()) || !Objects.equals(dto.getPasswordConfirm(), dto.getPassword())) {
                throw new BadConfirmPasswordException();
            }
            //user.setPassword(bCryptPasswordEncoder.encode(dto.getPasswordConfirm()));
        }
        checkDuplicate(id, dto);

        final Optional<Utilisateur> byUsername = getRepo().findByUsername(dto.getUsername());
        if (byUsername.isPresent() && !Objects.equals(byUsername.get().getId(), id)) {
            throw new ConflitException("Un utilisateur avec ce nom d'utilisateur existe déjà");
        }
        
        user.fromDto(dto);

        if (!has(id)) {
            Utilisateur conUser = sessionService.currentUserFromDb();
            user.setAdminCreateurId(has(conUser) ? conUser.getId() : user.getAdminCreateurId());
/*            if (!user.getEnabled() && has(user) && user.getStatutCreation().equals(StatutSecurityWorkflow.VALIDE)) {
                user.setEnabled(Boolean.TRUE);
                authenticationService.sendPaswordToUser(user, user.fullPassword());
            }*/
        }

        return save(user);
    }

    @Transactional
    public Page<Utilisateur> searchPageable2(UtilisateurFindDto filter) throws Exception {
        CustomPageable cpg = new CustomPageable(filter);
        if (cpg.getSort().isUnsorted()) {
            cpg.setSort(Sort.by(Sort.Order.asc("username")));
        }
        Page<Utilisateur> page;
        String txt = filter.getSearchText();

        QUtilisateur qPrivilege = QUtilisateur.utilisateur;

        JPAQuery query = new JPAQuery(em);
        JPAQueryBase exp2 = query.from(qPrivilege);
        List<BooleanExpression> expressionList = new ArrayList<>();
        expressionList.add(qPrivilege.username.containsIgnoreCase(txt));
        expressionList.add(qPrivilege.nom.containsIgnoreCase(txt));
        expressionList.add(qPrivilege.prenom.containsIgnoreCase(txt));
        expressionList.add(qPrivilege.telephone.containsIgnoreCase(txt));
        expressionList.add(qPrivilege.email.containsIgnoreCase(txt));

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

    @Transactional
    @Override
    public Page<Utilisateur> findBy(UtilisateurFilterDto filter) throws Exception {
        CustomPageable cpg = new CustomPageable(filter);
        if (cpg.getSort().isUnsorted()) {
            cpg.setSort(Sort.by(Sort.Order.asc("nom")));
        }
        Page<Utilisateur> page;
        String name = filter.getNom();
        String prenom = filter.getPrenom();
        String username = filter.getUsername();
        String tel = filter.getTelephone();
        String email = filter.getEmail();

        QUtilisateur qDevise = QUtilisateur.utilisateur;

        JPAQuery query = new JPAQuery(em);
        JPAQueryBase exp2 = query.from(qDevise);
        List<BooleanExpression> expressionList = new ArrayList<>();
        if (has(name)) {
            expressionList.add(qDevise.nom.containsIgnoreCase(name));
        }
        if (has(prenom)) {
            expressionList.add(qDevise.prenom.containsIgnoreCase(name));
        }
        if (has(username)) {
            expressionList.add(qDevise.username.containsIgnoreCase(username));
        }
        if (has(tel)) {
            expressionList.add(qDevise.telephone.containsIgnoreCase(tel));
        }
        if (has(email)) {
            expressionList.add(qDevise.email.containsIgnoreCase(tel));
        }


        BooleanExpression exp = expressionList.stream().reduce(null, (old, newE) -> has(old) ? old.and(newE) : newE);

        exp2.where(exp).orderBy(qDevise.nom.asc());

        if (has(exp)) {
            page = getRepo().findAll(exp, cpg);
        } else {
            page = getRepo().findAll(cpg);
        }
        return page;
    }

    @Override
    public UtilisateurBaseRepo<Utilisateur, UUID> getRepo() {
        return has(customRepo) ? customRepo : repo;
    }


    @Transactional
    public void validerUtilisateur(
            Boolean enabled,
            String comment,
            StatutSecurityWorkflow statutSecurityWorkflow,
            UUID idUser,
            UUID idWf
    ) {

        getRepo().validerUtilisateur(enabled, idUser);
//        repo.validateEntity(comment, statutSecurityWorkflow, idWf);
    }

    @Transactional
    public void updateProfil(Utilisateur user) {
        getRepo().updateProfil(user.getNom(), user.getPrenom(), user.getUsername(), user.getEmail(), user.getId());
    }

    @Override
    public void delete(Utilisateur entity) throws NotFoundException {
        if (has(entity.getDirtyValueId()) && existsById(entity.getDirtyValueId())) {
            Utilisateur oldEnt = searchById(entity.getDirtyValueId());
            oldEnt.getAuthorities().clear();
            getRepo().delete(oldEnt);
        }
        entity.getAuthorities().clear();
        getRepo().delete(entity);
    }

    /**
     * Change le mot de passe
     *
     * @param encPassword mot de passe encrypté
     * @param utilisateur utilisateur
     */
    @Transactional
    public void changePassword(String encPassword, Utilisateur utilisateur) {
        getRepo().changePassword(encPassword, utilisateur.getId(), Boolean.TRUE);
    }

    @Transactional
    public void changePassword(UUID userId, String encPassword) {
        getRepo().changePassword(encPassword, userId, Boolean.TRUE);
    }

    @Transactional
    public void changeUsername(UUID userId, String username) {
        getRepo().changeUsername(userId, username);
    }

    @Transactional
    public void checkDuplicateUserName(String username, UUID userId) throws ConflitException {
        Optional<Utilisateur> r = getRepo().findByUsernameAndIdNot(username, userId);
        if (r.isPresent()) {
            throw new ConflitException("Ce nom d'utilisateur est déjà utilisé");
        }
    }

    @Transactional
    @Override
    public Utilisateur save(Utilisateur entity, Utilisateur oldEntity) {
        // Modification d'une entité qui était valide. elle passe en dirty
        if (entity.isNew()) {
            Optional<Utilisateur> opt = getRepo().findByUsername(entity.getUsername());
            if (opt.isPresent()) {
                //Utils.addDetailMessage("Cet utilisateur existe déjà ", FacesMessage.SEVERITY_ERROR);
            }
        }
        if (!entity.isNew()) {
            oldEntity = searchById(entity.getId());
        }
        if (has(entity.getStatutCreation()) && entity.getStatutCreation().isValide()) {
            // recupération ancienne entité

            // sa dirty value avec la nouvelle donnée à enregistrer
            Utilisateur dirtyCopy = new Utilisateur();
            BeanUtils.copyProperties(entity, dirtyCopy, "id");
            dirtyCopy.setStatutCreation(StatutSecurityWorkflow.EN_ATTENTE_DE_VALIDATION);
            dirtyCopy.setDirty(Boolean.TRUE);
            getRepo().save(dirtyCopy);
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
                getRepo().save(oldEntity);
            }
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
    public void validateEntity(Utilisateur entity, StatutSecurityWorkflow statutSecurityWorkflow, UUID validateurId, String commentaireAdminValidateur, UUID id) {
        getRepo().validateEntity(statutSecurityWorkflow, validateurId, commentaireAdminValidateur, entity.getId(), statutSecurityWorkflow.isValide());
        entity = searchById(entity.getId());
        if (has(entity.getDirtyValueId())) {
            Utilisateur dirtyValue = searchById(entity.getDirtyValueId());

            if (statutSecurityWorkflow.isValide()) {
                dirtyValue.setStatutWorkflow(StatutSecurityWorkflow.VALIDE);
                Set<Role> authorities = dirtyValue.getAuthorities().stream().collect(Collectors.toSet());

                dirtyValue.getAuthorities().clear();
                entity.getAuthorities().clear();

                BeanUtils.copyProperties(dirtyValue, entity, "id", "authorities");
                entity.getAuthorities().addAll(authorities);
                entity.updateValidateur(currentUserId());
                entity.setDirty(null);
                entity.setDirtyValueId(null);
                entity.setEnabled(Boolean.TRUE);
                getRepo().save(entity);
                getRepo().delete(dirtyValue);
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

    @Transactional
    public void activer(UUID utilisateurId) {
        repo.validerUtilisateur(true, utilisateurId);
    }

    @Transactional
    public void desactiver(UUID utilisateurId) {
        repo.validerUtilisateur(false, utilisateurId);
    }
}
