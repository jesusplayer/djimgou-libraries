package com.djimgou.core.cooldto.processors;

import com.djimgou.core.cooldto.exception.DtoMappingException;

public interface IDtoAnotationProcessor {
    void dtoToEntity() throws DtoMappingException;

    void init(Object dto, Object entity);

    Object getDto();

    Object getEntity();

    java.util.List<java.lang.reflect.Field> getFields();

    void setDto(Object dto);

    void setEntity(Object entity);

}
