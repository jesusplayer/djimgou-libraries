package com.djimgou.filestorage.service;

import com.djimgou.core.exception.AppException;
import com.djimgou.filestorage.exception.FichierInvalidNameException;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import static com.djimgou.core.util.AppUtils2.has;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@Builder
public class LocalFileStorage implements FileStorage {
    public static String ROOT_FOLDER = ("." + File.separator + "filesStore" + File.separator);
    private String rootFolder;
    private String defaultFolder = "store";
    private String dossier;
    private String name;
    private MultipartFile multipartFile;

    @Override
    public String normilizePath(String path) {
        return has(path) ? path.replace("/", File.separator).replace("\\", File.separator) : path;
    }


    public String storePath(String dossier) {
        return rootFolder + (has(dossier) ? dossier : defaultFolder);
    }
/*
    public LocalFileStorage(MultipartFile multipartFile, String dossier, String name) throws AppException {
        this.multipartFile = multipartFile;
        // TODO pour le moment on stocke tous les fichiers dans le meme dossier
        this.dossier = has(dossier) ? dossier : defaultFolder;
        this.name = name;
        creerDossier();
    }*/

    public Path creerDossier() throws AppException {
        return creerDossier(rootFolder + dossier);
    }

    public Path creerDossier(String dossier) throws AppException {
        Path fileStorageLocation = Paths.get(dossier)
                .toAbsolutePath().normalize();
        try {
            if (!Files.exists(fileStorageLocation)) {
                Files.createDirectories(fileStorageLocation);
            }
        } catch (Exception ex) {
            throw new AppException("Could not create the directory where the uploaded files will be stored.", ex);
        }
        return fileStorageLocation;
    }


    public void checkValidFileName(String name) throws FichierInvalidNameException {
        String fileName = StringUtils.cleanPath(name);

        // Check if the file's name contains invalid characters
        if (fileName.contains("..")) {
            throw new FichierInvalidNameException(fileName);
        }
    }

    public void checkValidFileName() throws FichierInvalidNameException {
        checkValidFileName(name);
    }

    public String storeFile(MultipartFile file) throws AppException, FichierInvalidNameException {
        // Normalize file name
        //creerDossier();
        return storeInFolder(file);
    }

    public String storeFile() throws AppException, FichierInvalidNameException {
        // Normalize file name
        //creerDossier();
        return storeFile(multipartFile);
    }

    public String downloadUrl(String folder, String fileName) {
        return ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/fichier/afficherDansDossier/")
                .path(fileName)
                .queryParam("dossier", folder)
                .toUriString();
    }

    public String downloadUrl() {
        return downloadUrl(dossier, name);
    }

    public String dossier() {
        return rootFolder + File.separator + this.dossier + File.separator;
    }

    public String storeInFolder(MultipartFile file) throws AppException, FichierInvalidNameException {
        String dossier = dossier();
        // Normalize file name
        String fileName = has(name) ? name : file.getOriginalFilename();
        checkValidFileName(fileName);

        try {
            // Copy file to the target location (Replacing existing file with the same name)
            Path targetLocation = Paths.get(dossier)
                    .toAbsolutePath().normalize().resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            return fileName;
        } catch (IOException ex) {
            throw new AppException("Impossible d'enregistrer " + fileName + ". SVP essayez encore!", ex);
        }
    }

    public void deleteFile() throws IOException {
        if (has(name)) {
            Path path = Paths.get(dossier())
                    .toAbsolutePath().normalize().resolve(name);

            Files.deleteIfExists(path);
        }
    }

    public Resource loadFileAsResource() throws FileNotFoundException, AppException {
        return loadFileAsResource(dossier(), name);
    }

    public Resource loadFileAsResource(String dossier, String fileName) throws FileNotFoundException, AppException {
        try {
            Path filePath = null;
            if (has(dossier)) {
                filePath = Paths.get(dossier)
                        .toAbsolutePath().normalize().resolve(fileName).normalize();
            } else {
                filePath = Paths.get(fileName);
            }

            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists()) {
                return resource;
            } else {
                throw new FileNotFoundException("Fichier introuvable " + fileName);
            }
        } catch (MalformedURLException | FileNotFoundException ex) {
            throw new FileNotFoundException("Fichier introuvable  " + fileName);
        } catch (Exception e) {
            throw new AppException("Erreur de chargement du fichier " + fileName);
        }
    }
}
