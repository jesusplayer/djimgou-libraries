package com.djimgou.core.infra;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

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
}
