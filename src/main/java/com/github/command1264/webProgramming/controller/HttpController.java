package com.github.command1264.webProgramming.controller;


import com.github.command1264.webProgramming.dao.AccountDao;
import com.github.command1264.webProgramming.dao.MessagesDao;
import com.github.command1264.webProgramming.dao.UsersChatRoomDao;
import com.github.command1264.webProgramming.service.MessagesService;
import com.github.command1264.webProgramming.messages.ReturnJsonObject;
import com.github.command1264.webProgramming.service.AccountService;
import com.github.command1264.webProgramming.service.UsersChatRoomService;
import com.github.command1264.webProgramming.dao.SqlDao;
import com.github.command1264.webProgramming.util.Printer;
import com.google.gson.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
//@CrossOrigin(origins = "*")
public class HttpController {
    private final Gson gson = new Gson();
    @Autowired
    private SqlDao sqlDao;
    @Autowired
    private AccountDao accountDao;
    @Autowired
    private UsersChatRoomDao usersChatRoomDao;
    @Autowired
    private MessagesDao messagesDao;

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
//    @MessageMapping("/hello")
//    @SendTo("/topic/hello")
//    public ReturnJsonObject webSocketTest(String str) {
//        try {
//            Thread.sleep(1000);
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }
//        return new ReturnJsonObject(true, null, null, str);
//    }

//    @CrossOrigin(origins = "*")
    @CrossOrigin(origins = "*")
    @GetMapping("/ping")
    public String ping() {
//        Printer.println("Pong!");
        ReturnJsonObject returnJsonObject = new ReturnJsonObject();
        returnJsonObject.setSuccess(true);
        returnJsonObject.setData("alive");
        return gson.toJson(returnJsonObject, ReturnJsonObject.class);
    }

    @PostMapping("/test")
    @CrossOrigin(origins = "*")
    public String test(@RequestBody String json) {
        JsonObject jsonObject = gson.fromJson(json, JsonObject.class);
        return gson.toJson(jsonObject.get("test").getAsString());
//        return accountDao.getAccountWithUserId(jsonObject.get(JsonKeyEnum.id.name()).getAsString()).serialize();
//        return sqlDao.test(jsonObject.get(JsonKeyEnum.id.name()).getAsString());
//        JsonElement jsonElement = new JsonPrimitive("String");
//        return gson.toJson(jsonElement);
    }



    @PostMapping("/api/v1/createAccount")
    @CrossOrigin(origins = "*")
    public ReturnJsonObject createAccount(@RequestBody String json) {
        return accountService.createAccount(json);
    }

    @PostMapping("/api/v1/loginAccount")
    @CrossOrigin(origins = "*")
    public ReturnJsonObject loginAccount(@RequestBody String json) {
        return accountService.loginAccount(json);
    }

    @PostMapping("/api/v1/changeToken")
    @CrossOrigin(origins = "*")
    public ReturnJsonObject changeToken(@RequestBody String json) {
        return accountService.changeToken(json);
    }

    @GetMapping("/api/v1/getUser")
    @CrossOrigin(origins = "*")
    @Deprecated
    public ReturnJsonObject getUser(@RequestBody String json) {
        return new ReturnJsonObject(false, "", "", "");
//        return accountService.getUser(json);
    }

    @GetMapping("/api/v1/getAccount")
    @CrossOrigin(origins = "*")
    public ReturnJsonObject getAccount(@RequestBody String json) {
        return accountService.getAccount(json);
    }



    @GetMapping("/api/v1/getUserChatRoom")
    @CrossOrigin(origins = "*")
    public ReturnJsonObject getUserChatRoom(@RequestBody String json) {
        return usersChatRoomService.getUsersChatRoom(json);
    }

    @PostMapping("/api/v1/createUserChatRoom")
    @CrossOrigin(origins = "*")
    public ReturnJsonObject createUserChatRoom( @RequestBody String json) {
        return usersChatRoomService.createUsersChatRoom(json);
    }



    @GetMapping("/api/v1/getUsersChatRoomChats")
    @CrossOrigin(origins = "*")
    public ReturnJsonObject getUsersChatRoomChats(@RequestBody String json) {
        return messagesService.getUsersChatRoomChats(json);
    }

    @PutMapping("/api/v1/userSendMessage")
    @CrossOrigin(origins = "*")
    public ReturnJsonObject userSendMessage(@RequestBody String json) {
        return messagesService.userSendMessage(json);
    }

    @GetMapping("/api/v1/getUserReceiveMessage")
    @CrossOrigin(origins = "*")
    public ReturnJsonObject getUserReceiveMessage(@RequestBody String json) {
        return messagesService.getUserReceiveMessage(json);
    }

}
