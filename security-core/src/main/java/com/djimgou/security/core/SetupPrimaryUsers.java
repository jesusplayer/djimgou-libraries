package com.djimgou.security.core;

import com.djimgou.core.util.AppUtils2;
import com.djimgou.security.core.enpoints.EndPointsRegistry;
import com.djimgou.security.core.enpoints.SecuredEndPoint;
import com.djimgou.security.core.model.Privilege;
import com.djimgou.security.core.model.Role;
import com.djimgou.security.core.model.Utilisateur;
import com.djimgou.security.core.model.dto.role.IdDto;
import com.djimgou.security.core.model.dto.utilisateur.ModifierProfilDto;
import com.djimgou.security.core.model.dto.utilisateur.UtilisateurDto;
import com.djimgou.security.core.model.dto.utilisateur.UtilisateurFilterDto;
import com.djimgou.security.core.model.dto.utilisateur.UtilisateurFindDto;
import com.djimgou.security.core.repo.ConfirmationTokenRepo;
import com.djimgou.security.core.repo.PrivilegeRepo;
import com.djimgou.security.core.repo.RoleRepo;
import com.djimgou.security.core.repo.UtilisateurRepo;
import com.djimgou.security.core.service.PrivileEvaluator;
import com.djimgou.security.core.service.UtilisateurBdServiceBase;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.collections4.SetUtils;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpMethod;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static com.djimgou.core.util.AppUtils2.has;


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
    private UtilisateurRepo userRepository;

    @Autowired
    private RoleRepo roleRepository;

    @Autowired
    private PrivilegeRepo privilegeRepository;

    @Autowired
    private ConfirmationTokenRepo confirmationTokenRepo;


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
        return AppUtils2.has(customBdService) ? (UtilisateurBdServiceBase<Utilisateur, UtilisateurFindDto, UtilisateurFilterDto, UtilisateurDto, ModifierProfilDto>) customBdService : utilisateurBdService;
    }

    public void initDefaultPriv2() {
        final Collection<SecuredEndPoint> securedEndPoints = endPointsRegistry.endPoints();
        if (has(securedEndPoints)) {
            securedEndPoints.stream().map(endPoint -> {
                Privilege fullAccess = new Privilege();

                fullAccess.setCode(endPoint.getName());
                fullAccess.setName(endPoint.getName());
                fullAccess.setUrl(endPoint.toSecurityUrl());
                fullAccess.setDescription(endPoint.getDescription());
                fullAccess.setHttpMethod(endPoint.getHttpMethod());
                return fullAccess;
            }).forEach(this::createPrivilegeIfNotFound);
        }
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
    public void onApplicationEvent(ContextRefreshedEvent event) {

        if (alreadySetup)
            return;

        if (defaultPrivilegesCreation.equals("deleteAndCreate")) {
            privilegeRepository.deleteAllInBatch();
            // privilegeRepository.findAll().forEach(p->privilegeRepository.deleteById(p.getId()));
        }
        if (!has(defaultPrivilegesCreation) || (!defaultPrivilegesCreation.equals("disabled") || defaultPrivilegesCreation.isEmpty())) {
            initDefaultPriv2();

            PrivileEvaluator pe = new PrivileEvaluator(Privilege.class);

            Privilege fullPrivilege = privilegeRepository.findByCode(PrivileEvaluator.FULL_ACCESS).orElse(null);

            Privilege readPriv = createPrivilegeIfNotFound(
                    Privilege.READ_ONLY_PRIV,
                    Privilege.READ_ONLY_PRIV,
                    "Privilège ayant tous les droits de lecture seule dans le système",
                    null,
                    HttpMethod.GET
            );

          /*  Privilege paramPrivilege
                    = createPrivilegeIfNotFound("PrivParametresVoir");*/


            //Set<Privilege> adminPrivileges = SetUtils.hashSet(paramPrivilege, fullPrivilege);
            //Role userAuth = createAuthorityIfNotFound("ROLE_USER", SetUtils.hashSet(paramPrivilege), null);
            createAuthorityIfNotFound(Role.ROLE_ADMIN, null, null);

            createAuthorityIfNotFound(Role.ROLE_READONLY, SetUtils.hashSet(readPriv), null);


            Role adminRole = roleRepository.findByName(Role.ROLE_ADMIN);

            //Role userRole = roleRepository.findByName("ROLE_USER");

            Utilisateur admin = createUserIfNotFound("admin", "admin", "admin@actsarl.com",
                    "admin", SetUtils.hashSet(adminRole), Boolean.TRUE);

            Utilisateur admin2 = createUserIfNotFound("admin2", "admin2", "admin2@actsarl.com",
                    "admin", SetUtils.hashSet(adminRole), Boolean.TRUE);

            Role redOnlyRole = roleRepository.findByName(Role.ROLE_READONLY);
            Utilisateur readonly = createUserIfNotFound("readonly", "readonly", "readonly@actsarl.com",
                    "readonly", SetUtils.hashSet(redOnlyRole), Boolean.FALSE);

            /*Utilisateur user = createUserIfNotFound("user", "user", "nono", "nono",
                    SetUtils.hashSet(userRole), Boolean.TRUE);

            Utilisateur dan = createUserIfNotFound("dany", "marc", "djimgou", "djimgou",
                    SetUtils.hashSet(userRole), Boolean.FALSE);*/

            /*ConfirmationToken ct = new ConfirmationToken(dan);
            ct.setConfirmationToken("b326bebc-0498-4561-b0ed-099f31f2b193");
            confirmationTokenRepo.save(ct);*/

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
            userDto.setAuthorities(has(authorities) ? authorities.stream().map(role -> {
                IdDto is = new IdDto();
                is.setId(role.getId());
                return is;
            }).collect(Collectors.toSet()) : null);
            try {
                userDto.setEncodedPasswd(passwordEncoder().encode(password));
                user = getService().createUtilisateurGeneric(userDto);
                getService().activer(user.getId());
            } catch (DataIntegrityViolationException | ConstraintViolationException e) {

            }
        }
        return user;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
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
        return createPrivilegeIfNotFound(code, name, description, parent, null);
    }

    @Transactional
    Privilege createPrivilegeIfNotFound(String code, String name, String description, Privilege parent, HttpMethod httpMethod) {
        Optional<Privilege> opt = privilegeRepository.findByCode(name);
        Privilege privilege = opt.orElseGet(() -> createPrivilege(code, name, description, parent, httpMethod));
        return privilege;
    }

    @Transactional
    Privilege createPrivilege(String code, String name, String description, Privilege parent) {

        return createPrivilege(code, name, description, parent, null);
    }

    @Transactional
    Privilege createPrivilege(String code, String name, String description, Privilege parent, HttpMethod httpMethod) {
        Privilege p = new Privilege();
        p.setName(name);
        p.setCode(code);
        p.setReadonlyValue(true);
        p.setDescription(description);
        p.setParent(parent);
        p.setHttpMethod(httpMethod);
        return privilegeRepository.save(p);
    }

    @Transactional
    Privilege createPrivilegeIfNotFound(Privilege priv) {
        Optional<Privilege> opt = privilegeRepository.findByCode(priv.getCode());
        Privilege privilege = null;
        if (opt.isPresent()) {
            Privilege oldPriv = opt.get();
            boolean changed = false;
            if (!Objects.equals(oldPriv.getDescription(), priv.getDescription())) {
                oldPriv.setDescription(priv.getDescription());
                changed = true;
                log.info("Modification de l'Url du priv: " + priv.getDescription());
            }
            if (!Objects.equals(oldPriv.getUrl(), priv.getUrl())) {
                oldPriv.setUrl(priv.getUrl());
                changed = true;
                log.info("Modification de la description du priv: " + priv.getUrl());
            }
            if (!Objects.equals(oldPriv.getHttpMethod(), priv.getHttpMethod())) {
                oldPriv.setHttpMethod(priv.getHttpMethod());
                changed = true;
                log.info("Modification de la méthode Http du priv: " + priv.getHttpMethod());
            }
            if (changed) {
                privilege = privilegeRepository.save(oldPriv);
            }
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
            role.setPrivileges(privileges);
            role.setParent(parent);
            role.setReadonlyValue(true);
            roleRepository.save(role);
        } else {
            if (!role.hasPrivileges() && has(privileges)) {
                role.addAllPrivileges(privileges);
                roleRepository.save(role);
            }
        }
        return role;
    }
}
