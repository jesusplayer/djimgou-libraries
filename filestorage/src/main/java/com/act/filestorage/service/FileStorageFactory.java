package com.act.filestorage.service;

import com.act.core.exception.AppException;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.File;

import static com.act.core.util.AppUtils.has;

@Getter
@Setter
@Service
public class FileStorageFactory {
    private String filesStoreDir;
    private String provider;

    private FileStorage instance;

    @Autowired
    public FileStorageFactory(@Value("${filestore.directory:}") String filesStoreDir, @Value("${filestore.provider:}") String provider) {
        this.filesStoreDir = filesStoreDir;
        this.provider = provider;
    }

    /**
     * Est appelée impérativement apres le constructeur
     *
     * @return
     * @throws AppException
     */
    @PostConstruct
    public FileStorage init() throws AppException {
        if ("aws".equals(provider)) {
            instance = new AwsFileStorage();
        } else {
            instance = LocalFileStorage.builder().rootFolder(has(filesStoreDir) ? filesStoreDir.replace("/", File.separator) + File.separator
                    : LocalFileStorage.ROOT_FOLDER).build();
            instance.creerDossier(LocalFileStorage.ROOT_FOLDER);
            return instance;
        }
        return instance;
    }

    public FileStorage newInstance(String dossier, String nom) throws AppException {
        return newInstance(null, dossier, nom);
    }

    public FileStorage newInstance(MultipartFile multipartFile, String dossier, String nom) throws AppException {
        instance.setMultipartFile(multipartFile);
        instance.setDossier(dossier);
        instance.setName(nom);
        instance.creerDossier();
        return instance;
    }

}
