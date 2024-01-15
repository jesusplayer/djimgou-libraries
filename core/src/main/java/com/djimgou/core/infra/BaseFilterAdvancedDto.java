package com.djimgou.core.infra;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.stream.Collectors;

import static com.djimgou.core.util.AppUtils.has;

/**
 * Mettre
 */
@Getter
@Setter
public abstract class BaseFilterAdvancedDto extends BaseFilterDto {
    public static final String[] IGNORE = new String[]{
            "searchText", "page", "size", "sort", "otherFilters"
    };
    List<QueryOperation> otherFilters;
    public boolean hasOtherFilters() {
        return has(otherFilters);
    }

    public void ignoreNullOtherFilter(){
        if(hasOtherFilters()){
            otherFilters = otherFilters.stream().filter(queryOperation -> queryOperation.getValue1()!=null/* && queryOperation.getValue2()!=null*/).collect(Collectors.toList());
        }
    }
}
