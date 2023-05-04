package com.djimgou.security.core.service;

import com.djimgou.core.exception.AppException;
import com.djimgou.core.exception.ConflitException;
import com.djimgou.core.exception.NotFoundException;
import com.djimgou.core.export.DataExportParser;
import com.djimgou.core.infra.BaseFilterDto;
import com.djimgou.core.infra.CustomPageable;
import com.djimgou.security.core.exceptions.BadConfirmPasswordException;
import com.djimgou.security.core.exceptions.UnautorizedException;
import com.djimgou.security.core.exceptions.UtilisateurNotFoundException;
import com.djimgou.security.core.model.QUtilisateur;
import com.djimgou.security.core.model.Role;
import com.djimgou.security.core.model.StatutSecurityWorkflow;
import com.djimgou.security.core.model.Utilisateur;
import com.djimgou.security.core.model.dto.role.IdDto;
import com.djimgou.security.core.model.dto.utilisateur.*;
import com.djimgou.security.core.repo.RoleRepo;
import com.djimgou.security.core.repo.UtilisateurBaseRepo;
import com.djimgou.tenantmanager.exceptions.TenantNotFoundException;
import com.djimgou.tenantmanager.model.Tenant;
import com.djimgou.tenantmanager.repository.TenantRepo;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAQueryBase;
import com.querydsl.jpa.impl.JPAQuery;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.*;
import java.util.stream.Collectors;

import static com.djimgou.core.util.AppUtils2.has;

/**
 * @author DJIMGOU NKENNE DANY MARC 08/2020
 */
@Getter
@Service("appDefaultUtilisateurService")
public class UtilisateurBdService extends AbstractSecurityBdService<Utilisateur, UtilisateurFindDto, UtilisateurFilterDto> implements UtilisateurBdServiceBase<Utilisateur, UtilisateurFindDto, UtilisateurFilterDto, UtilisateurDto, ModifierProfilDto> {
    public static final String APP_UTILISATEUR_REPO = "appUtilisateurRepo";
    @Setter
    @PersistenceContext
    EntityManager em;


    private UtilisateurBaseRepo<Utilisateur, UUID> customRepo;

    private UtilisateurBaseRepo<Utilisateur, UUID> repo;

    private TenantRepo tenantRepo;

    private RoleRepo roleRepo;

    private SecuritySessionService securitySessionService;

    private DataExportParser dataExportParser;

    public UtilisateurBdService(
            @Qualifier(APP_UTILISATEUR_REPO) Optional<UtilisateurBaseRepo<Utilisateur, UUID>> customRepo,
            @Qualifier("appDefaultUtilisateurRepo") UtilisateurBaseRepo<Utilisateur, UUID> repo,
            ApplicationContext appContext,
            TenantRepo tenantRepo, RoleRepo roleRepo, SecuritySessionService sessionService, DataExportParser dataExportParser) {
        super(repo);
        this.repo = repo;
        this.tenantRepo = tenantRepo;
        this.roleRepo = roleRepo;
        this.securitySessionService = sessionService;
        this.dataExportParser = dataExportParser;

        if (customRepo.isPresent()) {
            this.customRepo = customRepo.get();
        } else {
            if (appContext.containsBean(APP_UTILISATEUR_REPO)) {
                this.customRepo = appContext.getBean(APP_UTILISATEUR_REPO, UtilisateurBaseRepo.class);
            }
        }
    }

    public List<List<?>> exporter() {
        List<List<?>> er = dataExportParser.parse(repo.exporter());
        return er;
    }

    @Transactional
    public Utilisateur createUtilisateur(UtilisateurDto utilisateurDto) throws BadConfirmPasswordException, ConflitException, UtilisateurNotFoundException {
        return saveUtilisateur(null, utilisateurDto);
    }

    @Override
    public Utilisateur createUtilisateurGeneric(UtilisateurDto utilisateurDto) throws BadConfirmPasswordException, ConflitException, NotFoundException {
        return saveUtilisateur(null, utilisateurDto);
    }

    @Transactional
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
        Optional<UUID> optid = securitySessionService.currentUserId();
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

    public Utilisateur ajouterRoles(UUID utilisateurId, IdsDto rolesIdDto) throws UtilisateurNotFoundException {
        Utilisateur user = repo.findById(utilisateurId).orElseThrow(UtilisateurNotFoundException::new);
        if (has(rolesIdDto.getIds())) {
            Set<Role> toAdd = rolesIdDto.getIds().stream()
                    .map(role -> roleRepo.findById(role.getId()).orElse(null))
                    .collect(Collectors.toSet());

            if (user.getAuthorities() == null) {
                user.setAuthorities(toAdd);
            } else {
                user.getAuthorities().addAll(toAdd);
            }
            return save(user);
        }
        return user;
    }

    @Transactional
    public Utilisateur modifierRoles(UUID utilisateurId, IdsDto rolesIdDto) throws UtilisateurNotFoundException {
        Utilisateur user = repo.findById(utilisateurId).orElseThrow(UtilisateurNotFoundException::new);
        if (has(rolesIdDto.getIds())) {
            Set<Role> toAdd = rolesIdDto.getIds().stream()
                    .map(role -> roleRepo.findById(role.getId()).orElse(null))
                    .collect(Collectors.toSet());

            if (user.getAuthorities() == null) {
                user.setAuthorities(toAdd);
            } else {
                user.getAuthorities().clear();
                user.getAuthorities().addAll(toAdd);
            }
            return save(user);
        }
        return user;
    }

