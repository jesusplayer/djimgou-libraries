package com.djimgou.security;

import com.djimgou.audit.service.AuditBdService;
import com.djimgou.core.util.AppUtils;
import com.djimgou.security.core.AppSecurityConfig;
import com.djimgou.security.core.enpoints.EndPointsRegistry;
import com.djimgou.security.core.enpoints.SecuredEndPoint;
import com.djimgou.security.core.model.Privilege;
import com.djimgou.security.core.model.Role;
import com.djimgou.security.core.model.UrlsAuthorized;
import com.djimgou.security.core.model.dto.role.AuthorityDto;
import com.djimgou.security.core.service.PrivileEvaluator;
import com.djimgou.security.service.AuthoritiesRepo;
import com.djimgou.tenantmanager.service.TenantSessionService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
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
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.access.channel.ChannelProcessingFilter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.djimgou.core.util.AppUtils2.has;

//import org.springframework.session.web.http.SessionRepositoryFilter;


/**
 * https://www.springcloud.io/post/2022-05/spring-security-accessdecisionvoter/#gsc.tab=0
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

    @Value("${auth.gateway:}")
    Boolean isGateway = false;

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

    @Autowired
    AuthoritiesRepo authoritiesRepo;

    @Bean
    public InitializingBean initializingBean() {
        return () -> SecurityContextHolder.setStrategyName(
                SecurityContextHolder.MODE_INHERITABLETHREADLOCAL);
    }

    List<String> of(String... strings) {
        return new ArrayList() {{
            addAll(Arrays.asList(strings));
        }};
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


        if (appSecurityConfig.permitAll()) {
            rule[0].antMatchers("*").permitAll();
            rule[0].antMatchers("**/**").permitAll();
            rule[0].antMatchers("/**/**").permitAll();
            rule[0].antMatchers("/**/*").permitAll();
        } else {
            final Map<String, SecuredEndPoint> endpointsMap = endPointsRegistry.getEndpointsMap();

            Stream<AuthorityDto> streamEndp = has(endpointsMap) ?
                    endpointsMap.values().stream().filter(AppUtils::has)
                            .map(endPoint -> new AuthorityDto(endPoint.getName(), endPoint.getUrl(), endPoint.getHttpMethod()))
                    : Stream.empty();

            final Set<AuthorityDto> authorities =
                    (isGateway != null && isGateway) ? authoritiesRepo.getAuthorities(streamEndp)
                            : streamEndp.sorted().collect(Collectors.toCollection(LinkedHashSet::new));

            authorities.forEach(endPoint -> {

                final String AUTHORITY_NAME = endPoint.getName();

                if (endPoint.hasHttpMethod()) {
                    final ExpressionUrlAuthorizationConfigurer<HttpSecurity>.AuthorizedUrl authorizedUrl =
                            rule[0].antMatchers(endPoint.getHttpMethod(), endPoint.getUrls());

                    if (Objects.equals(endPoint.getHttpMethod(), HttpMethod.GET) || Objects.equals(Privilege.READ_ONLY_PRIV, endPoint.getName())) {
                        authorizedUrl.hasAnyAuthority(AUTHORITY_NAME, Role.ROLE_ADMIN, PrivileEvaluator.FULL_ACCESS, Privilege.READ_ONLY_PRIV, Role.ROLE_READONLY);
                    } else {
                        authorizedUrl.hasAnyAuthority(AUTHORITY_NAME, Role.ROLE_ADMIN, PrivileEvaluator.FULL_ACCESS);
                    }
                } else {
                    rule[0].antMatchers(endPoint.getUrls()).hasAnyAuthority(AUTHORITY_NAME, Role.ROLE_ADMIN, PrivileEvaluator.FULL_ACCESS);
                }
            });

            rule[0].antMatchers(HttpMethod.GET, "*").hasAnyAuthority(Privilege.READ_ONLY_PRIV, Role.ROLE_READONLY, Role.ROLE_ADMIN, PrivileEvaluator.FULL_ACCESS);
            rule[0].antMatchers(HttpMethod.GET, "**/**").hasAnyAuthority(Privilege.READ_ONLY_PRIV, Role.ROLE_READONLY, Role.ROLE_ADMIN, PrivileEvaluator.FULL_ACCESS);
            rule[0].antMatchers(HttpMethod.GET, "/**/**").hasAnyAuthority(Privilege.READ_ONLY_PRIV, Role.ROLE_READONLY, Role.ROLE_ADMIN, PrivileEvaluator.FULL_ACCESS);
            rule[0].antMatchers(HttpMethod.GET, "/**/*").hasAnyAuthority(Privilege.READ_ONLY_PRIV, Role.ROLE_READONLY, Role.ROLE_ADMIN, PrivileEvaluator.FULL_ACCESS);

            rule[0].antMatchers(HttpMethod.POST, "*").hasAnyAuthority(Role.ROLE_ADMIN, PrivileEvaluator.FULL_ACCESS);
            rule[0].antMatchers(HttpMethod.POST, "**/**").hasAnyAuthority(Role.ROLE_ADMIN, PrivileEvaluator.FULL_ACCESS);
            rule[0].antMatchers(HttpMethod.POST, "/**/**").hasAnyAuthority(Role.ROLE_ADMIN, PrivileEvaluator.FULL_ACCESS);
            rule[0].antMatchers(HttpMethod.POST, "/**/*").hasAnyAuthority(Role.ROLE_ADMIN, PrivileEvaluator.FULL_ACCESS);

            rule[0].antMatchers(HttpMethod.PUT, "*").hasAnyAuthority(Role.ROLE_ADMIN, PrivileEvaluator.FULL_ACCESS);
            rule[0].antMatchers(HttpMethod.PUT, "**/**").hasAnyAuthority(Role.ROLE_ADMIN, PrivileEvaluator.FULL_ACCESS);
            rule[0].antMatchers(HttpMethod.PUT, "/**/**").hasAnyAuthority(Role.ROLE_ADMIN, PrivileEvaluator.FULL_ACCESS);
            rule[0].antMatchers(HttpMethod.PUT, "/**/*").hasAnyAuthority(Role.ROLE_ADMIN, PrivileEvaluator.FULL_ACCESS);

            rule[0].antMatchers(HttpMethod.OPTIONS, "*").hasAnyAuthority(Role.ROLE_ADMIN, PrivileEvaluator.FULL_ACCESS);
            rule[0].antMatchers(HttpMethod.OPTIONS, "**/**").hasAnyAuthority(Role.ROLE_ADMIN, PrivileEvaluator.FULL_ACCESS);
            rule[0].antMatchers(HttpMethod.OPTIONS, "/**/**").hasAnyAuthority(Role.ROLE_ADMIN, PrivileEvaluator.FULL_ACCESS);
            rule[0].antMatchers(HttpMethod.OPTIONS, "/**/*").hasAnyAuthority(Role.ROLE_ADMIN, PrivileEvaluator.FULL_ACCESS);

            rule[0].antMatchers(HttpMethod.DELETE, "*").hasAnyAuthority(Role.ROLE_ADMIN, PrivileEvaluator.FULL_ACCESS);
            rule[0].antMatchers(HttpMethod.DELETE, "**/**").hasAnyAuthority(Role.ROLE_ADMIN, PrivileEvaluator.FULL_ACCESS);
            rule[0].antMatchers(HttpMethod.DELETE, "/**/**").hasAnyAuthority(Role.ROLE_ADMIN, PrivileEvaluator.FULL_ACCESS);
            rule[0].antMatchers(HttpMethod.DELETE, "/**/*").hasAnyAuthority(Role.ROLE_ADMIN, PrivileEvaluator.FULL_ACCESS);

            rule[0].antMatchers(HttpMethod.PATCH, "*").hasAnyAuthority(Role.ROLE_ADMIN, PrivileEvaluator.FULL_ACCESS);
            rule[0].antMatchers(HttpMethod.PATCH, "**/**").hasAnyAuthority(Role.ROLE_ADMIN, PrivileEvaluator.FULL_ACCESS);
            rule[0].antMatchers(HttpMethod.PATCH, "/**/**").hasAnyAuthority(Role.ROLE_ADMIN, PrivileEvaluator.FULL_ACCESS);
            rule[0].antMatchers(HttpMethod.PATCH, "/**/*").hasAnyAuthority(Role.ROLE_ADMIN, PrivileEvaluator.FULL_ACCESS);

//            if (!has(endpointsMap)) {

////            }


        }


        for (String url : appSecurityConfig.authorizedUrls()) {
            rule[0] = rule[0].antMatchers(url).permitAll();
        }

        for (UrlsAuthorized url : UrlsAuthorized.values()) {
            rule[0] = rule[0].antMatchers(url.toString()).permitAll();
        }

//        rule[0].anyRequest().authenticated()
        rule[0].anyRequest().authenticated().and()

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

        // rule[0].anyRequest().access("@jdbcRoleChecker.check(authentication,request)");

//        appSecurityConfig.setUrlExpression(rule[0]);
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
