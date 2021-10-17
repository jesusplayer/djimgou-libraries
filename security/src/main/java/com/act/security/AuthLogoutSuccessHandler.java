package com.act.security;


import com.act.audit.model.AuditAction;
import com.act.security.model.Utilisateur;
import com.act.core.model.enums.SessionKeys;
import com.act.security.service.SessionServiceImpl;
import com.act.security.service.UtilisateurBdService;
import com.act.security.tracking.authentication.security.service.MyVoter;
import com.act.core.util.AppUtils;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.stereotype.Component;
import com.act.audit.service.AuditBdService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

import static com.act.core.util.AppUtils.has;

/**
 * @author DJIMGOU NKENNE DANY MARC 08/2020
 */
@Component
public class AuthLogoutSuccessHandler implements org.springframework.security.web.authentication.logout.LogoutSuccessHandler {
    private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();
    /*    @Autowired
        FindByIndexNameSessionRepository<? extends Session> sessions;*/
    @Autowired
    AuditBdService auditBdService;

    @Autowired
    SessionServiceImpl sessionService;

    @Autowired
    UtilisateurBdService utilisateurBdService;

    @SneakyThrows
    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        Utilisateur utilisa = null;
        String userId = (String) request.getAttribute(SessionKeys.CONNECTED_USER_ID);
        request.removeAttribute(SessionKeys.CONNECTED_USER_ID);
        //sessions.findByPrincipalName()
        if (has(userId)) {
            Optional<Utilisateur> u = utilisateurBdService.findById(UUID.fromString(userId));
            utilisa = u.get();
        }
        if (has(authentication)) {
            UtilisateurDetails uDetail = sessionService.currentUser(authentication);
            utilisa = uDetail.getUtilisateur();
        }
        if (AppUtils.has(utilisa)) {
            Utilisateur user = utilisa.singleInfo();
            auditBdService.add(user, AuditAction.DECONNEXION);
            MyVoter.userSessionToUpdate.remove(user.getUsername());
        }
        response.getWriter().write(String.valueOf(true));
        response.getWriter().flush();
        // redirectStrategy.sendRedirect(request, response, "/login.jsf");
    }
}
