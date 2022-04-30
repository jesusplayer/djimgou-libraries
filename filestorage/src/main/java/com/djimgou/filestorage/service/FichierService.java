package com.djimgou.filestorage.service;

import com.djimgou.core.exception.AppException;
import com.djimgou.core.exception.BadRequestException;
import com.djimgou.filestorage.exception.FichierInvalidNameException;
import com.djimgou.filestorage.exception.FichierNotFoundException;
import com.djimgou.filestorage.model.Fichier;
import com.djimgou.filestorage.repository.FichierRepo;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

/**
 * @author DJIMGOU NKENNNE
 * Manipule les fichiers quels que soit le provider: local ou Amzo
 */
@Log4j2
@Getter
@Service
public class FichierService {

    private FichierRepo repo;

    private FileStorageFactory fsFactory;

    /**
     * local
     * aws
     */

    public FichierService(FichierRepo repo, FileStorageFactory fsFactory) throws AppException {
        this.repo = repo;
        this.fsFactory = fsFactory;
        Fichier.fs = this.fsFactory.getInstance();
    }

    @Transactional
    public Fichier save(MultipartFile multipartFile, Fichier fichier) throws AppException, FichierInvalidNameException {
        FileStorage fsService = fsFactory.newInstance(multipartFile, fichier.getDossier(), fichier.getNom());
        String url = fsService.storeFile();
        fichier.setFichier1(url);
        fichier.setDossier(fsService.getDossier());
        return repo.save(fichier);
    }

    @Transactional
    public Fichier save3Files(MultipartFile[] multipartFiles, Fichier fichier) throws AppException, BadRequestException, FichierInvalidNameException {
        if (multipartFiles.length > 3) {
            throw new BadRequestException("Impossible d'enregistrer plus de 3 fichiers dans ce com.djimgou.audit.service");
        }
        if (multipartFiles.length > 0) {
            FileStorage fsService = fsFactory.newInstance(multipartFiles[0], fichier.getDossier(), fichier.getFichier1());
            String url = fsService.storeFile();
            fichier.setFichier1(url);
            fichier.setDossier(fsService.getDossier());
        }
        if (multipartFiles.length > 1) {
            FileStorage fsService = fsFactory.newInstance(multipartFiles[1], fichier.getDossier(), fichier.getFichier2());
            String url = fsService.storeFile();
            fichier.setFichier2(url);
            fichier.setDossier(fsService.getDossier());
        } else {
            fichier.setFichier2(null);
            fichier.setFichier3(null);
        }
        if (multipartFiles.length > 2) {
            FileStorage fsService = fsFactory.newInstance(multipartFiles[2], fichier.getDossier(), fichier.getFichier3());
            String url = fsService.storeFile();
            fichier.setFichier3(url);
            fichier.setDossier(fsService.getDossier());
        } else {
            fichier.setFichier3(null);
        }
        return repo.save(fichier);
    }

    public Fichier findbyId(UUID uuid) throws FichierNotFoundException {
        return repo.findById(uuid).orElseThrow(FichierNotFoundException::new);
    }

    @Transactional
    public Page<Fichier> findAll(Pageable pageable) {
        return repo.findAll(pageable);
    }

    @Transactional
    public void deleteById(UUID uuid) throws FichierNotFoundException, AppException, IOException {
        Fichier fichier = findbyId(uuid);
        deleteFileInLocalStorage(fichier);
        repo.deleteById(uuid);
    }

    public void deleteFileInLocalStorage(Fichier fichier) throws AppException, IOException {
        FileStorage fs = fsFactory.newInstance(fichier.getDossier(), fichier.getFichier1());
        fs.deleteFile();
        fs = fsFactory.newInstance(fichier.getDossier(), fichier.getFichier2());
        fs.deleteFile();
        fs = fsFactory.newInstance(fichier.getDossier(), fichier.getFichier3());
        fs.deleteFile();
    }

    @Transactional
    public void deleteByCustomData(String customData) throws FichierNotFoundException, AppException, IOException {
        List<Fichier> fichiers = repo.findByCustomData(customData);
        for (Fichier fichier : fichiers) {
            deleteById(fichier.getId());
        }
    }

    public void deleteAll() throws Exception {
        for (Fichier fichier : findAll(Pageable.unpaged()).getContent()) {
            UUID id = fichier.getId();
            deleteById(id);
        }
    }

}
