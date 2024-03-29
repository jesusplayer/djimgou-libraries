package com.djimgou.security;


import com.djimgou.audit.model.AuditAction;
import com.djimgou.audit.service.AuditBdService;
import com.djimgou.core.util.AppUtils2;
import com.djimgou.security.core.UtilisateurDetails;
import com.djimgou.security.core.model.Utilisateur;
import com.djimgou.security.core.service.MyVoter;
import com.djimgou.security.core.service.SecuritySessionService;
import com.djimgou.security.core.service.UtilisateurBdService;
import com.djimgou.security.service.TokenAuthenticationService;
import com.djimgou.session.context.SessionContext;
import com.djimgou.session.enums.SessionKeys;
import com.djimgou.tenantmanager.aop.TenantContext;
import lombok.SneakyThrows;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

import static com.djimgou.core.util.AppUtils2.has;

/**
 * @author DJIMGOU NKENNE DANY MARC 08/2020
 */
@Component
public class AuthLogoutSuccessHandler implements org.springframework.security.web.authentication.logout.LogoutSuccessHandler {
    private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();
    /*    @Autowired
        FindByIndexNameSessionRepository<? extends Session> sessions;*/
    private AuditBdService auditBdService;

    private SecuritySessionService sessionService;

    private UtilisateurBdService utilisateurBdService;
    private TokenAuthenticationService tokenAuthenticationService;

    public AuthLogoutSuccessHandler(AuditBdService auditBdService, SecuritySessionService sessionService, UtilisateurBdService utilisateurBdService, TokenAuthenticationService tokenAuthenticationService) {
        this.auditBdService = auditBdService;
        this.sessionService = sessionService;
        this.utilisateurBdService = utilisateurBdService;
        this.tokenAuthenticationService = tokenAuthenticationService;
    }

    @SneakyThrows
    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        Utilisateur utilisa = null;
        String username = (String) request.getAttribute(SessionKeys.USERNAME);
        request.removeAttribute(SessionKeys.USER_ID);
        if (!has(username)) {
            String jwt = TokenAuthenticationService.parseJwt(request);
            try {
                username = TokenAuthenticationService.getUserNameFromJwtToken(jwt);
            } catch (Exception e) {
                // Le token est invalide
            }
        }
        //sessions.findByPrincipalName()
        if (has(username)) {
            Optional<Utilisateur> u = utilisateurBdService.findByUsername(username);
            utilisa = u.get();
        }
        if (has(authentication)) {
            UtilisateurDetails uDetail = sessionService.currentUser(authentication);
            utilisa = uDetail.getUtilisateur();
        }
        if (AppUtils2.has(utilisa)) {
            Utilisateur user = utilisa.singleInfo();
            auditBdService.add(user, AuditAction.DECONNEXION);
            MyVoter.userSessionToUpdate.remove(user.getUsername());
            TenantContext.clear();
            SessionContext.clear();
        }
        response.getWriter().write(String.valueOf(true));
        response.getWriter().flush();
        // redirectStrategy.sendRedirect(request, response, "/login.jsf");
    }
}
