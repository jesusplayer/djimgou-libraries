/*
 * Copyright (c) 2022. Créé par DJIMGOU NKENNE Dany
 */

package com.djimgou.core.infra;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PUBLIC)
@Data
public class QueryFieldFilter<T> implements IQueryFieldFilter<T> {
    T[] between;
    T eq;
    T ge;
    T le;
    T lt;
    T gt;
    String contains;
    String containsIgnoreCase;
    String like;
    QueryOrder order;
}
