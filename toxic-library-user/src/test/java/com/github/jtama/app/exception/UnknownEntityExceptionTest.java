package com.github.jtama.app.exception;

import org.junit.Test;
import static org.junit.Assert.*;

public class UnknownEntityExceptionTest {
    @Test
    public void testExceptionMessage() {
        String message = "Unknown entity message";
        UnknownEntityException exception = new UnknownEntityException(message);
        assertEquals(message, exception.getMessage());
    }
}
