package com.act.security.tracking.authentication.dao;

import com.act.security.UtilisateurDetails;

public interface ResourceRepositoryInterface {
    UtilisateurDetails refreshUser(String username) throws Exception;
}