package com.djimgou.core.coolvalidation.processors;

import com.djimgou.core.coolvalidation.annotations.CanDelete;
import com.djimgou.core.coolvalidation.annotations.Validations;
import com.djimgou.core.coolvalidation.annotations.Unique;
import com.djimgou.core.coolvalidation.exception.CoolValidationException;
import com.djimgou.core.coolvalidation.model.FkModel;
import com.djimgou.core.util.AppUtils;
import com.djimgou.core.util.EntityRepository;
import com.djimgou.core.util.exception.NotManagedEntityException;
import lombok.extern.log4j.Log4j2;
import org.hibernate.Session;
import org.hibernate.query.NativeQuery;
import org.hibernate.type.StringType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.lang.reflect.Field;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.djimgou.core.util.AppUtils.has;

@Log4j2
@Component
public class ValidationParserImpl implements ValidationParser {


    public static String getOrclQuery(String refTable){
        return "SELECT " +
                "CONS.CONSTRAINT_NAME AS constraint_name ," +
                "CONS.TABLE_NAME AS table_name, " +
                "COLS.COLUMN_NAME AS column_name," +
                "CONS.R_CONSTRAINT_NAME AS r_constraint_name," +
                "CONS_R.TABLE_NAME AS r_table_name," +
                "COLS_R.COLUMN_NAME AS r_column_name " +
                "FROM USER_CONSTRAINTS CONS     " +
                "LEFT JOIN USER_CONS_COLUMNS COLS ON COLS.CONSTRAINT_NAME = CONS.CONSTRAINT_NAME     " +
                "LEFT JOIN USER_CONSTRAINTS CONS_R ON CONS_R.CONSTRAINT_NAME = CONS.R_CONSTRAINT_NAME     " +
                "LEFT JOIN USER_CONS_COLUMNS COLS_R ON COLS_R.CONSTRAINT_NAME = CONS.R_CONSTRAINT_NAME  " +
                "WHERE CONS.CONSTRAINT_TYPE = 'R' AND  CONS_R.TABLE_NAME = " +
                "'" +refTable.toUpperCase()+"' "+
                "ORDER BY CONS.TABLE_NAME, COLS.COLUMN_NAME";
    }
    private EntityRepository er;

    @Autowired
    public ValidationParserImpl(EntityRepository er) {
        this.er = er;
    }

    public ValidationParserImpl(EntityManager em) {
        this(new EntityRepository(em));
        this.er.loadEntity();
    }

    @Transactional
    @Override
    public <T, ID> void validate(T entity) throws CoolValidationException {
        if (entity != null && entity.getClass().isAnnotationPresent(Validations.class)) {
            List<Field> fields = AppUtils.getFields(entity.getClass(), field -> field.isAnnotationPresent(Unique.class));
            Object id;
            String idName;
            try {
                idName = er.getIdKey(entity.getClass());
                id = AppUtils.getDeepProperty(entity, idName);
            } catch (NotManagedEntityException e) {

                throw new CoolValidationException(e.getMessage());
            }
            boolean isCreation = id == null;
            for (Field field : fields) {
                Unique an = field.getAnnotation(Unique.class);
                boolean exist = er.existBy(entity, field, an.ignoreCase());
                String message = an.message().isEmpty() ? String.format("The value of property %s is already exists in the database. it should be unique", field.getName()) : an.message();
                if (exist) {
                    if (isCreation) {
                        if (an.createMsg().length > 0) {
                            message = String.join("\n", an.createMsg());
                        }
                        throw new CoolValidationException(message);
                    } else {
                        List<?> oldEntity = er.findBy(entity, field, an.ignoreCase());
                        if (oldEntity != null && !oldEntity.isEmpty()) {
                            boolean isSame = oldEntity.stream().allMatch(e -> {
                                Object id2 = AppUtils.getDeepProperty(e, idName);
                                return Objects.equals(id2, id);
                            });
                            if (!isSame) {
                                if (an.updateMsg().length > 0) {
                                    message = String.join("\n", an.updateMsg());
                                }
                                // instruction très importante pour éviter dans le cadre d'une transaction
                                // d'effectuer une modification dans la BD si l'entité a été modifiée avant par le dev
                                // Spring automatiquement va essayer de mettre à jour la BD même si la methode save du repo n'a pas été appelée
                                this.er.getEm().detach(entity);
                                throw new CoolValidationException(message);
                            }
                        }

                    }

                }

            }
        }

    }

