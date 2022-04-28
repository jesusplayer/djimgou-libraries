package com.djimgou.core.aop;

import com.djimgou.core.annotations.GetById;
import com.djimgou.core.exception.NotFoundException;
import com.djimgou.core.util.EntityRepository;
import org.apache.commons.beanutils.ConvertUtils;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.HandlerMapping;

import javax.persistence.metamodel.Type;
import java.util.Map;
import java.util.UUID;

import static com.djimgou.core.util.AppUtils.has;

public class GetByIdResolver implements HandlerMethodArgumentResolver {
    EntityRepository er;

    public GetByIdResolver(EntityRepository er) {
        this.er = er;
    }

    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterAnnotation(GetById.class) != null;
    }

    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest,
                                  WebDataBinderFactory binderFactory) throws Exception {
        GetById an = parameter.getParameterAnnotation(GetById.class);
        final Map pathVar = (Map) ((ServletWebRequest) webRequest).getRequest().getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
        final Class<?> type = parameter.getParameter().getType();
        String value = has(an.value()) ? an.value()[0] : er.getIdKey(type);
        Object id = pathVar.get(value);
        if (!has(id)) {
            throw new NotFoundException("La valeur de L'identifiant " +value+
                    " ne peux pas être nul");
        }
        Type idType = er.getIdType(type);
        Object newId;
        if (UUID.class.equals(idType.getJavaType())) {
            newId = UUID.fromString(id.toString());
        } else {
            newId = ConvertUtils.convert(id, idType.getJavaType());
        }

        return er.findById(type, newId).orElseThrow(() -> new NotFoundException(type));
    }
}
