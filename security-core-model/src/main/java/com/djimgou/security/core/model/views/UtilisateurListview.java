package com.djimgou.security.core.model.views;

import java.util.UUID;

public interface UtilisateurListview {
    UUID getId();
    String getUsername();

    Boolean getEnabled();

    String getEmail();

    String getNoms();

    String getTelephone();

    String getFonction();

    Boolean getDeleted();
    Boolean getReadonlyValue();
}
