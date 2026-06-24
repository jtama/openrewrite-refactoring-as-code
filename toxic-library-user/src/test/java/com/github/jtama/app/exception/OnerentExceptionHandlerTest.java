package com.github.jtama.app.exception;

import jakarta.ws.rs.NotAuthorizedException;
import jakarta.ws.rs.core.Response;
import org.junit.Test;
import static org.junit.Assert.*;

public class OnerentExceptionHandlerTest {
    private final OnerentExceptionHandler handler = new OnerentExceptionHandler();

    @Test
    public void testToResponse_DuplicateEntityException() {
        Exception ex = new DuplicateEntityException("Duplicate");
        Response response = handler.toResponse(ex);
        assertEquals(Response.Status.CONFLICT.getStatusCode(), response.getStatus());
        assertEquals("Duplicate", response.getEntity());
    }

    @Test
    public void testToResponse_UnknownEntityException() {
        Exception ex = new UnknownEntityException("Not Found");
        Response response = handler.toResponse(ex);
        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
        assertEquals("Not Found", response.getEntity());
    }

    @Test
    public void testToResponse_NotAuthorizedException() {
        Exception ex = new NotAuthorizedException("Unauthorized");
        Response response = handler.toResponse(ex);
        assertEquals(Response.Status.UNAUTHORIZED.getStatusCode(), response.getStatus());
        assertEquals("He non du con", response.getEntity());
    }

    @Test
    public void testToResponse_InvalidBookingException() {
        Exception ex = new InvalidBookingException("Invalid Booking");
        Response response = handler.toResponse(ex);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals("Invalid Booking", response.getEntity());
    }

    @Test
    public void testToResponse_InvalidNameException() {
        Exception ex = new InvalidNameException("Invalid Name");
        Response response = handler.toResponse(ex);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals("Invalid Name", response.getEntity());
    }

    @Test
    public void testToResponse_UnavailableException() {
        Exception ex = new UnavailableException("Unavailable");
        Response response = handler.toResponse(ex);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals("Unavailable", response.getEntity());
    }

    @Test
    public void testToResponse_DefaultException() {
        Exception ex = new NullPointerException("NPE");
        Response response = handler.toResponse(ex);
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
        assertEquals("NPE", response.getEntity());
    }
}
