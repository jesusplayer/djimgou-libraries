package com.djimgou.security.core.model.views;

import org.springframework.http.HttpMethod;

import java.util.UUID;

public interface IPrivilegeDto {
    UUID getId();

    String getCode();

    String getName();

    String getUrl();

    HttpMethod getHttpMethod();
}
