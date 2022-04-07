package com.act.reporting.model;

import com.act.audit.model.EntityListener;
import com.act.core.model.BaseBdEntity;
import com.act.filestorage.model.Fichier;
import com.act.filestorage.service.FileStorage;
import lombok.Data;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.FullTextField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.Indexed;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Transient;

import static com.act.core.util.AppUtils.has;

/**
 * https://medium.com/@ahmedgrati1999/an-easier-way-to-upload-retrieve-images-with-spring-boot-2-0-angular-8-400d1a51dccb
 * https://www.javaguides.net/2018/11/spring-boot-2-file-upload-and-download-rest-api-tutorial.html
 */
@Data
@Entity
@Indexed
//@FieldDefaults(level = AccessLevel.PRIVATE)
@EntityListeners(EntityListener.class)
public class Report extends BaseBdEntity {

    @FullTextField
    String nom;

    @FullTextField
    String nomReport;
    String fichier1;
    String fichier2;
    String dossier;
    String type;
    // Url d'accès du report dans l'application cliente
    String clientRouteUrl;

    Integer position;
    public static final String FOLDER = "Reports";

    public Report() {
    }

    @Transient
    public String getFichier1Url() {
        if (!has(fichier1)) return null;
        return Fichier.fs.downloadUrl(dossier, getFichier1());
    }

    @Transient
    public String getFichier2Url() {
        if (!has(fichier2)) return null;
        return Fichier.fs.downloadUrl(dossier, getFichier2());
    }

    public Report(String dossier, String type) {
        this.nom = getId().toString();
        this.dossier = dossier;
        this.type = type;
        buildUrl();
    }

    public Report(String dossier, String nom, String type) {
        this.nom = nom;
        this.dossier = dossier;
        this.type = type;
        buildUrl();
    }

    public void buildUrl() {
        this.fichier1 = String.format("%s-1-%s"/*, dossier, File.separator*/, getId().toString(), nom);
        this.fichier2 = String.format("%s-2-%s"/*, dossier, File.separator*/, getId().toString(), nom);
    }

    public String generateUniqUrl() {
        return getId() + "-" + getNom();
    }
}
