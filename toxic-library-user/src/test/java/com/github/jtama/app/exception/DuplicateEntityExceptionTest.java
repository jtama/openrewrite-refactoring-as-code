package com.github.jtama.app.exception;

import org.junit.Test;
import static org.junit.Assert.*;

public class DuplicateEntityExceptionTest {
    @Test
    public void testExceptionMessage() {
        String message = "Duplicate entity message";
        DuplicateEntityException exception = new DuplicateEntityException(message);
        assertEquals(message, exception.getMessage());
    }
}
