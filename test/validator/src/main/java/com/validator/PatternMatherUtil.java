package com.validator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PatternMatherUtil {
    public PatternMatherUtil() {
    }

    public static boolean matches(String str, String ex) {
        Pattern pattern = Pattern.compile(ex);
        Matcher matcher = pattern.matcher(str);
        return matcher.matches();
    }

    public static String matchesGroup(String str, String ex) {
        Pattern pattern = Pattern.compile(ex);
        Matcher matcher = pattern.matcher(str);
        return matcher.find() ? matcher.group(1) : null;
    }
}
