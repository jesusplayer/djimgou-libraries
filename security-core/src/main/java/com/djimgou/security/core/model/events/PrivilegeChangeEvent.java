package com.djimgou.security.core.model.events;

import com.djimgou.audit.model.AuditAction;
import com.djimgou.security.core.model.Privilege;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

/**
 * @author DJIMGOU NKENNE DANY MARC 08/2020
 */
@Getter
@Setter
public class PrivilegeChangeEvent extends ApplicationEvent {
    AuditAction auditAction;
    Privilege privilege;
    public PrivilegeChangeEvent(Object source, AuditAction auditAction, Privilege privilege) {
        super(source);
        this.auditAction = auditAction;
        this.privilege = privilege;
    }
}
