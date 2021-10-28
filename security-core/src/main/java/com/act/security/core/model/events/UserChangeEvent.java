package com.act.security.core.model.events;

import com.act.audit.model.AuditAction;
import com.act.security.core.model.Utilisateur;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

/**
 * @author DJIMGOU NKENNE DANY MARC 08/2020
 */
@Getter
@Setter
public class UserChangeEvent extends ApplicationEvent {
    AuditAction auditAction;
    Utilisateur utilisateur;
    public UserChangeEvent(Object source, AuditAction auditAction, Utilisateur utilisateur) {
        super(source);
        this.auditAction = auditAction;
        this.utilisateur = utilisateur;
    }
}
