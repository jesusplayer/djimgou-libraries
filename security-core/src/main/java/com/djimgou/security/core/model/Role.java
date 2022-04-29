package com.djimgou.security.core.model;

import com.djimgou.audit.model.EntityListener;
import com.djimgou.security.core.listeners.AuthorityChangeListener;
import com.djimgou.security.core.model.dto.role.AuthorityDto;
import com.djimgou.core.util.AppUtils;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.util.*;

import static com.djimgou.core.util.AppUtils.has;

/**
 * @author DJIMGOU NKENNE DANY MARC 08/2020
 */
@Entity
@Data
@Table(name = "authorities"//,
//        uniqueConstraints = @UniqueConstraint(name = "authorities_unique", columnNames = {"username", "authority"})
)
@EqualsAndHashCode(callSuper = false)
@JsonIgnoreProperties(value = {"enfants", "utilisateurs",/* "privileges",*/ "allRoles"})
@EntityListeners({EntityListener.class, AuthorityChangeListener.class})
public class Role extends SecurityBaseEntity {
    public static final String ROLE_READONLY = "ROLE_READONLY";

    @Column(name = "name", nullable = false, length = 128)
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
    private Set<Role> enfants;

    @ManyToMany(mappedBy = "authorities")
    private Set<Utilisateur> utilisateurs;

    @ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.MERGE})
    @JoinTable(name = "authorities_privilege",
            joinColumns = @JoinColumn(name = "authority_id"),
            inverseJoinColumns = @JoinColumn(name = "privilege_id"))
    Set<Privilege> privileges = new HashSet<>();


    public Role(String name) {
        this();
        this.name = name;
    }

    public Set<String> getAllRolesAndPriv() {
        Set<String> roles = new HashSet<>();
        roles.add(name);

        if (AppUtils.has(parent)) {
            roles.addAll(parent.getAllRolesAndPriv());
        }
        if (AppUtils.has(privileges)) {
            privileges.forEach(privilege -> {
                roles.addAll(privilege.getAllPriv());
            });
        }
        return roles;
    }

    public Set<AuthorityDto> getAllAuthoritiesDto() {
        Set<AuthorityDto> roles = new HashSet<>();
//        roles.add(name);

        if (AppUtils.has(parent)) {
            roles.addAll(parent.getAllAuthoritiesDto());
        }
        if (AppUtils.has(privileges)) {
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

    public void clear() {
        if (has(privileges)) {
            privileges.clear();
        }
        if (has(enfants)) {
            enfants.clear();
        }
        if (has(enfants)) {
            enfants.clear();
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), name, description);
    }
}
