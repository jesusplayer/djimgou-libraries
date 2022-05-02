package com.djimgou.core.service;

import com.djimgou.core.cooldto.exception.DtoChildFieldNotFound;
import com.djimgou.core.cooldto.exception.DtoMappingException;
import com.djimgou.core.cooldto.model.IEntityDetailDto;
import com.djimgou.core.cooldto.model.IEntityDto;
import com.djimgou.core.cooldto.service.DtoSerializerService;
import com.djimgou.core.exception.NotFoundException;
import com.djimgou.core.infra.*;
import com.djimgou.core.repository.BaseJpaRepository;
import com.djimgou.core.util.AppUtils;
import com.djimgou.core.util.model.IBaseEntity;
import com.querydsl.core.support.QueryBase;
import com.querydsl.core.types.*;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.BooleanOperation;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAQueryBase;
import com.querydsl.jpa.impl.JPAQuery;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.util.ReflectionUtils;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.lang.reflect.Field;
import java.util.*;
import java.util.function.Predicate;

import static com.djimgou.core.util.AppUtils.*;


/**
 * Service abstrait de gestion des opérations métiers de la base de données
 *
 * @param <T> parametre
 * @author djimgou
 */

@Log4j2
public abstract class AbstractDomainServiceBaseV2<T extends IBaseEntity, FIND_DTO extends BaseFindDto, FILTER_DTO extends BaseFilterDto, DTO extends IEntityDto, DETAIL_DTO extends IEntityDetailDto, ID>
        extends AbstractDomainServiceBase<T, FIND_DTO, FILTER_DTO, ID> {

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
    public T create(DTO produitDto) throws NotFoundException, DtoMappingException {
        return save(null, produitDto);
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
    public T update(ID id, DTO produitDto) throws NotFoundException, DtoMappingException {
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
    public T save(ID id, DTO entityDto) throws NotFoundException, DtoMappingException {
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

        T saved = persist(id, entity);
        return saved;
    }

    /**
     * Enregiistre simplement une entité
     *
     * @param id
     * @param entity
     * @return
     */
    public final T persist(ID id, T entity) {
        if (this instanceof IBeforeSave) {
            entity = ((IBeforeSave<T, ID>) this).beforeSave(id, entity);
        }
        T saved = save(entity);
        if (this instanceof IAfterSave) {
            saved = ((IAfterSave<T, ID>) this).afterSave(id, saved);
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
        List<Field> dtoIdFields = AppUtils.getFields(
                entityDto.getClass(), fieldFilter.and(f -> Objects.equals(f.getType().getName(), UUID.class.getName())
                        && !Objects.equals("id", f.getName())
                        && f.getName().endsWith("Id")
                )

        );

        for (Field field : dtoIdFields) {
            UUID dtoId = (UUID) AppUtils.getField(field.getName(), entityDto);

            String objName1 = field.getName();
            String objName2 = field.getName().substring(0, field.getName().length() - 2);
            String objName3 = field.getName() + "Ob";

            String choosedKey = objName2;

            boolean childOb1 = AppUtils.hasField(entity.getClass(), objName1);
            boolean childOb2 = AppUtils.hasField(entity.getClass(), objName2);
            boolean childOb3 = AppUtils.hasField(entity.getClass(), objName3);
            // lordre est important
            if (childOb3) {
                choosedKey = objName3;
            } else {
                if (childOb1) {
                    choosedKey = objName1;
                }
            }

            if (childOb1 || childOb2 || childOb3) {
                Object childObj = AppUtils.getField(choosedKey, entity);
                String finalChoosedKey = choosedKey;

                UUID childObId = null;
                if (has(childObj)) {
                    childObId = (UUID) AppUtils.getField("id", childObj);
                }
                final boolean existChildId = has(id) && Objects.equals(childObId, dtoId);
                if (!existChildId) {
                    Field childObjField = AppUtils.getFields(entity.getClass(), field1 ->
                                    Objects.equals(finalChoosedKey, field1.getName()))
                            .stream().findFirst().orElse(null);

                    Class key = childObjField.getType();
                    Object childDbValue = em.find(key, dtoId);
                    if (childDbValue == null) {
                        throw new NotFoundException(key.getSimpleName() + "#" +
                                id + " N'existe pas"
                        );
                    }
                    AppUtils.setField(choosedKey, entity, childDbValue);
                }
            } else {
                throw new DtoChildFieldNotFound(entityDto, entity, field);
            }

        }
    }

    @Override
    public Page<T> findBy(FILTER_DTO filter) throws Exception {
        CustomPageable cpg = new CustomPageable(filter);

        Page<T> page;

        EntityPathBase<T> qQSommier = getQEntity();

        JPAQuery query = new JPAQuery(em);
        JPAQueryBase exp2 = query.from(qQSommier);
        //LuceneQuery query2 = new LuceneQuery();
        List<BooleanExpression> expressionList = new ArrayList<>();

        List<OrderSpecifier> orders = new ArrayList<>();
        List<Field> fiels = getFields(filter.getClass(), field -> true);
        final Class<T> entityClass = getFilterDtoClass(0);
        Path<T> p = Expressions.path(entityClass, entityClass.getSimpleName().toLowerCase());
        for (Field field : fiels) {
            Object ffi = getField(field.getName(), filter);
            if (ffi instanceof IFieldFilter) {
                FieldFilter ff = (FieldFilter) ffi;


                if (has(ff.between)) {
                    Path fName = Expressions.path(ff.between[0].getClass(), p, field.getName());
                    Expression<Object> constant1 = Expressions.constant(ff.between[0]);
                    Expression<Object> constant2 = Expressions.constant(ff.between[1]);
                    BooleanOperation exp = Expressions.predicate(Ops.BETWEEN, fName, constant1, constant2);
                    expressionList.add(exp);
                }
                if (has(ff.eq)) {
                    Path fName = Expressions.path(ff.eq.getClass(), p, field.getName());
                    Expression<Object> constant1 = Expressions.constant(ff.eq);
                    BooleanOperation exp = Expressions.predicate(Ops.EQ, fName, constant1);
                    expressionList.add(exp);
                }
                if (has(ff.lt)) {
                    Path fName = Expressions.path(ff.eq.getClass(), p, field.getName());
                    Expression<Object> constant1 = Expressions.constant(ff.eq);
                    BooleanOperation exp = Expressions.predicate(Ops.LT, fName, constant1);
                    expressionList.add(exp);
                }
                if (has(ff.le)) {
                    Path fName = Expressions.path(ff.le.getClass(), p, field.getName());
                    Expression<Object> constant1 = Expressions.constant(ff.le);
                    BooleanOperation exp = Expressions.predicate(Ops.LOE, fName, constant1);
                    expressionList.add(exp);
                }
                if (has(ff.gt)) {
                    Path fName = Expressions.path(ff.gt.getClass(), p, field.getName());
                    Expression<Object> constant1 = Expressions.constant(ff.gt);
                    BooleanOperation exp = Expressions.predicate(Ops.GT, fName, constant1);
                    expressionList.add(exp);
                }
                if (has(ff.ge)) {
                    Path fName = Expressions.path(ff.ge.getClass(), p, field.getName());
                    Expression<Object> constant1 = Expressions.constant(ff.ge);
                    BooleanOperation exp = Expressions.predicate(Ops.GOE, fName, constant1);
                    expressionList.add(exp);
                }
                if (has(ff.contains)) {
                    Path fName = Expressions.path(ff.contains.getClass(), p, field.getName());
                    Expression<Object> constant1 = Expressions.constant(ff.contains);
                    BooleanOperation exp = Expressions.predicate(Ops.STRING_CONTAINS, fName, constant1);
                    expressionList.add(exp);
                }
                if (has(ff.containsIgnoreCase)) {
                    Path fName = Expressions.path(ff.containsIgnoreCase.getClass(), p, field.getName());
                    Expression<Object> constant1 = Expressions.constant(ff.containsIgnoreCase);
                    BooleanOperation exp = Expressions.predicate(Ops.STRING_CONTAINS_IC, fName, constant1);
                    expressionList.add(exp);
                }
                if (has(ff.order)) {
                    Path path = Expressions.path(Object.class, p, field.getName());
                    OrderSpecifier orderSpecifier = new OrderSpecifier(Order.ASC, path);
                    orders.add(orderSpecifier);
                }

            } else {
                if (has(ffi)) {
                    Path fName = Expressions.path(Objects.requireNonNull(ffi).getClass(), p, field.getName());
                    Expression<Object> constant1 = Expressions.constant(ffi);
                    BooleanOperation exp = Expressions.predicate(Ops.EQ, fName, constant1);
                    expressionList.add(exp);
                }
            }
        }

        BooleanExpression exp = expressionList.stream().reduce(null, (old, newE) -> has(old) ? old.and(newE) : newE);

        final QueryBase exp3 = exp2.where(exp);
        if (has(orders)) {
            exp3.orderBy(orders.toArray(new OrderSpecifier[orders.size()]));
        }

        if (has(exp)) {
            page = getRepo().findAll(exp, cpg);
        } else {
            page = getRepo().findAll(cpg);
        }
        return page;
    }

}
