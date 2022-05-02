package com.djimgou.core.infra;

public enum FieldOrder {
    asc("asc"),
    desc("desc");
    private final String order;

    FieldOrder(String order) {
        this.order = order;
    }

    @Override
    public String toString() {
        return order;
    }
}
