package com.djimgou.audit.model;

import org.springframework.http.HttpMethod;

/**
 * @author DJIMGOU NKENNE DANY MARC 08/2020
 */
public enum AuditAction {
    CREATION("CREATION"),
    LECTURE("LECTURE"),
    MODIFICATION("MODIFICATION"),
    SUPPRESSION("SUPPRESSION"),
    CONNEXION("CONNEXION"),
    DECONNEXION("DECONNEXION");

    private final String text;

    AuditAction(final String text) {
        this.text = text;
    }

    public static boolean isAuditableMethod(String method) {
        return "GET".equals(method) || "POST".equals(method) || "PUT".equals(method) || "DELETE".equals(method);
    }

    public static AuditAction fromHttp(String method) {
        HttpMethod m = HttpMethod.valueOf(method);
        switch (m){
            case GET:
                return AuditAction.LECTURE;
            case PUT:
                return AuditAction.MODIFICATION;
            case POST:
                return AuditAction.CREATION;
            case DELETE:
                return AuditAction.SUPPRESSION;

        }
        return null;
    }

    @Override
    public String toString() {
        return text;
    }
}
