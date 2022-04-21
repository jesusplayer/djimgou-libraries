package com.djimgou.security.controller;


import com.djimgou.core.util.model.BaseBdEntity;

import java.util.Objects;
import java.util.UUID;

/**
 * @author DJIMGOU NKENNE DANY MARC 08/2020
 * @param <T> parametre
 */
public class Item<T extends BaseBdEntity> {
    T data;
    UUID id;

    public Item(T data) {
        this.data = data;
        this.id = data.getId();
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Item item = (Item) o;
        return getData().equals(item.getData());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getData());
    }
}
