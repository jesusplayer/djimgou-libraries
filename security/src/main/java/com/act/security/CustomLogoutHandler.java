package com.act.security;

import com.act.session.enums.SessionKeys;
import com.act.security.core.UtilisateurDetails;
import com.act.security.core.service.SecuritySessionService;
import com.act.session.context.SessionContext;
import com.act.tenantmanager.aop.TenantContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;

import static com.act.core.util.AppUtils.has;

/**
 * DJIMGOU NKENNE DANY MARC 08/2020- 09/2021
 */
@Service
public class CustomLogoutHandler implements LogoutHandler {
   private SecuritySessionService sessionService;
/*    @Autowired
    FindByIndexNameSessionRepository<? extends Session> sessions;*/

    public CustomLogoutHandler(SecuritySessionService sessionService) {
        this.sessionService = sessionService;
    }

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response,
                       Authentication authentication) {
       /* String userName = UserUtils.getAuthenticatedUserName();
        userCache.evictUser(userName);*/
        //String userName = UserUtils.getAuthenticatedUserName();
        UtilisateurDetails u = sessionService.currentUser(authentication);
        if(has(u)){
            request.setAttribute(SessionKeys.USER_ID, Objects.requireNonNull(u.getUtilisateur().getId()).toString());
        }
        request.getSession().invalidate();
        SecurityContextHolder.getContext().setAuthentication(null);
        TenantContext.clear();
        SessionContext.clear();
    }
}
