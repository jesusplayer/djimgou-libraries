package com.act.session.jdbc;

import com.act.session.context.SessionContext;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.filter.RequestContextFilter;
//import org.springframework.web.filter.RequestContextFilter
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;


@Service
public class RequestInterceptor extends RequestContextFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

       final HttpSession session = request.getSession();
        String username = (String) session.getAttribute("X-USERNAME");
        String sessionId = (String) session.getAttribute("X-SESSION-ID");
        String sessionId2 = (String) session.getAttribute("CONNECTEDUSERID");
        Object Object = session.getAttribute("CONNECTEDUSER");
        String sessionId4 = (String) session.getAttribute("TENANT");
        if (username != null && sessionId != null) {
            SessionContext.setCurrentSessionId(sessionId);
            SessionContext.setCurrentUsername(username);
        }
        logger.info("Username: " + username);

        filterChain.doFilter(request, response);
    }

}