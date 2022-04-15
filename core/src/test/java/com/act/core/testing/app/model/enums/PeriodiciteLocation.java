package com.act.core.testing.app.model.enums;

public enum PeriodiciteLocation {
    HORAIRE("HORAIRE"),
    JOURNALIERE("JOURNALIERE");
    private final String text;

    PeriodiciteLocation(final String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }

    public boolean isParHeure() {
        return text.equals(PeriodiciteLocation.HORAIRE.text);
    }

    public boolean isParJour() {
        return text.equals(PeriodiciteLocation.JOURNALIERE.text);
    }
}
