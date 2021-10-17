package com.act.security.model.events;

import com.act.audit.model.AuditAction;
import com.act.security.model.Role;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;


/**
 * @author DJIMGOU NKENNE DANY MARC 08/2020
 */
@Getter @Setter
public class AuthorityChangeEvent extends ApplicationEvent {
    AuditAction auditAction;
    Role role;
    public AuthorityChangeEvent(Object source, AuditAction auditAction, Role role) {
        super(source);
        this.auditAction = auditAction;
        this.role = role;
    }
}
