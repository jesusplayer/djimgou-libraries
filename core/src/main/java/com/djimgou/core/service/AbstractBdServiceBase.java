package com.djimgou.core.service;

//import com.djimgou.core.coolvalidation.exception.CoolValidationException;
import com.djimgou.core.coolvalidation.exception.CoolValidationException;
import com.djimgou.core.coolvalidation.processors.ValidationParser;
//import com.djimgou.core.coolvalidation.processors.ValidationParserImpl;
import com.djimgou.core.exception.AppException;
import com.djimgou.core.exception.NotFoundException;
import com.djimgou.core.infra.Filter;
import com.djimgou.core.util.AppUtils2;
import com.djimgou.core.util.MessageService;
import com.djimgou.core.util.model.IBaseEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.Serializable;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * Service abstrait de gestion des opérations métiers de la base de données
 *
 * @param <T> parametre
 * @author djimgou
 */

@Log4j2
public abstract class AbstractBdServiceBase<T extends IBaseEntity, ID> implements ISharedServiceBase<T>, Serializable {
    @PersistenceContext
    EntityManager em;

    @Getter
    private JpaRepository<T, ID> repo;

    private ValidationParser validationParser;

    /**
     * Permet d'enregister la premiere page chargée de requetes pour eviter le
     * bug de primeface qui fait deux requetes pour un seul clique
     */
    @Getter
    @Setter
    Page<T> pageFetched = null;

    public AbstractBdServiceBase() {
    }

    public AbstractBdServiceBase(JpaRepository<T, ID> repo) {
        this.repo = repo;
    }

    /**
     * Permet de savoir si bloomberg est disponible
     * on suppose que la BD est toujours dispo
     */
    public boolean isOnline() {
        return true;
    }

    /**
     * Fonction de recherche des éléments. Elle fait une recherche globale
     * dans la table en fonction de la requête définie par le developpeur
     *
     * @param filter Texte à rechercher
     * @param pg     Objet Pageable pour faire la requête de la page. voir PageRequest
     * @return la page contenant les resultats
     * @throws Exception exception
     */
    public abstract Page<T> searchBy(Filter<T> filter, Pageable pg) throws Exception;

    /**
     * @param item item à traiter
     * @return le nouvel item avec les données recupérées
     */
    public T get(T item) {
        // System.err.println("A Implementer par Dany");
        return getRepo().getOne((ID) item.getId());
    }

    public T searchById(ID id) {
        return getRepo().findById(id).get();
    }

    public Optional<T> findById(ID id) throws NotFoundException {
        return getRepo().findById(id);
    }

 /*   public Optional<T> findById(String id) throws NotFoundException {
        return findById(UUID.fromString(id));
    }*/

   /* public T searchById(String id) {
        if (existsById(id)) {
            return getRepo().findById(fromIdString(id)).orElse(null);
        }
        return null;
    }*/

    public List<T> findAll() {
        return getRepo().findAll();
    }

    public Page<T> findAll(Pageable pageable) {
        return getRepo().findAll(pageable);
    }

    public long count() {
        return getRepo().count();
    }

    public boolean isEmpty() {
        Pageable pg = PageRequest.of(0, 1);
        Page<T> page = getRepo().findAll(pg);
        return !page.hasContent();
    }

/*    public boolean existsById(String id) {
        return getRepo().existsById(fromIdString(id));
    }*/

    public boolean existsById(ID id) {
        return getRepo().existsById(id);
    }

    public T save(T entity) throws AppException {
        T item = null;
        try {

            /*if (validationParser == null && em != null) {
                validationParser = new ValidationParserImpl(em);
            }
            if (validationParser != null) {
                validationParser.validate(entity);
            }*/
            item = getRepo().save(entity);
        } catch (Throwable e) {
            if (AppUtils2.has(entity)) {
                MessageService.errorMessage("Erreur d'enregistrement de l'objet " + e.getMessage(), log);
            }
            String message;
            if (e instanceof UndeclaredThrowableException) {// généralement les exceptions qui viennent de l'AOP
                message = ((UndeclaredThrowableException) e).getUndeclaredThrowable().getMessage();
            } else {
                message = e.getMessage();
                if (!(e instanceof CoolValidationException)) {
                    e.printStackTrace();
                }
            }
            throw new AppException(message);
        }
        return item;
    }

    @Async
    public CompletableFuture<T> saveAsync(T entity) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return save(entity);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    ///@Transactional
    public T save(T entity, T oldEntity) throws AppException {
        T item = null;
        try {
            item = getRepo().save(entity);
        } catch (Exception e) {
            MessageService.errorMessage("Erreur d'enregistrement de l'objet " + e.getMessage(), log);
            throw new AppException(e);
        }
        return item;
    }

    @Transactional
    public void delete(T entity) throws NotFoundException {
        try {
            if (!getRepo().existsById((ID) entity.getId())) {
                throw new NotFoundException("Elément à supprimer inexistant");
            }
            getRepo().delete(entity);
        } catch (Exception e) {
            throw e;
        }
    }

    public void deleteById(ID id) throws NotFoundException, AppException {
        try {
            if (!getRepo().existsById(id)) {
                throw new NotFoundException("Elément à supprimer inexistant");
            }
            T ent = getRepo().getById(id);
            getRepo().delete(ent);
        } catch (Exception e) {
            throw e;
        }
    }

    @Transactional
    public void refresh(T entity) {
        em.refresh(entity);
    }

    @Transactional
    public CompletableFuture<T> refreshAsync(T entity) {
        return CompletableFuture.supplyAsync(() -> {
            em.refresh(entity);
            return entity;
        });
    }

    //public abstract JpaRepository<T, UUID> getRepo();

    public UUID fromIdString(String id) {
        return id == null ? null : UUID.fromString(id);
    }

    @Transactional
    public CompletableFuture<List<T>> saveAllAsync(List<T> entities) {
        return CompletableFuture.supplyAsync(() -> entities.stream().map(t -> {
            try {
                return this.save(t);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }).collect(Collectors.toList()));
    }
}