    public void updateRoleChildren(Utilisateur user, UtilisateurDto dto) {
        Set<Role> toRemove = new HashSet<>();
        Set<Role> toAdd = new HashSet<>();
        if (has(user.getAuthorities())) {
            user.getAuthorities().stream()
                    .filter(role -> dto.getAuthorities().stream().map(IdDto::getId)
                            .noneMatch(uuid -> Objects.equals(uuid, role.getId()))
                    )
                    .forEach(toRemove::add);
        }
        if (has(dto.getAuthorities())) {
            Utilisateur finalUser = user;
            Set<Role> auto = dto.getAuthorities().stream()
                    .map(privilege -> roleRepo.findById(privilege.getId()).orElse(null))
                    .collect(Collectors.toSet());

            auto.stream()
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
                    .filter(tenant -> dto.getTenants().stream().map(IdDto::getId)
                            .noneMatch(uuid -> Objects.equals(uuid, tenant.getId()))
                    )
                    .forEach(toRemove::add);
        }
        if (has(dto.getTenants())) {
            Utilisateur finalUser = user;
            Set<Tenant> tenants = dto.getTenants().stream()
                    .map(privilege -> tenantRepo.findById(privilege.getId()).orElse(null))
                    .collect(Collectors.toSet());
            tenants.stream()
                    .filter(tenant -> !finalUser.getTenants().contains(tenant))
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
        } else {
            if (!has(dto.getPasswordConfirm()) || !Objects.equals(dto.getPasswordConfirm(), dto.getPassword())) {
                throw new BadConfirmPasswordException();
            }
            dto.setPassword(dto.getEncodedPasswd());
            user.setPassword(dto.getEncodedPasswd());
            //user.setPassword(bCryptPasswordEncoder.encode(dto.getPasswordConfirm()));
        }
        updateRoleChildren(user, dto);
        updateTenantChildren(user, dto);
        checkDuplicate(id, dto);

        final Optional<Utilisateur> byUsername = getRepo().findByUsername(dto.getUsername());
        if (byUsername.isPresent() && !Objects.equals(byUsername.get().getId(), id)) {
            throw new ConflitException("Un utilisateur avec ce nom d'utilisateur existe déjà");
        }

        user.fromDto(dto);

        if (!has(id)) {
            Utilisateur conUser = securitySessionService.currentUserFromDb(this);
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

    @Override
    public Page<Utilisateur> advancedSearchBy(BaseFilterDto baseFilter) throws Exception {
        return null;
    }

    @Transactional
    @Override
    public Page<Utilisateur> findBy(UtilisateurFilterDto baseFilter) throws Exception {
        CustomPageable cpg = new CustomPageable(baseFilter);
        if (cpg.getSort().isUnsorted()) {
            cpg.setSort(Sort.by(Sort.Order.asc("username")));
        }
        Page<Utilisateur> page;
        String name = baseFilter.getNom();
        String prenom = baseFilter.getPrenom();
        String username = baseFilter.getUsername();
        String tel = baseFilter.getTelephone();
        String email = baseFilter.getEmail();
        String roleName = baseFilter.getRoleName();

        QUtilisateur qUser = QUtilisateur.utilisateur;

        JPAQuery query = new JPAQuery(em);
        JPAQueryBase exp2 = query.from(qUser);
        List<BooleanExpression> expressionList = new ArrayList<>();
        if (has(name)) {
            expressionList.add(qUser.nom.containsIgnoreCase(name));
        }
        if (has(prenom)) {
            expressionList.add(qUser.prenom.containsIgnoreCase(name));
        }
        if (has(username)) {
            expressionList.add(qUser.username.containsIgnoreCase(username));
        }
        if (has(tel)) {
            expressionList.add(qUser.telephone.containsIgnoreCase(tel));
        }
        if (has(email)) {
            expressionList.add(qUser.email.containsIgnoreCase(tel));
        }
        if (has(roleName)) {
            expressionList.add(qUser.authorities.any().name.eq(roleName));
        }


        BooleanExpression exp = expressionList.stream().reduce(null, (old, newE) -> has(old) ? old.and(newE) : newE);

        exp2.where(exp).orderBy(qUser.nom.asc());

        if (has(exp)) {
            page = getRepo().findAll(exp, cpg);
        } else {
            page = getRepo().findAll(cpg);
        }
        return page;
    }

    @Override
    public Optional<Utilisateur> findByUsername(String username) {
        return repo.findByUsername(username);
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
    public void delete(Utilisateur user) throws NotFoundException {
        if (has(user.getAuthorities())) {
            user.getAuthorities().clear();
        }
        if (has(user.getTenants())) {
            user.getTenants().clear();
        }
        save(user);
        getRepo().delete(user);
    }

    @Transactional
    @Override
    public void deleteById(UUID uuid) throws AppException {
        Utilisateur user = getRepo().findById(uuid).orElseThrow(UtilisateurNotFoundException::new);
        delete(user);
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


        return super.save(entity);
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
