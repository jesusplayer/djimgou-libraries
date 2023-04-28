package com.djimgou.audit.views;

import com.djimgou.audit.model.AuditAction;
import com.djimgou.core.annotations.ColumnExport;

public interface AuditListView {

    String getDate();

    String getNomEntite();

    String getUsername();

    AuditAction getAction();
}
