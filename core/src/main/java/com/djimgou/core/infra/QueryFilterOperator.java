package com.djimgou.core.infra;

public enum QueryFilterOperator {
    eq("eq"),
    le("le"),
    lt("lt"),
    ge("ge"),
    gt("gt"),
    between("between"),
    contains("contains"),
    containsIgnoreCase("containsIgnoreCase"),
    like("like");
    private final String text;

    QueryFilterOperator(final String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }
}
