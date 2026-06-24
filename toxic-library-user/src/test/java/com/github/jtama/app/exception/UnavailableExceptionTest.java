package com.github.jtama.app.exception;

import org.junit.Test;
import static org.junit.Assert.*;

public class UnavailableExceptionTest {
    @Test
    public void testExceptionMessage() {
        String message = "Unavailable message";
        UnavailableException exception = new UnavailableException(message);
        assertEquals(message, exception.getMessage());
    }
}
