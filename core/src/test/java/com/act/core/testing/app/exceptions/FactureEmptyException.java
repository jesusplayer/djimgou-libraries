package com.act.core.testing.app.exceptions;


public class FactureEmptyException extends Exception {
    public FactureEmptyException() {
        super("Impossible de créer cette facture car le panier du client est vide");
    }

    public FactureEmptyException(String message) {
        super(message);
    }
}
