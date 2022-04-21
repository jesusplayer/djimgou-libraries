package com.djimgou.audit.model;

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
}
