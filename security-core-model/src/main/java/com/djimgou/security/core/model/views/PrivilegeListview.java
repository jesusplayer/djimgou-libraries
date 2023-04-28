package com.djimgou.security.core.model.views;

import org.springframework.http.HttpMethod;

import java.util.UUID;

public interface PrivilegeListview {
    UUID getId();
    String getCode();

    String getName();
    String getUrl();
    String getDescription();
    String getParentId();
    String getNameParent();
    Boolean getDeleted();
    Boolean getReadonlyValue();
    HttpMethod getHttpMethod();
}
