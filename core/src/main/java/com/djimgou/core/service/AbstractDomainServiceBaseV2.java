package com.djimgou.core.service;

import com.djimgou.core.annotations.*;
import com.djimgou.core.cooldto.annotations.DtoField;
import com.djimgou.core.cooldto.exception.DtoChildFieldNotFound;
import com.djimgou.core.cooldto.exception.DtoMappingException;
import com.djimgou.core.cooldto.model.IEntityDetailDto;
import com.djimgou.core.cooldto.model.IEntityDto;
import com.djimgou.core.cooldto.service.DtoSerializerService;
import com.djimgou.core.exception.AppException;
import com.djimgou.core.exception.NotFoundException;
import com.djimgou.core.exception.UnknowQueryFilterOperator;
import com.djimgou.core.infra.*;
import com.djimgou.core.repository.BaseJpaRepository;
import com.djimgou.core.util.AppUtils;
import com.djimgou.core.util.AppUtils2;
import com.djimgou.core.util.model.IBaseEntity;
import com.querydsl.core.support.QueryBase;
import com.querydsl.core.types.*;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.BooleanOperation;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAQueryBase;
import com.querydsl.jpa.impl.JPAQuery;
import lombok.*;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.util.ReflectionUtils;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static com.djimgou.core.util.AppUtils2.*;


/**
 * Service abstrait de gestion des opérations métiers de la base de données
 *
 * @param <T> parametre
 * @author djimgou
 */

