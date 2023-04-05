package com.djimgou.security.core.model.views;

import com.djimgou.core.annotations.ColumnExport;

public interface IRoleExport {


    @ColumnExport(value = "Nom du rôle", order = 1)
    String getName();

    @ColumnExport(value = "Description", order = 2)
    String getDescription();

    @ColumnExport(value = "Rôle parent", order = 3)
    String getNomParent();

}
