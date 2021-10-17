package com.act.core.service;

/**
 * Interface permettant de lancer le cycle de vie d'initialisation d'un Module
 * il est semblable à l'annotation @PostContruct
 */
public interface IOnInit {
    void onInit();
}
