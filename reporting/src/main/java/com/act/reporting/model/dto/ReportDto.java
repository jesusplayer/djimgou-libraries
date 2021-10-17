package com.act.reporting.model.dto;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

@Data
public class ReportDto {
    @NotBlank()
    String nomReport;

    String clientRouteUrl;

    @Min(0)
    Integer position;
}
