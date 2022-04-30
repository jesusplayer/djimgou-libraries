package com.djimgou.session.service;

import javax.servlet.http.HttpSession;
import java.util.Optional;
import java.util.UUID;

/**
 * Interface com.djimgou.audit.service.SessionService,
 * referenced in field AuditBdService.sessionService,
 * will not be accessible from module audit
 * Interface com.djimgou.audit.service.SessionService,
 * referenced in class com.djimgou.security.service.SessionServiceImpl,
 * will not be accessible from module security
 * Interface com.djimgou.audit.service.SessionService,
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
