package com.djimgou.core.infra;

import lombok.Getter;
import lombok.Setter;

import static com.djimgou.core.util.AppUtils.has;

/**
 * Mettre
 */
@Getter
@Setter
public abstract class BaseFilterDto extends BasePageableDto {
    /**
     * Si search = true alors on implémente un filtre de 'or'. Sinon un filtre de 'and'
     */
    Boolean search$$ = Boolean.FALSE;
}
