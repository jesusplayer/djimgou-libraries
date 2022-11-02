package com.djimgou.core.infra;

import com.djimgou.core.util.AppUtils;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.djimgou.core.util.AppUtils.has;

// https://dzone.com/articles/pagination-in-springboot-applications

/**
 * @author djimgou
 */
@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
public class CustomBasePageable<T> implements Pageable {
    Pageable pg;

    Sort sort;

    Filter<T> filter;
    boolean notPaged = false;
    public CustomBasePageable(Pageable pg) {
        this.pg = pg;
        this.notPaged = pg.isUnpaged();
    }

    // TODO A ameliorer le trie de la page
    public CustomBasePageable(BasePageableDto pg) {
        Sort sort1 = buildSort(pg.getSort());
        if (has(sort1)) {
            this.pg = PageRequest.of(pg.getPage(), pg.getSize(), sort1);
        } else {
            this.pg = PageRequest.of(pg.getPage(), pg.getSize());
        }
        this.notPaged = pg.isUnpaged();
        /*if (has(pg.getSearchText())) {
            if (!has(filter)) {
                filter = new Filter<>(pg.getSearchText(), null);
            } else {
                filter.setSearchText(pg.getSearchText());
            }
        }*/
    }

    /**
     * Converti un sort venant de l'url à sa correspondance permettant d'éffectuer des requetes
     * NB les sorts se font de cette manière:
     * sort:[prop1,asc,prop2,desc,propn,asc,...]
     *
     * @param sortStr
     * @return
     */
    private Sort buildSort(String[] sortStr) {
        List<Sort.Order> b = new ArrayList<>();
        Sort sort = null;
        if (has(sortStr)) {
            if (sortStr.length == 1) {
                b.add(Sort.Order.asc(sortStr[0]));
            } else {
                for (int i = 1; i < sortStr.length; i++) {
                    String s = sortStr[i];
                    String s1 = sortStr[i - 1];
                    Sort.Order o = null;
                    if (has(s)) {
                        if ("asc".equalsIgnoreCase(s)) {
                            o = Sort.Order.asc(s1);
                        } else if ("desc".equalsIgnoreCase(s)) {
                            o = Sort.Order.desc(s1);
                        }

                    }
                    if (has(o)) {
                        b.add(o);
                    }
                }
            }

            sort = Sort.by(b.stream().filter(AppUtils::
                    has).collect(Collectors.toList()));
        }
        return sort;
    }

    public CustomBasePageable() {
    }

    @Override
    public boolean isPaged() {
        return !notPaged;
    }

    @Override
    public boolean isUnpaged() {
        return pg.isUnpaged();
    }

    @Override
    public int getPageNumber() {
        return pg.getPageNumber();
    }

    @Override
    public int getPageSize() {
        return pg.getPageSize();
    }

    @Override
    public long getOffset() {
        return pg.getOffset();
    }

    @Override
    public Sort getSort() {
        return has(sort) ? sort : pg.getSort();
    }

    @Override
    public Sort getSortOr(Sort sort) {
        return pg.getSortOr(sort);
    }

    @Override
    public Pageable next() {
        return pg.next();
    }

    @Override
    public Pageable previousOrFirst() {
        return pg.previousOrFirst();
    }

    @Override
    public Pageable first() {
        return pg.first();
    }

    @Override
    public Pageable withPage(int i) {
        return pg.withPage(i);
    }

    @Override
    public boolean hasPrevious() {
        return pg.hasPrevious();
    }

    @Override
    public Optional<Pageable> toOptional() {
        return pg.toOptional();
    }
}
