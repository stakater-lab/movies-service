package com.stakater.lab.moviesservice.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.security.Principal;

/**
 * Unique user-id is extracted with 4 different ways as shown below.
 *
 * The one with facade is more easily testable!
 */
@Controller
public class SecurityController {

    @Autowired
    private IAuthenticationFacade authenticationFacade;

    @RequestMapping(value = "/api/username-from-principal", method = RequestMethod.GET)
    @ResponseBody
    public String currentUserNameFromPrincipal(Principal principal) {
        return principal.getName();
    }

    @RequestMapping(value = "/api/username-from-authentication", method = RequestMethod.GET)
    @ResponseBody
    public String currentUserNameFromAuthentication(Authentication authentication) {
        return authentication.getName();
    }

    @RequestMapping(value = "/api/username-from-authentication-facade", method = RequestMethod.GET)
    @ResponseBody
    public String currentUserNameFromAuthenticationFacade() {
        Authentication authentication = authenticationFacade.getAuthentication();
        return authentication.getName();
    }

    @RequestMapping(value = "/api/username-from-oauth2-authentication", method = RequestMethod.GET)
    @ResponseBody
    public String currentUserNameFromAuthenticationFacade(OAuth2Authentication oAuth2Authentication) {
        // All OAuth2/OpenId Connect claims can be extracted from "details"; they are stored in a map
        return oAuth2Authentication.getPrincipal().toString();
    }

}
