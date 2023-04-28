package com.djimgou.security.core.model.views;

import java.util.UUID;

public interface RoleListview {
    UUID getId();
    String getName();



    String getDescription();

    String getParentId();
    String getNameParent();
    Boolean getDeleted();
    Boolean getReadonlyValue();
}
