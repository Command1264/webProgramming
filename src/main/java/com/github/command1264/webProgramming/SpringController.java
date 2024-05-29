package com.github.command1264.webProgramming;


import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import jakarta.annotation.Nullable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@RestController
public class SpringController {
    private final Gson gson = new Gson();
    private Connection conn = null;
    private SQLController sqlController = null;

    public SpringController() {
        sqlController = new SQLController("jdbc:mysql://localhost:3306/webprogramming?serverTimezone=UTC", "root", "Margaret20070922");
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("找不到JDBC！");
            e.printStackTrace();
        }
        try {
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/webprogramming?serverTimezone=UTC", "root", "Margaret20070922");
            conn.setAutoCommit(false);
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

    }

    @RequestMapping("/api/v1/createUser")
    public String createUser(@RequestBody String json) {
        Account account = gson.fromJson(json, Account.class);
        System.out.println(gson.toJson(account, Account.class));

        ReturnJsonObject returnJsonObject = new ReturnJsonObject();
        if (conn == null) {
            returnJsonObject.setSuccess(false);
            returnJsonObject.setErrorMessage("未連接資料庫");
            return gson.toJson(returnJsonObject, ReturnJsonObject.class);
        }
        try (Statement stmt = conn.createStatement()) {
            for (String key : new String[]{"id", "loginAccount"}) {
                if (sqlController.checkRepeat("accountInfo", key, account.get(key))) {
                    returnJsonObject.setSuccess(false);
                    returnJsonObject.setErrorMessage(key + "已經有人使用");
                    return gson.toJson(returnJsonObject, ReturnJsonObject.class);
                }
            }
            int count = stmt.executeUpdate(
                    String.format("insert into accountInfo values('%s', '%s', '%s', '%s')",
                            account.getId(),
                            account.getName(),
                            account.getLoginAccount(),
                            account.getLoginPassword()
            ));
            if (count == 1) {
                returnJsonObject.setSuccess(true);
                conn.commit();
            } else {
                returnJsonObject.setSuccess(false);
                returnJsonObject.setErrorMessage("無法新增帳戶");
                conn.rollback();
            }
        } catch (Exception e) {
            returnJsonObject.setSuccess(false);
            returnJsonObject.setErrorMessage("例外");;
            returnJsonObject.setException(e.getMessage());
        }
        return gson.toJson(returnJsonObject, ReturnJsonObject.class);
    }



    @RequestMapping("/api/v1/getUserName")
    public String getUserName(@RequestBody String json) {
        ReturnJsonObject returnJsonObject = new ReturnJsonObject();;
        if (conn == null) {
            returnJsonObject.setSuccess(false);
            returnJsonObject.setErrorMessage("未連接資料庫");
             return gson.toJson(returnJsonObject, ReturnJsonObject.class);
        }
        JsonObject jsonObject = gson.fromJson(json, JsonObject.class);

        try (Statement stmt = conn.createStatement()) {
            ResultSet set = stmt.executeQuery(String.format("select * from accountInfo where id='%s'", jsonObject.get("id").getAsString()));
            int size = 0;
            String name = "";
            while(set.next()) {
                name = set.getString("name");
                ++size;
            }
            if (size == 1) {
                returnJsonObject.setSuccess(true);
                returnJsonObject.setData(name);
            } else if (size == 0) {
                returnJsonObject.setSuccess(false);
                returnJsonObject.setErrorMessage("找不到此id");
            } else {
                returnJsonObject.setSuccess(false);
                returnJsonObject.setErrorMessage("此id有多個name");
            }
        } catch (Exception e) {
            returnJsonObject.setSuccess(false);
            returnJsonObject.setErrorMessage("例外");
            returnJsonObject.setException(e.getMessage());
        }
         return gson.toJson(returnJsonObject, ReturnJsonObject.class);
    }

    public String getUser(@RequestBody String json) {
        ReturnJsonObject returnJsonObject = new ReturnJsonObject();;
        if (conn == null) {
            returnJsonObject.setSuccess(false);
            returnJsonObject.setErrorMessage("未連接資料庫");
             return gson.toJson(returnJsonObject, ReturnJsonObject.class);
        }
        try {
            returnJsonObject.setSuccess(true);
        } catch (Exception e) {
            returnJsonObject.setSuccess(false);
            returnJsonObject.setErrorMessage("例外");
            returnJsonObject.setException(e.getMessage());
        }
         return gson.toJson(returnJsonObject, ReturnJsonObject.class);
    }

