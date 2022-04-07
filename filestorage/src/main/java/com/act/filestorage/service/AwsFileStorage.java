package com.act.filestorage.service;

import com.act.core.exception.AppException;
import com.act.filestorage.exception.FichierInvalidNameException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
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

import static com.act.core.util.AppUtils.has;

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
