package com.djimgou.filestorage.service;

import com.djimgou.core.exception.AppException;
import com.djimgou.filestorage.exception.FichierInvalidNameException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;

import static com.djimgou.core.util.AppUtils.has;

@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@Getter@Setter
public class AwsFileStorage implements FileStorage {
    private String rootFolder;
    private String defaultFolder = "store";
    private String dossier;
    private String name;
    private MultipartFile multipartFile;
/*
    public AwsFileStorage(MultipartFile multipartFile, String dossier, String name ) {
        this.dossier = dossier;
        this.name = name;
        this.multipartFile = multipartFile;
    }*/

    @Override
    public String normilizePath(String path) {
        return null;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public MultipartFile getMultipartFile() {
        return null;
    }

    @Override
    public String storePath(String dossier) {
        return null;
    }

    @Override
    public Path creerDossier() throws AppException {
        return null;
    }

    @Override
    public Path creerDossier(String dossier) throws AppException {
        return null;
    }

    @Override
    public String downloadUrl(String folder, String fileName) {
        return null;
    }

    @Override
    public String downloadUrl() {
        return null;
    }

    @Override
    public String dossier() {
        return null;
    }

    @Override
    public String storeInFolder(MultipartFile file) throws AppException, FichierInvalidNameException {
        return null;
    }

    @Override
    public void deleteFile() throws IOException {

    }

    @Override
    public Resource loadFileAsResource() throws FileNotFoundException, AppException {
        return null;
    }

    @Override
    public Resource loadFileAsResource(String dossier, String fileName) throws FileNotFoundException, AppException {
        return null;
    }
}
