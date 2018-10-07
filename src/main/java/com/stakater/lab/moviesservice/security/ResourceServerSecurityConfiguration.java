package com.stakater.lab.moviesservice.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationManager;
import org.springframework.security.oauth2.provider.error.OAuth2AccessDeniedHandler;
import org.springframework.security.oauth2.provider.error.OAuth2AuthenticationEntryPoint;
import org.springframework.security.oauth2.provider.error.WebResponseExceptionTranslator;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.servlet.http.HttpSession;

/**
 * This config defined the access rules and paths that are protected by OAuth2 security.
 *
 * Any REST API which serves any Resources (endpoints) is called a Resource Server.
 *
 * This is a security configuration for resources held/served by the REST API.
 */
@Configuration
/**
 * Enables a resource server. By default this annotation creates a security filter which authenticates requests via an
 * incoming OAuth2 token. The filter is an instance of WebSecurityConfigurerAdapter which has an hard-coded order of 3
 * (Due to some limitations of Spring Framework). You need to tell Spring Boot to set OAuth2 request filter order to 3
 * to align with the hardcoded value. You do that by adding security.oauth2.resource.filter-order = 3 in the
 * application.properties file.
 */
@EnableResourceServer
/**
 * debug = true has been set for testing purposes only and shouldn't be used in production
 * @EnableWebSecurity(debug = true)
 */
@EnableWebSecurity
public class ResourceServerSecurityConfiguration extends ResourceServerConfigurerAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(ResourceServerSecurityConfiguration.class);

    @Value("${lab.jwtPublicKey}")
    private String jwtPublicKey;

    @Value("${lab.audience}")
    private String audience;

    @Autowired
    private CustomAccessTokenConverter customAccessTokenConverter;

    /**
     * Override configure to define the access rules for secure resources.
     *
     * @param httpSecurity the current http filter configuration
     * @throws Exception if there is a problem
     */
    @Override
    public void configure(final HttpSecurity httpSecurity) throws Exception {

        httpSecurity
                .cors()
            .and()
                .csrf().disable()
                .headers().frameOptions().sameOrigin()
            .and()
                .sessionManagement()
                /**
                 * Spring Security will never create an {@link HttpSession} and it will never use it
                 * to obtain the {@link SecurityContext}
                 */
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
                .httpBasic().disable()
                .authorizeRequests()
                .antMatchers("/actuator/health", "/actuator/info").permitAll()
                .anyRequest().authenticated();
    }

    /**
     * Overridden to inject overridden dependency
     *
     * @param config
     */
    @Override
    public void configure(ResourceServerSecurityConfigurer config) {
        LOGGER.debug("Running with OAuth2 Security!");
        config.tokenServices(tokenServices());
        /**
         * If you don't set this then you will get this error
         * "error_description": "Invalid token does not contain resource id (oauth2-resource)"
         *
         * In case of keycloak; the resourceId should be set to the clientId.
         */
        config.resourceId(audience);
        // This is where you inject your custom error management
        config
            .accessDeniedHandler(accessDeniedHandler())
                .authenticationEntryPoint(authenticationEntryPoint());
    }

    @Bean
    public JwtAccessTokenConverter accessTokenConverter() {
        JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
        converter.setVerifierKey(jwtPublicKey);
        converter.setAccessTokenConverter(customAccessTokenConverter);
        return converter;
    }

    @Bean
    public TokenStore tokenStore() {
        return new JwtTokenStore(accessTokenConverter());
    }

    @Bean
    @Primary
    public DefaultTokenServices tokenServices() {
        DefaultTokenServices defaultTokenServices = new DefaultTokenServices();
        defaultTokenServices.setTokenStore(tokenStore());
        return defaultTokenServices;
    }

    /**
     * Declared this bean to exclude UserDetailsServiceAutoConfiguration which prints a default password on console;
     * (remove this and look in the console output)
     *
     * @param defaultTokenServices
     * @return
     */
    @Bean
    public AuthenticationManager authenticationManagerBean(final DefaultTokenServices defaultTokenServices) {
        final OAuth2AuthenticationManager authenticationManager = new OAuth2AuthenticationManager();
        authenticationManager.setTokenServices(defaultTokenServices);
        return authenticationManager;
    }

    /**
     * Inject your custom exception translator into the OAuth2 {@link AuthenticationEntryPoint}.
     *
     * @return AuthenticationEntryPoint
     */
    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {
        final OAuth2AuthenticationEntryPoint entryPoint = new OAuth2AuthenticationEntryPoint();
        entryPoint.setExceptionTranslator(exceptionTranslator());
        return entryPoint;
    }

    /**
     * Classic Spring Security stuff, defining how to handle {@link AccessDeniedException}s.
     * Inject your custom exception translator into the OAuth2AccessDeniedHandler.
     * (if you don't add this access denied exceptions may use a different format)
     *
     * @return AccessDeniedHandler
     */
    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        final OAuth2AccessDeniedHandler handler = new OAuth2AccessDeniedHandler();
        handler.setExceptionTranslator(exceptionTranslator());
        return handler;
    }

    /** Define your custom exception translator bean here */
    @Bean
    public WebResponseExceptionTranslator<?> exceptionTranslator() {
        return new ApiErrorWebResponseExceptionTranslator();
    }
}
