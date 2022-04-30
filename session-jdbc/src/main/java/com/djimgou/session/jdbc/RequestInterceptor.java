package com.djimgou.session.jdbc;

import com.djimgou.session.context.SessionContext;
import com.djimgou.session.enums.SessionKeys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.session.Session;
import org.springframework.session.SessionRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.RequestContextFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;


@Service
public class RequestInterceptor extends RequestContextFilter {
    @Autowired
    SessionRepository sessionRepository;

    /**
     * NB lors du debogage, ce filtre peut être appelé deux fois pour la meme
     * requete. ceci est du au UI microservice qui l'appelle(via Zuul)
     * et au microservice de destination qui l'appelle aussi
     *
     * @param request
     * @param response
     * @param filterChain
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        String proxySesssionId = request.getHeader(SessionKeys.PROXY_SESSION_ID);
        String username;
        String sessionId;
        String tenantId;
        if (proxySesssionId != null) {
            Session sessionS = sessionRepository.findById(proxySesssionId);
            username = sessionS.getAttribute(SessionKeys.USERNAME);
            sessionId = sessionS.getAttribute(SessionKeys.SESSION_ID);
            tenantId =  sessionS.getAttribute(SessionKeys.TENANT_ID);
        } else {
            username = (String) session.getAttribute(SessionKeys.USERNAME);
            sessionId = (String) session.getAttribute(SessionKeys.SESSION_ID);
            tenantId = (String) session.getAttribute(SessionKeys.TENANT_ID);
        }
        /*String sessionId2 = (String) session.getAttribute(SessionKeys.USER_ID);
        String tenantId = (String) session.getAttribute(SessionKeys.TENANT_ID);*/
        if (username != null && sessionId != null) {
            SessionContext.setCurrentSessionId(sessionId);
            SessionContext.setCurrentUsername(username);
            SessionContext.setCurrentTenantId(tenantId);
        }
        logger.info("Username: " + username);

        filterChain.doFilter(request, response);
    }

}