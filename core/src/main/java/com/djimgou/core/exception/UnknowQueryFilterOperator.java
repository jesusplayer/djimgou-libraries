package com.djimgou.core.exception;

public class UnknowQueryFilterOperator extends NotFoundException {
    public UnknowQueryFilterOperator(String operator) {
        super("L'opperateur " + operator + " N'est pas pris en charge dans le filtre");
    }
}
