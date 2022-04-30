package com.djimgou.security.core.service;

import com.djimgou.security.core.AppSecurityConfig;
import com.djimgou.security.core.UtilisateurDetails;
import com.djimgou.security.core.model.PrivileEvaluator;
import com.djimgou.security.core.model.UrlsAuthorized;
import com.djimgou.security.core.tracking.authentication.dao.ResourceRepository;
import com.djimgou.security.core.tracking.authentication.dao.ResourceRepositoryInterface;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.FilterInvocation;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpSession;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.djimgou.core.util.AppUtils.has;


/**
 * BASSANGONEN HERVE LUDOVIC 23.08.2019 ..
 * DJIMGOU NKENNE DANY MARC 08/2020
 */
@Log4j2
public class MyVoter implements AccessDecisionVoter {
    public static final String USER_PASSWORD_NOT_CHANGED = "USER_PASSWORD_NOT_CHANGED";
    public static final String USER_CHANGE_USER_PASSWORD = "USER_CHANGE_USER_PASSWORD";
    //    WebInvocationPrivilegeEvaluator evaluator;
//    MethodSecurityExpressionOperations w
    static List<String> authorizedUrls = Arrays.asList(UrlsAuthorized.values()).stream().map(u -> u.toString()).collect(Collectors.toList());
    static final Logger logger = LogManager.getLogger(MyVoter.class);
    // username
    /**
     * Indique quel utilisateur à mettre à jour la session
     */
    public static final Map<String, Boolean> userSessionToUpdate = Collections.synchronizedMap(new HashMap<>());
    ResourceRepositoryInterface roleResourceRepository;


    SecuritySessionService sessionService = new SecuritySessionService();

    private BCryptPasswordEncoder bCryptPasswordEncoder;

    AppSecurityConfig appSecurityConfig;

    @Autowired
    public MyVoter(ResourceRepository roleResourceRepository, BCryptPasswordEncoder bCryptPasswordEncoder, AppSecurityConfig appSecurityConfig) {
        this.roleResourceRepository = roleResourceRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.appSecurityConfig = appSecurityConfig;
        //this.evaluator = evaluator;
    }

    @Override
    public boolean supports(ConfigAttribute attribute) {
        return true;
    }

    // Function to get ArrayList from Stream
    public static <T> ArrayList<T> getArrayListFromStream(Stream<T> stream) {

        // Convert the Stream to List
        List<T> list = stream.collect(Collectors.toList());

        // Create an ArrayList of the List
        ArrayList<T> arrayList = new ArrayList<T>(list);

        // Return the ArrayList
        return arrayList;
    }


    public List<GrantedAuthority> getRefreshedAuthorities(Authentication authentication) {
        UtilisateurDetails uDet = sessionService.currentUser(authentication);

        List<GrantedAuthority> authorities = new ArrayList(authentication.getAuthorities());
        if (userSessionToUpdate.containsKey(uDet.getUsername())) {
            if (userSessionToUpdate.get(uDet.getUsername())) { // rafraichir ses Autorities
                try {
                    UtilisateurDetails uDet2 = roleResourceRepository.refreshUser(uDet.getUsername());
                    authorities = new ArrayList(uDet2.getAuthorities());
                    sessionService.updateUserAuthorities(authorities);
                    userSessionToUpdate.put(uDet.getUsername(), Boolean.FALSE); // on lui demande de ne plus rafraichir
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return authorities;
    }

    public static boolean isPasswordChanged(UtilisateurDetails uDetails, BCryptPasswordEncoder bCryptPasswordEncoder) {
        if (has(uDetails) && has(uDetails.getUtilisateur())) {
            Boolean isPasswdChanged = uDetails.getUtilisateur().getIsPasswordChangedByUser();
            if (isPasswdChanged == null || !isPasswdChanged) {
                return Boolean.FALSE;
            }
            if (isPasswdChanged) {
                return Boolean.TRUE;
            }
            String password = uDetails.getPassword();
            String password2 = bCryptPasswordEncoder.encode(uDetails.getUtilisateur().fullPassword());
            return !password.equals(password2);
        }
        return Boolean.FALSE;
    }

    @Override
    public int vote(Authentication authentication, Object object, Collection collection) {
        boolean granted = false;
        // Collection<ConfigAttribute> configAttributes = (ConfigAttribute)collection;
        FilterInvocation filterInvocation = (FilterInvocation) object;
        String url = determineModule(filterInvocation);

        if (appSecurityConfig.match(authorizedUrls, url)) {
            return ACCESS_GRANTED;
        }

        if (!sessionService.hasUser(authentication)) {
            return ACCESS_DENIED;
        } else {
            if (!isPasswordChanged(sessionService.currentUser(authentication), bCryptPasswordEncoder)) {
                // le mot de passe n'a pas changé. le rediriger vers le changement de mot de passe

                ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
                HttpSession session = attr.getRequest().getSession(true);
                session.setAttribute(USER_PASSWORD_NOT_CHANGED, true);
                return ACCESS_DENIED;
            }
            if (url.equals(UrlsAuthorized.INDEX.toString())) {
                return ACCESS_GRANTED;
            }
        }
        Stream<ConfigAttribute> configAttributeStream = collection.stream();
        List<ConfigAttribute> configAttributeList = getArrayListFromStream(configAttributeStream);
        List<String> configList = new ArrayList<String>();
        configAttributeList.forEach(configAttribute -> configList.add(configAttribute.getAttribute()));

        List<GrantedAuthority> authorities = getRefreshedAuthorities(authentication);
        List<String> roleNames = new ArrayList<String>();
        authorities.forEach(grantedAuthority -> {
            roleNames.add(grantedAuthority.getAuthority());
        });
        if (roleNames.contains(PrivileEvaluator.FULL_ACCESS)) {
            return ACCESS_GRANTED;
        }
        log.info("roleNames {}", roleNames.toString());
        log.info("configList {}", configList.toString());

        if (roleNames.toString() == "ROLE_ANONYMOUS") {
            System.out.println("ROLE_ANONYMOUS ");
            return ACCESS_DENIED;
        }

        // Store the comparison output
        // in ArrayList list3
        configList.retainAll(roleNames);
        log.info("La différence des deux {}", configList.toString());
        log.info("ConfigList size {}", configList.size());
        if ((configList.size() == 0 || configList.isEmpty()) && roleNames.isEmpty()) {
            log.info("ACCESS_DENIED");
            return ACCESS_DENIED;
        } else {
            log.info("URL demandée {}", url);
            Boolean decision = isAccessGranted(roleNames, url);
            return decision ? ACCESS_GRANTED : ACCESS_DENIED;
        }
    }

    @Override
    public boolean supports(Class clazz) {
        return clazz.isAssignableFrom(FilterInvocation.class);

    }

    String determineModule(FilterInvocation filterObject) {
        String url = filterObject.getRequestUrl();
        return url;
    }

    boolean isAccessGranted(List<String> roleOrAuthorities, String url) {
        // to strip of query string from url.
        if (url.indexOf('?') != -1)
            url = url.substring(0, url.indexOf('?'));
        url = url.trim();
        return roleOrAuthorities.contains(url);
    }

    public static boolean isUrlGranted(List<String> roleOrAuthorities, String url, AppSecurityConfig appSecurityConfig) {
        // to strip of query string from url.
        if (url.indexOf('?') != -1)
            url = url.substring(0, url.indexOf('?'));
        url = url.trim();
        if (roleOrAuthorities.contains(PrivileEvaluator.FULL_ACCESS) || appSecurityConfig.match(authorizedUrls, url)) {
            return true;
        }
        return roleOrAuthorities.contains(url);
    }
}
