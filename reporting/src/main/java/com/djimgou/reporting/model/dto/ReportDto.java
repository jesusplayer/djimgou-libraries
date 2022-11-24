package com.djimgou.reporting.model.dto;

import com.djimgou.core.cooldto.model.IEntityDto;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

@Data
public class ReportDto implements IEntityDto {
    @NotBlank()
    String nom;

    @NotBlank()
    String nomReport;

    String clientRouteUrl;

    @Min(0)
    Integer position;
}
