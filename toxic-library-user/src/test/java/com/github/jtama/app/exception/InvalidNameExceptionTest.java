package com.github.jtama.app.exception;

import org.junit.Test;
import static org.junit.Assert.*;

public class InvalidNameExceptionTest {
    @Test
    public void testExceptionMessage() {
        String message = "Invalid name message";
        InvalidNameException exception = new InvalidNameException(message);
        assertEquals(message, exception.getMessage());
    }
}
