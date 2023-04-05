package com.djimgou.tenantmanager.views;

import com.djimgou.core.annotations.ColumnExport;

public interface ITenantExport {
    @ColumnExport(value = "Code du centre", order = 1)
    String getCode();

    @ColumnExport(value = "Nom du centre", order = 2)
    String getNom();

    @ColumnExport(value = "Ville", order = 3)
    String getVille();

    @ColumnExport(value = "Pays", order = 4)
    String getNomPays();

}
