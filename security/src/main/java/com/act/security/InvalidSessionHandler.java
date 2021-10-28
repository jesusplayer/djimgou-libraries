package com.act.security;

import com.act.session.context.SessionContext;
import com.act.tenantmanager.aop.TenantContext;
import org.springframework.security.web.session.InvalidSessionStrategy;
import org.springframework.stereotype.Service;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * DJIMGOU NKENNE DANY MARC 08/2020- 09/2021
 */
@Service
public class InvalidSessionHandler implements InvalidSessionStrategy {
    @Override
    public void onInvalidSessionDetected(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
/*        SessionContext.clear();
        TenantContext.clear();*/
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Votre session a expiré");
    }
}
