package com.djimgou.filestorage.model;

import com.djimgou.core.infra.BaseFilterDto;
import lombok.Data;

@Data
public class FichierFilterDto extends BaseFilterDto {
    String nom;
    String fichier1;
    String fichier2;
    String dossier;
    String customData;
    String type;
    String description;
}
