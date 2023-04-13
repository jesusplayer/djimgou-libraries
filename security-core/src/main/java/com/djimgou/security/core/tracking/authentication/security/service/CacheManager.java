package com.djimgou.security.core.tracking.authentication.security.service;

import com.djimgou.security.core.model.dto.role.AuthorityDto;
import com.djimgou.security.core.tracking.authentication.security.CacheEventMessage;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.ApplicationListener;

import java.util.List;
import java.util.Map;

import static com.djimgou.core.util.AppUtils2.has;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

@Log4j2
public class CacheManager implements ApplicationListener<CacheEventMessage> {

    private Map<String, List<AuthorityDto>> authorities;

    public Map<String, List<AuthorityDto>> getAuthorities() {
        return authorities;
    }

    public List<AuthorityDto> getAuthoritie(String key) {
        if (has(authorities) && authorities.containsKey(key)) {
            return authorities.get(key);
        }
        return null;
    }

    @Override
    public void onApplicationEvent(CacheEventMessage event) {
        authorities = event.getAuthoritiesDto()
                .stream().collect(groupingBy(AuthorityDto::getUrl, toList()));
        log.info("Cache Manager - OnApplicationEvent {}", authorities);
    }
}
