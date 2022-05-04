package com.djimgou.core.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "api.crud.url")
public class ApiCrudUrlConfig {
    String create;
    String list;
    String detail;
    String delete;
    String update;
    String find;
    String findUnpaged;
    String filter;
    String advancedFilter;
}
