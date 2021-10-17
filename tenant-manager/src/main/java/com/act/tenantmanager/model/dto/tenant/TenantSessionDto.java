package com.act.tenantmanager.model.dto.tenant;

import com.act.tenantmanager.model.Pays;
import lombok.Data;

import java.io.Serializable;

@Data
public class TenantSessionDto implements Serializable {
    String externalId;

    String code;

    String nom;

    String ville;

    Pays pays;
}