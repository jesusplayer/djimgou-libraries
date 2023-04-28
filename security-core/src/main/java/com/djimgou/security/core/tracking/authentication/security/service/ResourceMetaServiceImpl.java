package com.djimgou.security.core.tracking.authentication.security.service;

import com.djimgou.security.core.model.dto.role.AuthorityDto;
import com.djimgou.security.core.tracking.authentication.security.CacheEventMessage;
import com.djimgou.security.core.tracking.dao.BackendServiceProxy;
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
                    log.info("url {}", String.join(", ", userRoleDto.getUrls()));
                });
            }
            applicationContext.publishEvent(new CacheEventMessage(this, authorities));
        });
    }
}
