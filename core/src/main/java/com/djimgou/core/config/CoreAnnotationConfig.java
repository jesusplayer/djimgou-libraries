package com.djimgou.core.config;

import com.djimgou.core.aop.DeleteByIdResolver;
import com.djimgou.core.aop.GetByIdResolver;
import com.djimgou.core.util.EntityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class CoreAnnotationConfig implements WebMvcConfigurer {
    @Autowired
    EntityRepository er;

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(new GetByIdResolver(er));
        argumentResolvers.add(new DeleteByIdResolver(er));
    }

}
