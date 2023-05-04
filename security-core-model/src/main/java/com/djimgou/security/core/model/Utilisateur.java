package com.djimgou.security.core.model;

import com.djimgou.audit.annotations.IgnoreOnAudit;
import com.djimgou.audit.model.EntityListener;
import com.djimgou.core.coolvalidation.annotations.Unique;
import com.djimgou.core.coolvalidation.annotations.Validations;
import com.djimgou.core.util.AppUtils2;
import com.djimgou.tenantmanager.model.Pays;
import com.djimgou.tenantmanager.model.Tenant;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;

import javax.persistence.*;
import javax.validation.constraints.Email;
import java.util.*;
import java.util.stream.Collectors;

import static com.djimgou.core.util.AppUtils2.has;

//@Validations
@FilterDef(name = "logicalDeleteFilter",
        parameters = {
                @ParamDef(name = "deleted", type = "boolean")
        },
        defaultCondition = "(deleted = :deleted OR deleted IS NULL)"
)

@Filter(name = "logicalDeleteFilter")
@Entity
@Data
@Table(name = "users")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@EqualsAndHashCode(callSuper = false)
@EntityListeners({EntityListener.class})
public class Utilisateur extends SecurityBaseEntity {

    //    @Unique(ignoreCase = true, message = "Impossible d'enregistrer cet utilisateur car un utilisateur de même nom d'utilisateur(login) existe déjà")
    @Column(name = "username", nullable = false, unique = true)
    private String username;

    @JsonIgnore
    @Column(nullable = false)
    private String password;

    @Column(name = "enabled", nullable = false)
    private Boolean enabled = Boolean.FALSE;

    @Email
    private String email;

    private String nom;

    private String prenom;

    private String telephone;

    private String fonction;


    @Column(name = "is_invitation_pending")
    private Boolean isInvitationPending = Boolean.FALSE;

    @Column(name = "is_account_non_expired")
    private boolean isAccountNonExpired = true;

    @Column(name = "is_account_locked")
    private boolean isAccountLocked = false;

    @Column(name = "is_credentials_expired")
    private boolean isCredentialsExpired = false;

    /**
     * Indique si le mot de passe a été modifié par l'utilisateur
     * lors de sa première connection
     */
    @Column(name = "is_pswd_changed_by_user")
    private Boolean isPasswordChangedByUser = Boolean.FALSE;

    //    @OneToMany(fetch = FetchType.EAGER, mappedBy = "utilisateur")
    @ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.MERGE})
    @JoinTable(
            name = "users_authorities",
            joinColumns = @JoinColumn(
                    name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(
                    name = "authority_id", referencedColumnName = "id")
    )
    @IgnoreOnAudit
    Set<Role> authorities;

    //@JsonManagedReference(value = "utilisateur-token")
    @OneToOne(orphanRemoval = true)
    @IgnoreOnAudit
    ConfirmationToken confirmationToken;

    @ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.MERGE})
    @JoinTable(
            name = "user_tenants",
            joinColumns = @JoinColumn(
                    name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(
                    name = "tenant_id", referencedColumnName = "id")
    )
    @IgnoreOnAudit
    Set<Tenant> tenants;

    @Transient
    private String roles;

    @Column(name = "password_part1")
    String passwordPart1;

    /**
     * Deuxième partie du mot de passe à voir par l'Admin validateur
     */
    @Column(name = "password_part2")
    String passwordPart2;

    public String getRoles() {
        if (AppUtils2.has(authorities)) {
            roles = authorities.stream().map(a -> a.getName())
                    .collect(Collectors.joining(","));
        }
        return roles;
    }

    public boolean hasRole(String roleName) {
        Optional<Role> opt = getAuthorities().stream().filter(f -> f.getName().equals(roleName))
                .findFirst();

        return opt.isPresent();
    }

    public String fullname() {
        return (AppUtils2.has(nom) ? nom : "") + " " + (AppUtils2.has(prenom) ? prenom : "");
    }

    /*@ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "utilisateur_role",
            joinColumns = @JoinColumn(name = "id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    List<Authority> authorities;*/

    public Utilisateur() {

        authorities = new HashSet<>();
        tenants = new HashSet<>();

    }

    public String fullPassword() {
        return passwordPart1 + passwordPart2;
    }

    /**
     * Retourne quelques infos utiles de l'utilisateur
     * en vue d'effectuer la journalisation
     *
     * @return utilisateur
     */
    public Utilisateur singleInfo() {
        Utilisateur user = new Utilisateur();
        user.setId(getId());
        user.setEmail(getEmail());
        user.setNom(getNom());
        user.setPrenom(getPrenom());
        user.setUsername(getUsername());
        return user;
    }

    public Pays pays() {
        if (hasTenants()) {
            return tenants.iterator().next().getPays();
        }
        return null;
    }

    public void addRole(Role role) {
        if (!has(authorities)) {
            authorities = new HashSet<>();
        }
        authorities.add(role);
    }

    public void addRole(Set<Role> roles) {
        if (!has(authorities)) {
            authorities = new HashSet<>();
        }
        authorities.addAll(roles);
    }

    public void addTenants(Set<Tenant> tenants) {
        if (!has(this.tenants)) {
            this.tenants = new HashSet<>();
        }
        this.tenants.addAll(tenants);
    }

    public boolean hasTenants() {
        return has(tenants);
    }

    public boolean hasTenant(String tenantCode) {
        if (has(tenantCode) && has(tenants)) {
            return tenants.stream().anyMatch(tenant -> Objects.equals(tenant.getCode(), tenantCode));
        }
        return false;
    }

    public boolean hasPaysInTenants(String paysCode) {
        if (has(paysCode) && has(tenants)) {
            return tenants.stream().anyMatch(tenant -> Objects.equals(tenant.getPays().getCode(), paysCode));
        }
        return false;
    }

    public boolean hasTenantId(UUID tenantId) {
        if (has(tenantId) && has(tenants)) {
            return tenants.stream().anyMatch(tenant -> Objects.equals(tenant.getId(), tenantId));
        }
        return false;
    }

    @JsonIgnore
    @Transient
    public boolean isSuperAdmin() {
        if (has(this.getAuthorities())) {
            return this.getAuthorities().stream().anyMatch(authority -> authority.getName().equals("ROLE_ADMIN"));
        }
        return false;
    }

    @Override
    public String toString() {
        return getId().toString();
    }

}
