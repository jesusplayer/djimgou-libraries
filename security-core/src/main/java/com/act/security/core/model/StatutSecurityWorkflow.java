package com.act.security.core.model;

/**
 * @author DJIMGOU NKENNE DANY MARC 08/2020
 */
public enum StatutSecurityWorkflow {
    //    PAS_ENCORE_DE_PRIVILLEGE("Pas encore de privileges"),
    EN_ATTENTE_DE_VALIDATION("En attente de validation"),
    VALIDE("Validé"),
    SUPPRIMER("Supprimé"),
    REJETER("Rejeté");

    private final String text;

    StatutSecurityWorkflow(final String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }

    public boolean isValide() {
        return text.equals(StatutSecurityWorkflow.VALIDE.text);
    }
    public boolean isSupprimer() {
        return text.equals(StatutSecurityWorkflow.SUPPRIMER.text);
    }

    public boolean isRejeter() {
        return text.equals(StatutSecurityWorkflow.REJETER.text);
    }

    public boolean isEnAttente() {
        return text.equals(StatutSecurityWorkflow.EN_ATTENTE_DE_VALIDATION.text);
    }
}
