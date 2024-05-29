package com.github.command1264.webProgramming;

public class Account extends User {
    protected String loginAccount = "";
    protected String loginPassword = "";
    public Account(String id, String name, String loginAccount, String loginPassword) {
        this(id, name, loginAccount, loginPassword, "");
    }
    public Account(String id, String name, String loginAccount, String loginPassword, String photoStickerBase64) {
        super(id, name, photoStickerBase64);
        this.loginAccount = loginAccount;
        this.loginPassword = loginPassword;
    }

    public String getLoginAccount() {
        return this.loginAccount;
    }
    public String getLoginPassword() {
        return this.loginPassword;
    }

    public boolean setLoginAccount(String loginAccount) {
        if (loginAccount == null) return false;
        this.loginAccount = loginAccount;
        return true;
    }
    public boolean setLoginPassword(String loginPassword) {
        if (loginPassword == null) return false;
        this.loginPassword = loginPassword;
        return true;
    }

    @Override
    public String get(String key) {
        if (key == null) return null;
        return switch (key.toLowerCase()) {
            default -> null;
            case "id" -> this.id;
            case "name" -> this.name;
            case "photoStickerBase64" -> this.photoStickerBase64;
            case "loginaccount" -> this.loginAccount;
            case "loginpassword" -> this.loginPassword;
        };
    }
}
