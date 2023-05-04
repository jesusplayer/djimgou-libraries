package com.djimgou.audit.views;

import com.djimgou.audit.model.AuditAction;
import com.djimgou.core.annotations.ColumnExport;

import java.util.UUID;

public interface AuditListView {
    UUID getId();
    String getDate();

    String getNomEntite();

    String getUsername();

    AuditAction getAction();
}
