package com.github.command1264.webProgramming.accouunt;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import org.jetbrains.annotations.Nullable;

public class AccountAndRooms extends UserAndRooms {
    protected String loginAccount;
    protected String loginPassword;


    public AccountAndRooms() {
        this(null, null, null, null);
    }
    public AccountAndRooms(String userId, String name, String loginAccount, String loginPassword) {
        this(userId, name, loginAccount, loginPassword, "");
    }

    public AccountAndRooms(String userId,
                   String name,
                   String loginAccount,
                   String loginPassword,
                   String photoStickerBase64) {
        super(userId, name, photoStickerBase64);
        this.loginAccount = loginAccount;
        this.loginPassword = loginPassword;
    }

    public String getLoginAccount() {
        return this.loginAccount;
    }
    public String getLoginPassword() {
        return this.loginPassword;
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
            case "loginaccount" -> this.loginAccount = value;
            case "loginpassword" -> this.loginPassword = value;
            case "chatRooms" -> this.setChatRooms(value);
            default -> {}
        };
    }

    public boolean setLoginAccount(@Nullable String loginAccount) {
        if (loginAccount == null) return false;
        this.loginAccount = loginAccount;
        return true;
    }
    public boolean setLoginPassword(@Nullable String loginPassword) {
        if (loginPassword == null) return false;
        this.loginPassword = loginPassword;
        return true;
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
            case "loginaccount" -> this.loginAccount;
            case "loginpassword" -> this.loginPassword;
            case "chatRooms" -> this.getChatRoomsSerialize();
        };
    }

    @Override
    public String serialize() {
        try {
            return new Gson().toJson(this, AccountAndRooms.class);
        } catch (JsonSyntaxException e) {
            return null;
        }
    }


    public static AccountAndRooms deserialize(String json) {
        try {
            return new Gson().fromJson(json, AccountAndRooms.class);
        } catch (JsonSyntaxException e) {
            return null;
        }
    }

}
