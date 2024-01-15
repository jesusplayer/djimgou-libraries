package com.djimgou.security.core.model;

import com.djimgou.audit.model.EntityListener;
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

    @Column(name = "email", nullable = false, unique = true)
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
        if (has(authorities)) {
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
        return (has(nom) ? nom : "") + " " + (has(prenom) ? prenom : "");
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

    public String telephoneFormated() {
        if (!has(getTelephone())) {
            return "";
        }
        return getTelephone().replaceAll("\\+","").replaceAll("\\s","");
    }

    @Transient
    public Set<String> getAllUrls(){
        if(has(authorities)){
            return authorities.stream().flatMap(role -> role.getAllUrls().stream()).collect(Collectors.toSet());
        }
        return new HashSet<>();
    }

    @Transient
    public Set<String> getAllRolesAndPriv(){
        if(has(authorities)){
            return authorities.stream().flatMap(role -> role.getAllRolesAndPriv().stream()).collect(Collectors.toSet());
        }
        return new HashSet<>();
    }


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getFonction() {
        return fonction;
    }

    public void setFonction(String fonction) {
        this.fonction = fonction;
    }

    public Boolean getIsInvitationPending() {
        return isInvitationPending;
    }

    public void setIsInvitationPending(Boolean invitationPending) {
        isInvitationPending = invitationPending;
    }

    public boolean isAccountNonExpired() {
        return isAccountNonExpired;
    }

    public void setIsAccountNonExpired(boolean accountNonExpired) {
        isAccountNonExpired = accountNonExpired;
    }

    public boolean getIsAccountLocked() {
        return isAccountLocked;
    }

    public void setIsAccountLocked(boolean accountLocked) {
        isAccountLocked = accountLocked;
    }

    public boolean getIsCredentialsExpired() {
        return isCredentialsExpired;
    }

    public void setIsCredentialsExpired(boolean credentialsExpired) {
        isCredentialsExpired = credentialsExpired;
    }

    public Boolean getIsPasswordChangedByUser() {
        return isPasswordChangedByUser;
    }

    public void setIsPasswordChangedByUser(Boolean passwordChangedByUser) {
        isPasswordChangedByUser = passwordChangedByUser;
    }

    public Set<Role> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(Set<Role> authorities) {
        this.authorities = authorities;
    }

    public ConfirmationToken getConfirmationToken() {
        return confirmationToken;
    }

    public void setConfirmationToken(ConfirmationToken confirmationToken) {
        this.confirmationToken = confirmationToken;
    }

    public Set<Tenant> getTenants() {
        return tenants;
    }

    public void setTenants(Set<Tenant> tenants) {
        this.tenants = tenants;
    }

    public void setRoles(String roles) {
        this.roles = roles;
    }

    public String getPasswordPart1() {
        return passwordPart1;
    }

    public void setPasswordPart1(String passwordPart1) {
        this.passwordPart1 = passwordPart1;
    }

    public String getPasswordPart2() {
        return passwordPart2;
    }

    public void setPasswordPart2(String passwordPart2) {
        this.passwordPart2 = passwordPart2;
    }
}
