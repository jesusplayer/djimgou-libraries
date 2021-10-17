package com.act.security.listeners;


import com.act.security.model.events.AuthorityChangeEvent;
import com.act.security.model.events.PrivilegeChangeEvent;
import com.act.security.model.events.UserChangeEvent;
import com.act.security.service.SessionServiceImpl;
import com.act.security.service.UtilisateurBdService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;

import java.util.Collection;


class AuthorityChangeManager implements ApplicationListener<AuthorityChangeEvent> {
    @Autowired
    SessionServiceImpl sessionService;
    @Autowired
    UtilisateurBdService utilisateurBdService;

    @Override
    public void onApplicationEvent(AuthorityChangeEvent authorityChangeEvent) {
        /*AuthSuccessHandler.sessions.forEach((username, context) -> {
            UtilisateurDetails uDet = sessionService.currentUser(context.getAuthentication());
            // on prend tous les user qui on le rôle en question
            if(uDet.getUtilisateur().hasRole(authorityChangeEvent.getAuthority().getName())){
                Utilisateur user = utilisateurBdService.findById(uDet.getUtilisateur().getId());
                UtilisateurDetails uDet2 = new UtilisateurDetails(user);

                change(context, uDet2.getAuthorities());
            }


        });*/
    }

    public void change(SecurityContext securityContext, Collection<? extends GrantedAuthority> authorities) {
        Object principal = securityContext.getAuthentication().getPrincipal();
        securityContext
                .setAuthentication(new UsernamePasswordAuthenticationToken(principal, securityContext.getAuthentication()
                        .getCredentials(), authorities));
    }
}

class PrivilegeChangeManager implements ApplicationListener<PrivilegeChangeEvent> {

    @Override
    public void onApplicationEvent(PrivilegeChangeEvent privilegeChangeEvent) {

    }
}

class UserChangeManager implements ApplicationListener<UserChangeEvent> {

    @Override
    public void onApplicationEvent(UserChangeEvent userChangeEvent) {

    }
}
