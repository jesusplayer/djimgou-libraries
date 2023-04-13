package com.djimgou.core.coolvalidation.app.model.enums;

public enum TypeEnergie {
    /**
     * Indique le motteur utilise l'essence
     */
    ESSENCE("ESSENCE"),
    /**
     * Indique que le moteur est diesel
     */
    DIESEL("DIESEL"),
    /**
     * Indique que le moteur utilise le
     */
    ELECTRIQUE("ELECTRIQUE");

    private final String text;

    TypeEnergie(final String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }

    public boolean isEssence() {
        return text.equals(TypeEnergie.ESSENCE.text);
    }

    public boolean isDiesel() {
        return text.equals(TypeEnergie.DIESEL.text);
    }

    public boolean isElectrique() {
        return text.equals(TypeEnergie.ELECTRIQUE.text);
    }
}
