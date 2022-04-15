package com.act.core.testing.proxy;

import com.act.core.exception.NotFoundException;

/**
 * Ce code n'est pas SOLID. Mais pour faciliter les les traitements, on est obligé de traiter ainsi
 */
public interface IServiceProxy<T, DTO> {
    T create(DTO dto);

    DTO fakeDto();

    default T create() throws NotFoundException {
        return create(fakeDto());
    }
}
