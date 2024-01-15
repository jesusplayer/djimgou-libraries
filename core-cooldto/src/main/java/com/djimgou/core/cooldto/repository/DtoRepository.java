package com.djimgou.core.cooldto.repository;

public interface DtoRepository<ENTITY, DTO> {
    IDtoRepository<ENTITY, DTO> dto();
}
