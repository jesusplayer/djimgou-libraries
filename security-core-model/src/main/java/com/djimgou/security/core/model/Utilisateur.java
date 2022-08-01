package com.djimgou.security.core.model;

import com.djimgou.audit.model.EntityListener;
import com.djimgou.core.util.AppUtils;
import com.djimgou.tenantmanager.model.Tenant;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import javax.validation.constraints.Email;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static com.djimgou.core.util.AppUtils.has;

@Entity
@Data
@Table(name = "users")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@EqualsAndHashCode(callSuper = false)
@EntityListeners({EntityListener.class})
public class Utilisateur extends SecurityBaseEntity {

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
    Set<Role> authorities;

    //@JsonManagedReference(value = "utilisateur-token")
    @OneToOne(orphanRemoval = true)
    ConfirmationToken confirmationToken;

    @ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.MERGE})
    @JoinTable(
            name = "user_tenants",
            joinColumns = @JoinColumn(
                    name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(
                    name = "tenant_id", referencedColumnName = "id")
    )
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
        if (AppUtils.has(authorities)) {
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
        return (AppUtils.has(nom) ? nom : "") + " " + (AppUtils.has(prenom) ? prenom : "");
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

    @Override
    public String toString() {
        return getId().toString();
    }

}
