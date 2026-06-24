package com.github.jtama.app.security;

import io.quarkus.security.AuthenticationFailedException;
import io.quarkus.security.identity.AuthenticationRequestContext;
import io.quarkus.security.identity.SecurityIdentity;
import io.smallrye.mutiny.Uni;
import org.jboss.logging.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.security.Principal;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class NaiveSecurityProviderTest {

    @Mock
    private Logger logger;

    @Mock
    private AuthenticationRequestContext context;

    @InjectMocks
    private NaiveSecurityProvider provider;

    @Test
    public void testGetRequestType() {
        assertEquals(HeaderAuthenticationRequest.class, provider.getRequestType());
    }

    @Test
    public void testAuthenticate_Success() {
        Set<String> roles = new HashSet<>();
        roles.add("ADMIN");
        roles.add("USER");
        HeaderAuthenticationRequest request = new HeaderAuthenticationRequest("alice", roles);

        Uni<SecurityIdentity> uni = provider.authenticate(request, context);
        assertNotNull(uni);

        SecurityIdentity identity = uni.await().indefinitely();
        assertNotNull(identity);
        assertEquals("alice", identity.getPrincipal().getName());
        assertTrue(identity.getRoles().contains("ADMIN"));
        assertTrue(identity.getRoles().contains("USER"));
    }

    @Test
    public void testAuthenticate_NullUserName() {
        HeaderAuthenticationRequest request = new HeaderAuthenticationRequest(null, new HashSet<>());

        Uni<SecurityIdentity> uni = provider.authenticate(request, context);
        assertNotNull(uni);

        SecurityIdentity identity = uni.await().indefinitely();
        assertNotNull(identity);
        assertEquals("", identity.getPrincipal().getName());
    }

    @Test
    public void testAuthenticate_SecurityException() {
        HeaderAuthenticationRequest request = mock(HeaderAuthenticationRequest.class);
        Principal principal = mock(Principal.class);
        when(request.getPrincipal()).thenReturn(principal);
        when(principal.getName()).thenThrow(new SecurityException("Simulated security error"));

        Uni<SecurityIdentity> uni = provider.authenticate(request, context);
        assertNotNull(uni);

        try {
            uni.await().indefinitely();
            fail("Should have thrown AuthenticationFailedException");
        } catch (AuthenticationFailedException e) {
            assertNotNull(e.getCause());
            assertTrue(e.getCause() instanceof SecurityException);
            assertEquals("Simulated security error", e.getCause().getMessage());
            verify(logger, times(1)).debug(eq("Authentication failed"), eq(e.getCause()));
        }
    }
}
