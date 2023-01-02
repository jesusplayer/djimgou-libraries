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
public abstract class BaseFilterDto extends BasePageableDto {
    String searchText;
}
