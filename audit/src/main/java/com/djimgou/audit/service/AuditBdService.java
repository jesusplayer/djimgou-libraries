package com.djimgou.audit.service;

import com.djimgou.audit.model.Audit;
import com.djimgou.audit.model.AuditAction;
import com.djimgou.audit.model.QAudit;
import com.djimgou.audit.model.dto.AuditFilterDto;
import com.djimgou.audit.model.dto.AuditFindDto;
import com.djimgou.audit.repository.AuditRepo;
import com.djimgou.core.infra.CustomPageable;
import com.djimgou.core.service.AbstractDomainService;
import com.djimgou.core.util.AppUtils;
import com.djimgou.session.service.SessionService;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAQueryBase;
import com.querydsl.jpa.impl.JPAQuery;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.*;

import static com.djimgou.core.util.AppUtils.endOfTheDay;
import static com.djimgou.core.util.AppUtils.startOfTheDay;


/**
 * @author DJIMGOU NKENNE DANY MARC 08/2020
 */
@Log4j2
@Service
public class AuditBdService extends AbstractDomainService<Audit, AuditFindDto, AuditFilterDto> {
    @PersistenceContext
    EntityManager em;

    private SessionService sessionService;

    private AuditRepo repo;

    public AuditBdService(AuditRepo repo, SessionService sessionService) {
        super(repo);
        this.sessionService = sessionService;
        this.repo = repo;
    }

    @Transactional
    @Override
    public Page<Audit> findBy(AuditFilterDto baseFilter) throws Exception {
        CustomPageable cpg = new CustomPageable(baseFilter);
        cpg.setSort(Sort.by(Sort.Order.desc("date")));

        Date dateDebut = baseFilter.getDateDebut();
        Date dateFin = baseFilter.getDateFin();
        AuditAction action = baseFilter.getAction();
        UUID userId = baseFilter.getUtilisateurId();
        String nomEntite = baseFilter.getNomEntite();
        String username = baseFilter.getUsername();
        QAudit audit = QAudit.audit;

        //HibernateQuery<?>  query = new HibernateQuery<>(sessionFactory.getCurrentSession());
        JPAQuery query = new JPAQuery(em);
        JPAQueryBase exp2 = query.from(audit);
                /* .where(audit.nomEntite.containsIgnoreCase(nomEntite))
         .orderBy(audit.date.asc());*/


        List<BooleanExpression> expressionList = new ArrayList<>();
        if (AppUtils.has(dateDebut)) {
            expressionList.add(audit.date.goe(startOfTheDay(dateDebut)));
        }
        if (AppUtils.has(dateDebut)) {
            expressionList.add(audit.date.loe(endOfTheDay(dateFin)));
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
        } catch (JsonProcessingException | NullPointerException e) {
            log.error("Erreur d'enregistrement de l'audit :" + e.getMessage(), audit);
            // log.error(entity);
            //log.error("AUDIT="+audit);
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
