package com.github.jtama.app.security;

import org.junit.Test;
import java.security.Principal;
import java.util.HashSet;
import java.util.Set;
import static org.junit.Assert.*;

public class HeaderAuthenticationRequestTest {
    @Test
    public void testHeaderAuthenticationRequest() {
        Set<String> roles = new HashSet<>();
        roles.add("admin");
        roles.add("user");

        HeaderAuthenticationRequest request = new HeaderAuthenticationRequest("john_doe", roles);

        Principal principal = request.getPrincipal();
        assertNotNull(principal);
        assertEquals("john_doe", principal.getName());
        assertEquals(roles, request.getRoles());
    }
}
