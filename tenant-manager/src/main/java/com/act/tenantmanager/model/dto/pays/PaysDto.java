package com.act.tenantmanager.model.dto.pays;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class PaysDto {
    @NotBlank()
    String code;
    @NotBlank()
    String nom;
}
