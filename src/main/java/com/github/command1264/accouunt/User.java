package com.github.command1264.accouunt;

public class User {
    protected String id = "";
    protected String name = "";
    protected String photoStickerBase64 = "";
    public User(String id, String name) {
        this(id, name, "");
    }
    public User(String id, String name, String photoStickerBase64) {
        this.id = id;
        this.name = name;
        this.photoStickerBase64 = photoStickerBase64;
    }

    public String getName() {
        return this.name;
    }
    public String getId() {
        return this.id;
    }
    public String getPhotoStickerBase64() {
        return this.photoStickerBase64;
    }

    public boolean setName(String name) {
        if (name == null) return false;
        this.name = name;
        return true;
    }
    public boolean setId(String id) {
        if (id == null) return false;
        this.id = id;
        return true;
    }
    public boolean setPhotoStickerBase64(String photoStickerBase64) {
        if (photoStickerBase64 == null) return false;
        this.photoStickerBase64 = photoStickerBase64;
        return true;
    }

    public String get(String key) {
        if (key == null) return null;
        return switch (key.toLowerCase()) {
            default -> null;
            case "id" -> this.id;
            case "name" -> this.name;
            case "photoStickerBase64" -> this.photoStickerBase64;
        };
    }
}
