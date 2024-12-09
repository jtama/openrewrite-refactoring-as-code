package com.github.jtama.app.security;

import io.quarkus.security.AuthenticationFailedException;
import io.quarkus.security.identity.AuthenticationRequestContext;
import io.quarkus.security.identity.IdentityProvider;
import io.quarkus.security.identity.SecurityIdentity;
import io.quarkus.security.runtime.QuarkusSecurityIdentity;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

@ApplicationScoped
public class NaiveSecurityProvider implements IdentityProvider<HeaderAuthenticationRequest> {

    @Inject
    Logger logger;

    @Override
    public Class<HeaderAuthenticationRequest> getRequestType() {
        return HeaderAuthenticationRequest.class;
    }

    @Override
    public Uni<SecurityIdentity> authenticate(HeaderAuthenticationRequest request, AuthenticationRequestContext context) {
        return Uni.createFrom().item(() -> {
            try {
                QuarkusSecurityIdentity.Builder builder = QuarkusSecurityIdentity.builder();
                if (request.getPrincipal().getName() == null) {
                    builder.setPrincipal(() -> "");
                } else {
                    builder.setPrincipal(request.getPrincipal());
                    for (String i : request.getRoles()) {
                        builder.addRole(i);
                    }
                }
                return builder.build();
            } catch (SecurityException e) {
                logger.debug("Authentication failed", e);
                throw new AuthenticationFailedException(e);
            }
        });
    }

}
