package com.act.reporting.model.dto;

import com.act.core.infra.BaseFilterDto;
import lombok.Data;

@Data
public class ReportFilterDto extends BaseFilterDto {
    String fichier1;

    String nom;
}
