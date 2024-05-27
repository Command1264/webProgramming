package com.github.command1264.webProgramming;

public class Account extends User {
    protected String loginAccount = "";
    protected String loginPassword = "";
    public Account(String id, String name, String loginAccount, String loginPassword) {
        super(id, name);
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
}
