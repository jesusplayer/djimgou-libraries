package com.act.filestorage.model;

import com.act.core.model.BaseBdEntity;
import com.act.filestorage.service.FileStorage;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.FullTextField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.Indexed;

import javax.persistence.Entity;
import javax.persistence.Transient;

import static com.act.core.util.AppUtils.has;

/**
 * https://medium.com/@ahmedgrati1999/an-easier-way-to-upload-retrieve-images-with-spring-boot-2-0-angular-8-400d1a51dccb
 * https://www.javaguides.net/2018/11/spring-boot-2-file-upload-and-download-rest-api-tutorial.html
 */
@Entity
@Indexed
@NoArgsConstructor
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Fichier extends BaseBdEntity {
    @FullTextField
    String nom;
    String fichier1;
    String fichier2;
    String fichier3;
    String dossier;
    String type;

    @Transient
    public String getFichier1Url() {
        if(!has(fichier1)) return null;
        return FileStorage.downloadUrl(dossier, getFichier1());
    }

    @Transient
    public String getFichier2Url() {
        if(!has(fichier2)) return null;
        return FileStorage.downloadUrl(dossier, getFichier2());
    }

    @Transient
    public String getFichier3Url() {
        if(!has(fichier3)) return null;
        return FileStorage.downloadUrl(dossier, getFichier3());
    }

    public Fichier(String dossier, String type) {
        this.nom = getId().toString();
        this.dossier = dossier;
        this.type = type;
        buildUrl();
    }

    public Fichier(String dossier, String nom, String type) {
        this.nom = nom;
        this.dossier = dossier;
        this.type = type;
        buildUrl();
    }

    public void buildUrl() {
        this.fichier1 = String.format("%s-1-%s"/*, dossier, File.separator*/, getId().toString(), nom);
        this.fichier2 = String.format("%s-2-%s"/*, dossier, File.separator*/, getId().toString(), nom);
        this.fichier3 = String.format("%s-3-%s"/*, dossier, File.separator*/, getId().toString(), nom);
    }

    public String generateUniqUrl() {
        return getId() + "-" + getNom();
    }
}
