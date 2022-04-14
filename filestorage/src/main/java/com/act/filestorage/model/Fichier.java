package com.act.filestorage.model;

import com.act.core.model.BaseBdEntity;
import com.act.filestorage.service.FileStorage;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.FullTextField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.Indexed;
import org.springframework.web.multipart.MultipartFile;

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
    /**
     * Cette variable est fournie pour permettre le téléchargement facile du fichier
     * Quand bien meme on change de serveur. Elle est initialisée lors de la création
     * du service de fichier, dans le constructeur FichierService
     */
    public static FileStorage fs;

    @FullTextField
    String nom;
    String fichier1;
    String fichier2;
    String fichier3;
    String dossier;
    String type;
    String customData;

    @JsonIgnore
    @Transient
    MultipartFile[] multipartFiles;

    /**
     * Utile pour des traitements hors servlet comme les test
     * On a besoin de l'adresse du fichier sans avoir besoin de requête
     */
    /*@Transient
    String f1OldUrl;*/

    /**
     * Utile pour des traitements hors servlet comme les test
     * On a besoin de l'adresse du fichier sans avoir besoin de requête
     */
  /*  @Transient
    String f2Url;
*/
    /**
     * Utile pour des traitements hors servlet comme les test
     * On a besoin de l'adresse du fichier sans avoir besoin de requête
     */
 /*   @Transient
    String f3Url;*/

    /**
     * Chemin complet d'accès au fichier1. Il est généré à la demande
     *
     * @return
     */
    @Transient
    public String getFichier1Url() {
        if (!has(fichier1)) return null;
        String f1 = fs.downloadUrl(dossier, getFichier1());
       /* if (!has(f1OldUrl)) {
            setF1OldUrl(f1);
        }*/
        return f1;
    }

    /**
     * Chemin complet d'accès au fichier2. Il est généré à la demande
     *
     * @return
     */
    @Transient
    public String getFichier2Url() {
        if (!has(fichier2)) return null;
        String f2 = fs.downloadUrl(dossier, getFichier2());
       /* if (!has(f2Url)) {
            setF1OldUrl(f2);
        }*/
        return f2;
    }

    /**
     * Chemin complet d'accès au fichier3. Il est généré à la demande
     *
     * @return
     */
    @Transient
    public String getFichier3Url() {
        if (!has(fichier3)) return null;
        String f3 = fs.downloadUrl(dossier, getFichier3());
      /*  if (!has(f3Url)) {
            setF1OldUrl(f3);
        }*/
        return f3;
    }

    public Fichier(String dossier, String type, MultipartFile[] multipartFiles) {
        this.nom = getId().toString();
        this.dossier = dossier;
        this.type = type;
        this.multipartFiles = multipartFiles;
        buildUrl();
    }

    public Fichier(String dossier, String nom, String type, MultipartFile[] multipartFiles) {
        this.dossier = dossier;
        this.nom = nom;
        this.type = type;
        this.multipartFiles = multipartFiles;
        buildUrl();
    }

    public Fichier(String dossier, String nom, String type, String customData, MultipartFile[] multipartFiles) {
        this(dossier, nom, type, multipartFiles);
        this.customData = customData;
    }

    public void buildUrl() {
        String nom1 = this.nom;
        String nom2 = this.nom;
        String nom3 = this.nom;
        if (has(multipartFiles)) {
            if (multipartFiles.length > 0) {
                nom1 = multipartFiles[0].getOriginalFilename();
            }
            if (multipartFiles.length > 1) {
                nom1 = multipartFiles[1].getOriginalFilename();
            }
            if (multipartFiles.length > 2) {
                nom3 = multipartFiles[2].getOriginalFilename();
            }
        }
        this.fichier1 = String.format("%s-1-%s"/*, dossier, File.separator*/, getId().toString(), nom1);
        this.fichier2 = String.format("%s-2-%s"/*, dossier, File.separator*/, getId().toString(), nom2);
        this.fichier3 = String.format("%s-3-%s"/*, dossier, File.separator*/, getId().toString(), nom3);
    }

    public String generateUniqUrl() {
        return getId() + "-" + getNom();
    }
}
