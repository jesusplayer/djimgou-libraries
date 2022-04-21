package com.djimgou.core.test.proxy;



/**
 * Ce code n'est pas SOLID. Mais pour faciliter les les traitements, on est obligé de traiter ainsi
 */
public interface IServiceProxy<T, DTO> {
    T create(DTO dto);

    DTO fakeDto();

    default T create() throws Exception {
        return create(fakeDto());
    }
}
