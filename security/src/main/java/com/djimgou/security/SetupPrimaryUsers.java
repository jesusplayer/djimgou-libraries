package com.djimgou.security;

import com.djimgou.core.util.AppUtils;
import com.djimgou.security.core.AppSecurityConfig;
import com.djimgou.security.core.model.*;
import com.djimgou.security.core.model.dto.role.IdDto;
import com.djimgou.security.core.model.dto.utilisateur.ModifierProfilDto;
import com.djimgou.security.core.model.dto.utilisateur.UtilisateurDto;
import com.djimgou.security.core.model.dto.utilisateur.UtilisateurFilterDto;
import com.djimgou.security.core.model.dto.utilisateur.UtilisateurFindDto;
import com.djimgou.security.core.repo.ConfirmationTokenRepo;
import com.djimgou.security.core.repo.PrivilegeRepo;
import com.djimgou.security.core.repo.RoleRepo;
import com.djimgou.security.core.repo.UtilisateurRepo;
import com.djimgou.security.core.service.UtilisateurBdServiceBase;
import com.djimgou.security.enpoints.EndPointsRegistry;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.collections4.SetUtils;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static com.djimgou.core.util.AppUtils.has;


/**
 * @author DJIMGOU NKENNE DANY MARC 08/2020
 * compossant d'initialisation des utilisateurs par défaut dans l'application
 */
@Log4j2
@Component
class SetupPrimaryUsers implements ApplicationListener<ContextRefreshedEvent> {
    public static final String APP_UTILISATEUR_SERVICE = "appUtilisateurService";
    boolean alreadySetup = false;

    @Value("${auth.defaultPrivilegesCreation:}")
    String defaultPrivilegesCreation;

    @Autowired
    AppUtils appUtils;

    @Autowired
    AppSecurityConfig appSecurityConfig;

    @Autowired
    private UtilisateurRepo userRepository;

    @Autowired
    private RoleRepo roleRepository;

    @Autowired
    private PrivilegeRepo privilegeRepository;

    @Autowired
    private ConfirmationTokenRepo confirmationTokenRepo;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    EndPointsRegistry endPointsRegistry;

    @Autowired
    UtilisateurBdServiceBase<Utilisateur, UtilisateurFindDto, UtilisateurFilterDto, UtilisateurDto, ModifierProfilDto> utilisateurBdService;

    @Qualifier(APP_UTILISATEUR_SERVICE)
    @Autowired(required = false)
    UtilisateurBdServiceBase<? extends Utilisateur, UtilisateurFindDto, UtilisateurFilterDto, UtilisateurDto, ModifierProfilDto> customBdService;

    @Autowired
    ApplicationContext appContext;

    public UtilisateurBdServiceBase<Utilisateur, UtilisateurFindDto, UtilisateurFilterDto, UtilisateurDto, ModifierProfilDto>
    getService() {
        if (appContext.containsBean(APP_UTILISATEUR_SERVICE) && !has(customBdService)) {
            customBdService = appContext.getBean(APP_UTILISATEUR_SERVICE, UtilisateurBdServiceBase.class);
        }
        return AppUtils.has(customBdService) ? (UtilisateurBdServiceBase<Utilisateur, UtilisateurFindDto, UtilisateurFilterDto, UtilisateurDto, ModifierProfilDto>) customBdService : utilisateurBdService;
    }

    public void initDefaultPriv2() {
        endPointsRegistry.endPoints().stream().map(endPoint -> {
            Privilege fullAccess = new Privilege();
            fullAccess.setCode(endPoint.getName());
            fullAccess.setName(endPoint.getName());
            fullAccess.setUrl(endPoint.toSecurityUrl());
            fullAccess.setDescription(endPoint.getDescription());
            return fullAccess;
        }).forEach(this::createPrivilegeIfNotFound);
        Privilege fullAccess = new Privilege();
        fullAccess.setCode(PrivileEvaluator.FULL_ACCESS);
        fullAccess.setName("Avoir tous les droits");
        fullAccess.setDescription("Privilège qui permet d'avoir tous les droits dans l'application");
        createPrivilegeIfNotFound(fullAccess);
    }