    @RequestMapping("/api/v1/getUserChatRoom")
    public String getUserChatRoom(@RequestBody String json) {
        ReturnJsonObject returnJsonObject = new ReturnJsonObject();;
        if (conn == null) {
            returnJsonObject.setSuccess(false);
            returnJsonObject.setErrorMessage("未連接資料庫");
             return gson.toJson(returnJsonObject, ReturnJsonObject.class);
        }

        JsonObject jsonObject = gson.fromJson(json, JsonObject.class);
        JsonArray users = jsonObject.getAsJsonArray("users");
        List<String> usersIdList = new ArrayList<>();

        for (JsonElement jsonElement : users.asList()) {
            try {
                User user = sqlController.getUser(jsonElement.getAsString());
                if (user == null) continue;

//                usersList.add(user);
                usersIdList.add(user.id);
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }
        }

        if (usersIdList.isEmpty()) {
            returnJsonObject.setSuccess(false);
            returnJsonObject.setErrorMessage("人數不能為0");
             return gson.toJson(returnJsonObject, ReturnJsonObject.class);
        }
        // 排序，讓之後的聊天室更好判斷
//        usersList.sort(Comparator.comparing((User user) -> user.id));
        usersIdList.sort(Comparator.naturalOrder());
        try (Statement stmt = conn.createStatement()){
            String usersIdListJsonStr = gson.toJson(gson.toJsonTree(usersIdList, new TypeToken<List<String>>() {}.getType()).getAsJsonArray(), JsonArray.class);
            ResultSet set = stmt.executeQuery(String.format("select * from userchatrooms where users='%s'", usersIdListJsonStr));
            int size = 0;
            UUID chatUUID = null;
            while (set.next()) {
                try {
                    chatUUID = UUID.fromString(set.getString("uuid"));
                } catch (IllegalArgumentException ignored) {}
                ++size;
            }
            if (size == 0 || chatUUID == null) {
                returnJsonObject.setSuccess(false);
                returnJsonObject.setErrorMessage("找不到聊天室");
                 return gson.toJson(returnJsonObject, ReturnJsonObject.class);
            } else if (size == 1) {
                returnJsonObject.setSuccess(true);
                returnJsonObject.setData(chatUUID.toString());
                 return gson.toJson(returnJsonObject, ReturnJsonObject.class);
            } else {
                returnJsonObject.setSuccess(false);
                returnJsonObject.setErrorMessage("此使用者群組有多個聊天室");
                 return gson.toJson(returnJsonObject, ReturnJsonObject.class);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "";
    }

    @RequestMapping("/api/v1/createUserChatRoom")
    public String createUserChatRoom(@RequestBody String json) {
        ReturnJsonObject returnJsonObject = new ReturnJsonObject();;
        if (conn == null) {
            returnJsonObject.setSuccess(false);
            returnJsonObject.setErrorMessage("未連接資料庫");
             return gson.toJson(returnJsonObject, ReturnJsonObject.class);
        }

        JsonObject jsonObject = gson.fromJson(json, JsonObject.class);
        JsonArray users = jsonObject.getAsJsonArray("users");
//        List<User> usersList = new ArrayList<>();
        List<String> usersIdList = new ArrayList<>();

        for (JsonElement jsonElement : users.asList()) {
            try {
                User user = sqlController.getUser(jsonElement.getAsString());
                if (user == null) continue;

//                usersList.add(user);
                usersIdList.add(user.id);
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }
        }

        if (usersIdList.isEmpty()) {
            returnJsonObject.setSuccess(false);
            returnJsonObject.setErrorMessage("人數不能為0");
             return gson.toJson(returnJsonObject, ReturnJsonObject.class);
        }
        // 排序，讓之後的聊天室更好判斷
//        usersList.sort(Comparator.comparing((User user) -> user.id));
        usersIdList.sort(Comparator.naturalOrder());

        try (Statement stmt = conn.createStatement()) {

//            String usersListJsonStr = gson.toJson(gson.toJsonTree(usersList, new TypeToken<List<User>>() {}.getType()).getAsJsonArray(), JsonArray.class);
            String usersIdListJsonStr = gson.toJson(gson.toJsonTree(usersIdList, new TypeToken<List<String>>() {}.getType()).getAsJsonArray(), JsonArray.class);
            // 檢查是否有重複的聊天室
            if (sqlController.checkRepeat("userchatrooms", "users", usersIdListJsonStr)) {
                returnJsonObject.setSuccess(false);
                returnJsonObject.setErrorMessage("已有聊天室");
                 return gson.toJson(returnJsonObject, ReturnJsonObject.class);
            }

            UUID uuid;
            do {
                uuid = UUID.randomUUID();
            } while(sqlController.checkRepeat("userchatrooms", "uuid", uuid.toString()));
            String roomName = String.format("room_%s", uuid.toString().replaceAll("-", "_"));


            int updateRoom = stmt.executeUpdate( String.format(
                    "insert into userChatRooms values('%s', '%s')",
                    uuid.toString(),
                    usersIdListJsonStr
            ));
            stmt.executeUpdate( String.format(
                    "create table %s(" +
                            "sender varchar(64) not null," +
                            "message text not null," +
                            "type varchar(20) not null default 'text'," +
                            "time text not null" +
                            ");",
                    roomName
            ));
            if (updateRoom != 1 || !sqlController.findTableName(roomName)) {
                conn.rollback();
                returnJsonObject.setSuccess(false);
                returnJsonObject.setErrorMessage("資料庫添加失敗，請稍後再試");
                 return gson.toJson(returnJsonObject, ReturnJsonObject.class);
            } else {
                returnJsonObject.setSuccess(true);
                conn.commit();
            }

        } catch (SQLException e) {
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
            returnJsonObject.setSuccess(false);
            returnJsonObject.setErrorMessage("例外");
            returnJsonObject.setException(e.getMessage());
        }

        return gson.toJson(returnJsonObject, ReturnJsonObject.class);
    }

    @RequestMapping("/api/v1/getUsersChatRoomChat")
    public String getUsersChatRoomChat(@RequestBody String json) {
        ReturnJsonObject returnJsonObject = new ReturnJsonObject();;
        if (conn == null) {
            returnJsonObject.setSuccess(false);
            returnJsonObject.setErrorMessage("未連接資料庫");
            return gson.toJson(returnJsonObject, ReturnJsonObject.class);
        }

        JsonObject jsonObject = gson.fromJson(json, JsonObject.class);
        JsonArray users = jsonObject.getAsJsonArray("users");
        List<String> usersIdList = new ArrayList<>();

        for (JsonElement jsonElement : users.asList()) {
            try {
                User user = sqlController.getUser(jsonElement.getAsString());
                if (user == null) continue;

//                usersList.add(user);
                usersIdList.add(user.id);
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }
        }

        if (usersIdList.isEmpty()) {
            returnJsonObject.setSuccess(false);
            returnJsonObject.setErrorMessage("人數不能為0");
            return gson.toJson(returnJsonObject, ReturnJsonObject.class);
        }
        // 排序，讓之後的聊天室更好判斷
//        usersList.sort(Comparator.comparing((User user) -> user.id));
        usersIdList.sort(Comparator.naturalOrder());
        try (Statement stmt = conn.createStatement()){
            String usersIdListJsonStr = gson.toJson(gson.toJsonTree(usersIdList, new TypeToken<List<String>>() {}.getType()).getAsJsonArray(), JsonArray.class);
            ResultSet set = stmt.executeQuery(String.format("select * from userchatrooms where users='%s'", usersIdListJsonStr));
            int size = 0;
            UUID chatUUID = null;
            while (set.next()) {
                try {
                    chatUUID = UUID.fromString(set.getString("uuid"));
                } catch (IllegalArgumentException ignored) {}
                ++size;
            }

            if (size == 0 || chatUUID == null) {
                returnJsonObject.setSuccess(false);
                returnJsonObject.setErrorMessage("找不到聊天室");
                 return gson.toJson(returnJsonObject, ReturnJsonObject.class);
            } else if (size != 1) {
                returnJsonObject.setSuccess(false);
                returnJsonObject.setErrorMessage("此使用者群組有多個聊天室");
                 return gson.toJson(returnJsonObject, ReturnJsonObject.class);
            }

            set = stmt.executeQuery(String.format("select * from %s", "room_" + chatUUID.toString().replaceAll("-", "_")));
            List<MessageSendReceive> messageList = new ArrayList<>();
            while (set.next()) {
                messageList.add(new MessageSendReceive(
                        set.getString("sender"),
                        set.getString("message"),
                        set.getString("type"),
                        set.getString("time")
                ));
            }
            String messageListStr = gson.toJson(gson.toJsonTree(messageList,
                        new TypeToken<List<MessageSendReceive>>() {}.getType())
                    .getAsJsonArray(), JsonArray.class);
            returnJsonObject.setSuccess(true);
            returnJsonObject.setData(messageListStr);
        } catch (SQLException e) {
            returnJsonObject.setSuccess(false);
            returnJsonObject.setErrorMessage("例外");
            returnJsonObject.setException(e.getMessage());
        }

        return gson.toJson(returnJsonObject, ReturnJsonObject.class);
    }

    @RequestMapping("/api/v1/userSendMessage")
    public String userSendMessage(@RequestBody String json) {

        ReturnJsonObject returnJsonObject = new ReturnJsonObject();;
        if (conn == null) {
            returnJsonObject.setSuccess(false);
            returnJsonObject.setErrorMessage("未連接資料庫");
             return gson.toJson(returnJsonObject, ReturnJsonObject.class);
        }

        JsonObject jsonObject = gson.fromJson(json, JsonObject.class);
        try {
            returnJsonObject.setSuccess(true);
        } catch (Exception e) {
            returnJsonObject.setSuccess(false);
            returnJsonObject.setException(e.getMessage());
        }
         return gson.toJson(returnJsonObject, ReturnJsonObject.class);
    }

    @RequestMapping("/api/v1/getUserReceiveMessage")
    public String getUserReceiveMessage(@RequestBody String json) {
        ReturnJsonObject returnJsonObject = new ReturnJsonObject();;
        if (conn == null) {
            returnJsonObject.setSuccess(false);
            returnJsonObject.setErrorMessage("未連接資料庫");
             return gson.toJson(returnJsonObject, ReturnJsonObject.class);
        }

        JsonObject jsonObject = gson.fromJson(json, JsonObject.class);
        try {
            returnJsonObject.setSuccess(true);
        } catch (Exception e) {
            returnJsonObject.setSuccess(false);
            returnJsonObject.setException(e.getMessage());
        }
         return gson.toJson(returnJsonObject, ReturnJsonObject.class);
    }
}
