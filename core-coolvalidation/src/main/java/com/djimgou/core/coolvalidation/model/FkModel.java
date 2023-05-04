package com.djimgou.core.coolvalidation.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FkModel {
    String constraintName;
    String tableName;
    String columnName;
    String refConstraintName;
    String refTableName;
    String refColumnName;
    /**
     * Nombre de dépendance trouvée dans la référence
     */
    long refCount;
}
