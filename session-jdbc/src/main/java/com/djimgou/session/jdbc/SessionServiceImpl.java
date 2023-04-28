package com.djimgou.session.jdbc;

import com.djimgou.session.context.SessionContext;
import com.djimgou.session.enums.SessionKeys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.session.Session;
import org.springframework.session.SessionRepository;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpSession;
import java.util.Optional;
import java.util.UUID;


/**
 * Interface Obligatoire
 * On veille à ce que la sesion soit indépendante selon
 * le modèle à utiliser
 * Si j'utilise Web Http alors je le configure ici. Sinon
 * J'utilise Redis
 */
@Component
public class SessionServiceImpl  {

    @Autowired
    SessionRepository sessionRepo;

    @Autowired(required = false)
    HttpSession httpSession;

    public boolean hasUser() {
        return SessionContext.getCurrentUsername() != null;
    }

    public Optional<UUID> currentUserId() {
        return Optional.empty();
    }

    public String username() {
        return SessionContext.getCurrentUsername();
    }

    /**
     * C'est ici qu'on défini quelle session utiliser selon qu'on utilise Redis ou non ou tout autre truc
     *
     * @return
     */
    public HttpSession getSession() {
        if (null == httpSession || httpSession.getAttribute(SessionKeys.USERNAME) == null) {
            if (SessionContext.getCurrentSessionId() == null) {
                return httpSession;
            }

            Session sess = sessionRepo.findById(SessionContext.getCurrentSessionId());
            if (sess != null) {
                SessionContext.setCurrentUsername(sess.getAttribute(SessionKeys.USERNAME));
            }

            return new CusHttpSession(sess);
        }

        return SessionContext.getCurrentSession()!=null?SessionContext.getCurrentSession():httpSession;
    }
}
