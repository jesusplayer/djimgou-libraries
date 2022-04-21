package com.djimgou.core.infra;

import lombok.Getter;
import lombok.Setter;

/**
 * Mettre
 */
@Getter
@Setter
public abstract class BaseFindDto extends BasePageableDto{
    String searchText;
    String[] searchKeys;
}
