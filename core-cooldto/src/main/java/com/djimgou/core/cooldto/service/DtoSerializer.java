package com.djimgou.core.cooldto.service;

import com.djimgou.core.cooldto.exception.DtoMappingException;
import com.djimgou.core.cooldto.processors.DtoClassProcessor;
import com.djimgou.core.cooldto.processors.DtoProcessorfactory;
import com.djimgou.core.util.EntityRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

public interface DtoSerializer {
    /**
     * Convertit automatiquement un dto en une entité pouvant être enregistrée
     *
     * @param dto    le DTO à convertir
     * @param entity l'entité à produire
     * @throws DtoMappingException
     */
    void serialize(Object dto, Object entity) throws DtoMappingException;
}
