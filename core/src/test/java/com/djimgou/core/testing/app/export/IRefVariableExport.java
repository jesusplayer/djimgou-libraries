package com.djimgou.core.testing.app.export;

import com.djimgou.core.annotations.ColumnExport;

public interface IRefVariableExport {
    @ColumnExport(value = "Code sous-domaine")
    String getCodeSousDomaine();

    @ColumnExport(value = "Nom sous-domaine",order = 1)
    String getNomSousDomaine();

    @ColumnExport(value = "Code domaine", order = 2)
    String getCodeDomaine();

    @ColumnExport(value = "Nom domaine", order = 3)
    String getNomDomaine();



}
