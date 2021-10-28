package com.act.session.context;

public class SessionContext {
    /**
     * Une session de type spring-session: org.springframework.session.Session;
     *
     * @Autowired FindByIndexNameSessionRepository<? extends Session> sessions;
     */
    private static ThreadLocal<Object> currentSession = new InheritableThreadLocal<>();
    private static ThreadLocal<String> currentSessionId = new InheritableThreadLocal<>();
    private static ThreadLocal<String> currentUsername = new InheritableThreadLocal<>();

    public static Object getCurrentSession() {
        return currentSession.get();
    }

    public static String getCurrentUsername() {
        return currentUsername.get();
    }
    public static String getCurrentSessionId() {
        return currentSessionId.get();
    }

    public static void setCurrentSession(Object tenant) {
        currentSession.set(tenant);
    }

    public static void setCurrentUsername(String username) {
        currentUsername.set(username);
    }

    public static void setCurrentSessionId(String sessionId) {
        currentUsername.set(sessionId);
    }

    public static void clear() {
        currentSession.set(null);
        currentUsername.set(null);
        currentSessionId.set(null);
    }

}
