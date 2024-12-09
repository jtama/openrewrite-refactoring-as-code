package com.github.jtama.app.security;

import io.quarkus.security.identity.request.AuthenticationRequest;
import io.quarkus.security.identity.request.BaseAuthenticationRequest;

import java.security.Principal;
import java.util.Set;

public class HeaderAuthenticationRequest extends BaseAuthenticationRequest {
    private String userName;
    private Set<String> roles;

    public HeaderAuthenticationRequest(String userName, Set<String> roles) {
        this.userName = userName;
        this.roles = roles;
    }

    public Principal getPrincipal() {
        return () -> userName;
    }

    public Set<String> getRoles() {
        return roles;
    }
}
