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

    @Override
    public String toString() {
        return text;
    }

    public static boolean isAuditableMethod(String httpMethod) {
        return HttpMethod.GET.name().equals(httpMethod) ||
                HttpMethod.POST.name().equals(httpMethod) ||
                HttpMethod.PUT.name().equals(httpMethod) ||
                HttpMethod.DELETE.name().equals(httpMethod);
    }

    public static AuditAction fromHttp(String httpMethod) {
        if (HttpMethod.GET.name().equals(httpMethod)) {
            return AuditAction.LECTURE;
        }

        if (HttpMethod.POST.name().equals(httpMethod)) {
            return AuditAction.CREATION;
        }

        if (HttpMethod.PUT.name().equals(httpMethod)) {
            return AuditAction.MODIFICATION;
        }
        if (HttpMethod.DELETE.name().equals(httpMethod)) {
            return AuditAction.SUPPRESSION;
        }
        return AuditAction.LECTURE;
    }
}
