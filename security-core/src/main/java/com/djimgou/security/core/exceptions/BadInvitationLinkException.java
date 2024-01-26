package com.djimgou.security.core.exceptions;


public class BadInvitationLinkException extends Exception {
    public BadInvitationLinkException() {
        super("bad.invitationLink");
    }

    public BadInvitationLinkException(String message) {
        super(message);
    }
}
