package com.act.core.testing.app.model.enums;

public enum StatutLocation {
    /**
     * Indique que la location est confirmée par le client donc facture peut être générée
     */
    RESERVE("RESERVE"),
    /**
     * Indique que la location est dans le panier
     */
    PANIER("PANIER"),
    PAYER("PAYER");
    private final String text;

    StatutLocation(final String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }

    public boolean isReserve() {
        return text.equals(StatutLocation.RESERVE.text);
    }

    public boolean isPayer() {
        return text.equals(StatutLocation.PAYER.text);
    }

    public boolean isEnCoursReservation() {
        return text.equals(StatutLocation.PANIER.text);
    }
}
