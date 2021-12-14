package com.act.security.core.service;

import com.act.core.exception.ConflitException;
import com.act.core.exception.NotFoundException;
import com.act.security.core.exceptions.BadConfirmPasswordException;
import com.act.security.core.exceptions.UnautorizedException;
import com.act.security.core.exceptions.UtilisateurNotFoundException;
import com.act.security.core.model.Utilisateur;
import com.act.security.core.model.dto.utilisateur.ModifierProfilDto;
import com.act.security.core.model.dto.utilisateur.UtilisateurDto;
import com.act.security.core.model.dto.utilisateur.UtilisateurFilterDto;
import com.act.security.core.model.dto.utilisateur.UtilisateurFindDto;
import com.act.security.core.repo.UtilisateurBaseRepo;
import com.act.tenantmanager.exceptions.TenantNotFoundException;
import org.hibernate.search.engine.search.query.SearchResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Cette interface permet de personnaliser toute la gestion des utilisateur
 *
 * @param <U>
 * @param <D>
 * @param <F>
 * @param <T>
 * @param <M>
 */
public interface UtilisateurBdServiceBase<U extends Utilisateur, D extends UtilisateurFindDto, F extends UtilisateurFilterDto, T extends UtilisateurDto, M extends ModifierProfilDto> {
    @Transactional
    U saveUtilisateur(UUID id, T dto) throws NotFoundException, BadConfirmPasswordException, ConflitException;

    Page<U> findBy(F filter) throws Exception;

    void checkDuplicate(UUID id, M dto) throws ConflitException;

    U addTenant(UUID utilisateurId, UUID tenantId) throws UtilisateurNotFoundException, TenantNotFoundException;

    U createUtilisateur(T utilisateurDto) throws BadConfirmPasswordException, ConflitException, NotFoundException;

    U createCompteUtilisateur(T utilisateurDto) throws BadConfirmPasswordException, ConflitException, NotFoundException;

    UtilisateurBaseRepo<U, UUID> getRepo();

    Optional<U> findById(UUID id) throws NotFoundException;

    SearchResult<U> search(D utilisateurFindDto);

    Page<U> searchPageable2(D filter) throws Exception;

    Page<U> searchPageable(D utilisateurFindDto);

    List<U> findAll();

    Page<U> findAll(Pageable pageable);

    U modifierProfil(M utilisateurDto) throws NotFoundException, ConflitException, UnautorizedException;

    void activer(UUID utilisateurId);

    void desactiver(UUID utilisateurId);
}
