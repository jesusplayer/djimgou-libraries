package com.djimgou.security.core.enpoints;

import com.djimgou.core.annotations.Endpoint;
import com.djimgou.security.core.model.UrlsAuthorized;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.condition.PatternsRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.djimgou.core.util.AppUtils2.has;

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
        if (!(e.getName().contains("openApiResource#") || e.getName().contains("swaggerWelcome#")) && Stream.of(UrlsAuthorized.values()).map(UrlsAuthorized::toString).noneMatch(s -> Objects.equals(s,url))) {
            endpointsMap.put(url, e);
        }
    }

    public Collection<SecuredEndPoint> endPoints() {
        return endpointsMap != null ? endpointsMap.values() : null;
    }

    void loadUrls() {
        RequestMappingHandlerMapping requestMappingHandlerMapping = applicationContext
                .getBean("requestMappingHandlerMapping", RequestMappingHandlerMapping.class);
        final Map<RequestMappingInfo, HandlerMethod> map = requestMappingHandlerMapping.getHandlerMethods();
        if (!map.isEmpty()) {
            map.forEach((key, value) -> {
                PatternsRequestCondition patternsCondition = key.getPatternsCondition();
                String path;
                if (has(patternsCondition) && has(patternsCondition.getPatterns())) {
                    path = patternsCondition.getPatterns().iterator().next();
                } else {
                    path = key.getPathPatternsCondition().getPatterns().iterator().next().toString();
                }
                if (has(path)) {
                    final Iterator<RequestMethod> methodIterator = key.getMethodsCondition().getMethods().iterator();
                    HttpMethod httpMethod = null;
                    SecuredEndPoint endPoint = SecuredEndPoint.builder().url(path).build();

                    Endpoint enAn = value.getMethod().getAnnotation(Endpoint.class);
                    if (has(enAn) && has(enAn.value())) {
                        endPoint.setDescription(enAn.value());
                    }
                    if (has(enAn) && enAn.readOnlyMethod()) {
                        endPoint.setIsReadOnlyMethod(true);
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
                }

            });
        }
    }
}
