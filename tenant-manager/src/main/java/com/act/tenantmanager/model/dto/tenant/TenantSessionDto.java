package com.act.tenantmanager.model.dto.tenant;

import com.act.tenantmanager.model.Pays;
import lombok.Data;

import java.io.Serializable;

@Data
public class TenantSessionDto implements Serializable {

    private static final long serialVersionUID = 1234567L;

    String externalId;

    String code;

    String nom;

    String ville;

    Pays pays;
}