package com.djimgou.security.core;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * @author Djimgou
 * Interface implémenté par chaque applications qui utilise le module de sécurité
 */
public interface AppSecurityConfig {
    boolean permitAll();

    /**
     * permet d'obtenir toutes les URL autorisées
     *
     * @return
     */
    List<String> authorizedUrls();

    /**
     * Permet 'obtenir le package de la classe métier dans laquelle
     * set trouve les entités métier de l'application
     * Ceci permettra de générer automatiquement les privilèges par
     * défaut de chaque com.djimgou.audit.service
     *
     * @return
     */
    String businessEntityPackage();

    /**
     * Si on souhaite configuger la sécurité de manière plus personnelle
     *
     * @param http
     * @throws Exception
     */
    void configure(HttpSecurity http) throws Exception;

    /**
     * Fonction qui teste si une Url Match
     *
     * @param authorizedUrls
     * @param url
     * @return
     */
    default boolean match(List<String> authorizedUrls, String url) {

        Optional<String> opt = Stream.concat(authorizedUrls.stream(), authorizedUrls().stream())
                .filter(u -> {
                    if (u.endsWith("/**")) {
                        String newUrl = u.substring(0, u.length() - 2);
                        return url.startsWith(newUrl);
                    }
                    return url.equals(u);
                }).findFirst();
        return opt.isPresent();
    }
}
