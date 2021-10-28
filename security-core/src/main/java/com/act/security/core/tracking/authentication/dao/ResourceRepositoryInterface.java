package com.act.security.core.tracking.authentication.dao;

import com.act.security.core.UtilisateurDetails;

public interface ResourceRepositoryInterface {
    UtilisateurDetails refreshUser(String username) throws Exception;
}