@Getter
@Setter
@Log4j2
public abstract class AbstractDomainServiceBaseV2<T extends IBaseEntity, FIND_DTO extends BaseFindDto, FILTER_DTO extends BaseFilterDto, DTO extends IEntityDto, DETAIL_DTO extends IEntityDetailDto, ID>
        extends AbstractDomainServiceBase<T, FIND_DTO, FILTER_DTO, ID> {
    static Map<Class<? extends Annotation>, Ops> map = new HashMap<>();

    static {
        map.putAll(Map.of(
                Eq.class, Ops.EQ,
                EqIc.class, Ops.EQ_IGNORE_CASE,
                Gt.class, Ops.GT,
                Goe.class, Ops.GOE,
                Lt.class, Ops.LT,
                Loe.class, Ops.LOE,
                In.class, Ops.IN,
                StrContains.class, Ops.STRING_CONTAINS,
                StrContainsIc.class, Ops.STRING_CONTAINS_IC
        ));
        map.putAll(Map.of(
                Like.class, Ops.LIKE,
                LikeIc.class, Ops.LIKE_IC,
                StartWith.class, Ops.STARTS_WITH,
                StartWithIc.class, Ops.STARTS_WITH_IC,
                EndWith.class, Ops.ENDS_WITH,
                EndWithIc.class, Ops.ENDS_WITH_IC
        ));
    }

    @Getter
    @PersistenceContext
    private EntityManager em;

    @Autowired
    private DtoSerializerService dtoSerializer;


    @Getter
    private BaseJpaRepository<T, ID> repo;

    @Getter
    private EntityPathBase<T> qEntity;

    public AbstractDomainServiceBaseV2(BaseJpaRepository<T, ID> repo, EntityPathBase<T> qEntity) {
        super(repo);
        this.repo = repo;
        this.qEntity = qEntity;
    }

    /**
     * Crée automatiquement une nouvelle entité en BD à partir de son DTO
     * Ne pas oublier de respecter la convention de nomage
     * class EntiteParent{
     * UUID id;
     * ...
     * }
     * <p>
     * class Entite {
     * String unChamp;
     *
     * @param produitDto
     * @return
     * @throws NotFoundException
     * @throws DtoChildFieldNotFound indique que les id des clés étrangères n'ont pas été renseignés bien écrites.
     *                               Bien vouloir respecter la convention
     * @ManyToOne() EntiteParent entiteParent;
     * }
     * <p>
     * class EntiteDto{
     * String unChamp;
     * UUID entiteentiteParentId;
     * }
     */
    public T create(DTO produitDto) throws NotFoundException, DtoMappingException, AppException {
        return save(null, produitDto);
    }

    @Transactional
    public Collection<T> createAll(Collection<DTO> produitDto) throws NotFoundException, DtoMappingException, AppException {
        Collection<T> targetCol = new ArrayList<>();
        for (DTO dto : produitDto) {
            T res = save(null, dto);
            targetCol.add(res);
        }
        return targetCol;
    }

    /**
     * Met à jour automatiquement une nouvelle entité en BD à partir de son DTO
     * Ne pas oublier de respecter la convention de nomage
     * class EntiteParent{
     * UUID id;
     * ...
     * }
     * <p>
     * class Entite {
     * String unChamp;
     *
     * @param produitDto
     * @return
     * @throws NotFoundException
     * @throws DtoChildFieldNotFound indique que les id des clés étrangères n'ont pas été renseignés bien écrites.
     *                               Bien vouloir respecter la convention
     * @ManyToOne() EntiteParent entiteParent;
     * }
     * <p>
     * class EntiteDto{
     * String unChamp;
     * UUID entiteentiteParentId;
     * }
     */
    public T update(ID id, DTO produitDto) throws NotFoundException, DtoMappingException, AppException {
        return save(id, produitDto);
    }

    /**
     * Crée/modifie automatiquement une nouvelle entité en BD à partir de son DTO
     * La fonction va faire le mapping entre les Id du DTO avec les entités correspondantes
     * et va enregistrer le tout en BD. Contrairement à persist(id,entity) qui fait un enregistrement simple
     * Ne pas oublier de respecter la convention de nomage
     * class EntiteParent{
     * UUID id;
     * ...
     * }
     * <p>
     * class Entite {
     * String unChamp;
     *
     * @param id
     * @param entityDto
     * @throws NotFoundException
     * @throws DtoChildFieldNotFound indique que les id des clés étrangères n'ont pas été renseignés bien écrites.
     *                               Bien vouloir respecter la convention
     * @ManyToOne() EntiteParent entiteParent;
     * }
     * <p>
     * class EntiteDto{
     * String unChamp;
     * UUID entiteentiteParentId;
     * }
     */
    @Transactional(/*propagation = Propagation.NESTED*/)
    public T save(ID id, DTO entityDto) throws NotFoundException, DtoMappingException, AppException {
        T entity = ReflectionUtils.createInstanceIfPresent(getFilterDtoClass(0).getName(), null);
        if (has(id)) {
            entity = getRepo().findById(id).orElseThrow(() ->
                    new NotFoundException(getFilterDtoClass(0).getSimpleName() + "#" + id + " N'existe pas")
            );
        }
/*
        entity.fromDto(entityDto);

        injectReferencedField(id, entityDto, entity);*/
        dtoSerializer.serialize(entityDto, entity);
        //getEm().detach(entity);
        T saved = persist(id, entity, entityDto);
        return saved;
    }

    /**
     * Enregiistre simplement une entité
     *
     * @param id
     * @param entity
     * @param dto
     * @return
     */
    // @Transactional
    public T persist(ID id, T entity, DTO dto) throws NotFoundException, DtoMappingException, AppException {
        if (this instanceof IBeforeSave) {
            try {
                entity = ((IBeforeSave<T, ID, DTO>) this).beforeSave(id, entity, dto);
            } catch (Throwable e) {
                if (has(id)) {
                    getEm().detach(entity);
                }
                throw e;
            }
        }
        /*try {
            validationParser.validate(entity);
        } catch (CoolValidationException e) {
            throw new AppException(e.getMessage());
        }*/
        T saved = save(entity);
        if (this instanceof IAfterSave) {
//            try {
            saved = ((IAfterSave<T, ID, DTO>) this).afterSave(id, saved, dto);
//            } catch (Throwable e) {
//                getEm().detach(entity);
//                throw e;
//            }
        }
        return saved;
    }

    public final void injectReferencedField(UUID id, IEntityDto entityDto, T entity) throws NotFoundException, DtoChildFieldNotFound {
        injectReferencedField(id, entityDto, entity, null);
    }

    /**
     * Injecte dynamiquement les entité des clés étrangères présentent dans le DTO entityDto
     * dans la nouvelle entité entity
     * à enregistrer
     *
     * @param id
     * @param entityDto
     * @param entity
     * @param fieldFilter filtre sur les champs
     * @throws NotFoundException
     * @throws DtoChildFieldNotFound
     */
    public final void injectReferencedField(UUID id, IEntityDto entityDto, T entity, Predicate<Field> fieldFilter) throws NotFoundException, DtoChildFieldNotFound {
        if (!has(fieldFilter)) {
            fieldFilter = (t) -> true;
        }
        List<Field> dtoIdFields = AppUtils2.getFields(
                entityDto.getClass(), fieldFilter.and(f -> Objects.equals(f.getType().getName(), UUID.class.getName())
                        && !Objects.equals("id", f.getName())
                        && f.getName().endsWith("Id")
                )

        );

        for (Field field : dtoIdFields) {
            UUID dtoId = (UUID) AppUtils2.getField(field.getName(), entityDto);

            String objName1 = field.getName();
            String objName2 = field.getName().substring(0, field.getName().length() - 2);
            String objName3 = field.getName() + "Ob";

            String choosedKey = objName2;

            boolean childOb1 = AppUtils2.hasField(entity.getClass(), objName1);
            boolean childOb2 = AppUtils2.hasField(entity.getClass(), objName2);
            boolean childOb3 = AppUtils2.hasField(entity.getClass(), objName3);
            // lordre est important
            if (childOb3) {
                choosedKey = objName3;
            } else {
                if (childOb1) {
                    choosedKey = objName1;
                }
            }

            if (childOb1 || childOb2 || childOb3) {
                Object childObj = AppUtils2.getField(choosedKey, entity);
                String finalChoosedKey = choosedKey;

                UUID childObId = null;
                if (has(childObj)) {
                    childObId = (UUID) AppUtils2.getField("id", childObj);
                }
                final boolean existChildId = has(id) && Objects.equals(childObId, dtoId);
                if (!existChildId) {
                    Field childObjField = AppUtils2.getFields(entity.getClass(), field1 ->
                            Objects.equals(finalChoosedKey, field1.getName()))
                            .stream().findFirst().orElse(null);

                    Class key = childObjField.getType();
                    Object childDbValue = em.find(key, dtoId);
                    if (childDbValue == null) {
                        throw new NotFoundException(key.getSimpleName() + "#" +
                                id + " N'existe pas"
                        );
                    }
                    AppUtils2.setField(choosedKey, entity, childDbValue);
                }
            } else {
                throw new DtoChildFieldNotFound(entityDto, entity, field);
            }

        }
    }

    @Transactional
    @Override
    public Page<T> advancedFindBy(BaseFilterDto filter) throws Exception {
        CustomPageable cpg = new CustomPageable(filter);

        Page<T> page;

        EntityPathBase<T> qQSommier = getQEntity();

        JPAQuery query = new JPAQuery(em);
        JPAQueryBase exp2 = query.from(qQSommier);
        //LuceneQuery query2 = new LuceneQuery(new IndexSearcher());
        List<BooleanExpression> expressionList = new ArrayList<>();

        List<OrderSpecifier> orders = new ArrayList<>();
        List<Field> fiels = getFields(filter.getClass(), field -> true);
        final Class<T> entityClass = getFilterDtoClass(0);
        String simpleName = entityClass.getSimpleName();
        String className = Character.toLowerCase(simpleName.charAt(0)) + simpleName.substring(1);
        Path<T> p = Expressions.path(entityClass, className);

        processDefaultFilters(filter, expressionList, orders, fiels, p);

        if (filter instanceof BaseFilterAdvancedDto) {
            final BaseFilterAdvancedDto advancedDto = (BaseFilterAdvancedDto) filter;
            if (advancedDto != null && advancedDto.hasOtherFilters()) {
                processCustomFilters(advancedDto, expressionList, p);
            }
        }
        BooleanExpression exp = expressionList.stream().reduce(null, (old, newE) -> has(old) ? (filter.getSearch$$() ? old.or(newE) : old.and(newE)) : newE);

        final QueryBase exp3 = exp2.where(exp);
        if (has(orders)) {
            exp3.orderBy(orders.toArray(new OrderSpecifier[orders.size()]));
        }

        if (has(exp)) {
            page = getRepo().findAll(exp, cpg.getPg());
        } else {
            page = getRepo().findAll(cpg);
        }
        return page;
    }

    @Override
    public Page<T> advancedSearchBy(BaseFilterDto baseFilter) throws Exception {
        baseFilter.setSearch$$(true);
        return advancedFindBy(baseFilter);
    }

    private void processDefaultFilters(BaseFilterDto filter, List<BooleanExpression> expressionList, List<OrderSpecifier> orders, List<Field> fiels, Path<T> p) throws IllegalAccessException, DtoMappingException {
        Map<String, Boolean> ignoreMap = new HashMap<>();

        Arrays.stream(BaseFilterAdvancedDto.IGNORE)
                .forEach(s -> ignoreMap.put(s, Boolean.TRUE));

        extractBetween(filter, expressionList, p, ignoreMap);

        for (Field field : fiels) {
            if (!ignoreMap.containsKey(field.getName()) && !java.lang.reflect.Modifier.isStatic(field.getModifiers())) {
                Object fieldValue = field.get(filter);
                String name = extractUnaryOpAnotations(expressionList, p, field, fieldValue);
                if (field.isAnnotationPresent(DtoField.class)) {
                    DtoField an = field.getAnnotation(DtoField.class);
                    if (an != null && has(an.value())) {
                        name = an.value()[0];
                    }
                }
                if (fieldValue instanceof IQueryFieldFilter) {
                    QueryFieldFilter ff = (QueryFieldFilter) fieldValue;


                    if (has(ff.between)) {
                        Path fName = Expressions.path(ff.between[0].getClass(), p, name);
                        Expression<Object> constant1 = Expressions.constant(ff.between[0]);
                        Expression<Object> constant2 = Expressions.constant(ff.between[1]);
                        BooleanOperation exp = Expressions.predicate(Ops.BETWEEN, fName, constant1, constant2);
                        expressionList.add(exp);
                    }
                    if (has(ff.eq)) {
                        Path fName = Expressions.path(ff.eq.getClass(), p, name);
                        Expression<Object> constant1 = Expressions.constant(ff.eq);
                        BooleanOperation exp = Expressions.predicate(Ops.EQ, fName, constant1);
                        expressionList.add(exp);
                    }
                    if (has(ff.lt)) {
                        Path fName = Expressions.path(ff.lt.getClass(), p, name);
                        Expression<Object> constant1 = Expressions.constant(ff.lt);
                        BooleanOperation exp = Expressions.predicate(Ops.LT, fName, constant1);
                        expressionList.add(exp);
                    }
                    if (has(ff.le)) {
                        Path fName = Expressions.path(ff.le.getClass(), p, name);
                        Expression<Object> constant1 = Expressions.constant(ff.le);
                        BooleanOperation exp = Expressions.predicate(Ops.LOE, fName, constant1);
                        expressionList.add(exp);
                    }
                    if (has(ff.gt)) {
                        Path fName = Expressions.path(ff.gt.getClass(), p, name);
                        Expression<Object> constant1 = Expressions.constant(ff.gt);
                        BooleanOperation exp = Expressions.predicate(Ops.GT, fName, constant1);
                        expressionList.add(exp);
                    }
                    if (has(ff.ge)) {
                        Path fName = Expressions.path(ff.ge.getClass(), p, name);
                        Expression<Object> constant1 = Expressions.constant(ff.ge);
                        BooleanOperation exp = Expressions.predicate(Ops.GOE, fName, constant1);
                        expressionList.add(exp);
                    }
                    if (has(ff.contains)) {
                        Path fName = Expressions.path(ff.contains.getClass(), p, name);
                        Expression<Object> constant1 = Expressions.constant(ff.contains);
                        BooleanOperation exp = Expressions.predicate(Ops.STRING_CONTAINS, fName, constant1);
                        expressionList.add(exp);
                    }
                    if (has(ff.containsIgnoreCase)) {
                        Path fName = Expressions.path(ff.containsIgnoreCase.getClass(), p, name);
                        Expression<Object> constant1 = Expressions.constant(ff.containsIgnoreCase);
                        BooleanOperation exp = Expressions.predicate(Ops.STRING_CONTAINS_IC, fName, constant1);
                        expressionList.add(exp);
                    }
                    if (has(ff.like)) {
                        Path fName = Expressions.path(ff.like.getClass(), p, name);
                        Expression<Object> constant1 = Expressions.constant(ff.like);
                        BooleanOperation exp = Expressions.predicate(Ops.LIKE, fName, constant1);
                        expressionList.add(exp);
                    }
                    if (has(ff.order)) {
                        Path path = Expressions.path(Object.class, p, name);
                        OrderSpecifier orderSpecifier = new OrderSpecifier(Order.ASC, path);
                        orders.add(orderSpecifier);
                    }

                } else {
                    // Lorsqu'il s'agit d'un filtre avec plusieurs champs qui ne dépendent pas de
                    // IQueryFieldFilter
                    boolean exist = Arrays.stream(BaseFilterAdvancedDto.IGNORE).anyMatch(s -> s.equals(field.getName()));
                    if (has(fieldValue) && !exist) {

                        Path fName = Expressions.path(Objects.requireNonNull(fieldValue).getClass(), p, name);
                        Expression<Object> constant1 = Expressions.constant(fieldValue);
                        BooleanOperation exp = Expressions.predicate(Ops.EQ, fName, constant1);
                        expressionList.add(exp);
                    }
                }
            }

        }


    }

    private String extractUnaryOpAnotations(List<BooleanExpression> expressionList, Path<T> p, Field field, Object fieldValue) {
        String newName = field.getName();
        Ops ops = null;

        if (field.isAnnotationPresent(DtoField.class)) {
            DtoField an = field.getAnnotation(DtoField.class);
            if (an != null && has(an.value())) {
                newName = an.value()[0];
            }
        }


        for (Annotation annotation : field.getDeclaredAnnotations()) {
            if(map.containsKey(annotation.annotationType())){
                Annotation an = field.getAnnotation(annotation.annotationType());
                ops = map.get(annotation.annotationType());
                try {
                    if (an != null) {
                        Object value = an.getClass().getMethod("value").invoke(an);
                        if (value != null) {
                            newName = value.toString();
                        }
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                }
            }
        }

        /*for (Map.Entry<Class<? extends Annotation>,Ops> opsClassEntry : map.entrySet()) {
            if (field.isAnnotationPresent(opsClassEntry.getKey())) {
                Annotation an = field.getAnnotation(opsClassEntry.getKey());
                ops = opsClassEntry.getValue();
                try {
                    if (an != null) {
                        Object value = an.getClass().getMethod("value").invoke(an);
                        if (value != null) {
                            newName = value.toString();
                        }
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                }
            }
        }*/
        /*if (field.isAnnotationPresent(Eq.class)) {
            Eq an = field.getAnnotation(Eq.class);
            if (an != null && has(an.value())) {
                newName = an.value();
            }
            ops = Ops.EQ;
        }
        if (field.isAnnotationPresent(EqIc.class)) {
            EqIc an = field.getAnnotation(EqIc.class);
            if (an != null && has(an.value())) {
                newName = an.value();
            }
            ops = Ops.EQ_IGNORE_CASE;
        }
        if (field.isAnnotationPresent(Lt.class)) {
            Lt an = field.getAnnotation(Lt.class);
            if (an != null && has(an.value())) {
                newName = an.value();
                ops = Ops.LT;
            }
        }

        if (field.isAnnotationPresent(Loe.class)) {
            Loe an = field.getAnnotation(Loe.class);
            if (an != null && has(an.value())) {
                newName = an.value();
                ops = Ops.LOE;
            }
        }
        if (field.isAnnotationPresent(Gt.class)) {
            Gt an = field.getAnnotation(Gt.class);
            if (an != null && has(an.value())) {
                newName = an.value();
                ops = Ops.GT;
            }
        }
        if (field.isAnnotationPresent(Goe.class)) {
            Goe an = field.getAnnotation(Goe.class);
            if (an != null && has(an.value())) {
                newName = an.value();
                ops = Ops.GOE;
            }
        }

        if (field.isAnnotationPresent(Like.class)) {
            Like an = field.getAnnotation(Like.class);
            if (an != null && has(an.value())) {
                newName = an.value();
                ops = Ops.LIKE;
            }
        }
        if (field.isAnnotationPresent(In.class)) {
            In an = field.getAnnotation(In.class);
            if (an != null && has(an.value())) {
                newName = an.value();
                ops = Ops.IN;
            }
        }

        if (field.isAnnotationPresent(StrContains.class)) {
            StrContains an = field.getAnnotation(StrContains.class);
            if (an != null && has(an.value())) {
                newName = an.value();
                ops = Ops.STRING_CONTAINS;
            }
        }

        if (field.isAnnotationPresent(StartWith.class)) {
            StartWith an = field.getAnnotation(StartWith.class);
            if (an != null && has(an.value())) {
                newName = an.value();
                ops = Ops.STARTS_WITH;
            }
        }

        if (field.isAnnotationPresent(EndWith.class)) {
            EndWith an = field.getAnnotation(EndWith.class);
            if (an != null && has(an.value())) {
                newName = an.value();
                ops = Ops.ENDS_WITH;
            }
        }
        if (field.isAnnotationPresent(EndWithIc.class)) {
            EndWithIc an = field.getAnnotation(EndWithIc.class);
            if (an != null && has(an.value())) {
                newName = an.value();
                ops = Ops.ENDS_WITH;
            }
        }

        if (field.isAnnotationPresent(StrContainsIc.class)) {
            StrContainsIc an = field.getAnnotation(StrContainsIc.class);
            if (an != null && has(an.value())) {
                newName = an.value();
                ops = Ops.STRING_CONTAINS_IC;
            }
        }*/

        if (has(ops) && has(fieldValue)) {
            Path fName = Expressions.path(field.getType(), p, newName);
            Expression<Object> constExp = Expressions.constant(fieldValue);
            BooleanOperation exp = Expressions.predicate(ops, fName, constExp);
            expressionList.add(exp);
        }
        return newName;
    }

    private void extractBetween(BaseFilterDto filter, List<BooleanExpression> expressionList, Path<T> p, Map<String, Boolean> ignoreMap) throws DtoMappingException {
        if (filter.getClass().isAnnotationPresent(BetweenConditions.class)) {
            BetweenConditions an = filter.getClass().getAnnotation(BetweenConditions.class);
            for (Between between : an.value()) {
                String name = between.entityField();
                String fieldName1 = between.value1();
                String fieldName2 = between.value2();
                if (!AppUtils.hasField(filter.getClass(), fieldName1)) {
                    throw new DtoMappingException(String.format("Impossible d'appliquer le filtre Beteen car la propriété value1=%s n'existe pas dans la classe %s ", fieldName1, filter.getClass().getName()));
                }
                if (!AppUtils.hasField(filter.getClass(), fieldName2)) {
                    throw new DtoMappingException(String.format("Impossible d'appliquer le filtre Beteen car la propriété value2=%s n'existe pas dans la classe %s ", fieldName2, filter.getClass().getName()));
                }

                final Class type = getDeepPropertyType(filter, fieldName1);
                final Class type2 = getDeepPropertyType(filter, fieldName2);
                if (!type.equals(type2)) {
                    throw new DtoMappingException(String.format("Impossible d'appliquer le filtre Beteen dans la classe %s car les propriétés %s et %s sont de type différents (%s # %s)",
                            filter.getClass().getName(), fieldName1, fieldName2, type, type2
                    ));
                }
                Path fName = Expressions.path(type, p, name);
                Object val1 = getDeepProperty(filter, fieldName1);
                Object val2 = getDeepProperty(filter, fieldName2);
               /* val1 = has(val1) ? val1 : val2;
                val2 = has(val2) ? val2 : val1;*/
                if (has(val1) && has(val2)) {
                    Expression<Object> constant1 = Expressions.constant(val1);
                    Expression<Object> constant2 = Expressions.constant(val2);
                    BooleanOperation exp = Expressions.predicate(Ops.BETWEEN, fName, constant1, constant2);
                    expressionList.add(exp);
                }

                ignoreMap.putIfAbsent(fieldName1, Boolean.TRUE);
                ignoreMap.putIfAbsent(fieldName2, Boolean.TRUE);
            }
        }
    }

    private void processCustomFilters(BaseFilterAdvancedDto filterDto, List<BooleanExpression> expressionList, Path<T> p) throws UnknowQueryFilterOperator {
        for (QueryOperation operation : filterDto.getOtherFilters()) {
            Path fName = Expressions.path(operation.getValue1().getClass(), p, operation.getKey());
            Expression<Object> constant1 = null;
            Expression<Object> constant2 = null;
            if (has(operation.getValue1())) {
                constant1 = Expressions.constant(operation.getValue1());
            }
            if (has(operation.getValue2())) {
                constant2 = Expressions.constant(operation.getValue2());
            }
            BooleanOperation exp;
            if (QueryFilterOperator.between.equals(operation.getOperator())) {
                exp = Expressions.predicate(Ops.BETWEEN, fName, constant1, constant2);
            } else {
                exp = Expressions.predicate(operation.ops(), fName, constant1);
            }
            expressionList.add(exp);
        }
    }

}
