package com.djimgou.core.testing.app.service;


import com.djimgou.core.service.AbstractDomainServiceV2;
import com.djimgou.core.testing.app.model.*;
import com.djimgou.core.testing.app.reposirtory.CategorieRepo;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.hibernate.search.SearchQuery;
import org.hibernate.SessionFactory;
import org.hibernate.search.mapper.orm.Search;
import org.hibernate.search.mapper.orm.session.SearchSession;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author djimgou
 */
@Service
public class CategorieService extends AbstractDomainServiceV2<Categorie, CategorieFindDto, CategorieFilterDto, CategorieDto, CategorieDetailDto> {
    public CategorieService(CategorieRepo repo) {

        super(repo, QCategorie.categorie);
    }

    @Transactional
    public void clear() {
        getRepo().deleteAll();
    }

   /* void hibernateSearch() {
        SessionFactory sessionFactory = getEm().unwrap(SessionFactory.class);
        SearchSession searchSession = Search.session(getEm());
        EntityPathBase<Categorie> qQSommier = getQEntity();
        SearchQuery<Categorie> query = new SearchQuery<>(sessionFactory.getCurrentSession(), qQSommier);

        List<Categorie> list = query
                .where(QCategorie.categorie.annee.eq(2033))
                .fetch();
    }*/
}
