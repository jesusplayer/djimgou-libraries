package com.djimgou.tenantmanager.aop;

import com.djimgou.tenantmanager.exceptions.TenantNotFoundException;
import com.djimgou.tenantmanager.model.Tenant;
import com.djimgou.tenantmanager.model.dto.tenant.TenantSessionDto;
import com.djimgou.tenantmanager.repository.TenantRepo;
import com.djimgou.tenantmanager.service.TenantSessionService;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpSession;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Optional;

import static com.djimgou.core.util.AppUtils.has;
import static java.util.Optional.empty;
import static java.util.Optional.of;

@Aspect
@Component
@RequiredArgsConstructor
public class TenantInterceptor {
    @Autowired
    TenantSessionService sessionService;

    @Autowired
    private HttpSession httpSession;

    private static final Logger log = LoggerFactory.getLogger(TenantInterceptor.class);

    private final TenantRepo tenantRepository;

    @Around("@annotation(InboundRequest)")
    public Object logInboundRequest(ProceedingJoinPoint joinPoint) throws Throwable {
        log.debug("Intercepting inbound request...");

        log.debug("Extracting tenant id from method arguments!");
        String companyId = null;/*extractTenantId(joinPoint)
                .orElseThrow(RuntimeException::new);*/

        // if(!has(companyId)){
        TenantSessionDto ten = (TenantSessionDto) httpSession.getAttribute("tenant");
        if (has(ten)) {
            companyId = ten.getExternalId();
        }
        //}
        log.debug("Finding tenant by id!");


        try {
            Tenant tenant = sessionService.putTenant(companyId).orElseThrow(TenantNotFoundException::new);
            log.debug("Setting current tenant to Thread local variable!");
            TenantContext.setCurrentTenant(tenant);
            log.debug("Continuing with the execution!");
            return joinPoint.proceed();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            log.error(" Clearing tenant context!");
            TenantContext.clear();
        }
        return null;
    }

    public Optional<String> extractTenantId(ProceedingJoinPoint joinPoint) {
        Signature signature = joinPoint.getSignature();
        Method method = ((MethodSignature) signature).getMethod();
        Annotation[][] annotationMatrix = method.getParameterAnnotations();
        for (int i = 0; i < joinPoint.getArgs().length; i++) {
            Annotation[] annotations = annotationMatrix[i];
            for (Annotation annotation : annotations) {
                if (annotation.annotationType().equals(TenantId.class)) {
                    return of((String) joinPoint.getArgs()[i]);
                }
            }
        }
        return empty();
    }

}

