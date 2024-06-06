package com.github.command1264.webProgramming.util;


import java.util.Random;

public class BaseRandomGenerator {

    private static final String BASE64_CHAR = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";
    private static final String BASE62_CHAR = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";;
    private static final String BASE58_CHAR = "123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz";
    private static final Random rand = new Random();

    public static String base64(int length) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < Math.abs(length); i++) {
            stringBuilder.append(BASE64_CHAR.charAt(rand.nextInt(0, 64)));
        }
        return stringBuilder.toString();
    }
    public static String base62(int length) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < Math.abs(length); i++) {
            stringBuilder.append(BASE62_CHAR.charAt(rand.nextInt(0, 62)));
        }
        return stringBuilder.toString();
    }
    public static String base58(int length) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < Math.abs(length); i++) {
            stringBuilder.append(BASE58_CHAR.charAt(rand.nextInt(0, 58)));
        }
        return stringBuilder.toString();
    }
}
