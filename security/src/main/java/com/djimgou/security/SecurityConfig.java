package com.djimgou.security;

import com.djimgou.audit.service.AuditBdService;
import com.djimgou.security.core.AppSecurityConfig;
import com.djimgou.security.core.enpoints.EndPointsRegistry;
import com.djimgou.security.core.model.Role;
import com.djimgou.security.core.model.UrlsAuthorized;
import com.djimgou.tenantmanager.service.TenantSessionService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.task.DelegatingSecurityContextAsyncTaskExecutor;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.access.channel.ChannelProcessingFilter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import static com.djimgou.core.util.AppUtils.has;

//import org.springframework.session.web.http.SessionRepositoryFilter;

//import scx.beac.etransfert.tracking.authentication.dao.ResourceRepository;
//import scx.beac.etransfert.tracking.authentication.security.com.djimgou.audit.service.myVoter;

/**
 * BASSANGONEN HERVE LUDOVIC 26.08.2019 ..
 * DJIMGOU NKENNE DANY MARC 08/2020- 09/2021
 */
///@ComponentScan("com.djimgou.carrent")
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    /*    @Autowired
        WebInvocationPrivilegeEvaluator evaluator;*/
    @Value("${auth.jwt.enabled:}")
    Boolean jwtEnabled = false;

    @Autowired
    AppSecurityConfig appSecurityConfig;

/*    @Autowired
    CustomLogoutHandler logoutHandler;*/

    @Autowired
    InvalidSessionHandler invalidSessionHandler;


    @Qualifier("userDetailsServiceImpl")

    @Autowired
    private UserDetailsService authenticationService;
/*
    @Autowired
    private FilterInvocationSecurityMetadataSource filterInvocationSecurityMetadataSource;*/

    @Autowired
    AuthSuccessHandler authSuccessHandler;

    @Autowired
    LogoutSuccessHandler authLogoutSuccessHandler;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private TenantSessionService tenantSessionService;
