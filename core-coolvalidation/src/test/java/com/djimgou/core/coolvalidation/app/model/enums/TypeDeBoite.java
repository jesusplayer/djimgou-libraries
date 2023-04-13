package com.djimgou.core.coolvalidation.app.model.enums;

public enum TypeDeBoite {
    MANUELLE("MANUELLE"),
    AUTOMATIQUE("AUTOMATIQUE");
    private final String text;

    TypeDeBoite(final String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }

    public boolean isManuel() {
        return text.equals(TypeDeBoite.MANUELLE.text);
    }

    public boolean isAutomatique() {
        return text.equals(TypeDeBoite.AUTOMATIQUE.text);
    }
}
