package com.act.security.tracking.authentication.security.service;

import com.act.security.model.dto.role.AuthorityDto;
import com.act.security.tracking.authentication.security.CacheEventMessage;
import com.act.security.tracking.dao.BackendServiceProxy;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Created by wonwoo on 2016. 4. 9..
 */
@Log4j2
public class ResourceMetaServiceImpl implements ResourceMetaService {

    public static List<AuthorityDto> authorities;

    @Autowired
    private BackendServiceProxy proxy;

    @Autowired
    private ApplicationContext applicationContext;

    @Override
    public void findAllResources() {
        CompletableFuture.supplyAsync(proxy::findAllAuthorities).thenAccept((List<AuthorityDto> authorities) -> {
            this.authorities = authorities;
            if (authorities != null) {
                authorities.stream().forEach(userRoleDto -> {
                    log.info("role name {}", userRoleDto.getName());
                    log.info("url {}", userRoleDto.getUrl());
                });
            }
            applicationContext.publishEvent(new CacheEventMessage(this, authorities));
        });
    }
}
