package com.djimgou.core.cooldto.repository;

public interface CoolDtoRepository<ENTITY, DTO> {
    ICoolDtoRepository<ENTITY, DTO> dto();
}
