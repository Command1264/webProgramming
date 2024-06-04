package com.github.command1264.webProgramming.util;

import com.google.gson.JsonObject;

public class KeyValueConvertor {
    public static JsonObject toJsonObject(String keyValuePairs, String splitRegex) {
        JsonObject jsonObject = new JsonObject();
        for(String line : keyValuePairs.split(splitRegex)) {
            String[] splitData = line.split("=", 2);
            if (splitData.length != 2) continue;
            jsonObject.addProperty(splitData[0], splitData[1]);
        }
        return jsonObject;

    }
}
