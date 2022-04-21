package com.djimgou.security;

import com.djimgou.security.core.AppSecurityConfig;
import com.djimgou.security.core.model.*;
import com.djimgou.security.core.repo.ConfirmationTokenRepo;
import com.djimgou.security.core.repo.PrivilegeRepo;
import com.djimgou.security.core.repo.RoleRepo;
import com.djimgou.security.core.repo.UtilisateurRepo;
import com.djimgou.core.util.AppUtils;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.collections4.SetUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.ParameterizedType;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

import static com.djimgou.core.util.AppUtils.has;


/**
 * @author DJIMGOU NKENNE DANY MARC 08/2020
 * compossant d'initialisation des utilisateurs par défaut dans l'application
 */
@Log4j2
@Component
class SetupPrimaryUsers implements ApplicationListener<ContextRefreshedEvent> {

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

    @Transactional
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



            //parent.setEnfants(SetUtils.hashSet(rd, cr, up, dl/*, vl*/));
            //privilegeRepository.save(parent);

        });

        Privilege fullAccess = new Privilege();
        fullAccess.setCode(PrivileEvaluator.FULL_ACCESS);
        fullAccess.setName("Avoir tous les droits");
        fullAccess.setDescription("Privilège qui permet d'avoir tous les droits dans l'application");
        createPrivilegeIfNotFound(fullAccess);
        /*
        String packageName = DeviseListMB.class.getPackage().getName();
        String packageSecName = UtilisateurFormMB.class.getPackage().getName();
        // recuperation des modules de liste

        entitesList.addAll(appUtils.getClasses(packageName, Named.class, filterFn.apply(AbstractListMB.class)));
        entitesList.addAll(appUtils.getClasses(packageSecName, Named.class,filterFn.apply(AbstractListMB.class)));*/

        // Création des privillèges action
    }

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
            initDefaultPriv();

            PrivileEvaluator pe = new PrivileEvaluator(Privilege.class);
            Privilege fullPrivilege
                    = privilegeRepository.findByCode(PrivileEvaluator.FULL_ACCESS).orElse(null);
        /*Privilege writePrivilege
                = createPrivilegeIfNotFound("WRITE_PRIVILEGE");*/

            Privilege paramPrivilege
                    = createPrivilegeIfNotFound("PrivParametresVoir");


            Set<Privilege> adminPrivileges = SetUtils.hashSet(paramPrivilege, fullPrivilege);
            Role userAuth = createAuthorityIfNotFound("ROLE_USER", SetUtils.hashSet(paramPrivilege), null);
            createAuthorityIfNotFound("ROLE_ADMIN", adminPrivileges, userAuth);


            Role adminRole = roleRepository.findByName("ROLE_ADMIN");

            Role userRole = roleRepository.findByName("ROLE_USER");

            Utilisateur admin = createUserIfNotFound("admin", "admin", "admin@actsarl.com",
                    "admin", SetUtils.hashSet(adminRole), Boolean.TRUE);

            Utilisateur admin2 = createUserIfNotFound("admin2", "admin2", "admin2@actsarl.com",
                    "admin", SetUtils.hashSet(adminRole), Boolean.TRUE);

            Utilisateur user = createUserIfNotFound("user", "user", "user@actsarl.com", "user",
                    SetUtils.hashSet(userRole), Boolean.TRUE);

            Utilisateur dan = createUserIfNotFound("dany", "marc", "danmfacto@gmail.com", "user",
                    SetUtils.hashSet(userRole), Boolean.FALSE);

            ConfirmationToken ct = new ConfirmationToken(dan);
            ct.setConfirmationToken("b326bebc-0498-4561-b0ed-099f31f2b193");
        /*LocalDateTime ldt = LocalDateTime.now().minusDays(2);
        ct.setCreatedDate(Date.from(ldt.atZone(ZoneId.systemDefault()).toInstant()));*/
            confirmationTokenRepo.save(ct);

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

    @Transactional
    Utilisateur createUserIfNotFound(String nom, String prenom, String username, String password, Set<Role> authorities, Boolean enabled) {
        Optional<Utilisateur> opt = userRepository.findByUsername(username);
        Utilisateur user;
        if (opt.isPresent()) {
            user = opt.get();
        } else {
            user = new Utilisateur();
            user.setNom(nom);
            user.setPrenom(prenom);
            String en = bCryptPasswordEncoder.encode(password);
            user.setPassword(/*"{bcrypt}"+*/en);
            user.setUsername(username);
            user.setEmail(username);
            user.setAuthorities(authorities);
            user.setEnabled(enabled);
            user = userRepository.save(user);
        }
        return user;
    }

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
        Privilege privilege;
        if (opt.isPresent()) {
            privilege = opt.get();
        } else {
            priv.setReadonlyValue(true);
            privilege = privilegeRepository.save(priv);
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
