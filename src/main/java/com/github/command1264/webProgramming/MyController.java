package com.github.command1264.webProgramming;


import com.google.gson.Gson;
import com.google.gson.JsonObject;
import jakarta.annotation.Nullable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.*;
import java.util.UUID;

@RestController
public class MyController {
    private final Gson gson = new Gson();
    private Connection conn = null;

    public MyController() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("找不到JDBC！");
            e.printStackTrace();
        }
        try {
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/webprogramming?serverTimezone=UTC", "root", "Margaret20070922");
        } catch (SQLException e) {
            System.err.println("無法連接資料庫！");
            e.printStackTrace();
        }
    }

    @RequestMapping("/ping")
    public String ping() {
        System.out.println("Ping!");
        return "{\"alive\":true}";
    }

    @RequestMapping("/test")
    public void test(@RequestBody String json) {
        JsonObject jsonObject = gson.fromJson(json, JsonObject.class);
        System.out.println("id: " + checkRepeat("accountInfo", "id", jsonObject.get("id").getAsString()));
        System.out.println("loginAccount: " + checkRepeat("accountInfo", "loginAccount", jsonObject.get("loginAccount").getAsString()));
    }

    @RequestMapping("/api/v1/createUser")
    public String createUser(@RequestBody String json) {
        Account account = gson.fromJson(json, Account.class);
        System.out.println(gson.toJson(account, Account.class));

        JsonObject jsonObject = new JsonObject();
        if (conn == null) {
            jsonObject.addProperty("success", false);
            jsonObject.addProperty("errorMessage", "未連接資料庫");
            return gson.toJson(jsonObject, JsonObject.class);
        }
        try (Statement stmt = conn.createStatement()) {
            for (String key : new String[]{"id", "loginAccount"}) {
                if (checkRepeat("accountInfo", key, account.get(key))) {
                    jsonObject.addProperty("success", false);
                    jsonObject.addProperty("errorMessage", key + "已經有人使用");
                    return gson.toJson(jsonObject, JsonObject.class);
                }
            }
            int count = stmt.executeUpdate(
                    String.format("insert into accountInfo values(\"%s\", \"%s\", \"%s\", \"%s\")",
                            account.getId(),
                            account.getName(),
                            account.getLoginAccount(),
                            account.getLoginPassword()
            ));
            if (count == 1) jsonObject.addProperty("success", true);
            else {
                jsonObject.addProperty("success", false);
                jsonObject.addProperty("errorMessage", "無法新增帳戶");
            }
        } catch (Exception e) {
            jsonObject.addProperty("success", false);
            jsonObject.addProperty("errorMessage", "例外");
            jsonObject.addProperty("exception", e.getMessage());
        }
        return gson.toJson(jsonObject, JsonObject.class);
    }

    private boolean checkRepeat(String table, String key, String value) {
        if (conn == null) return true;
        try (Statement stmt = conn.createStatement()) {
            ResultSet set = stmt.executeQuery(String.format("select * from %s where %s=\"%s\"", table, key, value));
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


    @RequestMapping("/api/v1/getUserName")
    public String getUserName(@RequestBody String json) {
        JsonObject jsonObject = gson.fromJson(json, JsonObject.class);

        JsonObject returnJsonObject = new JsonObject();
        if (conn == null) {
            returnJsonObject.addProperty("success", false);
            returnJsonObject.addProperty("errorMessage", "未連接資料庫");
            return gson.toJson(returnJsonObject, JsonObject.class);
        }
        try (Statement stmt = conn.createStatement()) {
            ResultSet set = stmt.executeQuery(String.format("select * from accountInfo where id=\"%s\"", jsonObject.get("id").getAsString()));
            int size = 0;
            String name = "";
            while(set.next()) {
                name = set.getString("name");
                ++size;
            }
            if (size == 1) {
                returnJsonObject.addProperty("success", true);
                returnJsonObject.addProperty("name", name);
            } else if (size == 0) {
                returnJsonObject.addProperty("success", false);
                returnJsonObject.addProperty("errorMessage", "找不到此id");
            } else {
                returnJsonObject.addProperty("success", false);
                returnJsonObject.addProperty("errorMessage", "此id有多個name");
            }
        } catch (Exception e) {
            returnJsonObject.addProperty("success", false);
            returnJsonObject.addProperty("errorMessage", "例外");
            returnJsonObject.addProperty("exception", e.getMessage());
        }
        return gson.toJson(returnJsonObject, JsonObject.class);
    }

    public String getUser(@RequestBody String json) {
        JsonObject jsonObject = new JsonObject();
        try {
            jsonObject.addProperty("success", true);
        } catch (Exception e) {
            jsonObject.addProperty("success", false);
            jsonObject.addProperty("exception", e.getMessage());
        }
        return gson.toJson(jsonObject, JsonObject.class);
    }

    private @Nullable User sqlGetUser(String id) {
        return sqlGetAccount(id);
    }
    private @Nullable User sqlGetAccount(String id) {
        if (conn == null) return null;

        try (Statement stmt = conn.createStatement()) {
            ResultSet set = stmt.executeQuery(String.format("select * from accountInfo where id=\"%s\"", id));
            int size = 0;
            Account account = null;
            while(set.next()) {
                account = new Account(
                        id,
                        set.getString("name"),
                        set.getString("loginAccount"),
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

    @RequestMapping("/api/v1/getUserChatRoom")
    public String getUserChatRoom(@RequestBody String json) {
        JsonObject jsonObject = gson.fromJson(json, JsonObject.class);

//        String senderId = jsonObject.get("sender").getAsString();
//        String receiverId = jsonObject.get("receiver").getAsString();
//        User sender = new User()
//        User receiver = gson.fromJson(, User.class);

        return "";
    }
    @RequestMapping("/api/v1/createUserChatRoom")
    public String createUserChatRoom(@RequestBody String json) {
        JsonObject jsonObject = new JsonObject();
        try (Statement stmt = conn.createStatement()) {
            UUID uuid = null;
            do {
                uuid = UUID.randomUUID();
            } while(checkRepeat("userchatrooms", "uuid", uuid.toString()));
            int updateRoom = stmt.executeUpdate( String.format("insert into userChatRooms values(%s, %s)", uuid.toString()));
            int createRoom = stmt.executeUpdate( String.format(
                    "create table %s values(" +
                            "sender varchar(64) not null," +
                            "message text not null" +
                            ")",
                    uuid.toString()));

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        try {
            jsonObject.addProperty("success", true);
        } catch (Exception e) {
            jsonObject.addProperty("success", false);
            jsonObject.addProperty("exception", e.getMessage());
        }
        return gson.toJson(jsonObject, JsonObject.class);
    }

    @RequestMapping("/api/v1/userSendMessage")
    public String userSendMessage() {
        JsonObject jsonObject = new JsonObject();
        try {
            jsonObject.addProperty("success", true);
        } catch (Exception e) {
            jsonObject.addProperty("success", false);
            jsonObject.addProperty("exception", e.getMessage());
        }
        return gson.toJson(jsonObject, JsonObject.class);
    }

    @RequestMapping("/api/v1/getUserReceiveMessage")
    public String getUserReceiveMessage() {
        JsonObject jsonObject = new JsonObject();
        try {
            jsonObject.addProperty("success", true);
        } catch (Exception e) {
            jsonObject.addProperty("success", false);
            jsonObject.addProperty("exception", e.getMessage());
        }
        return gson.toJson(jsonObject, JsonObject.class);
    }
}
