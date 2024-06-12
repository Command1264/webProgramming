package com.github.command1264.webProgramming.accouunt;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class UserAndRooms extends User {
    protected Map<String, Long> chatRooms;
    public static final Type CHAT_ROOMS_TYPE = new TypeToken<Map<String, Long>>() {}.getType();

    public UserAndRooms() {
        this(null, null);
    }
    public UserAndRooms(String userId, String name) {
        this(userId, name, "");
    }
    public UserAndRooms(String userId, String name, String photoStickerBase64) {
        this(userId, name, photoStickerBase64, new HashMap<>());
    }

    public UserAndRooms(String userId,
                           String name,
                           String photoStickerBase64,
                           Map<String, Long> chatRooms) {
        super(userId, name, photoStickerBase64);
        this.chatRooms = chatRooms;
    }

    public void setChatRooms(@Nullable String chatRooms) {
        if (chatRooms == null) return;
        this.chatRooms = new Gson().fromJson(chatRooms, CHAT_ROOMS_TYPE);
    }
    public void setChatRooms(@Nullable Map<String, Long> chatRooms) {
        if (chatRooms == null) return;
        this.chatRooms = chatRooms;
    }
    @Override
    public void set(@Nullable String key, @Nullable String value) {
        if (key == null || value == null) return;
        switch (key.toLowerCase()) {
            case "id" -> this.id = value;
            case "userId" -> this.userId = value;
            case "name" -> this.name = value;
            case "createTime" -> this.createTime = value;
            case "photoStickerBase64" -> this.photoStickerBase64 = value;
            case "chatRooms" -> this.setChatRooms(value);
            default -> {}
        };
    }


    public Map<String, Long> getChatRooms() {
        return this.chatRooms;
    }
    public String getChatRoomsSerialize() {
        return new Gson().toJson(this.chatRooms, CHAT_ROOMS_TYPE);
    }
    @Override
    public String get(@Nullable String key) {
        if (key == null) return null;
        return switch (key.toLowerCase()) {
            default -> null;
            case "id" -> this.id;
            case "userId" -> this.userId;
            case "name" -> this.name;
            case "createTime" -> this.createTime;
            case "photoStickerBase64" -> this.photoStickerBase64;
            case "chatRooms" -> this.getChatRoomsSerialize();
        };
    }

    @Override
    public String serialize() {
        try {
            return new Gson().toJson(this, UserAndRooms.class);
        } catch (JsonSyntaxException e) {
            return null;
        }
    }


    public static UserAndRooms deserialize(String json) {
        try {
            return new Gson().fromJson(json, UserAndRooms.class);
        } catch (JsonSyntaxException e) {
            return null;
        }
    }
}
