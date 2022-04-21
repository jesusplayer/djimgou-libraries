package com.djimgou.tenantmanager.aop;

import com.djimgou.session.service.SessionService;
import com.djimgou.tenantmanager.exceptions.TenantSessionNotFoundException;
import com.djimgou.tenantmanager.model.Tenant;
import com.djimgou.session.enums.SessionKeys;
import com.djimgou.tenantmanager.repository.TenantRepo;
import com.djimgou.tenantmanager.service.TenantSessionService;
import lombok.extern.log4j.Log4j2;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpSession;
import java.util.Optional;

import static com.djimgou.core.util.AppUtils.has;

@Log4j2
@Aspect()
@Component
public class RepositoryInterceptor {
    @Autowired
    TenantSessionService tenantSessionService;

    @Autowired
    SessionService sessionService;

    @Autowired
    private EntityManager entityManager;


    @Value("${spring.profiles.active:prod}")
    private String activeProfile;

    @Around("execution(* com.djimgou.tenantmanager.aop.CustomRepository+.*(..))")
    public Object inWebLayer(ProceedingJoinPoint joinPoint) throws Throwable {
        boolean checkTenant = false;
        if (activeProfile.equals("prod")) {
            if (entityManager.isOpen() && notOfAClassTypeTenantRepository(joinPoint.getSignature().getDeclaringType())) {
                checkTenant = true;
            }
        } else {
            if (/*entityManager.isJoinedToTransaction() && */notOfAClassTypeTenantRepository(joinPoint.getSignature().getDeclaringType())) {
                checkTenant = true;
            }
        }
        if (checkTenant) {
            HttpSession httpSession = sessionService.getSession();
            //HttpSession httpSession = sessionService.getSession();
            String tenantId = (String) httpSession.getAttribute(SessionKeys.TENANT_ID);
            Optional<Tenant> tenant = tenantSessionService.putTenant(tenantId);
            TenantContext.setCurrentTenant(tenant.orElseThrow(TenantSessionNotFoundException::new));
            Session session = entityManager.unwrap(Session.class);
            String id = TenantContext.getCurrentTenantId();
            if (has(id)) {
                session.enableFilter("tenantFilter").setParameter("tenantId", id);
            }
        }

        return joinPoint.proceed();
    }

    private boolean notOfAClassTypeTenantRepository(Class declaringType) {
        return !declaringType.equals(TenantRepo.class);
    }

}

