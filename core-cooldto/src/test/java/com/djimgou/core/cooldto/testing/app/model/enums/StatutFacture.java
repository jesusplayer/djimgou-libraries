package com.djimgou.core.cooldto.testing.app.model.enums;

public enum StatutFacture {
    PAYER("PAYER"),
    IMPAYER("IMPAYER");
    private final String text;

    StatutFacture(final String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }

    public boolean isPayer() {
        return text.equals(StatutFacture.PAYER.text);
    }

    public boolean isImpayer() {
        return text.equals(StatutFacture.IMPAYER.text);
    }
}
