package com.act.security;


import com.act.audit.model.AuditAction;
import com.act.security.model.Utilisateur;
import com.act.security.model.dto.utilisateur.UtilisateurSessionDto;
import com.act.core.model.enums.SessionKeys;
import com.act.security.service.SessionServiceImpl;
import com.act.security.tracking.authentication.security.service.MyVoter;
import com.act.core.util.AppUtils;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import com.act.audit.service.AuditBdService;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

import static com.act.core.util.AppUtils.has;

/**
 * @author DJIMGOU NKENNE DANY MARC 08/2020
 */
@Log4j2
@Component
public class AuthSuccessHandler implements AuthenticationSuccessHandler {
    private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    @Autowired
    private HttpSession httpSession;

    @Autowired
    AuditBdService auditBdService;

    @Autowired
    SessionServiceImpl sessionService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) throws IOException, ServletException {
        chain.doFilter(request, response);
    }

    /**
     * Appelée lorsque le user se logue
     *
     * @param request        le servlet de la requete
     * @param response       le servlet de la réponse
     * @param authentication l'objet authentification de Spring
     * @throws IOException      exception
     * @throws ServletException exception
     */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        UtilisateurDetails uDetail = sessionService.currentUser(authentication);
        if (AppUtils.has(uDetail)) {
            MyVoter.userSessionToUpdate.put(uDetail.getUsername(), Boolean.FALSE);
            String uId = uDetail.getUtilisateur().getId().toString();
            request.getSession().setAttribute(SessionKeys.CONNECTED_USER_ID, uId);
            httpSession.setAttribute(SessionKeys.CONNECTED_USER_ID, uId);
            Utilisateur user = uDetail.getUtilisateur().singleInfo();
            auditBdService.add(user, AuditAction.CONNEXION);


            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
            UtilisateurSessionDto uSess = new UtilisateurSessionDto();
            uSess.fromDto(uDetail.getUtilisateur());
            String token = response.getHeader("Authorization");
            uSess.setToken(token);
            String str = objectMapper.writeValueAsString(uSess);
            httpSession.setAttribute(SessionKeys.CONNECTED_USER, uSess);
            response.setHeader("Content-Type", "application/json");
            response.getWriter().write(str);
            response.getWriter().flush();
            //redirectStrategy.sendRedirect(request, response, UrlsAuthorized.INDEX.toString());
        }
    }
}
