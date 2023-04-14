package com.djimgou.core.aop;

import com.djimgou.core.annotations.LogicalDelete;
import com.djimgou.core.exception.AppException;
import com.djimgou.core.util.AppUtils;
import lombok.extern.log4j.Log4j2;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;

import static com.djimgou.core.util.AppUtils.has;
import static com.djimgou.core.util.AppUtils.hasField;

@Log4j2
@Aspect()
@Component
public class LogicalDeleteInterceptor {
    @Autowired
    EntityManager em;
/*
    @Around("execution(* org.springframework.data.jpa.repository.JpaRepository+.*(..)))) && @target(com.djimgou.core.annotations.LogicalDelete)")
    public Object interceptList(ProceedingJoinPoint joinPoint) throws Throwable {
        Object entity = joinPoint.getArgs()[0];
        //if (deleteLogicaly(entity)) return null;
        Session session = em.unwrap(Session.class);
        session.enableFilter("logicalDeleteFilter");

        return joinPoint.proceed();
    }*/

  /*  @Around("execution(* org.springframework.data.jpa.repository.JpaRepository+.delete(..))))")
    public Object interceptDelete(ProceedingJoinPoint joinPoint) throws Throwable {
        Object entity = joinPoint.getArgs()[0];
        if (deleteLogicaly(entity)) return null;

        return joinPoint.proceed();
    }*/

    private boolean deleteLogicaly(Object entity) throws AppException {
        if (entity != null && entity.getClass().isAnnotationPresent(LogicalDelete.class)) {
            LogicalDelete an = entity.getClass().getAnnotation(LogicalDelete.class);
            String discrimField = an.value();

            if (!(has(discrimField) && hasField(entity, discrimField))) {
                throw new AppException(String.format("Impossible de trouver la propiété discriminateur de suppréssion %s dans la classe %s", discrimField, entity.getClass().getName()));
            } else {
                Class type = AppUtils.getDeepPropertyType(entity, discrimField);
                if (type.equals(Boolean.TYPE) || type.isAssignableFrom(Boolean.class)) {
                    AppUtils.setDeepProperty(entity, discrimField, Boolean.TRUE);
                    return true;
                } else {
                    throw new AppException(String.format("La propiété discriminateur de suppréssion %s de la classe %s doît être de type Boolean", discrimField, entity.getClass().getName()));
                }
            }

            /*Session session = entityManager.unwrap(Session.class);
            if (has(id)) {
                session.enableFilter("deleteFilter").setParameter("discriminator", id);
            }*/
        }
        return false;
    }


}

