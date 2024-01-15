package com.djimgou.security.core;


import com.djimgou.security.core.model.Role;
import com.djimgou.security.core.model.Utilisateur;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author DJIMGOU NKENNE DANY MARC 08/2020
 */
@Data
public class UtilisateurDetails implements UserDetails {
    Utilisateur utilisateur;

    public UtilisateurDetails(Utilisateur utilisateur) {
        this.utilisateur = utilisateur;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();
        for (Role role : utilisateur.getAuthorities()) {

            List<SimpleGrantedAuthority> ganted = role.getAllRolesAndPriv().stream()
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());
            authorities.addAll(ganted);
        }
        return authorities;
    }

    public boolean hasAuthority(String authorityName) {
        return getAuthorities().contains(new SimpleGrantedAuthority(authorityName));
    }

    @Override
    public String getPassword() {
        return utilisateur.getPassword();
    }

    @Override
    public String getUsername() {
        return utilisateur.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return utilisateur.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return !utilisateur.getIsAccountLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return !utilisateur.getIsCredentialsExpired();
    }

    @Override
    public boolean isEnabled() {
        return utilisateur.getEnabled();
    }
}
