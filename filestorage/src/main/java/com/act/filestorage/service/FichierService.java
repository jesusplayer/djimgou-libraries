package com.act.filestorage.service;

import com.act.core.exception.AppException;
import com.act.core.exception.BadRequestException;
import com.act.filestorage.exception.FichierInvalidNameException;
import com.act.filestorage.exception.FichierNotFoundException;
import com.act.filestorage.model.Fichier;
import com.act.filestorage.repository.FichierRepo;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.UUID;

import static com.act.core.util.AppUtils.has;


@Getter
@Service
public class FichierService {

    private FichierRepo repo;


    private String filesStoreDir;

    public FichierService(FichierRepo repo, @Value("${filestore.directory}") String filesStoreDir) {
        this.repo = repo;
        this.filesStoreDir = filesStoreDir;
    }

    @PostConstruct
    void init() throws Exception {
        FileStorage.ROOT_FOLDER = has(filesStoreDir)?filesStoreDir:FileStorage.ROOT_FOLDER;
        FileStorage.creerDossier(FileStorage.ROOT_FOLDER);
    }

    @Transactional
    public Fichier save(MultipartFile multipartFile, Fichier fichier) throws AppException, FichierInvalidNameException {
        FileStorage fsService = new FileStorage(multipartFile, fichier.getDossier(), fichier.getNom());
        String url = fsService.storeFile();
        fichier.setFichier1(url);
        fichier.setDossier(fsService.getDossier());
        return repo.save(fichier);
    }

    @Transactional
    public Fichier save3Files(MultipartFile[] multipartFiles, Fichier fichier) throws AppException, BadRequestException, FichierInvalidNameException {
        if (multipartFiles.length > 3) {
            throw new BadRequestException("Impossible d'enregistrer plus de 3 fichiers dans ce com.act.audit.service");
        }
        if (multipartFiles.length > 0) {
            FileStorage fsService = new FileStorage(multipartFiles[0], fichier.getDossier(), fichier.getFichier1());
            String url = fsService.storeFile();
            fichier.setFichier1(url);
            fichier.setDossier(fsService.getDossier());
        }
        if (multipartFiles.length > 1) {
            FileStorage fsService = new FileStorage(multipartFiles[1], fichier.getDossier(), fichier.getFichier2());
            String url = fsService.storeFile();
            fichier.setFichier2(url);
            fichier.setDossier(fsService.getDossier());
        } else {
            fichier.setFichier2(null);
            fichier.setFichier3(null);
        }
        if (multipartFiles.length > 2) {
            FileStorage fsService = new FileStorage(multipartFiles[2], fichier.getDossier(), fichier.getFichier3());
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

    public Page<Fichier> findAll(Pageable pageable) {
        return repo.findAll(pageable);
    }

    @Transactional
    public void deleteById(UUID uuid) throws FichierNotFoundException, AppException, IOException {
        Fichier fichier = findbyId(uuid);
        FileStorage fs = new FileStorage(fichier.getDossier(), fichier.getFichier1());
        fs.deleteFile();
        fs = new FileStorage(fichier.getDossier(), fichier.getFichier2());
        fs.deleteFile();
        fs = new FileStorage(fichier.getDossier(), fichier.getFichier3());
        fs.deleteFile();
        repo.deleteById(uuid);
    }

    public void deleteAll() throws Exception {
        for (Fichier fichier : findAll(Pageable.unpaged()).getContent()) {
            UUID id = fichier.getId();
            deleteById(id);
        }
    }

}
