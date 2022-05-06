package com.djimgou.filestorage.model;

import com.djimgou.core.infra.BaseFilterAdvancedDto;
import com.djimgou.core.infra.QueryFieldFilter;
import lombok.Data;


@Data
public class FichierFilterAdvDto extends BaseFilterAdvancedDto {
    QueryFieldFilter<String> nom;
    QueryFieldFilter<String> fichier1;
    QueryFieldFilter<String> fichier2;
    QueryFieldFilter<String> dossier;
    QueryFieldFilter<String> customData;
    QueryFieldFilter<String> type;
    QueryFieldFilter<String> description;
}
