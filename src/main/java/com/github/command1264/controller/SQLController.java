package com.github.command1264.controller;

import com.github.command1264.accouunt.Account;
import com.github.command1264.accouunt.User;
import jakarta.annotation.Nullable;

import java.sql.*;
import java.util.UUID;

public class SQLController {
    private Connection conn = null;

    public SQLController(String url, String user, String password) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("找不到JDBC！");
            e.printStackTrace();
        }
        try {
            conn = DriverManager.getConnection(url, user,password);
            conn.setAutoCommit(false);
        } catch (SQLException e) {
            System.err.println("無法連接資料庫！");
            e.printStackTrace();
        }
    }
    public SQLController() {
        this("jdbc:mysql://localhost:3306/webprogramming?serverTimezone=UTC", "root", "Margaret20070922");
    }
    public boolean checkConnect() {
        return (conn != null);
    }

    public boolean findTableName(String tableName) {
        if (!checkConnect() || tableName == null) return false;

        try (Statement stmt = conn.createStatement()) {
            ResultSet set = stmt.getConnection().getMetaData().getTables(null, null, tableName, null);
            while(set.next()) {
                String name = set.getString("TABLE_NAME");
                if (tableName.equalsIgnoreCase(name)) return true;
            }
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public @Nullable User getUser(String id) {
        return getAccount(id);
    }
    public @Nullable Account getAccount(String id) {
        if (!checkConnect()) return null;

        try (Statement stmt = conn.createStatement()) {
            ResultSet set = stmt.executeQuery(String.format("select * from accountInfo where id='%s'", id));
            int size = 0;
            Account account = null;
            while(set.next()) {
                account = new Account(
                        id,
                        set.getString("name"),
                        set.getString("loginAccount"),
                        set.getString("loginPassword"),
                        set.getString("loginPassword")
                );
                ++size;
            }
            if (size == 1) return account;
            else return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean checkRepeat(String table, String key, String value) {
        if (!checkConnect()) return true;

        try (Statement stmt = conn.createStatement()) {
            ResultSet set = stmt.executeQuery(String.format("select * from %s where %s='%s'", table, key, value));
            if (set != null) {
                int size = 0;
                while(set.next()) ++size;
                if (size != 0) return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return true;
        }
        return false;
    }

    public UUID getChatRoomUUID(String users) {
        if (!checkConnect()) return null;

        try (Statement stmt = conn.createStatement()){
            UUID chatUUID = null;
            ResultSet set = stmt.executeQuery(String.format("select * from userchatrooms where users='%s';", users));
            int size = 0;
            while (set.next()) {
                chatUUID = UUID.fromString(set.getString("uuid"));
                ++size;
            }
            if (size != 1) return null;
            return chatUUID;
        } catch (SQLException e) {
            return null;
        }
    }
}
