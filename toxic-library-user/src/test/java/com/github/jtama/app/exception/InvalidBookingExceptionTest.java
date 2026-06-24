package com.github.jtama.app.exception;

import org.junit.Test;
import static org.junit.Assert.*;

public class InvalidBookingExceptionTest {
    @Test
    public void testExceptionMessage() {
        String message = "Invalid booking message";
        InvalidBookingException exception = new InvalidBookingException(message);
        assertEquals(message, exception.getMessage());
    }
}
