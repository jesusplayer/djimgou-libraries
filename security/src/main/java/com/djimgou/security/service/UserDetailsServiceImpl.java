package com.djimgou.security.service;

import com.djimgou.security.core.UtilisateurDetails;
import com.djimgou.security.core.model.Utilisateur;
import com.djimgou.security.core.repo.UtilisateurBaseRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

import static com.djimgou.core.util.AppUtils.has;


// https://www.baeldung.com/role-and-privilege-for-spring-security-registration

/**
 * @author DJIMGOU NKENNE DANY MARC 08/2020
 */
@Transactional
@Service//("userDetailsService")
public class UserDetailsServiceImpl implements UserDetailsService {

    @Qualifier("appUtilisateurRepo")
    @Autowired(required = false)
    UtilisateurBaseRepo<Utilisateur, UUID> customRepo;

    @Qualifier("appDefaultUtilisateurRepo")
    @Autowired
    UtilisateurBaseRepo<Utilisateur, UUID> repo;

    // @Autowired
    // BCryptPasswordEncoder bCryptPasswordEncoder;


    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        Optional<Utilisateur> optionalUser = getRepo().findValidatedUsername(userName);
        if (optionalUser.isPresent()) {
            Utilisateur user = optionalUser.get();
            if (!user.getEnabled()) {
                throw new UsernameNotFoundException("Cet utilisateur est inactif. Activez le");
            }
            return new UtilisateurDetails(user);
        } else {
            throw new UsernameNotFoundException("Cet utilisateur n'existe pas");
        }
    }

    public UtilisateurBaseRepo<Utilisateur, UUID> getRepo() {
        return has(customRepo) ? customRepo : repo;
    }
}