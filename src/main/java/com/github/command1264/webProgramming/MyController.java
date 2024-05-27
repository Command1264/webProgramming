package com.github.command1264.webProgramming;


import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

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
            conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/", "root", "Margaret20070922");
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

    @RequestMapping("/api/v1/createUser")
    public String createUser(@RequestBody String json) {
        User user = gson.fromJson(json, User.class);
        System.out.println(gson.toJson(user, User.class));

        JsonObject jsonObject = new JsonObject();
        try {
            jsonObject.addProperty("success", true);
        } catch (Exception e) {
            jsonObject.addProperty("success", false);
            jsonObject.addProperty("exception", e.getMessage());
        }
        return gson.toJson(jsonObject, JsonObject.class);
    }


    @RequestMapping("/api/v1/getUserName")
    public String getUserName(String id) {
        return "";
    }

    @RequestMapping("/api/v1/getUserChatRoom")
    public String getUserChatRoom(@RequestBody String json) {
        JsonObject jsonObject = gson.fromJson(json, JsonObject.class);

        User sender = gson.fromJson(jsonObject.getAsJsonObject("sender"), User.class);
        User receiver = gson.fromJson(jsonObject.getAsJsonObject("receiver"), User.class);

        return "";
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
        return "";
    }
}
