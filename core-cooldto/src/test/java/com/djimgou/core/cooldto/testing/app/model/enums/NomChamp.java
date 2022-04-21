package com.djimgou.core.cooldto.testing.app.model.enums;

public enum NomChamp {
    NOMBRE_DE_JOUR("NOMBRE_DE_JOUR");
    private final String text;

    NomChamp(final String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }

    public boolean isNombreJour() {
        return text.equals(NomChamp.NOMBRE_DE_JOUR.text);
    }

}
