/*
 * Copyright (c) 2022. Créé par DJIMGOU NKENNE Dany
 */

package com.act.core.testing.app;

import com.act.core.model.IEntityDto;

import java.util.function.Consumer;

public interface EntityDtoConsumer<T extends IEntityDto> extends Consumer {

    @Override
    default void accept(Object o) {
        this.accept((T) o);
    }

    void accept(T o);
}
