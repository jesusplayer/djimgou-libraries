package com.act.core.infra;

import lombok.Getter;
import lombok.Setter;

/**
 * Mettre
 */
@Getter
@Setter
public abstract class BasePageableDto {
    public static final String[] IGNORE = new String[]{
          "searchText"  ,"page","size","sort"
    };

    int page;
    int size;
    String[] sort;
}
