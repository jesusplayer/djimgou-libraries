package com.act.security;

import com.act.security.model.Utilisateur;
import com.act.security.repo.UtilisateurRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;


// https://www.baeldung.com/role-and-privilege-for-spring-security-registration

/**
 * @author DJIMGOU NKENNE DANY MARC 08/2020
 */
@Transactional
@Service//("userDetailsService")
public class UserDetailsServiceImpl implements UserDetailsService {

    @Qualifier("appDefaultUtilisateurRepo")
    @Autowired
    private UtilisateurRepo utilisateurRepo;

    // @Autowired
    // BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        Optional<Utilisateur> optionalUser = utilisateurRepo.findValidatedUsername(userName);
        if (optionalUser.isPresent()) {
            Utilisateur user = optionalUser.get();
            return new UtilisateurDetails(user);
        } else {
            throw new UsernameNotFoundException("Cet utilisateur n'existe pas");
        }
    }
}