package com.djimgou.reporting.model.dto;

import com.djimgou.core.infra.BaseFilterDto;
import lombok.Data;

@Data
public class ReportFilterDto extends BaseFilterDto {
    String fichier1;

    String nom;
}
