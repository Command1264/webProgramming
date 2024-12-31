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

    @GetMapping("/ping")
    public ReturnJsonObject ping() {
        if (printLog) Printer.println("Pong!");
        return new ReturnJsonObject().setSuccessAndData("alive");
    }

    @PostMapping("/test")
    public String test(@RequestBody String json) {
//        return String.valueOf(messagesDao.systemSendMessage("room_b1a7ec39_7f69_40f3_baa5_e992ac55e69b", "create ChatRoom", "create"));
        return "test";
    }



    @PostMapping("/api/v1/createAccount")
    public ReturnJsonObject createAccount(@RequestBody String json) {
        if (printLog) Printer.println("createAccount!");
        return accountService.createAccount(json);
    }

    @PostMapping("/api/v1/loginAccount")
    public ReturnJsonObject loginAccount(@RequestBody String json) {
        if (printLog) Printer.println("loginAccount!");
        return accountService.loginAccount(json);
    }

    @PostMapping("/api/v1/changeToken")
    public ReturnJsonObject changeToken(@RequestBody String json) {
        if (printLog) Printer.println("changeToken!");
        return accountService.changeToken(json);
    }

    @PostMapping("/api/v1/getContainsUser")
    public ReturnJsonObject getContainsUser(@RequestBody String json) {
        if (printLog) Printer.println("getContainsUser!");
        return accountService.getContainsUser(json);
    }

    @PostMapping("/api/v1/deleteAccount")
    public ReturnJsonObject deleteAccount(@RequestBody String json) {
        if (printLog) Printer.println("deleteAccount!");
        return accountService.deleteAccount(json);
    }



    @PostMapping("/api/v1/getUserChatRoom")
    public ReturnJsonObject getUserChatRoom(@RequestBody String json) {
        if (printLog) Printer.println("getUserChatRoom!");
        return userChatRoomService.getUserChatRoom(json);
    }

    @PostMapping("/api/v1/createUserChatRoom")
    public ReturnJsonObject createUserChatRoom( @RequestBody String json) {
        if (printLog) Printer.println("createUserChatRoom!");
        return userChatRoomService.createUserChatRoom(json);
    }

//    @PostMapping("/api/v1/deleteUserChatRoom")
//    public ReturnJsonObject deleteUserChatRoom(@RequestBody String json) {
//        if (printLog) Printer.println("deleteUserChatRoom!");
//        return userChatRoomService.deleteUserChatRoom(json);
//    }



    @PostMapping("/api/v1/getUserChatRoomChats")
    public ReturnJsonObject getUserChatRoomChats(@RequestBody String json) {
        if (printLog) Printer.println("getUserChatRoomChats!");
        return messagesService.getUserChatRoomChats(json);
    }

    @PutMapping("/api/v1/userSendMessage")
    public ReturnJsonObject userSendMessage(@RequestBody String json) {
        if (printLog) Printer.println("userSendMessage!");
        return messagesService.userSendMessage(json);
    }

    @PostMapping("/api/v1/getUserReceiveMessage")
    public ReturnJsonObject getUserReceiveMessage(@RequestBody String json) {
        if (printLog) Printer.println("getUserReceiveMessage!");
        return messagesService.getUserReceiveMessage(json);
    }

    @PostMapping("/api/v1/userReadMessage")
    public ReturnJsonObject userReadMessage(@RequestBody String json) {
        if (printLog) Printer.println("userReadMessage!");
        return messagesService.userReadMessage(json);
    }



    @PostMapping("/api/v1/messageReplayAi")
    public ReturnJsonObject messageReplayAi(@RequestBody String json) {
        if (printLog) Printer.println("messageReplayAi!");
        return messagesService.messageReplayAi(json);
    }

}
