package com.apollo;


import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;
import org.jasypt.intf.cli.JasyptPBEStringEncryptionCLI;

public class JasyptEncrypt {
    private static Pattern BLANK_PATTERN = Pattern.compile("\\s*|\t|\r|\n");
    private static String ALGORITHM = "PBEWithMD5AndDES";

    public JasyptEncrypt() {
    }

    public String encrypt(String input, String password) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(1024);
        PrintStream cacheStream = new PrintStream(byteArrayOutputStream);
        System.setOut(cacheStream);
        String[] args = new String[]{"input=" + input, "password=" + password, "algorithm=" + ALGORITHM};
        JasyptPBEStringEncryptionCLI.main(args);
        System.setOut(System.out);
        String message = byteArrayOutputStream.toString();
        String str = replaceBlank(message);
        int index = str.lastIndexOf("-");
        return str.substring(index + 1);
    }

    private static String replaceBlank(String str) {
        String dest = "";
        if (!StringUtils.isEmpty(str)) {
            Matcher matcher = BLANK_PATTERN.matcher(str);
            dest = matcher.replaceAll("");
        }

        return dest;
    }
}

