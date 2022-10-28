package com.djimgou.core.infra;

import com.djimgou.core.cooldto.model.IDto;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

/**
 * Mettre
 */
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
public abstract class BasePageableDto implements IDto {
    int page;
    int size;
    String[] sort;

    public boolean isUnpaged() {
        return page == 0 && size == 0;
    }
}
