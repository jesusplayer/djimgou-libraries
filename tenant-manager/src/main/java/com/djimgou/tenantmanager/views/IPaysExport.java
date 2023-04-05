package com.djimgou.tenantmanager.views;

import com.djimgou.core.annotations.ColumnExport;

public interface IPaysExport {
    @ColumnExport(value = "Code du pays", order = 1)
    String getCode();

    @ColumnExport(value = "Nom du pays", order = 2)
    String getNom();

}
