package com.djimgou.core.util;

import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;


@Service
public class MessageService {

    public void error(String message) {
        errorMessage(message);
    }

    public void error(String message, Logger log) {
        log.error(message);
        errorMessage(message);
    }

    /**
     * affiche les message d'erreur à l'utilisateur
     * @param message message à afficher
     * @param log log
     */
    public static void errorMessage(String message, Logger log) {
        log.error(message);

    }

    /**
     * affiche les message d'erreur à l'utilisateur
     * @param message message
     */
    public static void errorMessage(String message) {

    }

    /**
     * affiche les message d'erreur à l'utilisateur
     * @param message message à afficher
     * @param keep indique si ce message doit s'ajouter au message précédent. Donc toutes les erreur seront affichées.
     *             Par contre si c'est false, alors seul le dernier message est affiché
     */
    public static void errorMessage(String message, boolean keep) {

    }

    /**
     * affiche les message d'information à l'utilisateur
     * @param message message à afficher
     */
    public void info(String message) {
        infoMessage(message);
    }

    /**
     * affiche les message d'information à l'utilisateur
     * @param message message à afficher
     */
    public static void infoMessage(String message) {

    }

    /**
     * affiche les message d'information à l'utilisateur
     * @param message message à afficher
     * @param keep indique si ce message doit s'ajouter au message précédent. Donc toutes les erreur seront affichées.
     *             Par contre si c'est false, alors seul le dernier message est affiché
     */
    public static void infoMessage(String message, boolean keep) {

    }

    /**
     * affiche les message d'information à l'utilisateur
     * @param message message à afficher
     * @param log logger
     * @param objects liste des objets
     */
    public static void infoMessage(String message, Logger log, Object... objects) {
        log.info(message, objects);
    }

    /**
     * affiche les message d'avertissement à l'utilisateur
     * @param message message à afficher
     */
    public void warn(String message) {
        warnMessage(message);
    }

    /**
     * affiche les message d'avertissement à l'utilisateur
     * @param message message à afficher
     */
    public static void warnMessage(String message) {

    }

    /**
     * affiche les message d'avertissement à l'utilisateur
     * @param message message à afficher
     * @param keep indique si ce message doit s'ajouter au message précédent. Donc toutes les erreur seront affichées.
     *             Par contre si c'est false, alors seul le dernier message est affiché
     */
    public static void warnMessage(String message, boolean keep) {

    }
}
