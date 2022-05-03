package com.djimgou.core.infra;

public enum QueryOrder {
    asc("asc"),
    desc("desc");
    private final String order;

    QueryOrder(String order) {
        this.order = order;
    }

    @Override
    public String toString() {
        return order;
    }
}