   /* @Transactional
    public void initDefaultPriv() {
        // pour filter
        Function<Class, Predicate<Class>> filterFn = (target) -> (c) -> {
            if (c.getGenericSuperclass() instanceof ParameterizedType) {
                ParameterizedType pt = (ParameterizedType) c.getGenericSuperclass();
                String name = pt.getRawType().getTypeName();
                return name.equals(target.getCanonicalName());
            }
            return false;
        };
        // Récupération de toutes les entités
        Set<Class> entitesList = new HashSet<>();

        entitesList.addAll(appUtils.getClasses(appSecurityConfig.businessEntityPackage()));
        entitesList.addAll(appUtils.getClasses(Utilisateur.class.getPackage().getName(), new Class[]{
                ConfirmationToken.class
        }));

        entitesList.forEach(c -> {
            PrivileEvaluator pev = new PrivileEvaluator(c);
            Privilege parent = createPrivilegeIfNotFound(pev.doAll);
            //pev.read.setParent(parent);
            Privilege rd = createPrivilegeIfNotFound(pev.read);
            //pev.create.setParent(parent);
            Privilege cr = createPrivilegeIfNotFound(pev.create);
            //pev.update.setParent(parent);
            Privilege up = createPrivilegeIfNotFound(pev.update);
            //pev.delete.setParent(parent);
            Privilege dl = createPrivilegeIfNotFound(pev.delete);

        });

        Privilege fullAccess = new Privilege();
        fullAccess.setCode(PrivileEvaluator.FULL_ACCESS);
        fullAccess.setName("Avoir tous les droits");
        fullAccess.setDescription("Privilège qui permet d'avoir tous les droits dans l'application");
        createPrivilegeIfNotFound(fullAccess);

    }*/


    @Override
    @Transactional
    public void onApplicationEvent(ContextRefreshedEvent event) {

        if (alreadySetup)
            return;

        if (defaultPrivilegesCreation.equals("deleteAndCreate")) {
            privilegeRepository.deleteAllInBatch();
            // privilegeRepository.findAll().forEach(p->privilegeRepository.deleteById(p.getId()));
        }
        if (!has(defaultPrivilegesCreation) || (!defaultPrivilegesCreation.equals("disabled") || defaultPrivilegesCreation.isEmpty())) {
            initDefaultPriv2();

            //PrivileEvaluator pe = new PrivileEvaluator(Privilege.class);
            Privilege fullPrivilege
                    = privilegeRepository.findByCode(PrivileEvaluator.FULL_ACCESS).orElse(null);


/*
            Set<Privilege> adminPrivileges = SetUtils.hashSet(fullPrivilege);
            //Role userAuth = createAuthorityIfNotFound("ROLE_USER", SetUtils.hashSet(paramPrivilege), null);
            createAuthorityIfNotFound("ROLE_ADMIN", adminPrivileges,null);

*/
            //Role userRole = roleRepository.findByName("ROLE_USER");


        /*LocalDateTime ldt = LocalDateTime.now().minusDays(2);
        ct.setCreatedDate(Date.from(ldt.atZone(ZoneId.systemDefault()).toInstant()));*/

            if (defaultPrivilegesCreation.equals("deleteAndCreate")) {
                String msg = "Vous avez configuré la propriété auth.defaultPrivilegesCreation=deleteAndCreate " +
                        "celle si implique la suppression de tous les privillège par défaut et leur création. " +
                        "Les nouveaux privillèges on été créés, Mais nous vous suggérons de changer à present cette valeur" +
                        " et utiliser: disabled(pour désactiver) ";
                System.err.println(msg);
                log.warn("");
            }
        }

        alreadySetup = true;
    }

