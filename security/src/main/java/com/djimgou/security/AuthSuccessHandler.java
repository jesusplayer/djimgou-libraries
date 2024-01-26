package com.djimgou.security;


import com.djimgou.audit.service.AuditBdService;
import com.djimgou.security.core.UtilisateurDetails;
import com.djimgou.security.core.model.Utilisateur;
import com.djimgou.security.core.model.dto.utilisateur.UtilisateurSessionDto;
import com.djimgou.security.core.service.MyVoter;
import com.djimgou.security.core.service.SecuritySessionService;
import com.djimgou.security.service.AuthSuccessProvider;
import com.djimgou.security.service.TokenAuthenticationService;
import com.djimgou.session.context.SessionContext;
import com.djimgou.session.enums.SessionKeys;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Optional;

import static com.djimgou.core.util.AppUtils.has;

/**
 * @author DJIMGOU NKENNE DANY MARC 08/2020
 */
@Getter
@Log4j2
@Component
public class AuthSuccessHandler implements AuthenticationSuccessHandler {
    private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();


    private AuditBdService auditBdService;

    private SecuritySessionService sessionService;

    private TokenAuthenticationService tokenAuthenticationService;

    private AuthSuccessProvider authSuccessProvider;

    public AuthSuccessHandler(
            AuditBdService auditBdService,
            SecuritySessionService sessionService,
            TokenAuthenticationService tokenAuthenticationService,
            Optional<AuthSuccessProvider> authSuccessProvider
    ) {
        this.auditBdService = auditBdService;
        this.sessionService = sessionService;
        this.tokenAuthenticationService = tokenAuthenticationService;
        this.authSuccessProvider = authSuccessProvider.orElse(null);
    }

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
/*
        UtilisateurDetails uDetail = sessionService.currentUser(authentication);
        if (AppUtils.has(uDetail)) {
            MyVoter.userSessionToUpdate.put(uDetail.getUsername(), Boolean.FALSE);
            String uId = uDetail.getUtilisateur().getId().toString();
            final HttpSession session = request.getSession();
            session.setAttribute(SessionKeys.USER_ID, uId);

            Utilisateur user = uDetail.getUtilisateur().singleInfo();
            auditBdService.add(user, AuditAction.CONNEXION);


            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
            UtilisateurSessionDto uSess = new UtilisateurSessionDto();
            uSess.fromDto(uDetail.getUtilisateur());

            String token = TokenAuthenticationService.addAuthentication(response, uSess.getUsername());

            //String token = response.getHeader(TokenAuthenticationService.HEADER_STRING);
            uSess.setToken(token);


            String str = objectMapper.writeValueAsString(uSess);
            //session.setAttribute(SessionKeys.CONNECTED_USER, uSess);
            SessionContext.setCurrentSessionId(session.getId());
            SessionContext.setCurrentUsername(uSess.getUsername());

            session.setAttribute(SessionKeys.USERNAME, uSess.getUsername());
            session.setAttribute(SessionKeys.SESSION_ID, session.getId());
            response.setHeader("Content-Type", "application/json");
            response.getWriter().write(str);
            response.getWriter().flush();
        }

*/
        if (!has(authSuccessProvider)) {
            UtilisateurDetails uDetail = sessionService.currentUser(authentication);
            if (has(uDetail)) {
                MyVoter.userSessionToUpdate.put(uDetail.getUsername(), Boolean.FALSE);
                String uId = uDetail.getUtilisateur().getId().toString();
                final HttpSession session = request.getSession();
                session.setAttribute(SessionKeys.USER_ID, uId);

                Utilisateur user = uDetail.getUtilisateur().singleInfo();

//            auditBdService.add(user, AuditAction.CONNEXION, uDetail.getUsername(), uDetail.getUtilisateur().getId());


                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
                objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
                UtilisateurSessionDto uSess = new UtilisateurSessionDto();
                uSess.fromDto(uDetail.getUtilisateur());

                String token = tokenAuthenticationService.addAuthentication2(response, uSess.getUsername());
                //String token = response.getHeader(TokenAuthenticationService.HEADER_STRING);
                uSess.setToken(token);


                String str = objectMapper.writeValueAsString(uSess);
                // session.setAttribute(SessionKeys.CONNECTED_USER, uSess);
                SessionContext.setCurrentSessionId(session.getId());
                SessionContext.setCurrentUsername(uSess.getUsername());
                SessionContext.setCurrentUserId(uSess.getId().toString());

                session.setAttribute(SessionKeys.USERNAME, uSess.getUsername());
                session.setAttribute(SessionKeys.USER_ID, uSess.getId().toString());
                session.setAttribute(SessionKeys.SESSION_ID, session.getId());
                response.setHeader("Content-Type", "application/json");
                response.getWriter().write(str);
                response.getWriter().flush();
            }
        } else {
            authSuccessProvider.onAuthenticationSuccess(request, response, authentication,this);
        }

    }
}
