package com.djimgou.security.core.model;

import com.djimgou.core.util.AppUtils;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.HashSet;

/**
 * @author DJIMGOU NKENNE DANY MARC 08/2020
 * Définit les code des privilege par défaut à partir de la classe
 * Attention ne pas modifier ce code,
 * tous les rôles et conventions y dépendent
 */
@Getter
@Setter
public class PrivileEvaluator {
    Class c;
    String name;
    String pageUrlPrefix;
    Authentication authentication;
    Collection<? extends GrantedAuthority> authorities = new HashSet<>();
    public static String FULL_ACCESS = "PrivFullApplicationPrivileges";
    public String READ;
    // a tous les droits sur la ressource
    public String DO_ALL;
    public String CREATE;
    public String UPDATE;
    public String DELETE;
    public String VALIDATE;
    public Privilege allAppPriv;
    public Privilege doAll;
    public Privilege read;
    public Privilege create;
    public Privilege update;
    public Privilege delete;
    public Privilege validate;

    public Boolean canRead;
    public Boolean canDoAll;
    public Boolean canCreate;
    public Boolean canUpdate;
    public Boolean canValidate;
    public Boolean canDelete;

    private SimpleGrantedAuthority doAllAuth;
    private SimpleGrantedAuthority readAuth;
    private SimpleGrantedAuthority createAuth;
    private SimpleGrantedAuthority updateAuth;
    private SimpleGrantedAuthority deleteAuth;
    private SimpleGrantedAuthority validateAuth;
    private SimpleGrantedAuthority fullAccessAuth;

    public PrivileEvaluator(Class c) {
        init(c);
    }

    public PrivileEvaluator(Class c, Authentication authentication) {
        this.authentication = authentication;
        this.authorities = authentication.getAuthorities();
        init(c);
    }

    private void init(Class c) {
        this.c = c;
        name = AppUtils.localizeClassName(c.getSimpleName());
        pageUrlPrefix = "/" + toUrlPrefix();
        final String prefix = "Priv";
        READ = prefix + name + "Voir";
        CREATE = prefix + name + "Creer";
        UPDATE = prefix + name + "Modifier";
        DELETE = prefix + name + "Supprimer";
        VALIDATE = prefix + name + "Valider";
        DO_ALL = prefix + name + "AccesTotal";

        read = new Privilege();
        read.setCode(READ);
        read.setName("Acceder au module " + name);
        read.setUrl(pageUrlPrefix + "/list," + pageUrlPrefix + "/find," + pageUrlPrefix + "/filter," + pageUrlPrefix + "/detail");
        read.setDescription("Privilège qui permet d'accéder au module " + name);
        readAuth = new SimpleGrantedAuthority(READ);

        create = new Privilege();
        create.setCode(CREATE);
        create.setName("Créer un(e) " + name);
        create.setUrl(pageUrlPrefix + "/creer");
        create.setDescription("Privilège qui permet de créer un(e) " + name);
        createAuth = new SimpleGrantedAuthority(CREATE);

        update = new Privilege();
        update.setCode(UPDATE);
        update.setName("Modifier un(e) " + name);
        update.setUrl(pageUrlPrefix + "/modifier");
        update.setDescription("Privilège qui permet de modifier un(e) " + name);
        updateAuth = new SimpleGrantedAuthority(UPDATE);

        delete = new Privilege();
        delete.setCode(DELETE);
        delete.setName("Supprimer un(e) " + name);
        delete.setUrl(pageUrlPrefix + "/supprimer");
        delete.setDescription("Privilège qui permet de supprimer un(e) " + name);
        deleteAuth = new SimpleGrantedAuthority(DELETE);

     /*   validate = new Privilege();
        validate.setCode(VALIDATE);
        validate.setName("Valider un(e) " + name);
        validate.setDescription("Privilège qui permet de valider un(e) " + name);
        validateAuth = new SimpleGrantedAuthority(VALIDATE);
*/
        doAll = new Privilege();
        doAll.setCode(DO_ALL);
        doAll.setName("Accès total sur le module " + name);
        doAll.setUrl(pageUrlPrefix);
        doAll.setDescription("Privilège qui permet d'avoir tous les droits sur le module " + name);
        // doAll.setEnfants(SetUtils.hashSet(read, create, update, delete));
        doAllAuth = new SimpleGrantedAuthority(DO_ALL);

        fullAccessAuth = new SimpleGrantedAuthority(FULL_ACCESS);


        canRead = canRead() || canDoAll() || allAppPriv();
        canCreate = canCreate() || canDoAll() || allAppPriv();
        canUpdate = canUpdate() || canDoAll() || allAppPriv();
        canDelete = canDelete() || canDoAll() || allAppPriv();
        canValidate = canValidate() || canDoAll() || allAppPriv();
        canDoAll = canDoAll() || allAppPriv();
    }

    public String toUrlPrefix() {
        String url = (name).toLowerCase();
        /*for (int i = 1; i < name.length(); i++) {
            char c = name.charAt(i);
            if ('A' <= c && c <= 'Z') {
                url = (url + "-" + (name.charAt(i) + "").toLowerCase());
            } else {
                url = (url + name.charAt(i));
            }
        }*/
        return url;
    }

    public boolean canDoAll() {
        return this.authorities.contains(doAllAuth);
    }

    public boolean can(String priv) {
        return this.authorities.contains(new SimpleGrantedAuthority(priv));
    }

    public boolean canRead() {
        return this.authorities.contains(readAuth);
    }

    /**
     * Indique que l'utilisateur a tous les privillèges
     *
     * @return true si la ressource a tous les privilèges
     */
    public boolean allAppPriv() {
        return this.authorities.contains(fullAccessAuth);
    }

    public boolean canCreate() {
        return this.authorities.contains(createAuth);
    }

    public boolean canUpdate() {
        return this.authorities.contains(updateAuth);
    }

    public boolean canDelete() {
        return this.authorities.contains(deleteAuth);
    }

    public boolean canValidate() {
        return this.authorities.contains(validateAuth);
    }

}
