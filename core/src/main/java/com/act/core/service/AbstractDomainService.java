package com.act.core.service;

import com.act.core.infra.BaseFilterDto;
import com.act.core.infra.BaseFindDto;
import com.act.core.infra.CustomPageable;
import com.act.core.infra.Filter;
import com.act.core.model.AbstractBaseEntity;
import com.act.core.util.AppUtils;
import lombok.extern.log4j.Log4j2;
import org.hibernate.search.engine.search.query.SearchResult;
import org.hibernate.search.engine.search.query.dsl.SearchQueryOptionsStep;
import org.hibernate.search.mapper.orm.Search;
import org.hibernate.search.mapper.orm.search.loading.dsl.SearchLoadingOptionsStep;
import org.hibernate.search.mapper.orm.session.SearchSession;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static com.act.core.util.AppUtils.has;
import static com.act.core.util.AppUtils.toPage;


/**
 * Service abstrait de gestion des opérations métiers de la base de données
 *
 * @param <T> parametre
 * @author djimgou
 */

@Log4j2
public abstract class AbstractDomainService<T extends AbstractBaseEntity, S extends BaseFindDto, F extends BaseFilterDto> extends AbstractBdService<T> {
    public Class getFilterDtoClass(int pos) {
        ParameterizedType pt = (ParameterizedType) this.getClass().getGenericSuperclass();
        Class cc = null;
        if (pt.getActualTypeArguments().length > 0) {
            cc = (Class) pt.getActualTypeArguments()[pos];
        }
        return cc;
    }

    /**
     * Recherche à partir des champs spécifiés par le user ou à partir du
     * SearchResult<Book> result = searchSession.search( Book.class )
     * .where( f -> f.matchAll() )
     * .fetch( 40, 20 );
     * Set the offset to 40 and the limit to 20.
     * offset = zero-based-page-number * page-size
     *
     * @param findDto
     * @return
     */
    public Page<T> searchPageable(S findDto) {
        // long totalHitCount = result.total().hitCount();
        SearchResult<T> res = search(findDto);
        Page<T> p = AppUtils.toPage(new CustomPageable(findDto), res.hits(), (int) res.total().hitCount());
        return p;
    }

    /**
     * https://www.baeldung.com/hibernate-search
     * @param findDto
     * @return
     */
    public SearchResult<T> search(S findDto) {
        String[] fieldsSplit;
        final Class filterDtoClass = getFilterDtoClass(2);
        final Class<T> entityClass = getFilterDtoClass(0);
        final Class findDtoClass = getFilterDtoClass(1);

        if (has(findDto.getSearchKeys())) {
            fieldsSplit = findDto.getSearchKeys();
        } else {
            Set<Field> f = new HashSet<>();
            Collections.addAll(f, filterDtoClass.getDeclaredFields());
            Collections.addAll(f, findDtoClass.getDeclaredFields());
            String fields = f.stream().map(Field::getName)
                    .collect(Collectors.joining(","));
            fieldsSplit = fields.split(",");
        }
        SearchSession searchSession = Search.session(em);
        String[] finalFff = fieldsSplit;
        final SearchQueryOptionsStep<?, T, SearchLoadingOptionsStep, ?, ?>
                query = searchSession.search(entityClass)
                .where(f -> f.match().fields(finalFff)
                        .matching(findDto.getSearchText()));
        SearchResult<T> result;
        if (findDto.getSize() == 0) {
            result = query.fetchAll();
        } else {
            int offset = (findDto.getPage()) * findDto.getSize();
            result = query.fetch(offset, findDto.getSize());
        }
        return result;
    }

    public abstract Page<T> findBy(F factureFilterDto) throws Exception;
    
    public Page<T> searchBy(Filter<T> filter, Pageable pg) throws Exception {
        return Page.empty();
    }
}
