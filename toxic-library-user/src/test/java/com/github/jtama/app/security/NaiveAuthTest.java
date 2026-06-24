package com.github.jtama.app.security;

import io.quarkus.security.identity.IdentityProviderManager;
import io.quarkus.security.identity.SecurityIdentity;
import io.quarkus.security.identity.request.AuthenticationRequest;
import io.quarkus.vertx.http.runtime.security.ChallengeData;
import io.quarkus.vertx.http.runtime.security.HttpCredentialTransport;
import io.smallrye.mutiny.Uni;
import io.vertx.core.MultiMap;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.ext.web.RoutingContext;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Set;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class NaiveAuthTest {

    @InjectMocks
    private NaiveAuth naiveAuth;

    @Mock
    private RoutingContext routingContext;

    @Mock
    private HttpServerRequest httpServerRequest;

    @Mock
    private MultiMap headers;

    @Mock
    private IdentityProviderManager identityProviderManager;

    @Mock
    private SecurityIdentity securityIdentity;

    @Before
    public void setUp() {
        when(routingContext.request()).thenReturn(httpServerRequest);
        when(httpServerRequest.headers()).thenReturn(headers);
    }

    @Test
    public void testAuthenticate_WithRoles() {
        when(headers.get("X-user-name")).thenReturn("john");
        when(httpServerRequest.getHeader("X-user-roles")).thenReturn("admin,user");

        Uni<SecurityIdentity> expectedUni = Uni.createFrom().item(securityIdentity);
        when(identityProviderManager.authenticate(any(HeaderAuthenticationRequest.class))).thenReturn(expectedUni);

        Uni<SecurityIdentity> resultUni = naiveAuth.authenticate(routingContext, identityProviderManager);
        assertNotNull(resultUni);
        assertEquals(securityIdentity, resultUni.await().indefinitely());

        ArgumentCaptor<HeaderAuthenticationRequest> captor = ArgumentCaptor.forClass(HeaderAuthenticationRequest.class);
        verify(identityProviderManager).authenticate(captor.capture());

        HeaderAuthenticationRequest capturedRequest = captor.getValue();
        assertEquals("john", capturedRequest.getPrincipal().getName());
        Set<String> capturedRoles = capturedRequest.getRoles();
        assertEquals(2, capturedRoles.size());
        assertTrue(capturedRoles.contains("admin"));
        assertTrue(capturedRoles.contains("user"));
    }

    @Test
    public void testAuthenticate_WithoutRoles() {
        when(headers.get("X-user-name")).thenReturn("alice");
        when(httpServerRequest.getHeader("X-user-roles")).thenReturn(null);

        Uni<SecurityIdentity> expectedUni = Uni.createFrom().item(securityIdentity);
        when(identityProviderManager.authenticate(any(HeaderAuthenticationRequest.class))).thenReturn(expectedUni);

        Uni<SecurityIdentity> resultUni = naiveAuth.authenticate(routingContext, identityProviderManager);
        assertNotNull(resultUni);
        assertEquals(securityIdentity, resultUni.await().indefinitely());

        ArgumentCaptor<HeaderAuthenticationRequest> captor = ArgumentCaptor.forClass(HeaderAuthenticationRequest.class);
        verify(identityProviderManager).authenticate(captor.capture());

        HeaderAuthenticationRequest capturedRequest = captor.getValue();
        assertEquals("alice", capturedRequest.getPrincipal().getName());
        Set<String> capturedRoles = capturedRequest.getRoles();
        assertEquals(1, capturedRoles.size());
        assertTrue(capturedRoles.contains(""));
    }

    @Test
    public void testGetChallenge() {
        Uni<ChallengeData> resultUni = naiveAuth.getChallenge(routingContext);
        assertNotNull(resultUni);
        ChallengeData challengeData = resultUni.await().indefinitely();
        assertNotNull(challengeData);
        assertEquals(401, challengeData.status);
    }

    @Test
    public void testGetCredentialTypes() {
        Set<Class<? extends AuthenticationRequest>> credentialTypes = naiveAuth.getCredentialTypes();
        assertNotNull(credentialTypes);
        assertEquals(1, credentialTypes.size());
        assertTrue(credentialTypes.contains(HeaderAuthenticationRequest.class));
    }

    @Test
    public void testGetCredentialTransport() {
        Uni<HttpCredentialTransport> resultUni = naiveAuth.getCredentialTransport(routingContext);
        assertNotNull(resultUni);
        HttpCredentialTransport transport = resultUni.await().indefinitely();
        assertNotNull(transport);
        assertEquals(HttpCredentialTransport.Type.AUTHORIZATION, transport.getTransportType());
        assertEquals("onerent", transport.getTypeTarget());
    }
}
