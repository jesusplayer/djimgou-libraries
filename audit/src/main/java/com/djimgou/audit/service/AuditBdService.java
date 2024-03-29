package com.djimgou.audit.service;

import com.djimgou.audit.annotations.MyAnnotationIntrospector;
import com.djimgou.audit.model.Audit;
import com.djimgou.audit.model.AuditAction;
import com.djimgou.audit.model.QAudit;
import com.djimgou.audit.model.dto.AuditDetaildto;
import com.djimgou.audit.model.dto.AuditDto;
import com.djimgou.audit.model.dto.AuditFilterDto;
import com.djimgou.audit.model.dto.AuditFindDto;
import com.djimgou.audit.repository.AuditRepo;
import com.djimgou.audit.views.AuditListView;
import com.djimgou.core.exception.AppException;
import com.djimgou.core.export.DataExportParser;
import com.djimgou.core.infra.CustomPageable;
import com.djimgou.core.service.AbstractDomainServiceV2;
import com.djimgou.core.util.AppUtils2;
import com.djimgou.session.context.SessionContext;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.CompletableFuture;

import static com.djimgou.core.util.AppUtils2.has;


/**
 * @author DJIMGOU NKENNE DANY MARC 08/2020
 */
@Log4j2
@Service
public class AuditBdService extends AbstractDomainServiceV2<Audit, AuditFindDto, AuditFilterDto, AuditDto, AuditDetaildto> {

    //private SessionService sessionService;

    @Getter
    private AuditRepo repo;
    private DataExportParser dataExportParser;


    public AuditBdService(AuditRepo repo/*, SessionService sessionService*/, DataExportParser dataExportParser) {
        super(repo, QAudit.audit);
//        this.sessionService = sessionService;
        this.repo = repo;
        this.dataExportParser = dataExportParser;
    }

    public List<List<?>> exporter() {
        List<List<?>> er = dataExportParser.parse(repo.exporter());
        return er;
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
        String username = null;
        UUID userId = null;

        if (has(SessionContext.getCurrentUsername())) {
            username = SessionContext.getCurrentUsername();
            //userId = SessionContext.getCurrentUsername();
        }
        return add(entity, action, username, userId);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public <T> Audit add(T entity, AuditAction action, String username, UUID userId) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        objectMapper.setAnnotationIntrospector(new MyAnnotationIntrospector());
        Audit audit = null;
        try {
            String str = objectMapper.writeValueAsString(entity);

            audit = new Audit(Calendar.getInstance().getTime(), str,
                    AppUtils2.localizeClassName(entity.getClass().getSimpleName()), action,
                    userId,
                    username
            );
            return super.save(audit);
        } catch (JsonProcessingException | NullPointerException | AppException e) {
            log.error("Erreur d'enregistrement de l'audit :" + e.getMessage(), audit);
            // log.error(entity);
            //log.error("AUDIT="+audit);
        }
        return null;
    }

    @Async
    public <T> CompletableFuture<Audit> addAsync(T entity, AuditAction action) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return add(entity, action);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public Page<Audit> searchPageable(AuditFindDto findDto) {
        CustomPageable cpg = new CustomPageable(findDto);
        if (cpg.getSort().isUnsorted()) {
            cpg.setSort(Sort.by(Sort.Order.desc("date")));
        }
        Page<Audit> page;
        String txt = findDto.getSearchText();

        QAudit qUser = QAudit.audit;
        List<BooleanExpression> expList = new ArrayList<>();
        expList.add(qUser.nomEntite.containsIgnoreCase(txt));
        expList.add(qUser.data.containsIgnoreCase(txt));
        expList.add(qUser.username.containsIgnoreCase(txt));
        expList.add(qUser.action.stringValue().containsIgnoreCase(txt));
        expList.add(qUser.date.stringValue().containsIgnoreCase(txt));

        BooleanExpression exp = expList.stream().reduce(null, (old, newE) -> has(old) ? old.or(newE) : newE);

        if (has(exp)) {
            page = repo.findAll(exp, cpg);
        } else {
            page = repo.findAll(cpg);
        }
        return page;
    }

    public Page<AuditListView> listView(Pageable pg) {
        return repo.listView(pg);
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
