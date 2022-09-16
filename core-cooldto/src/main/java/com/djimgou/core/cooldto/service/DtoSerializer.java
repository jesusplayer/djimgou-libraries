package com.djimgou.core.cooldto.service;

import com.djimgou.core.cooldto.exception.DtoMappingException;

public interface DtoSerializer {
    void serialize(Object dto, Object targetEntity) throws DtoMappingException;
}
