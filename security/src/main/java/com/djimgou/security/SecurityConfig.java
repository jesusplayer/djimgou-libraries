package com.djimgou.security;

import com.djimgou.security.core.AppSecurityConfig;
import com.djimgou.security.core.AuthorizedUrl;
import com.djimgou.security.core.model.PrivileEvaluator;
import com.djimgou.security.core.model.Role;
import com.djimgou.security.core.model.UrlsAuthorized;
import com.djimgou.security.core.service.MyVoter;
import com.djimgou.security.core.tracking.authentication.dao.ResourceRepository;
import com.djimgou.security.enpoints.EndPointsRegistry;
import com.djimgou.security.enpoints.SecuredEndPoint;
import com.djimgou.tenantmanager.service.TenantSessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.access.vote.AffirmativeBased;
import org.springframework.security.access.vote.RoleHierarchyVoter;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.access.channel.ChannelProcessingFilter;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.context.AbstractSecurityWebApplicationInitializer;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.session.SessionManagementFilter;

import javax.servlet.Filter;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

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

    @Value("${auth.jwt.enabled:}")
    Boolean jwtEnabled = false;

    /*  @Autowired
        WebInvocationPrivilegeEvaluator evaluator;*/
    @Autowired
    AppSecurityConfig appSecurityConfig;

/*    @Autowired
    CustomLogoutHandler logoutHandler;*/

    @Autowired
    InvalidSessionHandler invalidSessionHandler;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Qualifier("userDetailsServiceImpl")
    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private FilterInvocationSecurityMetadataSource filterInvocationSecurityMetadataSource;

    @Autowired
    AuthSuccessHandler authSuccessHandler;

    @Autowired
    LogoutSuccessHandler authLogoutSuccessHandler;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private ResourceRepository roleResourceRepository;

    @Autowired
    EndPointsRegistry endPointsRegistry;

    @Autowired
    AppliCorsFilter corsFilter;

    @Autowired
    TenantSessionService tenantSessionService;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable();


        ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry req = http.cors().and()
                .httpBasic()
                .and()
                .authorizeRequests();

        req = req.antMatchers("/").permitAll();
        for (AuthorizedUrl dto : appSecurityConfig.authorizedUrls()) {

            req = (dto.hasHttpMethod() ?
                    req.antMatchers(dto.getHttpMethod(), dto.getUrl()) :
                    req.antMatchers(dto.getUrl())
            ).permitAll();
        }


        for (UrlsAuthorized url : UrlsAuthorized.values()) {
            req = req.antMatchers(url.toString()).permitAll();
        }
        req = req.filterSecurityInterceptorOncePerRequest(true);


        if (appSecurityConfig.permitAll()) {
            req.antMatchers("*").permitAll();
            req.antMatchers("**/**").permitAll();
            req.antMatchers("/**/**").permitAll();
            req.antMatchers("/**/*").permitAll();
        } else {
            final Map<String, SecuredEndPoint> endpointsMap = endPointsRegistry.getEndpointsMap();

            for (Map.Entry<String, SecuredEndPoint> entry : endpointsMap.entrySet()) {
                SecuredEndPoint v = entry.getValue();
//                if (has(v.getHttpMethod())) {
//                    req.antMatchers(v.getHttpMethod(), v.toSecurityUrl()).hasAnyAuthority(v.getName(), Role.ROLE_ADMIN, PrivileEvaluator.FULL_ACCESS);//.hasAnyAuthority(Role.ROLE_CLIENT, Role.ROLE_PARTENAIRE, Role.ROLE_ADMINISTRATEUR, Role.ROLE_ADMIN, PrivileEvaluator.FULL_ACCESS);
//                } else {
                    req.antMatchers(v.toSecurityUrl()).hasAnyAuthority(v.getName(), Role.ROLE_ADMIN, PrivileEvaluator.FULL_ACCESS);//.hasAnyAuthority(Role.ROLE_CLIENT, Role.ROLE_PARTENAIRE, Role.ROLE_ADMINISTRATEUR, Role.ROLE_ADMIN, PrivileEvaluator.FULL_ACCESS);
//                }
            }

//            req.antMatchers("*").fullyAuthenticated();//.hasAnyAuthority(Role.ROLE_CLIENT, Role.ROLE_PARTENAIRE, Role.ROLE_ADMINISTRATEUR, Role.ROLE_ADMIN, Role.ROLE_READONLY, PrivileEvaluator.FULL_ACCESS);
//            req.antMatchers("**/**").fullyAuthenticated();//.hasAnyAuthority(Role.ROLE_CLIENT, Role.ROLE_PARTENAIRE, Role.ROLE_ADMINISTRATEUR, Role.ROLE_READONLY, Role.ROLE_ADMIN, PrivileEvaluator.FULL_ACCESS);
//            req.antMatchers("/**/**").fullyAuthenticated();//.hasAnyAuthority(Role.ROLE_CLIENT, Role.ROLE_PARTENAIRE, Role.ROLE_ADMINISTRATEUR, Role.ROLE_READONLY, Role.ROLE_ADMIN, PrivileEvaluator.FULL_ACCESS);
//            req.antMatchers("/**/*").fullyAuthenticated();//.hasAnyAuthority(Role.ROLE_CLIENT, Role.ROLE_PARTENAIRE, Role.ROLE_ADMINISTRATEUR, Role.ROLE_READONLY, Role.ROLE_ADMIN, PrivileEvaluator.FULL_ACCESS);

        }

