package com.djimgou.core.aop;

import com.djimgou.core.annotations.DeleteById;
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

public class DeleteByIdResolver implements HandlerMethodArgumentResolver {
    EntityRepository er;

    public DeleteByIdResolver(EntityRepository er) {
        this.er = er;
    }

    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterAnnotation(DeleteById.class) != null;
    }

    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest,
                                  WebDataBinderFactory binderFactory) throws Exception {
        DeleteById attr = parameter.getParameterAnnotation(DeleteById.class);
        final Map pathVar = (Map) ((ServletWebRequest) webRequest).getRequest().getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
        final Class<?> type = parameter.getParameter().getType();
        String value = has(attr.value()) ? attr.value()[0] : er.getIdKey(type);
        Object id = pathVar.get(value);
        if (!has(id)) {
            throw new NotFoundException("L'identifiant ne peux pas être nul");
        }
        Type idType = er.getIdType(type);
        Object newId;
        if (UUID.class.equals(idType.getJavaType())) {
            newId = UUID.fromString(id.toString());
        } else {
            newId = ConvertUtils.convert(id, idType.getJavaType());
        }
        try {
            er.deleteById(type, newId);
        } catch (Exception e) {
            throw new NotFoundException(type, id);
        }
        return null;
    }
}
