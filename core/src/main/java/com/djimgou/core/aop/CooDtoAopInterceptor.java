package com.djimgou.core.aop;


import com.djimgou.core.annotations.GetById;
import com.djimgou.core.exception.NotFoundException;
import lombok.extern.log4j.Log4j2;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerMapping;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.http.HttpServletRequest;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Optional;

import static java.util.Optional.empty;

@Log4j2
@Aspect()
@Component
public class CooDtoAopInterceptor {
    @PersistenceContext
    EntityManager em;

    @Autowired
    HttpServletRequest httpServletRequest;

    @Around("@annotation(com.djimgou.core.annotations.GetById)")
    public Object getByIdRequest(ProceedingJoinPoint joinPoint) throws Throwable {
        try {
            Optional<String> idOpt = extractTenantId(joinPoint);
            String id = idOpt.orElseThrow(NotFoundException::new);
            extractTenantId(joinPoint);
            return joinPoint.proceed();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            log.error(" Clearing tenant context!");
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
                if (annotation.annotationType().equals(GetById.class)) {
                    Object providedId = joinPoint.getArgs()[i];
                    GetById an = (GetById) annotation;
                    System.out.println(providedId);
                    Map attr = (Map) httpServletRequest.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);

                }
            }
        }
        return empty();
    }
    /*@Around("execution(* com.djimgou.tenantmanager.aop.CustomRepository+.*(..))")
    public Object inWebLayer(ProceedingJoinPoint joinPoint) throws Throwable {
        boolean checkTenant = false;
        boolean er = notOfAClassTypeTenantRepository(joinPoint.getSignature().getDeclaringType());


        return joinPoint.proceed();
    }

    private boolean notOfAClassTypeTenantRepository(Class declaringType) {
        return !declaringType.equals(TenantRepo.class);
    }*/

}