//        req.anyRequest().authenticated()
        req.anyRequest().authenticated().and()

                //
                // Add Filter 1 - JWTLoginFilter
                //
                .addFilterBefore(corsFilter, ChannelProcessingFilter.class)
                .addFilterBefore(new AuthTokenFilter(userDetailsService,
                                tenantSessionService, jwtEnabled, appSecurityConfig),
                        UsernamePasswordAuthenticationFilter.class
                )
//                .addFilterAfter(new AuditRequestFilter(),UsernamePasswordAuthenticationFilter.class)

                /*.addFilterBefore(new JWTLoginFilter(UrlsAuthorized.LOGIN.toString(), authenticationManager()),
                        UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(new AuthTokenFilter(authenticationManager), UsernamePasswordAuthenticationFilter.class)*/


                //.addFilterAfter(expiredSessionFilter(), SessionManagementFilter.class)

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
                .exceptionHandling()
                .accessDeniedHandler(accessDeniedHandler())
//                .exceptionHandling().accessDeniedPage(UrlsAuthorized.UNAUTHORIZED.toString())
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

    @Bean
    public FilterRegistrationBean securityFilterChain(@Qualifier(AbstractSecurityWebApplicationInitializer.DEFAULT_FILTER_NAME) Filter securityFilter) {
        FilterRegistrationBean registration = new FilterRegistrationBean(securityFilter);
        registration.setOrder(Integer.MAX_VALUE - 1);
        registration.setName(AbstractSecurityWebApplicationInitializer.DEFAULT_FILTER_NAME);
        return registration;
    }

    @Bean
    public FilterRegistrationBean userFilterFilterRegistrationBean() {
        FilterRegistrationBean registrationBean = new FilterRegistrationBean();
        AuditRequestFilter userFilter = new AuditRequestFilter();
        registrationBean.setFilter(userFilter);
        registrationBean.setOrder(Integer.MAX_VALUE);
        return registrationBean;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);

    }

    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return new AuthAccessDeniedHandler();
    }

    @Bean
    public FilterSecurityInterceptor filterSecurityInterceptor() {
        FilterSecurityInterceptor filterSecurityInterceptor = new FilterSecurityInterceptor();
        filterSecurityInterceptor.setAuthenticationManager(authenticationManager);

        filterSecurityInterceptor.setSecurityMetadataSource(filterInvocationSecurityMetadataSource);
        filterSecurityInterceptor.setAccessDecisionManager(affirmativeBased());
        return filterSecurityInterceptor;
    }

    @Bean
    public AffirmativeBased affirmativeBased() {
        List<AccessDecisionVoter<? extends Object>> accessDecisionVoters = new ArrayList<>();
        // accessDecisionVoters.add(roleVoter());
        accessDecisionVoters.add(new MyVoter(roleResourceRepository, bCryptPasswordEncoder, appSecurityConfig));

        AffirmativeBased affirmativeBased = new AffirmativeBased(accessDecisionVoters);
        return affirmativeBased;
    }

    @Bean
    public RoleHierarchyVoter roleVoter() {
        RoleHierarchyVoter roleHierarchyVoter = new RoleHierarchyVoter(roleHierarchy());
        roleHierarchyVoter.setRolePrefix("ROLE_");
        return roleHierarchyVoter;
    }

    @Bean
    public RoleHierarchy roleHierarchy() {
        RoleHierarchyImpl roleHierarchy = new RoleHierarchyImpl();

        roleHierarchy.setHierarchy("ROLE_ADMIN > ROLE_USER");
        return roleHierarchy;
    }

    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    private Filter expiredSessionFilter() {
        SessionManagementFilter smf = new SessionManagementFilter(new HttpSessionSecurityContextRepository());
        smf.setInvalidSessionStrategy((request, response) -> response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Votre session a expiré"));
        return smf;
    }
}
