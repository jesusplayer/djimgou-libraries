package com.djimgou.core.cooldto.processors;

import com.djimgou.core.cooldto.exception.DtoMappingException;
import com.djimgou.core.util.EntityRepository;

public class DtoProcessorfactory {
    public static DtoClassProcessor newInstance(Object dto, Object entity, EntityRepository entityRepo) throws DtoMappingException {
        DtoClassProcessor processor = new DtoClassProcessor(dto, entity);

        // DtoFieldEntityProcessor est récursif, donc on le soumet l'instance de processor
        // Une Autre methode serait de créer un DtoClassProcessorFactory
        DtoIdProcessor fIdProc = new DtoIdProcessor(entityRepo, processor);
        final DtoFieldProcessor fieldProc = new DtoFieldProcessor(processor);
        final DtoFkIdProcessor fkIdProcessor = new DtoFkIdProcessor(entityRepo, processor);
        final DtoEntityFieldProcessor entityProc = new DtoEntityFieldProcessor(entityRepo, processor);
        final DtoCollectionProcessor coleProc = new DtoCollectionProcessor(entityRepo, processor);
        final DtoCollectionIdProcessor coleIdProc = new DtoCollectionIdProcessor(entityRepo, processor);

        processor.add(fIdProc);
        processor.add(fieldProc);
        processor.add(fkIdProcessor);
        processor.add(entityProc);
        processor.add(coleIdProc);
        processor.add(coleProc);

        return processor;
    }
}
