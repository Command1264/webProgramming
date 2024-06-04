package com.github.command1264.webProgramming.dao;

public enum SqlTableEnum {
    accountInfo("accountInfo"),
    usersChatRooms("usersChatRooms"),
    cookieSessions("cookieSessions");

    private String name = null;
    SqlTableEnum(String name) {
        this.name = name;
    }
    public String getName() {
        return this.name;
    }
}
