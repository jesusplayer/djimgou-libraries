package com.act.core.testing.app.model.enums;

public enum Operateur {
    INF("<"),
    INF_OU_EG("<="),
    SUP(">"),
    SUP_OU_EG(">=");
    private final String text;

    Operateur(final String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }

    public boolean isInf() {
        return text.equals(Operateur.INF.text);
    }

    public boolean isInfOuEgal() {
        return text.equals(Operateur.INF_OU_EG.text);
    }

    public boolean isSup() {
        return text.equals(Operateur.SUP.text);
    }

    public boolean isSupOuEgal() {
        return text.equals(Operateur.SUP_OU_EG.text);
    }
}
