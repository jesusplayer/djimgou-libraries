package com.act.audit.exceptions;

import com.act.core.exception.NotFoundException;

public class AuditNotFoundException extends NotFoundException {
    public AuditNotFoundException() {
        super("Cet élément n'existe pas dans l'audit");
    }
}
