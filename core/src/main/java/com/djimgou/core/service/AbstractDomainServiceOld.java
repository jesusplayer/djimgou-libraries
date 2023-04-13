package com.djimgou.core.service;

import com.djimgou.core.infra.BaseFilterDto;
import com.djimgou.core.infra.BaseFindDto;
import com.djimgou.core.infra.CustomPageable;
import com.djimgou.core.infra.Filter;
import com.djimgou.core.util.AppUtils2;
import com.djimgou.core.util.model.IUuidBaseEntity;
import lombok.extern.log4j.Log4j2;
import org.hibernate.search.engine.search.query.SearchResult;
import org.hibernate.search.engine.search.query.dsl.SearchQueryOptionsStep;
import org.hibernate.search.mapper.orm.Search;
import org.hibernate.search.mapper.orm.search.loading.dsl.SearchLoadingOptionsStep;
import org.hibernate.search.mapper.orm.session.SearchSession;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.djimgou.core.util.AppUtils2.has;


/**
 * Service abstrait de gestion des opérations métiers de la base de données
 *
 * @param <T> parametre
 * @author djimgou
 */

@Log4j2
public abstract class AbstractDomainServiceOld<T extends IUuidBaseEntity, FIND_DTO extends BaseFindDto, F extends BaseFilterDto> extends AbstractBdService<T> {
    /**
     *      * T=0, DTO=1 , DETAIL_DTO=2 , FIND_DTO=3 , FILTER_DTO=4
     * @param pos
     * @return
     */
    public Class getFilterDtoClass(int pos) {
        ParameterizedType pt = (ParameterizedType) this.getClass().getGenericSuperclass();
        Class cc = null;
        if (pt.getActualTypeArguments().length > 0) {
            cc = (Class) pt.getActualTypeArguments()[pos];
        }
        return cc;
    }

    public AbstractDomainServiceOld(JpaRepository<T, UUID> repo) {
        super(repo);
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
    public Page<T> searchPageable(FIND_DTO findDto) {
        // long totalHitCount = result.total().hitCount();
        SearchResult<T> res = search(findDto);
        Page<T> p = AppUtils2.toPage(new CustomPageable(findDto), res.hits(), (int) res.total().hitCount());
        return p;
    }

    /**
     * https://www.baeldung.com/hibernate-search
     *
     * @param findDto
     * @return
     */
    public SearchResult<T> search(FIND_DTO findDto) {
        String[] fieldsSplit;
        /**
         * F=4
         * T=0
         * DTO=1
         * DETAIL_DTO = 2
         */
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

    public abstract Page<T> findBy(F baseFilter) throws Exception;

    public Page<T> searchBy(Filter<T> filter, Pageable pg) throws Exception {
        return Page.empty();
    }
}
