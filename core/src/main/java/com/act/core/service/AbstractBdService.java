package com.act.core.service;

import com.act.core.exception.AppException;
import com.act.core.exception.NotFoundException;
import com.act.core.infra.Filter;
import com.act.core.model.AbstractBaseEntity;
import com.act.core.util.AppUtils;
import com.act.core.util.MessageService;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.Serializable;
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
public abstract class AbstractBdService<T extends AbstractBaseEntity> implements ISharedService<T>, Serializable {
    @PersistenceContext
    EntityManager em;

    @Autowired
    AppUtils appUtils;


    /**
     * Permet d'enregister la premiere page chargée de requetes pour eviter le
     * bug de primeface qui fait deux requetes pour un seul clique
     */
    @Getter
    @Setter
    Page<T> pageFetched = null;

    public AbstractBdService() {
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
        return getRepo().getOne(item.getId());
    }

    public T searchById(UUID id) {
        return getRepo().findById(id).get();
    }

    public Optional<T> findById(UUID id) throws NotFoundException {
        return getRepo().findById(id);
    }

    public Optional<T> findById(String id) throws NotFoundException {
        return findById(UUID.fromString(id));
    }

    public T searchById(String id) {
        if (existsById(id)) {
            return getRepo().findById(fromIdString(id)).orElse(null);
        }
        return null;
    }

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

    public boolean existsById(String id) {
        return getRepo().existsById(fromIdString(id));
    }

    public boolean existsById(UUID id) {
        return getRepo().existsById(id);
    }

    public T save(T entity) {
        T item = null;
        try {
            item = getRepo().save(entity);
        } catch (Exception e) {
            if (AppUtils.has(entity)) {
                e.printStackTrace();
                MessageService.errorMessage("Erreur d'enregistrement de l'objet " + e.getMessage(), log);
            }else {
                e.printStackTrace();
            }
        }
        return item;
    }

    @Async
    public CompletableFuture<T> saveAsync(T entity) {
        return CompletableFuture.supplyAsync(() -> save(entity));
    }

    @Transactional
    public T save(T entity, T oldEntity) {
        T item = null;
        try {
            item = getRepo().save(entity);
        } catch (Exception e) {
            MessageService.errorMessage("Erreur d'enregistrement de l'objet " + e.getMessage(), log);
        }
        return item;
    }

    @Transactional
    public void delete(T entity) throws NotFoundException {
        try {
            if (!getRepo().existsById(entity.getId())) {
                throw new NotFoundException("Elément à supprimer inexistant");
            }
            getRepo().deleteById(entity.getId());
        } catch (Exception e) {
            throw e;
        }
    }

    public void deleteById(UUID id) throws NotFoundException, AppException {
        try {
            if (!getRepo().existsById(id)) {
                throw new NotFoundException("Elément à supprimer inexistant");
            }
            getRepo().deleteById(id);
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

    public abstract JpaRepository<T, UUID> getRepo();

    public UUID fromIdString(String id) {
        return id == null ? null : UUID.fromString(id);
    }

    @Transactional
    public CompletableFuture<List<T>> saveAllAsync(List<T> entities) {
        return CompletableFuture.supplyAsync(() -> entities.stream().map(this::save).collect(Collectors.toList()));
    }
}
