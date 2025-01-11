package com.young.microservices.mlagenteval.utils;

public class UnicodeUtils {
    public static String decodeUnicodeString(String unicodeString) {
        StringBuilder decodedString = new StringBuilder();
        int length = unicodeString.length();
        for (int i = 0; i < length; i++) {
            char c = unicodeString.charAt(i);
            if (c == '\\' && i + 1 < length && unicodeString.charAt(i + 1) == 'u') {
                int codePoint = Integer.parseInt(unicodeString.substring(i + 2, i + 6), 16);
                decodedString.append((char) codePoint);
                i += 5;
            } else {
                decodedString.append(c);
            }
        }
        return decodedString.toString();
    }
}
