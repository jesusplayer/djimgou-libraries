package com.act.core.service;

import com.act.core.exception.DtoChildFieldNotFound;
import com.act.core.exception.NotFoundException;
import com.act.core.exception.RepoChildNotFound;
import com.act.core.infra.BaseFilterDto;
import com.act.core.infra.BaseFindDto;
import com.act.core.model.AbstractBaseEntity;
import com.act.core.model.BaseBdEntity;
import com.act.core.model.IEntityDetailDto;
import com.act.core.model.IEntityDto;
import com.act.core.util.AppUtils;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.util.ReflectionUtils;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Stream;

import static com.act.core.util.AppUtils.has;


/**
 * Service abstrait de gestion des opérations métiers de la base de données
 *
 * @param <T> parametre
 * @author djimgou
 */

@Log4j2
public abstract class AbstractDomainServiceV2<T extends BaseBdEntity, FIND_DTO extends BaseFindDto, FILTER_DTO extends BaseFilterDto, DTO extends IEntityDto, DETAIL_DTO extends IEntityDetailDto>

        extends AbstractDomainService<T, FIND_DTO, FILTER_DTO> {

    @Getter
    private Map<Class, JpaRepository<? extends AbstractBaseEntity, UUID>> childrenRepoMap;

    public AbstractDomainServiceV2(JpaRepository<T, UUID> repo, Map<Class, JpaRepository<? extends AbstractBaseEntity, UUID>> childrenRepoMap) {
        super(repo);
        this.childrenRepoMap = childrenRepoMap;
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
     * @ManyToOne() EntiteParent entiteParent;
     * }
     * <p>
     * class EntiteDto{
     * String unChamp;
     * UUID entiteentiteParentId;
     * }
     * @param produitDto
     * @return
     * @throws RepoChildNotFound indique que les repository dépendant n'ont pas été renseignés
     * @throws NotFoundException
     * @throws DtoChildFieldNotFound indique que les id des clés étrangères n'ont pas été renseignés bien écrites.
     * Bien vouloir respecter la convention

     */
    public T create(DTO produitDto) throws RepoChildNotFound, NotFoundException, DtoChildFieldNotFound {
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
     * @ManyToOne() EntiteParent entiteParent;
     * }
     * <p>
     * class EntiteDto{
     * String unChamp;
     * UUID entiteentiteParentId;
     * }
     *
     * @param produitDto
     * @return
     * @throws RepoChildNotFound indique que les repository dépendant n'ont pas été renseignés
     * @throws NotFoundException
     * @throws DtoChildFieldNotFound indique que les id des clés étrangères n'ont pas été renseignés bien écrites.
     * Bien vouloir respecter la convention
     */
    public T update(UUID id, DTO produitDto) throws RepoChildNotFound, NotFoundException, DtoChildFieldNotFound {
        return save(id, produitDto);
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
     * @ManyToOne() EntiteParent entiteParent;
     * }
     * <p>
     * class EntiteDto{
     * String unChamp;
     * UUID entiteentiteParentId;
     * }
     *
     * @param id
     * @param produitDto
     * @throws RepoChildNotFound indique que les repository dépendant n'ont pas été renseignés
     * @throws NotFoundException
     * @throws DtoChildFieldNotFound indique que les id des clés étrangères n'ont pas été renseignés bien écrites.
     * Bien vouloir respecter la convention
     */
    @Transactional(/*propagation = Propagation.NESTED*/)
    public T save(UUID id, DTO produitDto) throws NotFoundException, DtoChildFieldNotFound, RepoChildNotFound {
        T entity = ReflectionUtils.createInstanceIfPresent(getFilterDtoClass(0).getName(), null);
        if (has(id)) {
            entity = getRepo().findById(id).orElseThrow(() ->
                    new NotFoundException(getFilterDtoClass(0).getSimpleName() + "#" + id + " N'existe pas")
            );
        }

        entity.fromDto(produitDto);

        List<Field> dtoIdFields = AppUtils.getFields(
                produitDto.getClass(),
                f -> Objects.equals(f.getType().getName(), UUID.class.getName())
                        && !Objects.equals("id", f.getName())
                        && f.getName().endsWith("Id")
        );

        for (Field field : dtoIdFields) {
            UUID dtoId = (UUID) AppUtils.getField(field.getName(), produitDto);

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
                    if (has(getChildrenRepoMap()) && getChildrenRepoMap().containsKey(key)) {
                        JpaRepository<? extends AbstractBaseEntity, UUID> repository = getChildrenRepoMap().get(key);

                        Object childDbValue = repository.findById(dtoId).orElseThrow(() ->
                                new NotFoundException(childObj.getClass().getSimpleName() + "#" +
                                        id + " N'existe pas"
                                )
                        );

                        AppUtils.setField(choosedKey, entity, childDbValue);
                    } else {
                        throw new RepoChildNotFound(produitDto, entity, field);
                    }

                }
            } else {
                throw new DtoChildFieldNotFound(produitDto, entity, field);
            }

        }

        final T saved = save(entity);
        return saved;
    }

    @Override
    public abstract Page<T> findBy(FILTER_DTO baseFilter) throws Exception;


}
