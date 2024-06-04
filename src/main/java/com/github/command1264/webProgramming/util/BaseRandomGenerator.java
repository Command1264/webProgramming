package com.github.command1264.webProgramming.util;


import java.util.Random;

public class BaseRandomGenerator {

    private static final String BASE64_CHAR = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";
    private static final Random rand = new Random();

    public static String base64(int length) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < length; i++) {
            stringBuilder.append(BASE64_CHAR.charAt(rand.nextInt(0, 64)));
        }
        return stringBuilder.toString();
    }
}
