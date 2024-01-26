package com.djimgou.session.context;

import javax.servlet.http.HttpSession;

public class SessionContext {
    /**
     * Une session de type spring-session: org.springframework.session.Session;
     *
     * @Autowired FindByIndexNameSessionRepository<? extends Session> sessions;
     */
    private static ThreadLocal<String> currentSessionId = new InheritableThreadLocal<>();
    private static ThreadLocal<String> currentTenantId = new InheritableThreadLocal<>();
    private static ThreadLocal<String> currentUserId = new InheritableThreadLocal<>();
    private static ThreadLocal<String> currentUsername = new InheritableThreadLocal<>();
    private static ThreadLocal<HttpSession> currentSession = new InheritableThreadLocal<>();


    public static String getCurrentUsername() {
        return currentUsername.get();
    }

    public static String getCurrentSessionId() {
        return currentSessionId.get();
    }

    public static void setCurrentUsername(String username) {
        currentUsername.set(username);
    }

    public static void setCurrentTenantId(String tenantId) {
        currentTenantId.set(tenantId);
    }

    public static void setCurrentSessionId(String sessionId) {
        currentSessionId.set(sessionId);
    }

    public static void setCurrentUserId(String currentUserId) {
        SessionContext.currentUserId.set(currentUserId);
    }

    public static void clear() {
        currentUsername.set(null);
        currentSessionId.set(null);
        currentTenantId.set(null);
        currentUserId.set(null);
        currentSession.set(null);
    }

    public static void setCurrentSession(HttpSession session) {
        currentSession.set(session);
    }
}
