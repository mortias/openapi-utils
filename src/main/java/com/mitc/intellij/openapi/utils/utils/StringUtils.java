package com.mitc.intellij.openapi.utils.utils;

import java.util.Arrays;

public final class StringUtils {

    private StringUtils() {
    }

    public static boolean containsOneOf(String str, String... items) {
        return Arrays.stream(items).anyMatch(str::contains);
    }

    public static boolean equalsOneOf(String str, String... items) {
        return Arrays.asList(items).contains(str);
    }

}
