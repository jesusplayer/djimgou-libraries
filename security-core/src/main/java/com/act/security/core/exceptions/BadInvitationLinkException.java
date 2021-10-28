package com.act.security.core.exceptions;


public class BadInvitationLinkException extends Exception {
    public BadInvitationLinkException() {
        super("Le lien d'invitation est invalide, veuillez recommencer l'invitation");
    }

    public BadInvitationLinkException(String message) {
        super(message);
    }
}
