package com.act.security.core.tracking.dao;

import com.act.security.core.model.Utilisateur;
import com.act.security.core.model.dto.role.AuthorityDto;
import com.act.security.core.repo.RoleRepo;
import com.act.security.core.repo.UtilisateurRepo;
import com.act.security.core.service.SecuritySessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.act.core.util.AppUtils.has;


@Component
public class BackendServiceProxy {
    @Autowired
    RoleRepo roleRepo;

    @Autowired
    UtilisateurRepo utilisateurRepo;

    @Autowired
    SecuritySessionService sessionService;

    public BackendServiceProxy() {
        if (!has(sessionService)) {
            sessionService = new SecuritySessionService();
        }
    }


    public List<AuthorityDto> findAllAuthorities() {
        return roleRepo.findAll().stream().flatMap(authority -> authority.getAllAuthoritiesDto().stream())
                .collect(Collectors.toList());
    }

    public Utilisateur findByUsername(String username) throws Exception {
        Optional<Utilisateur> opt = utilisateurRepo.findByUsername(username);
        return opt.orElseThrow(() -> new Exception("Utilisateur inexistant"));
    }
}
