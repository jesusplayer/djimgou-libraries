package com.djimgou.security.core.tracking.authentication.dao;

import com.djimgou.security.core.UtilisateurDetails;

public interface ResourceRepositoryInterface {
    UtilisateurDetails refreshUser(String username) throws Exception;
}