    @SneakyThrows
    @Transactional
    Utilisateur createUserIfNotFound(String nom, String prenom, String username, String password, Set<Role> authorities, Boolean enabled) {
        Optional<Utilisateur> opt = getService().findByUsername(username);
        Optional<Utilisateur> opt2 = userRepository.findByUsername(username);
        Utilisateur user = null;
        if (opt.isPresent() || opt2.isPresent()) {

            if (opt.isPresent()) {
                user = opt.get();
            }
            if (opt2.isPresent()) {
                log.info("Utilisateur déjà existant mais plutot dans le discriminateur Utilisateur." + username);
                user = opt2.get();
            }
        } else {
            UtilisateurDto userDto = new UtilisateurDto();

            userDto.setNom(nom);
            userDto.setPrenom(prenom);
            userDto.setUsername(username);
            userDto.setEmail(username);
            userDto.setPassword(password);
            userDto.setPasswordConfirm(password);
            userDto.setAuthorities(authorities.stream().map(role -> {
                IdDto is = new IdDto();
                is.setId(role.getId());
                return is;
            }).collect(Collectors.toSet()));
            try {
                userDto.setEncodedPasswd(bCryptPasswordEncoder.encode(password));
                user = getService().createUtilisateurGeneric(userDto);
                getService().activer(user.getId());
            } catch (DataIntegrityViolationException | ConstraintViolationException e) {

            }
        }
        return user;
    }
/*

    @Transactional
    Utilisateur createUserIfNotFound(Utilisateur utilisateur) {
        Optional<Utilisateur> opt = userRepository.findByUsername(utilisateur.getUsername());
        Utilisateur user;
        if (opt.isPresent()) {
            user = opt.get();
        } else {
            String en = bCryptPasswordEncoder.encode(utilisateur.getPassword());
            utilisateur.setPassword(*/
    /*"{bcrypt}"+*//*
en);
            utilisateur.setEnabled(true);
            user = userRepository.save(utilisateur);
        }
        return user;
    }
*/

    @Transactional
    Privilege createPrivilegeIfNotFound(String name) {
        return createPrivilegeIfNotFound(name, name, "", null);
    }

    @Transactional
    Privilege createPrivilegeIfNotFound(String code, String name, String description, Privilege parent) {
        Optional<Privilege> opt = privilegeRepository.findByCode(name);
        Privilege privilege;
        if (opt.isPresent()) {
            privilege = opt.get();
            /*if (defaultPrivilegesCreation.equals("deleteAndCreate")) {
                privilegeRepository.delete(privilege);
                privilege = createPrivilege(code, name, description, parent);
            }*/
        } else {
            privilege = createPrivilege(code, name, description, parent);
        }
        return privilege;
    }

    @Transactional
    Privilege createPrivilege(String code, String name, String description, Privilege parent) {
        Privilege p = new Privilege();
        p.setName(name);
        p.setCode(code);
        p.setReadonlyValue(true);
        p.setDescription(description);
        p.setParent(parent);
        return privilegeRepository.save(p);
    }

    @Transactional
    Privilege createPrivilegeIfNotFound(Privilege priv) {
        Optional<Privilege> opt = privilegeRepository.findByCode(priv.getCode());
        Privilege privilege = null;
        if (opt.isPresent()) {
            Privilege oldPriv = opt.get();
            if (!Objects.equals(oldPriv.getDescription(), priv.getDescription())) {
                oldPriv.setDescription(priv.getDescription());
                log.info("Modification de l'Url du priv: " + priv.getDescription());
            }
            if (!Objects.equals(oldPriv.getUrl(), priv.getUrl())) {
                oldPriv.setUrl(priv.getUrl());
                log.info("Modification de la description du priv: " + priv.getUrl());
            }
            privilege = oldPriv;
        } else {
            priv.setReadonlyValue(true);
            try {
                privilege = privilegeRepository.save(priv);
                log.info("PrivilegeCréé " + privilege.getUrl());
            } catch (DataIntegrityViolationException | ConstraintViolationException e) {

            }

        }
        return privilege;
    }

    @Transactional
    Role createAuthorityIfNotFound(String authorityName, Set<Privilege> privileges, Role parent) {
        Role role = roleRepository.findByName(authorityName);
        if (role == null) {
            role = new Role(authorityName);
        }
        role.setReadonlyValue(true);
        role.setPrivileges(privileges);
        role.setParent(parent);
        roleRepository.save(role);
        return role;
    }
}