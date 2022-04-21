package com.djimgou.core.cooldto.repository;

import com.djimgou.core.cooldto.exception.DtoMappingException;
import org.springframework.transaction.annotation.Transactional;

public interface ICoolDtoRepository<ENTITY, DTO> {
    @Transactional
    ENTITY save(DTO dto) throws DtoMappingException;
}
