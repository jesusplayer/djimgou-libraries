package com.djimgou.core.cooldto.repository;

import org.springframework.stereotype.Component;

@Component
public class CoolDtoRepositoryImpl implements CoolDtoRepository {

    private final ICoolDtoRepository iCoolDtoRepository;

    public CoolDtoRepositoryImpl(ICoolDtoRepository iCoolDtoRepository) {
        this.iCoolDtoRepository = iCoolDtoRepository;
    }

    @Override
    public ICoolDtoRepository dto() {
        return iCoolDtoRepository;
    }

}
