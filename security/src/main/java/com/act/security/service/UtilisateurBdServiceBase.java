package com.act.security.service;

import com.act.core.exception.ConflitException;
import com.act.core.exception.NotFoundException;
import com.act.security.exceptions.BadConfirmPasswordException;
import com.act.security.exceptions.UnautorizedException;
import com.act.security.exceptions.UtilisateurNotFoundException;
import com.act.security.model.Utilisateur;
import com.act.security.model.dto.utilisateur.ModifierProfilDto;
import com.act.security.model.dto.utilisateur.UtilisateurDto;
import com.act.security.model.dto.utilisateur.UtilisateurFilterDto;
import com.act.security.model.dto.utilisateur.UtilisateurFindDto;
import com.act.security.repo.UtilisateurBaseRepo;
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
    Utilisateur saveUtilisateur(UUID id, T dto) throws UtilisateurNotFoundException, BadConfirmPasswordException, ConflitException;

    Page<Utilisateur> findBy(F filter) throws Exception;

    void checkDuplicate(UUID id, M dto) throws ConflitException;

    Utilisateur addTenant(UUID utilisateurId, UUID tenantId) throws UtilisateurNotFoundException, TenantNotFoundException, TenantNotFoundException;

    Utilisateur createUtilisateur(T utilisateurDto) throws BadConfirmPasswordException, ConflitException, UtilisateurNotFoundException;

    Utilisateur createCompteUtilisateur(T utilisateurDto) throws BadConfirmPasswordException, ConflitException, UtilisateurNotFoundException;

    UtilisateurBaseRepo<U, UUID> getRepo();

    Optional<U> findById(UUID id) throws NotFoundException;

    SearchResult<U> search(UtilisateurFindDto utilisateurFindDto);

    Page<Utilisateur> searchPageable2(UtilisateurFindDto filter) throws Exception;

    Page<U> searchPageable(UtilisateurFindDto utilisateurFindDto);

    List<U> findAll();

    Page<U> findAll(Pageable pageable);

    U modifierProfil(M utilisateurDto) throws UtilisateurNotFoundException, ConflitException, UnautorizedException;

    void activer(UUID utilisateurId);

    void desactiver(UUID utilisateurId);
}
