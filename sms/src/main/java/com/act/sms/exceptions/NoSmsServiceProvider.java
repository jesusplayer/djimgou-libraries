package com.act.sms.exceptions;

public class NoSmsServiceProvider extends Exception {
    public NoSmsServiceProvider(String message) {
        super(message);
    }
    public NoSmsServiceProvider() {
        super("Aucun fournisseur de SMS n'est défini. veuillez contacter l'administrateur pour l'inclure sa librairie dans le projet");
    }
}
