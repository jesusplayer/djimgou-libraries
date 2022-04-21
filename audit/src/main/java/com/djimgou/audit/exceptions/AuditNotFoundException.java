package com.djimgou.audit.exceptions;

import com.djimgou.core.exception.NotFoundException;

public class AuditNotFoundException extends NotFoundException {
    public AuditNotFoundException() {
        super("Cet élément n'existe pas dans l'audit");
    }
}
