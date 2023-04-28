package com.djimgou.core.testing.app.service;


import com.djimgou.core.service.AbstractDomainServiceV2;
import com.djimgou.core.testing.app.model.*;
import com.djimgou.core.testing.app.reposirtory.CategorieRepo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
