package com.djimgou.security.core.model.views;

import com.djimgou.core.annotations.ColumnExport;

public interface IUtilisateurExport {

    @ColumnExport(value = "Nom utilisateur", order = 1)
    String getUsername();

    @ColumnExport(value = "Actif", order = 2)
    Boolean getEnabled();

    @ColumnExport(value = "Email", order = 3)
    String getEmail();

    @ColumnExport(value = "Noms et prenoms", order = 4)
    String getFullName();

    @ColumnExport(value = "Telephone", order = 5)
    String getTelephone();

    @ColumnExport(value = "Fonction", order = 6)
    String getFonction();
}
