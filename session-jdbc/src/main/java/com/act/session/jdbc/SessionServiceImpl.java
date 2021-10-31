package com.act.session.jdbc;

import com.act.session.context.SessionContext;
import com.act.session.enums.SessionKeys;
import com.act.session.service.SessionService;
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
public class SessionServiceImpl implements SessionService {

    @Autowired
    SessionRepository sessionRepo;

    @Autowired(required = false)
    HttpSession httpSession;

    @Override
    public boolean hasUser() {
        return false;
    }

    @Override
    public Optional<UUID> currentUserId() {
        return Optional.empty();
    }

    @Override
    public String username() {
        return SessionContext.getCurrentUsername();
    }

    /**
     * C'est ici qu'on défini quelle session utiliser selon qu'on utilise Redis ou non ou tout autre truc
     *
     * @return
     */
    @Override
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

        return httpSession;
    }
}
