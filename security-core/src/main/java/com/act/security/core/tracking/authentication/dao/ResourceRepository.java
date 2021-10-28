package com.act.security.core.tracking.authentication.dao;

import com.act.security.core.UtilisateurDetails;
import com.act.security.core.tracking.dao.BackendServiceProxy;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Log4j2
@Repository("ResourceRepository")
public class ResourceRepository implements ResourceRepositoryInterface {
    @Autowired
    private BackendServiceProxy proxy;

    @Override
    public UtilisateurDetails refreshUser(String username) throws Exception {
        return new UtilisateurDetails(proxy.findByUsername(username));
    }
}