/*
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private ResourceRepository roleResourceRepository;*/

    @Autowired
    EndPointsRegistry endPointsRegistry;

    @Autowired
    AppCorsFilter appCorsFilter;

    @Autowired
    AuditBdService auditBdService;

    @Bean
    public InitializingBean initializingBean() {
        return () -> SecurityContextHolder.setStrategyName(
                SecurityContextHolder.MODE_INHERITABLETHREADLOCAL);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable();


        final ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry[] rule = new ExpressionUrlAuthorizationConfigurer.ExpressionInterceptUrlRegistry[]{
                http.cors().and()
                        .httpBasic()
                        .and()
                        .authorizeRequests()
                        .antMatchers("/").permitAll()
                        .filterSecurityInterceptorOncePerRequest(true)

        };


        if (has(endPointsRegistry.getEndpointsMap())) {
            endPointsRegistry.getEndpointsMap().values().forEach(endPoint -> {

                final ExpressionUrlAuthorizationConfigurer<HttpSecurity>.AuthorizedUrl authorizedUrl =
                        rule[0].antMatchers(endPoint.getHttpMethod(), endPoint.toSecurityUrl());
                if (appSecurityConfig.permitAll()) {
                    authorizedUrl.permitAll();
                } else {
                    authorizedUrl.hasAuthority(endPoint.getName());
                    if (endPoint.getIsReadOnlyMethod()) {
                        rule[0].antMatchers(endPoint.getHttpMethod(), endPoint.toSecurityUrl()).hasRole(Role.ROLE_READONLY);
                    }
                }
            });
        } else {
            if (appSecurityConfig.permitAll()) {
                rule[0].antMatchers("*").permitAll();
                rule[0].antMatchers("**/**").permitAll();
                rule[0].antMatchers("/**/**").permitAll();
                rule[0].antMatchers("/**/*").permitAll();
            }
        }


        for (String url : appSecurityConfig.authorizedUrls()) {
            rule[0] = rule[0].antMatchers(url).permitAll();
        }

        for (UrlsAuthorized url : UrlsAuthorized.values()) {
            rule[0] = rule[0].antMatchers(url.toString()).permitAll();
        }

        rule[0].anyRequest().authenticated()
                .and()

                //
                // Add Filter 1 - JWTLoginFilter
                //
                .addFilterBefore(appCorsFilter, ChannelProcessingFilter.class)
                .addFilterBefore(new AuthTokenFilter(authenticationService, tenantSessionService, jwtEnabled, appSecurityConfig),
                        UsernamePasswordAuthenticationFilter.class
                ).addFilterAfter(new AuditRequestFilter(auditBdService), UsernamePasswordAuthenticationFilter.class)
                /*.addFilterBefore(new JWTLoginFilter(UrlsAuthorized.LOGIN.toString(), authenticationManager()),
                        UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(new JWTAuthenticationFilter(authenticationManager), UsernamePasswordAuthenticationFilter.class)*/


                //.addFilterAfter(expiredSessionFilter(), SessionManagementFilter.class)
                .exceptionHandling()
                .accessDeniedHandler(accessDeniedHandler())
//                .exceptionHandling().accessDeniedPage(UrlsAuthorized.UNAUTHORIZED.toString())
                .and()
                .sessionManagement()
                .invalidSessionStrategy(invalidSessionHandler)
                .and()
                .cors().and()
                .formLogin()
                .successHandler(authSuccessHandler)
                .loginPage(UrlsAuthorized.LOGIN.toString())
                //.usernameParameter("identifiant").passwordParameter("password")
                .permitAll()
                .failureUrl(UrlsAuthorized.LOGIN_FAILURE.toString())
                //.defaultSuccessUrl("/home")
//                .defaultSuccessUrl("/index.jsf")
                .and()
                .logout()
                //.logoutSuccessUrl(UrlsAuthorized.LOGIN.toString())
                //.addLogoutHandler(logoutHandler)
                //.logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler(HttpStatus.OK))
                .logoutSuccessHandler(authLogoutSuccessHandler)
                .clearAuthentication(true)
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID");
               /* .and()
                .addFilter(filterSecurityInterceptor());*/
        appSecurityConfig.configure(http);
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(authenticationService).passwordEncoder(passwordEncoder);

    }

    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return new AuthAccessDeniedHandler();
    }

 /*   @Bean
    public FilterSecurityInterceptor filterSecurityInterceptor() {
        FilterSecurityInterceptor filterSecurityInterceptor = new FilterSecurityInterceptor();
        filterSecurityInterceptor.setAuthenticationManager(authenticationManager);
        filterSecurityInterceptor.setSecurityMetadataSource(filterInvocationSecurityMetadataSource);
        filterSecurityInterceptor.setAccessDecisionManager(affirmativeBased());
        return filterSecurityInterceptor;
    }*/

/*    @Bean
    public AffirmativeBased affirmativeBased() {
        List<AccessDecisionVoter<? extends Object>> accessDecisionVoters = new ArrayList<>();
        // accessDecisionVoters.add(roleVoter());
        accessDecisionVoters.add(new MyVoter(roleResourceRepository, bCryptPasswordEncoder, appSecurityConfig));

        AffirmativeBased affirmativeBased = new AffirmativeBased(accessDecisionVoters);
        return affirmativeBased;
    }*/

/*    @Bean
    public RoleHierarchyVoter roleVoter() {
        RoleHierarchyVoter roleHierarchyVoter = new RoleHierarchyVoter(roleHierarchy());
        roleHierarchyVoter.setRolePrefix("ROLE_");
        return roleHierarchyVoter;
    }*/

/*    @Bean
    public RoleHierarchy roleHierarchy() {
        RoleHierarchyImpl roleHierarchy = new RoleHierarchyImpl();

        roleHierarchy.setHierarchy("ROLE_ADMIN > ROLE_USER");
        return roleHierarchy;
    }*/

    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
/*
    private Filter expiredSessionFilter() {
        SessionManagementFilter smf = new SessionManagementFilter(new HttpSessionSecurityContextRepository());
        smf.setInvalidSessionStrategy((request, response) -> response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Votre session a expiré"));
        return smf;
    }*/
}
