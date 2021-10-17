package com.act.security;

import com.act.security.tracking.authentication.security.service.MyVoter;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
/**
 * @author DJIMGOU NKENNE DANY MARC 08/2020- 09/2021
 */
public class AuthAccessDeniedHandler implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AccessDeniedException e) throws IOException, ServletException {
        HttpSession session = httpServletRequest.getSession(true);
        Boolean isNotC = (Boolean) session.getAttribute(MyVoter.USER_PASSWORD_NOT_CHANGED);
        if (isNotC != null && isNotC) {
            httpServletResponse.sendRedirect(UrlsAuthorized.CHANGE_PASSWORD.toString());
        } else {
            httpServletResponse.sendRedirect(UrlsAuthorized.UNAUTHORIZED.toString());
        }

    }
}
