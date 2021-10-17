package com.act.security.repo;

import com.act.security.model.Utilisateur;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * @author DJIMGOU NKENNE DANY MARC 08/2020
 */
@Transactional
@Repository("appDefaultUtilisateurRepo")
public interface UtilisateurRepo extends UtilisateurBaseRepo<Utilisateur, UUID> {

}
