package com.act.reporting.model.dto;

import com.act.core.model.IEntityDto;
import com.act.reporting.model.Report;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

@Data
public class ReportDto implements IEntityDto {
    @NotBlank()
    String nomReport;

    String clientRouteUrl;

    @Min(0)
    Integer position;

    @Override
    public Class<Report> originalClass() {
        return Report.class;
    }
}
