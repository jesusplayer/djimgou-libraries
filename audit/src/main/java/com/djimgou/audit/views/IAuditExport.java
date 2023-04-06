package com.djimgou.audit.views;

import com.djimgou.audit.model.AuditAction;
import com.djimgou.core.annotations.ColumnExport;

public interface IAuditExport {

    @ColumnExport(value = "Date de l'audit", order = 1)
    String getDate();

    @ColumnExport(value = "Nom de la cible", order = 2)
    String getNomEntite();

    @ColumnExport(value = "Nom utilisateur", order = 3)
    String getUsername();

    @ColumnExport(value = "Action", order = 4)
    AuditAction getAction();

    @ColumnExport(value = "Données", order = 5)
    String getData();

}
