package com.djimgou.security;

import com.djimgou.audit.model.AuditAction;
import com.djimgou.audit.service.AuditBdService;
import com.djimgou.security.core.AppSecurityConfig;
import com.djimgou.security.service.TokenAuthenticationService;
import com.djimgou.session.context.SessionContext;
import com.djimgou.session.enums.SessionKeys;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

import static com.djimgou.core.util.AppUtils.has;

public class AuditRequestFilter extends OncePerRequestFilter {


    private final AuditBdService auditBdService;

    public AuditRequestFilter(AuditBdService auditBdService) {
        this.auditBdService = auditBdService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        filterChain.doFilter(request, response);
        if (AuditAction.isAuditableMethod(request.getMethod())) {
            final AuditAction auditAction = AuditAction.fromHttp(request.getMethod());

            final String autitText = String.format("L'utilisateur: %s a effectué la requête %s sur l'adresse %s", SessionContext.getCurrentUsername(), auditAction, request.getRequestURI());
            logger.info(autitText);
            /*if(auditAction.equals(AuditAction.LECTURE)){

            }
            auditBdService.addAsync(autitText, auditAction);*/
        }
    }
}
