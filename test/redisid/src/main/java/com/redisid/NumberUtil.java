package com.redisid;


public class NumberUtil {
    private static final String CHARS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    public static final int MAX_RADIX = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".length();
    private static final ThreadLocal<StringBuilder> RESULT_BUILDER_THREAD_LOCAL = ThreadLocal.withInitial(StringBuilder::new);

    public NumberUtil() {
    }

    public static String toString(long number, int radix) {
        Preconditions.checkArgument(number >= 0L, "number must not be negative");
        Preconditions.checkArgument(radix > 1 && radix <= "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".length(), "radix is out of range [2, " + "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".length() + "]");
        StringBuilder resultBuilder = (StringBuilder)RESULT_BUILDER_THREAD_LOCAL.get();

        try {
            for(boolean var4 = false; number > (long)(radix - 1); number /= (long)radix) {
                int remainder = (int)(number % (long)radix);
                resultBuilder.append("0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".charAt(remainder));
            }

            resultBuilder.append("0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".charAt((int)number));
            String var5 = resultBuilder.reverse().toString();
            return var5;
        } finally {
            resultBuilder.setLength(0);
        }
    }

    public static String toString(int number, int radix) {
        return toString((long)number, radix);
    }
}

