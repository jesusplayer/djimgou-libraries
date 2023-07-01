package com.djimgou.security;

import com.djimgou.security.core.AppSecurityConfig;
import com.djimgou.security.core.model.Role;
import com.djimgou.security.core.model.UrlsAuthorized;
import com.djimgou.security.core.service.MyVoter;
import com.djimgou.security.core.tracking.authentication.dao.ResourceRepository;
import com.djimgou.security.enpoints.EndPointsRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
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
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.session.SessionManagementFilter;

import javax.servlet.Filter;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
            endPointsRegistry.getEndpointsMap().values().forEach(endPoint -> {
                final ExpressionUrlAuthorizationConfigurer<HttpSecurity>.AuthorizedUrl authorizedUrl =
                        rule[0].antMatchers(endPoint.getHttpMethod(), endPoint.toSecurityUrl());
               /* if (appSecurityConfig.permitAll()) {
                    authorizedUrl.permitAll();
                } else {*/
                List<String> authorities = new ArrayList() {{
                    addAll(Arrays.asList(endPoint.getName(), Role.ROLE_ADMIN, "ROLE_ADMINISTRATEUR"));
                }};
                if (endPoint.getIsReadOnlyMethod()) {
                    authorities.add(Role.ROLE_READONLY);
                }
                authorizedUrl.hasAnyAuthority(authorities.toArray(new String[0]));
                // }
            });

            for (String url : appSecurityConfig.authorizedUrls()) {
                rule[0] = rule[0].antMatchers(url).permitAll();
            }

            for (UrlsAuthorized url : UrlsAuthorized.values()) {
                rule[0] = rule[0].antMatchers(url.toString()).permitAll();
            }

        }


        rule[0].anyRequest().authenticated()
                .and()

                //
                // Add Filter 1 - JWTLoginFilter
                //
                .addFilterBefore(new CorsFilter(), ChannelProcessingFilter.class)
                .addFilterBefore(new AuthTokenFilter(userDetailsService, jwtEnabled, appSecurityConfig),
                        UsernamePasswordAuthenticationFilter.class
                )

                /*.addFilterBefore(new JWTLoginFilter(UrlsAuthorized.LOGIN.toString(), authenticationManager()),
                        UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(new AuthTokenFilter(authenticationManager), UsernamePasswordAuthenticationFilter.class)*/


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
