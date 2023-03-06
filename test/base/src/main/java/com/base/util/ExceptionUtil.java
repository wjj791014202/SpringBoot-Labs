package com.base.util;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class ExceptionUtil {
    public ExceptionUtil() {
    }

    public static String getStacktrace(Throwable ex) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream pout = new PrintStream(out);
        ex.printStackTrace(pout);
        String result = new String(out.toByteArray());

        try {
            pout.close();
            out.close();
        } catch (Exception var5) {
        }

        return result;
    }
}
