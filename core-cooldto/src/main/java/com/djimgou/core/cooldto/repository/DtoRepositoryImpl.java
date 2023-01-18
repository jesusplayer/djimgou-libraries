package com.djimgou.core.cooldto.repository;

import org.springframework.stereotype.Component;

/**
 * Implementation de l'interface DtoRepository. elle offrira des fonction de DTO utile aux différents
 * JpaRepository qui seront défini pas les utilisateurs
 */
@Component
public class DtoRepositoryImpl implements DtoRepository {

    private final IDtoRepository iDtoRepository;

    public DtoRepositoryImpl(IDtoRepository iDtoRepository) {
        this.iDtoRepository = iDtoRepository;
    }

    @Override
    public IDtoRepository dto() {
        return iDtoRepository;
    }

}
