package com.github.command1264.webProgramming.accouunt;

import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Account extends User {
    protected String loginAccount;
    protected String loginPassword;


    public Account() {
        this(null, null, null, null);
    }
    public Account(String id, String name, String loginAccount, String loginPassword) {
        this(id, name, loginAccount, loginPassword, "");
    }
    public Account(String id, String name, String loginAccount, String loginPassword, String photoStickerBase64) {
        this(id, name, loginAccount, loginPassword, photoStickerBase64, new ArrayList<>());
    }
    public Account(String id,
                   String name,
                   String loginAccount,
                   String loginPassword,
                   String photoStickerBase64,
                   List<Map<String, Long>> chatRooms) {
        super(id, name, LocalDateTime.now(), photoStickerBase64, chatRooms);
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
            case "name" -> this.name = value;
            case "createTime" -> this.createTime = value;
            case "photoStickerBase64" -> this.photoStickerBase64 = value;
            case "loginaccount" -> this.loginAccount = value;
            case "loginpassword" -> this.loginPassword = value;
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
            case "name" -> this.name;
            case "createTime" -> this.createTime;
            case "photoStickerBase64" -> this.photoStickerBase64;
            case "loginaccount" -> this.loginAccount;
            case "loginpassword" -> this.loginPassword;
        };
    }
}
