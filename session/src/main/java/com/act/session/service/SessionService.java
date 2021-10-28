package com.act.session.service;

import java.util.Optional;
import java.util.UUID;
import javax.servlet.http.HttpSession;

/**
 * Interface com.act.audit.service.SessionService,
 * referenced in field AuditBdService.sessionService,
 * will not be accessible from module audit
 * Interface com.act.audit.service.SessionService,
 * referenced in class com.act.security.service.SessionServiceImpl,
 * will not be accessible from module security
 * Interface com.act.audit.service.SessionService,
 * referenced in field AbstractSecurityBdService.httpSession,
 * will not be accessible from module security
 */
public interface SessionService {
    boolean hasUser();
    Optional<UUID> currentUserId();
    String username();

    /**
     * Les sessions peuvent varier selong qu'on utilise jdbc ou
     *  Redis
     * @return
     */
    HttpSession getSession();
}