    @Override
    public <T, ID> boolean canSave(T entity) {
        try {
            validate(entity);
        } catch (Exception | CoolValidationException e) {
            return false;
        }
        return true;
    }

    @Override
    public <T, ID> boolean canDelete(T entity) {
        try {
            checkBeforeDelete(entity);
        } catch (Exception | CoolValidationException e) {
            return false;
        }

        return true;
    }

    public static String COUN_QUERY = " SELECT count(*) FROM :tableName WHERE :columnName = :idValue ";

    @Transactional
    @Override
    public <T, ID> void checkBeforeDelete(T entity) throws CoolValidationException {
        List<FkModel> found = new ArrayList<>();
        List<FkModel> all = new ArrayList<>();
        if (entity != null) {
            String tName = er.getTableName(entity.getClass());
            Validations vAn = null;
            if (entity.getClass().isAnnotationPresent(Validations.class)) {
                vAn = entity.getClass().getAnnotation(Validations.class);


                if (vAn.canDeleteChecks().length > 0) {
                    CanDelete[] canDelAns = vAn.canDeleteChecks();
                    for (CanDelete canDelAn : canDelAns) {
                        String fk = canDelAn.childColName();
                        String trefTable = canDelAn.childTableName();
                        String message = canDelAn.message();
                    }


                }


            }
            Session session = er.getEm().unwrap(Session.class);
            List<Object[]> rows=new ArrayList<>();
            try {
                NativeQuery query = session.createNativeQuery(getOrclQuery(tName))
                        .addScalar("constraint_name", new StringType())
                        .addScalar("table_name", new StringType())
                        .addScalar("column_name ", new StringType())
                        .addScalar("r_constraint_name", new StringType())
                        .addScalar("r_table_name", new StringType())
                        .addScalar("r_column_name", new StringType());
                        //.setParameter("ref_table_name", tName);
                 rows = query.list();
            }catch (Exception e){
                e.printStackTrace();
            }

            for (Object[] row : rows) {
                FkModel emp = new FkModel(
                        row[0].toString(),
                        row[1].toString(),
                        row[2].toString(),
                        row[3].toString(),
                        row[4].toString(),
                        row[5].toString(),
                        0
                );

                NativeQuery query2 = null;
                try {
                    query2 = session.createSQLQuery(COUN_QUERY)
                            .setParameter("tableName", emp.getTableName())
                            .setParameter("columnName", emp.getColumnName())
                            .setParameter("idValue", er.getIdValue(entity));
                } catch (NotManagedEntityException e) {
                    throw new CoolValidationException(e.getMessage());
                }

                long count = (long) query2.getSingleResult();

                emp.setRefCount(count);
                if (count > 0) {
                    found.add(emp);
                }
                all.add(emp);
            }
            if (vAn == null || has(all) &&  Objects.requireNonNull(vAn).canDeleteChecks().length != all.size()) {
                String anotations = found.stream().map(fkModel -> MessageFormat.format(
                        "@CanDelete(childColName = \"{0}\", childTableName = \"{1}\", message=\"...\")", fkModel.getColumnName(), fkModel.getTableName()
                )).collect(Collectors.joining(",\n "));
//                com.djimgou.core.coolvalidation.annotations
                String messageF = MessageFormat.format("@Validations(canDeleteChecks = \"{0}\")", anotations);
                log.warn("There are some delete check validations missing. please consider to place this annotation {} on top on the class", messageF);
                //throw new CoolValidationException(messageF);
            }

        }

    }
}
