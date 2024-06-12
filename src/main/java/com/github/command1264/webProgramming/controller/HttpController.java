package com.github.command1264.webProgramming.controller;


import com.github.command1264.webProgramming.dao.AccountDao;
import com.github.command1264.webProgramming.dao.MessagesDao;
import com.github.command1264.webProgramming.dao.UserChatRoomDao;
import com.github.command1264.webProgramming.service.MessagesService;
import com.github.command1264.webProgramming.messages.ReturnJsonObject;
import com.github.command1264.webProgramming.service.AccountService;
import com.github.command1264.webProgramming.service.UserChatRoomService;
import com.github.command1264.webProgramming.dao.SqlDao;
import com.github.command1264.webProgramming.util.Printer;
import com.google.gson.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "*")
public class HttpController {
    private final Gson gson = new Gson();
    @Autowired
    private SqlDao sqlDao;
    @Autowired
    private AccountDao accountDao;
    @Autowired
    private UserChatRoomDao userChatRoomDao;
    @Autowired
    private MessagesDao messagesDao;

    @Autowired
    private AccountService accountService;
    @Autowired
    private UserChatRoomService userChatRoomService;
    @Autowired
    private MessagesService messagesService;

    private final boolean printLog = false;

//    public SpringController() {
////        sqlDao = new SqlDao("jdbc:mysql://localhost:3306/webprogramming?serverTimezone=Asia/Taipei&characterEncoding=utf-8", "root", "Margaret20070922");
//        accountService = new AccountService(gson, sqlDao);
//        userChatRoomService = new UserChatRoomService(gson, sqlDao);
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
    @GetMapping("/ping")
    public String ping() {
        if (printLog) Printer.println("Pong!");
        ReturnJsonObject returnJsonObject = new ReturnJsonObject();
        returnJsonObject.setSuccess(true);
        returnJsonObject.setData("alive");
        return gson.toJson(returnJsonObject, ReturnJsonObject.class);
    }

    @PostMapping("/test")
//    @CrossOrigin(origins = "*")
    public String test(@RequestBody String json) {
        return "test";
//        JsonObject jsonObject = gson.fromJson(json, JsonObject.class);
//        return gson.toJson(jsonObject.get("test").getAsString());
//        return accountDao.getAccountWithUserId(jsonObject.get(JsonKeyEnum.id.name()).getAsString()).serialize();
//        return sqlDao.test(jsonObject.get(JsonKeyEnum.id.name()).getAsString());
//        JsonElement jsonElement = new JsonPrimitive("String");
//        return gson.toJson(jsonElement);
    }



    @PostMapping("/api/v1/createAccount")
//    @CrossOrigin(origins = "*")
    public ReturnJsonObject createAccount(@RequestBody String json) {
        if (printLog) Printer.println("createAccount!");
        return accountService.createAccount(json);
    }

    @PostMapping("/api/v1/loginAccount")
//    @CrossOrigin(origins = "*")
    public ReturnJsonObject loginAccount(@RequestBody String json) {
        if (printLog) Printer.println("loginAccount!");
        return accountService.loginAccount(json);
    }

    @PostMapping("/api/v1/changeToken")
//    @CrossOrigin(origins = "*")
    public ReturnJsonObject changeToken(@RequestBody String json) {
        if (printLog) Printer.println("changeToken!");
        return accountService.changeToken(json);
    }

    @PostMapping("/api/v1/getUser")
//    @CrossOrigin(origins = "*")
    @Deprecated
    public ReturnJsonObject getUser(@RequestBody String json) {
        if (printLog) Printer.println("getUser!");
        return new ReturnJsonObject(false, "", "", "");
//        return accountService.getUser(json);
    }

    @PostMapping("/api/v1/getAccount")
//    @CrossOrigin(origins = "*")
    public ReturnJsonObject getAccount(@RequestBody String json) {
        if (printLog) Printer.println("getAccount!");
        return accountService.getAccount(json);
    }



    @PostMapping("/api/v1/getUserChatRoom")
//    @CrossOrigin(origins = "*")
    public ReturnJsonObject getUserChatRoom(@RequestBody String json) {
        if (printLog) Printer.println("getUserChatRoom!");
        return userChatRoomService.getUserChatRoom(json);
    }

    @PostMapping("/api/v1/createUserChatRoom")
//    @CrossOrigin(origins = "*")
    public ReturnJsonObject createUserChatRoom( @RequestBody String json) {
        if (printLog) Printer.println("createUserChatRoom!");
        return userChatRoomService.createUserChatRoom(json);
    }



    @PostMapping("/api/v1/getUserChatRoomChats")
//    @CrossOrigin(origins = "*")
    public ReturnJsonObject getUserChatRoomChats(@RequestBody String json) {
        if (printLog) Printer.println("getUserChatRoomChats!");
        return messagesService.getUserChatRoomChats(json);
    }

    @PutMapping("/api/v1/userSendMessage")
//    @CrossOrigin(origins = "*")
    public ReturnJsonObject userSendMessage(@RequestBody String json) {
        if (printLog) Printer.println("userSendMessage!");
        return messagesService.userSendMessage(json);
    }

    @PostMapping("/api/v1/getUserReceiveMessage")
//    @CrossOrigin(origins = "*")
    public ReturnJsonObject getUserReceiveMessage(@RequestBody String json) {
        if (printLog) Printer.println("getUserReceiveMessage!");
        return messagesService.getUserReceiveMessage(json);
    }

    @PostMapping("/api/v1/userReadMessage")
//    @CrossOrigin(origins = "*")
    public ReturnJsonObject userReadMessage(@RequestBody String json) {
        if (printLog) Printer.println("userReadMessage!");
        return messagesService.userReadMessage(json);
    }

}
