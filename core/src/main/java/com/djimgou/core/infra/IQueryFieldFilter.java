/*
 * Copyright (c) 2022. Créé par DJIMGOU NKENNE Dany
 */

package com.djimgou.core.infra;

public interface IQueryFieldFilter<T> {
    T[] getBetween();

    T getEq();

    T getGe();

    T getLe();

    T getLt();

    T getGt();

    void setEq(T eq);

    void setGe(T ge);

    void setLe(T le);

    void setLt(T lt);

    void setGt(T gt);

    String getLike();

    void setLike(String pattern);

    String getContains();

    void setContains(String contains);

    String getContainsIgnoreCase();

    void setContainsIgnoreCase(String containsIgnoreCase);
}
