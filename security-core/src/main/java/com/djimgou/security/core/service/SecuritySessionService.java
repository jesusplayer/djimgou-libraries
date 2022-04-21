package com.djimgou.security.core.service;

import com.djimgou.core.util.AppUtils;
import com.djimgou.security.core.UtilisateurDetails;
import com.djimgou.security.core.model.Utilisateur;
import com.djimgou.session.service.SessionService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

@Log4j2
@Component
public class SecuritySessionService {

    @Autowired(required = false)
    UtilisateurBdService utilisateurBdService;

/*    @Autowired(required = false)
    SessionRegistry sessionRegistry;*/

    @Autowired
    SessionService sessionService;

    // @Autowired
    // WebInvocationPrivilegeEvaluator evaluator;

    public SessionService getSession() {
        return sessionService;
    }

    public UtilisateurDetails currentUser() {
        SecurityContext context = SecurityContextHolder.getContext();
        Authentication authentication = context.getAuthentication();
        return currentUser(authentication);
    }

    public Optional<UUID> currentUserId() {
        return sessionService.currentUserId();
    }


    public String username() {
        return sessionService.username();
    }


    public UtilisateurDetails currentUser(Authentication authentication) {
        if (authentication != null) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof UtilisateurDetails) {
                return (UtilisateurDetails) principal;
            }
        }
        return null;
    }

    public boolean hasUser(Authentication authentication) {
        return AppUtils.has(currentUser(authentication));
    }

    public boolean hasUser() {
        return AppUtils.has(currentUser());
    }

    public Utilisateur currentUserFromDb() {
        UtilisateurDetails curUser = currentUser();
        if (AppUtils.has(curUser)) {
            Utilisateur u = curUser.getUtilisateur();
            return utilisateurBdService.searchById(u.getId());
        } /*else {

        }*/
        return null;
    }


    public void updateUserAuthorities(Collection<GrantedAuthority> authorities) {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();
        updateUserAuthorities(authorities, authentication);
    }

    /**
     * rafraichie la session courante de l'utilisateur
     * en prenant en compte ses valeurs en BD
     */
    public void refreshUserFromDb() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();
        Utilisateur user = currentUserFromDb();
        UtilisateurDetails uDet = new UtilisateurDetails(user);
        updateUserAuthorities((Collection<GrantedAuthority>) uDet.getAuthorities(), uDet, authentication);
    }

    /**
     * rafraichie la session courante de l'utilisateur
     *
     * @param authorities    authoritiesd de spring
     * @param principal      principal de spring
     * @param authentication objet authentification de spring
     */
    public void updateUserAuthorities(Collection<GrantedAuthority> authorities, Object principal, Authentication authentication) {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        securityContext
                .setAuthentication(new UsernamePasswordAuthenticationToken(principal, securityContext.getAuthentication()
                        .getCredentials(), authorities));
    }

    /**
     * rafraichie la session courante de l'utilisateur
     *
     * @param authorities    liste des authorities spring
     * @param authentication objet spring authentification
     */
    public void updateUserAuthorities(Collection<GrantedAuthority> authorities, Authentication authentication) {
        Object principal = authentication.getPrincipal();
        updateUserAuthorities(authorities, principal, authentication);
    }
    /*public void updateUsersSession() {
        List<Object> loggedUsers = sessionRegistry.getAllPrincipals();
        for (Object principal : loggedUsers) {
            if (principal instanceof UtilisateurDetails) {
                final UtilisateurDetails loggedUser = (UtilisateurDetails) principal;
                List<SessionInformation> sessionsInfo = sessionRegistry.getAllSessions(principal, false);

                if (has(sessionsInfo)) {
                    for (SessionInformation sessionInformation : sessionsInfo) {
                        sessionInformation.
                        log.info("Exprire now :" + sessionInformation.getSessionId());
                        sessionInformation.expireNow();

                        sessionRegistry.removeSessionInformation(sessionInformation.getSessionId());
                        // User is not forced to re-logging
                    }
                }
            }
        }
    }*/
   /* public void updateUserRoles(String username, Set<GrantedAuthority> newRoles) {
        if (sessionRepository instanceof FindByIndexNameSessionRepository) {
            Map<String, Session> map = ((FindByIndexNameSessionRepository<Session>) sessionRepository)
                    .findByPrincipalName(username);
            for (org.springframework.session.Session session : map.values()) {
                if (!session.isExpired()) {
                    SecurityContext securityContext = session.getAttribute(SPRING_SECURITY_CONTEXT_KEY);
                    Authentication authentication = securityContext.getAuthentication();
                    if (authentication instanceof UsernamePasswordAuthenticationToken) {
                        Collection<GrantedAuthority> authorities = new HashSet<>(authentication.getAuthorities());
                        //1. Update of authorities
                        authorities.addAll(newRoles);
                        Object principalToUpdate = authentication.getPrincipal();
                        if (principalToUpdate instanceof UtilisateurDetails) {
                            //2. Update of principal: Your User probably extends UserDetails so call here method that update roles to allow
                            // org.springframework.security.core.userdetails.UserDetails.getAuthorities return updated
                            // Set of GrantedAuthority
                            securityContext
                                    .setAuthentication(new UsernamePasswordAuthenticationToken(principalToUpdate, authentication
                                            .getCredentials(), authorities));
                            session.setAttribute(SPRING_SECURITY_CONTEXT_KEY, securityContext);
                            sessionRepository.save(session);
                        }
                    }
                }
            }
        }
    }*/
}
