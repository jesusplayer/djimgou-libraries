package com.djimgou.security.core.model.views;

import com.djimgou.core.annotations.ColumnExport;
import org.springframework.http.HttpMethod;

public interface IPrivilegeExport {

    @ColumnExport(value = "Code du privilège", order = 1)
    String getCode();

    @ColumnExport(value = "Nom", order = 2)
    String getName();

    @ColumnExport(value = "Url de la ressource", order = 3)
    String getUrl();

    @ColumnExport(value = "Description", order = 4)
    String getDescription();

    @ColumnExport(value = "Méthode Http", order = 5)
    HttpMethod getHttpMethod();

}
