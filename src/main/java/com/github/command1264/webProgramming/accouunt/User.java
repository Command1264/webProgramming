package com.github.command1264.webProgramming.accouunt;

import com.github.command1264.webProgramming.util.DateTimeFormat;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class User {
    protected String id;
    protected String userId;
    protected String name;
    protected String createTime;
    protected String photoStickerBase64;
    protected boolean deleted;

    public User() {
        this(null, null);
    }
    public User(String userId, String name) {
        this(userId, name, "");
    }
    public User(String userId, String name, String photoStickerBase64) {
        this(userId, name, LocalDateTime.now(), photoStickerBase64, false);

    }
    public User(String userId, String name, LocalDateTime createTime, String photoStickerBase64, boolean deleted) {
        this.userId = userId;
        this.name = name;
        this.createTime = createTime.format(DateTimeFormatter.ofPattern(DateTimeFormat.format));
        this.photoStickerBase64 = photoStickerBase64;
        this.deleted = deleted;
    }

    public String getName() {
        return this.name;
    }
    public String getId() {
        return this.id;
    }
    public String getUserId() {
        return this.userId;
    }
    public String getPhotoStickerBase64() {
        return this.photoStickerBase64;
    }
    public String getCreateTime() {
        return createTime;
    }
    public boolean getDeleted() {
        return this.deleted;
    }

    public String get(@Nullable String key) {
        if (key == null) return null;
        return switch (key.toLowerCase()) {
            default -> null;
            case "id" -> this.id;
            case "userId" -> this.userId;
            case "name" -> this.name;
            case "createTime" -> this.createTime;
            case "photoStickerBase64" -> this.photoStickerBase64;
        };
    }


    public void setId(@Nullable String id) {
        if (id == null) return;
        this.id = id;
    }
    public void setUserId(@Nullable String userId) {
        if (userId == null) return;
        this.userId = userId;
    }
    public void setName(@Nullable String name) {
        if (name == null) return;
        this.name = name;
    }
    public void setCreateTime(@Nullable LocalDateTime createTime) {
        if (createTime == null) return;
        this.createTime = createTime.format(DateTimeFormatter.ofPattern(DateTimeFormat.format));
    }
    public void setCreateTime(@Nullable String createTime) {
        if (createTime == null) return;
        this.createTime = createTime;
    }
    public void setPhotoStickerBase64(@Nullable String photoStickerBase64) {
        if (photoStickerBase64 == null) return;
        this.photoStickerBase64 = photoStickerBase64;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public void set(@Nullable String key, @Nullable String value) {
        if (key == null || value == null) return;
        switch (key.toLowerCase()) {
            case "id" -> this.id = value;
            case "userId" -> this.userId = value;
            case "name" -> this.name = value;
            case "createTime" -> this.createTime = value;
            case "photoStickerBase64" -> this.photoStickerBase64 = value;
            default -> {}
        };
    }

    public String serialize() {
        try {
            return new Gson().toJson(this, User.class);
        } catch (JsonSyntaxException e) {
            return null;
        }
    }

    public static User deserialize(String json) {
        try {
            return new Gson().fromJson(json, User.class);
        } catch (JsonSyntaxException e) {
            return null;
        }
    }
}
