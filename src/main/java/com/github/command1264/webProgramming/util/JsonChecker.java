package com.github.command1264.webProgramming.util;

import com.google.gson.JsonObject;

public class JsonChecker {
    public static boolean checkKey(JsonObject jsonObject, String key) {
        try {
            return jsonObject.has(key) && !jsonObject.get(key).isJsonNull();
        } catch (Exception e) {
            return false;
        }
    }
    public static boolean checkNoKey(JsonObject jsonObject, String key) {
        return !checkKey(jsonObject, key);
    }
}
