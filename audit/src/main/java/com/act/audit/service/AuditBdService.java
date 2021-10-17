package com.act.audit.service;

import com.act.audit.exceptions.AuditNotFoundException;
import com.act.audit.model.Audit;
import com.act.audit.model.AuditAction;
import com.act.audit.model.QAudit;
import com.act.audit.model.dto.AuditFilterDto;
import com.act.audit.model.dto.AuditFindDto;
import com.act.audit.repository.AuditRepo;
import com.act.core.exception.NotFoundException;
import com.act.core.infra.Filter;
import com.act.core.infra.CustomPageable;
import com.act.core.service.AbstractBdService;
import com.act.core.service.AbstractDomainService;
import com.act.core.util.AppUtils;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAQueryBase;
import com.querydsl.jpa.impl.JPAQuery;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.*;

import static com.act.core.util.AppUtils.has;


/**
 * @author DJIMGOU NKENNE DANY MARC 08/2020
 */
@Log4j2
@Service
public class AuditBdService extends AbstractDomainService<Audit, AuditFindDto, AuditFilterDto> {
    @PersistenceContext
    EntityManager em;

    @Autowired
    AppUtils appUtils;

    @Autowired
    SessionService sessionService;

    @Autowired
    AuditRepo repo;

    public AuditBdService() {
        super();
    }

    @Override
    public AuditRepo getRepo() {
        return repo;
    }

    @Override
    public Page<Audit> findBy(AuditFilterDto dto) throws Exception {
        CustomPageable cpg = new CustomPageable(dto);
        cpg.setSort(Sort.by(Sort.Order.desc("date")));

        Date dateDebut = dto.getDateDebut();
        Date dateFin = dto.getDateFin();
        AuditAction action = dto.getAction();
        UUID userId = dto.getUtilisateurId();
        String nomEntite = dto.getNomEntite();
        String username = dto.getUsername();
        QAudit audit = QAudit.audit;

        //HibernateQuery<?>  query = new HibernateQuery<>(sessionFactory.getCurrentSession());
        JPAQuery query = new JPAQuery(em);
        JPAQueryBase exp2 = query.from(audit);
                /* .where(audit.nomEntite.containsIgnoreCase(nomEntite))
         .orderBy(audit.date.asc());*/


        List<BooleanExpression> expressionList = new ArrayList<>();
        if (AppUtils.has(dateDebut)) {
            expressionList.add(audit.date.goe(appUtils.startOfDay(dateDebut)));
        }
        if (AppUtils.has(dateDebut)) {
            expressionList.add(audit.date.loe(appUtils.endOfDay(dateFin)));
        }
        if (AppUtils.has(action)) {
            expressionList.add(audit.action.eq(action));
        }
        if (AppUtils.has(userId)) {
            expressionList.add(audit.utilisateurId.eq(userId));
        }

        if (AppUtils.has(nomEntite)) {
            expressionList.add(audit.nomEntite.eq(nomEntite));
        }

        if (AppUtils.has(username)) {
            expressionList.add(audit.username.containsIgnoreCase(username));
        }

        BooleanExpression exp = expressionList.stream().reduce(null, (old, newE) -> AppUtils.has(old) ? old.and(newE) : newE);

        exp2.where(exp).orderBy(audit.date.asc());

        Page<Audit> page = null;
        if (AppUtils.has(exp)) {
            page = repo.findAll(exp, cpg);

        } else {
            page = repo.findAll(cpg);
        }
        return page;
    }

    /**
     * Ajoute une entité dans l'audit
     *
     * @param entity entités
     * @param action action
     * @param <T>    parametre
     * @return nouvel audit
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public <T> Audit add(T entity, AuditAction action) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        Audit audit = null;
        try {
            String str = objectMapper.writeValueAsString(entity);
            UUID userId = null;
            String username = null;
            if (sessionService.hasUser()) {
                username = sessionService.username();
                userId = sessionService.currentUserId().orElse(null);
            }
            audit = new Audit(Calendar.getInstance().getTime(), str,
                    AppUtils.localizeClassName(entity.getClass().getSimpleName()), action,
                    userId,
                    username
            );
            return super.save(audit);
        } catch (JsonProcessingException e) {
            log.error("Erreur d'enregistrement de l'audit :" + e.getMessage(), audit);
        }
        return null;
    }
    /*public Page<Audit> findByUserAndDate(Date dateDebut, Date dateFin, UUID utilisateurId) {
        Date startDate = appUtils.startOfDay(dateDebut);
        Date endDate = appUtils.endOfDay(dateFin);
        Page<Audit> pag = repo.findByUtilisateurIdDate(utilisateurId, startDate, endDate, Pageable.unpaged());
        return pag;
    }

    public Page<Audit> findByActionAndDate(AuditAction action, Date dateDebut, Date dateFin, UUID utilisateurId) {
        Date startDate = appUtils.startOfDay(dateDebut);
        Date endDate = appUtils.endOfDay(dateFin);
        Page<Audit> pag = repo.findByActionDate(action, startDate, endDate, Pageable.unpaged());
        return pag;
    }

    public Page<Audit> findByDate(Date date, Pageable pg) {
        Date satartDate = appUtils.startOfDay(date);
        Date endDate = appUtils.endOfDay(date);
        Page<Audit> page = repo.findByDate(satartDate, endDate, pg);
        return page;
    }

    public Page<Audit> findByDate(Date dateDebut, Date dateFin, Pageable pg) {
        Date satartDate = appUtils.startOfDay(dateDebut);
        Date endDate = appUtils.endOfDay(dateFin);
        Page<Audit> page = repo.findByDate(satartDate, endDate, pg);
        return page;
    }*/


}
