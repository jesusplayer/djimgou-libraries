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

    public boolean isBetween() {
        return text.equals(between.text);
    }

    public boolean isEq() {
        return text.equals(eq.text);
    }

    public boolean isGt() {
        return text.equals(gt.text);
    }

    public boolean isGe() {
        return text.equals(ge.text);
    }

    public boolean isLe() {
        return text.equals(le.text);
    }

    public boolean isLt() {
        return text.equals(lt.text);
    }

    public boolean isLike() {
        return text.equals(like.text);
    }

    public boolean isContains() {
        return text.equals(contains.text);
    }

    public boolean isContainsIgnoreCase() {
        return text.equals(containsIgnoreCase.text);
    }
}
