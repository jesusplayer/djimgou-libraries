package com.djimgou.security.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.Set;
import java.util.function.Supplier;

@Component
public class JdbcRoleChecker implements RoleChecker {
    private Supplier<Set<AntPathRequestMatcher>> supplier;


    @Override
    public boolean check(Authentication authentication, HttpServletRequest request) {
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

        // 当前用户的角色集合
        System.out.println("authorities = " + authorities);
        //todo 这里自行实现比对逻辑
        //   supplier.get().stream().filter(matcher -> matcher.matches(request));
        // true false 为是否放行
        return true;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        //Assert.notNull(supplier.get(), "function must not be null");
    }
}
