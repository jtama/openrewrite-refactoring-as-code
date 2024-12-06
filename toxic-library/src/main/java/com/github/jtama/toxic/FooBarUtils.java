package com.github.jtama.toxic;

import java.util.Comparator;
import java.util.List;

public class FooBarUtils {

    public String stringFormatted(String template, Object... args) {
        return String.format(template, args);
    }

    public static boolean isEmpty(String value) {
        if (value == null) return true;
        return value.isEmpty();
    }

    public static boolean isEmptyList(List value) {
        if (value == null) return true;
        return value.isEmpty();
    }

    public <T> int compare(T o1, T o2, Comparator<T> comparator) {
        return comparator.compare(o1, o2);
    }
}
