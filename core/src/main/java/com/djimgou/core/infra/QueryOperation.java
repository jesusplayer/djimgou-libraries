package com.djimgou.core.infra;

import com.djimgou.core.exception.UnknowQueryFilterOperator;
import com.querydsl.core.types.Ops;
import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;

@Data
@AllArgsConstructor
public class QueryOperation {
    public static Map<QueryFilterOperator, Ops> mapOp = new HashMap() {{
        put(QueryFilterOperator.eq, Ops.EQ);
        put(QueryFilterOperator.lt, Ops.LT);
        put(QueryFilterOperator.le, Ops.LOE);
        put(QueryFilterOperator.gt, Ops.GT);
        put(QueryFilterOperator.between, Ops.BETWEEN);
        put(QueryFilterOperator.like, Ops.LIKE);
        put(QueryFilterOperator.contains, Ops.STRING_CONTAINS);
        put(QueryFilterOperator.containsIgnoreCase, Ops.STRING_CONTAINS_IC);
    }};

    public Ops ops() throws UnknowQueryFilterOperator {
        if (mapOp.containsKey(operator)) {
            return mapOp.get(operator);
        } else {
            throw new UnknowQueryFilterOperator(operator.toString());
        }
    }

    @NotNull(message = "La valeur de key doit aumoins être définie dans le filtre")
    String key;

    @NotNull(message = "La valeur de l'opérateur doit aumoins être définie dans le filtre")
    QueryFilterOperator operator = QueryFilterOperator.eq;

    @NotNull(message = "La valeur de value1 doit aumoins être définie dans le filtre")
    Object value1;

    Object value2;

    QueryOrder order;
}
