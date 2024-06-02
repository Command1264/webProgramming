package com.github.command1264.webProgramming.controller;


import com.github.command1264.webProgramming.service.MessagesService;
import com.github.command1264.webProgramming.messages.ReturnJsonObject;
import com.github.command1264.webProgramming.service.AccountService;
import com.github.command1264.webProgramming.service.UsersChatRoomService;
import com.github.command1264.webProgramming.dao.SqlDao;
import com.google.gson.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class HttpController {
    private final Gson gson = new Gson();
    @Autowired
    private SqlDao sqlDao;
    @Autowired
    private AccountService accountService;
    @Autowired
    private UsersChatRoomService usersChatRoomService;
    @Autowired
    private MessagesService messagesService;

//    public SpringController() {
////        sqlDao = new SqlDao("jdbc:mysql://localhost:3306/webprogramming?serverTimezone=Asia/Taipei&characterEncoding=utf-8", "root", "Margaret20070922");
//        accountService = new AccountService(gson, sqlDao);
//        usersChatRoomService = new UsersChatRoomService(gson, sqlDao);
//    }

    @GetMapping("/ping")
    public String ping() {
        System.out.println("Ping!");
        ReturnJsonObject returnJsonObject = new ReturnJsonObject();
        returnJsonObject.setSuccess(true);
        returnJsonObject.setData("alive");
        return gson.toJson(returnJsonObject, ReturnJsonObject.class);
    }

    @PostMapping("/test")
    public String test(@RequestBody String json) {
        JsonObject jsonObject = gson.fromJson(json, JsonObject.class);
        return sqlDao.test(jsonObject.get("id").getAsString());
    }

    @PostMapping("/api/v1/createAccount")
    public ReturnJsonObject createUser(@RequestBody String json) {
        return accountService.createAccount(json);
    }

    @GetMapping("/api/v1/getUser")
    public ReturnJsonObject getUser(@RequestBody String json) {
        return accountService.getUser(json);
    }

    @GetMapping("/api/v1/getAccount")
    public ReturnJsonObject getAccount(@RequestBody String json) {
        return accountService.getAccount(json);
    }

    @GetMapping("/api/v1/getUserChatRoom")
    public ReturnJsonObject getUserChatRoom(@RequestBody String json) {
        return usersChatRoomService.getUsersChatRoom(json);
    }

    @PostMapping("/api/v1/createUserChatRoom")
    public ReturnJsonObject createUserChatRoom(@RequestBody String json) {
        return usersChatRoomService.createUsersChatRoom(json);
    }

    @GetMapping("/api/v1/getUsersChatRoomChat")
    public ReturnJsonObject getUsersChatRoomChat(@RequestBody String json) {
        return usersChatRoomService.getUsersChatRoomChat(json);
    }

    @PutMapping("/api/v1/userSendMessage")
    public ReturnJsonObject userSendMessage(@RequestBody String json) {
        return messagesService.userSendMessage(json);
    }

    @GetMapping("/api/v1/getUserReceiveMessage")
    public ReturnJsonObject getUserReceiveMessage(@RequestBody String json) {
        return messagesService.getUserReceiveMessage(json);
    }

}
