package com.djimgou.core.cooldto.service;

import com.djimgou.core.cooldto.exception.DtoMappingException;
import com.djimgou.core.cooldto.processors.DtoClassProcessor;
import com.djimgou.core.cooldto.processors.DtoProcessorfactory;
import com.djimgou.core.util.EntityRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

@Log4j2
@Service
public class DtoSerializerService {

    final private EntityRepository entityRepo;

    public DtoSerializerService(EntityRepository entityRepo) {
        this.entityRepo = entityRepo;
    }

    /**
     * Convertit automatiquementto en une entité pouvant être enregistrée
     *
     * @param dto    le DTO à convertir
     * @param entity l'entité à produire
     * @throws DtoMappingException
     */
    public void serialize(Object dto, Object entity) throws DtoMappingException {
        DtoClassProcessor processor = DtoProcessorfactory.newInstance(dto, entity, entityRepo);
        // Extraction
        processor.dtoToEntity();
    }
}
