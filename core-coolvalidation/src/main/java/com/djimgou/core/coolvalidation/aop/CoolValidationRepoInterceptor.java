package com.djimgou.core.coolvalidation.aop;

import com.djimgou.core.coolvalidation.processors.ValidationParser;
import lombok.extern.log4j.Log4j2;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Log4j2
@Aspect()
@Component
public class CoolValidationRepoInterceptor {
    final
    ValidationParser validationParser;

    public CoolValidationRepoInterceptor(ValidationParser validationParser) {
        this.validationParser = validationParser;
    }

    @Before("execution(* org.springframework.data.jpa.repository.JpaRepository+.save(..))))")
    public void beforeSave(JoinPoint joinPoint) throws Throwable {
        validationParser.validate(joinPoint.getArgs()[0]);
    }

   /* @Before("execution(* org.springframework.data.jpa.repository.JpaRepository+.delete(..))))")
    public void beforeDelete(JoinPoint joinPoint) throws Throwable {
        validationParser.checkBeforeDelete(joinPoint.getArgs()[0]);
    }*/
}

