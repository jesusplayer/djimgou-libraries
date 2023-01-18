package com.djimgou.core.cooldto.repository;

import com.djimgou.core.cooldto.annotations.Dto;
import com.djimgou.core.cooldto.exception.DtoMappingException;
import com.djimgou.core.cooldto.exception.DtoTargetClassMissMatchException;
import com.djimgou.core.cooldto.service.DtoSerializer;
import com.djimgou.core.cooldto.service.DtoSerializerService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import static com.djimgou.core.util.AppUtils.has;

/**
 * Composant permetant
 */
@Component
class MyDtoRepository implements IDtoRepository {
    @PersistenceContext
    private final EntityManager em;

    private final DtoSerializer serializer;

    @Autowired
    public MyDtoRepository(DtoSerializer dtoSerial, EntityManager em) {
        this.serializer = dtoSerial;
        this.em = em;
    }

    @Transactional
    @Override
    public Object save(Object dto) throws DtoMappingException {
        if (has(dto)) {
            if (dto.getClass().isAnnotationPresent(Dto.class)) {
                Dto an = dto.getClass().getAnnotation(Dto.class);
                Class entityClass = has(an.value()) ? an.value()[0] : null;
                if (has(entityClass)) {
                    Object entity = BeanUtils.instantiateClass(entityClass);
                    serializer.serialize(dto, entity);
                    em.persist(entity);
                    //em.refresh(entity);
                    return entity;
                } else {
                    throw new DtoTargetClassMissMatchException("Aucune classe d'entité définie dans @Dto. l'enregistrement ne peut pas être effectuée");
                }
            }
        } else {
            throw new DtoMappingException("Impossible d'enregistrer un DTO null");
        }
        return null;
    }
}
