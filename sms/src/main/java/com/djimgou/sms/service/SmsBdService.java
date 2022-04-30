package com.djimgou.sms.service;

import com.djimgou.core.infra.CustomPageable;
import com.djimgou.core.service.AbstractDomainService;
import com.djimgou.sms.exceptions.SmsNotFoundException;
import com.djimgou.sms.model.QSms;
import com.djimgou.sms.model.Sms;
import com.djimgou.sms.model.dto.sms.SmsDto;
import com.djimgou.sms.model.dto.sms.SmsFilterDto;
import com.djimgou.sms.model.dto.sms.SmsFindDto;
import com.djimgou.sms.repo.SmsRepo;
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

import static com.djimgou.core.util.AppUtils.has;

/**
 * @author DJIMGOU NKENNE DANY MARC
 * 04/2022
 */
@Service
public class SmsBdService extends AbstractDomainService<Sms, SmsFindDto, SmsFilterDto> {
    private SmsRepo repo;


    @PersistenceContext
    EntityManager em;


    public SmsBdService(SmsRepo repo) {
        super(repo);
        this.repo = repo;
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
     * @param baseFilter
     * @return
     * @throws Exception
     */
    @Transactional
    public Page<Sms> findBy(SmsFilterDto baseFilter) throws Exception {
        CustomPageable cpg = new CustomPageable(baseFilter);
        if (cpg.getSort().isUnsorted()) {
            cpg.setSort(Sort.by(Sort.Order.desc("createdDate")));
        }
        Page<Sms> page;
        String name = baseFilter.getFrom();
        String code = baseFilter.getTo();
        String text = baseFilter.getText();

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
