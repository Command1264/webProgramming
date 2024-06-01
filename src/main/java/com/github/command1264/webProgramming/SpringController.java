package com.github.command1264.webProgramming;


import com.github.command1264.dao.SqlDao;
import com.github.command1264.messages.ReturnJsonObject;
import com.github.command1264.service.AccountService;
import com.github.command1264.service.UsersCahtRoomService;
import com.google.gson.*;
import org.springframework.web.bind.annotation.*;

@RestController
public class SpringController {
    private final Gson gson = new Gson();
    private SqlDao sqlDao = null;
    private AccountService accountService = null;
    private UsersCahtRoomService usersCahtRoomService = null;

    public SpringController() {
        sqlDao = new SqlDao("jdbc:mysql://localhost:3306/webprogramming?serverTimezone=Asia/Taipei&characterEncoding=utf-8", "root", "Margaret20070922");
        accountService = new AccountService(gson, sqlDao);
        usersCahtRoomService = new UsersCahtRoomService(gson, sqlDao);
    }

    @GetMapping("/ping")
    public String ping() {
        System.out.println("Ping!");
        ReturnJsonObject returnJsonObject = new ReturnJsonObject();
        returnJsonObject.setSuccess(true);
        returnJsonObject.setData("alive");
        return gson.toJson(returnJsonObject, ReturnJsonObject.class);
    }

    @PostMapping("/test")
    public void test(@RequestBody String json) {

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
        return usersCahtRoomService.getUsersChatRoom(json);
    }

    @PostMapping("/api/v1/createUserChatRoom")
    public ReturnJsonObject createUserChatRoom(@RequestBody String json) {
        return usersCahtRoomService.createUsersChatRoom(json);
    }

    @GetMapping("/api/v1/getUsersChatRoomChat")
    public ReturnJsonObject getUsersChatRoomChat(@RequestBody String json) {
        return usersCahtRoomService.getUsersChatRoomChat(json);
    }

    @PutMapping("/api/v1/userSendMessage")
    public ReturnJsonObject userSendMessage(@RequestBody String json) {
        return usersCahtRoomService.userSendMessage(json);
    }

    @GetMapping("/api/v1/getUserReceiveMessage")
    public ReturnJsonObject getUserReceiveMessage(@RequestBody String json) {
        return usersCahtRoomService.getUserReceiveMessage(json);
    }

}
