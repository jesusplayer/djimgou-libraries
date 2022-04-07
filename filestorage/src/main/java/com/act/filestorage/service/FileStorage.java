package com.act.filestorage.service;

import com.act.core.exception.AppException;
import com.act.filestorage.exception.FichierInvalidNameException;
import org.springframework.core.io.Resource;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;


public interface FileStorage {


    String getRootFolder();
    void setRootFolder(String rootFolder);

    String getDefaultFolder();
    void setDefaultFolder(String defaultFaolder);

    String getDossier();
    void setDossier(String dossier);

    String getName();
    void setName(String name);

    MultipartFile getMultipartFile();

    void setMultipartFile(MultipartFile multipartFile);

    String storePath(String dossier);


    Path creerDossier() throws AppException;

    Path creerDossier(String dossier) throws AppException;


    default void checkValidFileName(String name) throws FichierInvalidNameException {
        String fileName = StringUtils.cleanPath(name);

        // Check if the file's name contains invalid characters
        if (fileName.contains("..")) {
            throw new FichierInvalidNameException(fileName);
        }
    }

    default void checkValidFileName() throws FichierInvalidNameException {
        checkValidFileName(getName());
    }

    default String storeFile(MultipartFile file) throws AppException, FichierInvalidNameException {
        // Normalize file name
        //creerDossier();
        return storeInFolder(file);
    }

    default String storeFile() throws AppException, FichierInvalidNameException {
        // Normalize file name
        //creerDossier();
        return storeFile(getMultipartFile());
    }

    String downloadUrl(String folder, String fileName);

    String downloadUrl();

    String dossier();

    String storeInFolder(MultipartFile file) throws AppException, FichierInvalidNameException;

    void deleteFile() throws IOException;


    Resource loadFileAsResource() throws FileNotFoundException, AppException;

    Resource loadFileAsResource(String dossier, String fileName) throws FileNotFoundException, AppException;
}
