package com.djimgou.security.enpoints;

import com.djimgou.core.annotations.Endpoint;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.*;
import java.util.stream.Collectors;

import static com.djimgou.core.util.AppUtils.has;

@Getter
@Setter
@Service
public class EndPointsRegistry {

    private ApplicationContext applicationContext;

    Map<String, SecuredEndPoint> endpointsMap;

    public EndPointsRegistry(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
        loadUrls();
    }

    public void addUrl(String url, SecuredEndPoint e) {
        if (endpointsMap == null) {
            setEndpointsMap(new HashMap<>());
        }
        endpointsMap.put(url, e);
    }

    public Collection<SecuredEndPoint> endPoints() {
        return endpointsMap.values();
    }

    void loadUrls() {
        RequestMappingHandlerMapping requestMappingHandlerMapping = applicationContext
                .getBean("requestMappingHandlerMapping", RequestMappingHandlerMapping.class);
        Map<RequestMappingInfo, HandlerMethod> map = requestMappingHandlerMapping
                .getHandlerMethods();
        map.forEach((key, value) -> {
            String path = key.getPatternsCondition().getPatterns().iterator().next();
            final Iterator<RequestMethod> methodIterator = key.getMethodsCondition().getMethods().iterator();
            HttpMethod httpMethod = null;
            SecuredEndPoint endPoint = SecuredEndPoint.builder().url(path).build();

            Endpoint enAn = value.getMethod().getAnnotation(Endpoint.class);
            if (has(enAn) && has(enAn.value())) {
                endPoint.setDescription(enAn.value());
            }
            if (has(enAn) && has(enAn.readOnlyMethod())) {
                endPoint.setIsReadOnlyMethod(enAn.readOnlyMethod());
            }
            if (methodIterator.hasNext()) {
                RequestMethod m = methodIterator.next();
                httpMethod = HttpMethod.resolve(m.name());
                endPoint.setHttpMethod(httpMethod);
                endPoint.setIsReadOnlyMethod(endPoint.isGet() || endPoint.getIsReadOnlyMethod());
            }

            Map<String, Param> params = Arrays.stream(value.getMethodParameters()).filter(methodParameter ->
                            methodParameter.hasParameterAnnotation(RequestParam.class)
                    )
                    .collect(Collectors.toMap(methodParameter ->
                                    methodParameter.getParameter().getName(),
                            o -> {
                                RequestParam an = o.getParameterAnnotation(RequestParam.class);
                                return Param.builder()
                                        .required(an.required())
                                        .name(o.getParameter().getName())
                                        .build();
                            }));
            if (has(params)) {
                endPoint.setParameters(params);
            }
            if (has(httpMethod)) {
                String par = Arrays.stream(value.getMethod().getParameterTypes()).map(Class::getSimpleName)
                        .collect(Collectors.joining(","));
                endPoint.setFullname(value.toString());
                endPoint.setName(value.getBean() + "#" + value.getMethod().getName() + "(" + par + ")");
                addUrl(path, endPoint);
            }
        });
    }
}
