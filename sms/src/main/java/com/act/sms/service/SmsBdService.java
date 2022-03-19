package com.act.sms.service;

import com.act.core.infra.CustomPageable;
import com.act.core.service.AbstractDomainService;
import com.act.sms.exceptions.SmsNotFoundException;
import com.act.sms.model.QSms;
import com.act.sms.model.Sms;
import com.act.sms.model.dto.ville.SmsDto;
import com.act.sms.model.dto.ville.SmsFilterDto;
import com.act.sms.model.dto.ville.SmsFindDto;
import com.act.sms.repo.SmsRepo;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAQueryBase;
import com.querydsl.jpa.impl.JPAQuery;
import lombok.SneakyThrows;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.act.core.util.AppUtils.has;

/**
 * @author djimgou
 */
@Service
public class SmsBdService extends AbstractDomainService<Sms, SmsFindDto, SmsFilterDto> {
    private SmsRepo repo;


    @PersistenceContext
    EntityManager em;


    public SmsBdService(SmsRepo repo) {
        this.repo = repo;
    }

    @Override
    public SmsRepo getRepo() {
        return repo;
    }


    @Transactional(/*propagation = Propagation.NESTED*/)
    public Sms saveSms(UUID id, SmsDto SmsDto) throws SmsNotFoundException {
        Sms Sms = new Sms();
        if (has(id)) {
            Sms = repo.findById(id).orElseThrow(SmsNotFoundException::new);
        }
        Sms.fromDto(SmsDto);
        return save(Sms);
    }

    @Async
    @SneakyThrows
    public Sms createSms(SmsDto SmsDto) {
        return saveSms(null, SmsDto);
    }

    /**
     * Recherche avec pagination et filtre
     *
     * @param filter
     * @return
     * @throws Exception
     */
    public Page<Sms> findBy(SmsFilterDto filter) throws Exception {
        CustomPageable cpg = new CustomPageable(filter);
        if (cpg.getSort().isUnsorted()) {
            cpg.setSort(Sort.by(Sort.Order.desc("createdDate")));
        }
        Page<Sms> page;
        String name = filter.getFrom();
        String code = filter.getTo();
        String text = filter.getText();

        QSms qDevise = QSms.sms;

        JPAQuery query = new JPAQuery(em);
        JPAQueryBase exp2 = query.from(qDevise);
        List<BooleanExpression> expressionList = new ArrayList<>();
        if (has(name)) {
            expressionList.add(qDevise.to.containsIgnoreCase(name));
        }
        if (has(code)) {
            expressionList.add(qDevise.from.containsIgnoreCase(code));
        }
        if (has(text)) {
            expressionList.add(qDevise.text.containsIgnoreCase(code));
        }


        BooleanExpression exp = expressionList.stream().reduce(null, (old, newE) -> has(old) ? old.and(newE) : newE);

        exp2.where(exp).orderBy(qDevise.createdDate.desc());

        if (has(exp)) {
            page = repo.findAll(exp, cpg);
        } else {
            page = repo.findAll(cpg);
        }
        return page;
    }

}
