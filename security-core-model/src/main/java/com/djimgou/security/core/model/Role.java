package com.djimgou.security.core.model;

import com.djimgou.audit.annotations.IgnoreOnAudit;
import com.djimgou.audit.model.EntityListener;
import com.djimgou.core.coolvalidation.annotations.Unique;
import com.djimgou.core.coolvalidation.annotations.Validations;
import com.djimgou.core.util.AppUtils2;
import com.djimgou.security.core.model.dto.role.AuthorityDto;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import static com.djimgou.core.util.AppUtils2.has;

/**
 * @author DJIMGOU NKENNE DANY MARC 08/2020
 */
@Validations
@Entity
@Data
@Table(name = "authorities"//,
//        uniqueConstraints = @UniqueConstraint(name = "authorities_unique", columnNames = {"username", "authority"})
)
@EqualsAndHashCode(callSuper = false)
@JsonIgnoreProperties(value = {"enfants", "utilisateurs",/* "privileges",*/ "allRoles"})
@EntityListeners({EntityListener.class})
public class Role extends SecurityBaseEntity {
    public static final String ROLE_READONLY = "ROLE_READONLY";
    public static final String ROLE_ADMIN = "ROLE_ADMIN";

    @Unique(ignoreCase = true, message = "Impossible d'enregistrer ce rôle car un rôle de même nom existe déjà")
    @Column(name = "name", nullable = false, length = 128, unique = true)
    private String name;

    @Column(name = "description", length = 500)
    private String description;

    /*@ManyToOne
    @JoinColumn(name = "username", referencedColumnName = "username", foreignKey = @ForeignKey(name = "authorities_fk1"))
    Utilisateur utilisateur;*/

    @ManyToOne()
    Role parent;

    @OneToMany(mappedBy = "parent"/*, orphanRemoval = true*/)
    //@JoinColumn(name = "parent_id")
    @IgnoreOnAudit
    private Set<Role> enfants;

    @ManyToMany(mappedBy = "authorities")
    @IgnoreOnAudit
    private Set<Utilisateur> utilisateurs;

    @ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.MERGE})
    @JoinTable(name = "authorities_privilege",
            joinColumns = @JoinColumn(name = "authority_id"),
            inverseJoinColumns = @JoinColumn(name = "privilege_id"))
    @IgnoreOnAudit
    Set<Privilege> privileges = new HashSet<>();


    public Role(String name) {
        this();
        this.name = name;
    }

    @Transient
    public Set<String> getAllRolesAndPriv() {
        Set<String> roles = new HashSet<>();
        roles.add(name);

        if (AppUtils2.has(parent)) {
            roles.addAll(parent.getAllRolesAndPriv());
        }
        if (AppUtils2.has(privileges)) {
            privileges.forEach(privilege -> {
                roles.addAll(privilege.getAllPriv());
            });
        }
        return roles;
    }

    @Transient
    public Set<AuthorityDto> getAllAuthoritiesDto() {
        Set<AuthorityDto> roles = new HashSet<>();
//        roles.add(name);

        if (AppUtils2.has(parent)) {
            roles.addAll(parent.getAllAuthoritiesDto());
        }
        if (AppUtils2.has(privileges)) {
            privileges.forEach(privilege -> {
                roles.addAll(privilege.getAllAuthoritiesDto());
            });
        }
        return roles;
    }

    public Role() {
        utilisateurs = new HashSet<>();
        //privileges = new HashSet<>();
    }

    public boolean hasPrivileges() {
        return has(privileges);
    }

    @Override
    public String toString() {
        return getId().toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Role role = (Role) o;
        if (Objects.equals(this.getId(), role.getId())) return true;
        //if (!super.equals(o)) return false;
        return Objects.equals(name, role.name);
    }

    public void addPrivilege(Privilege privilege) {
        if (!has(privileges)) {
            privileges = new HashSet<>();
        }
        privileges.add(privilege);
    }

    public void clear() {
        if (AppUtils2.has(privileges)) {
            privileges.clear();
        }
        if (AppUtils2.has(enfants)) {
            enfants.clear();
        }
        if (AppUtils2.has(enfants)) {
            enfants.clear();
        }
    }

    public void addAllPrivileges(Set<Privilege> privileges) {
        if (this.privileges == null) {
            setPrivileges(privileges);
        } else {
            this.privileges.addAll(privileges);
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), name, description);
    }
}
