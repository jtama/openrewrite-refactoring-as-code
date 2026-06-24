package com.github.jtama.app.util;

import org.junit.Test;
import java.time.DateTimeException;
import static org.junit.Assert.*;

public class MonthValidatorTest {
    private final MonthValidator validator = new MonthValidator();

    @Test
    public void testValidateMonth_Success() {
        validator.validateMonth(1);
        validator.validateMonth(6);
        validator.validateMonth(12);
    }

    @Test(expected = DateTimeException.class)
    public void testValidateMonth_TooLow() {
        validator.validateMonth(0);
    }

    @Test(expected = DateTimeException.class)
    public void testValidateMonth_TooHigh() {
        validator.validateMonth(13);
    }
}
