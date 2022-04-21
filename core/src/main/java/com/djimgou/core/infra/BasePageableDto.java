package com.djimgou.core.infra;

import com.djimgou.core.cooldto.model.IDto;
import lombok.Getter;
import lombok.Setter;

/**
 * Mettre
 */
@Getter
@Setter
public abstract class BasePageableDto  implements IDto {
    public static final String[] IGNORE = new String[]{
          "searchText"  ,"page","size","sort"
    };

    int page;
    int size;
    String[] sort;
}
