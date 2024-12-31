package com.github.command1264.webProgramming.service;

import com.github.command1264.webProgramming.accouunt.AccountChatRooms;
import com.github.command1264.webProgramming.accouunt.UserAndRooms;
import com.github.command1264.webProgramming.dao.AccountDao;
import com.github.command1264.webProgramming.dao.MessagesDao;
import com.github.command1264.webProgramming.dao.SqlDao;
import com.github.command1264.webProgramming.dao.UserChatRoomDao;
import com.github.command1264.webProgramming.messages.ErrorType;
import com.github.command1264.webProgramming.messages.MessageKeyEnum;
import com.github.command1264.webProgramming.messages.MessageSendReceive;
import com.github.command1264.webProgramming.messages.ReturnJsonObject;
import com.github.command1264.webProgramming.util.DateTimeFormat;
import com.github.command1264.webProgramming.util.JsonChecker;
import com.github.command1264.webProgramming.util.JsonKeyEnum;
import com.github.command1264.webProgramming.util.UsersSorter;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
public class MessagesService {
    private final Gson gson = new Gson();
    @Autowired
    private UsersSorter usersSorter;
    @Autowired
    private SqlDao sqlDao;
    @Autowired
    private AccountDao accountDao;
    @Autowired
    private UserChatRoomDao userChatRoomDao;
    @Autowired
    private MessagesDao messagesDao;

    public ReturnJsonObject userSendMessage(String json) {
        ReturnJsonObject returnJsonObject = new ReturnJsonObject();

        JsonObject jsonObject;
        try {
            jsonObject = gson.fromJson(json, JsonObject.class);
        } catch (Exception e) {
            return returnJsonObject.setSuccessAndErrorMessage(ErrorType.dataNotFound.getMessage());
        }

        String token = null;
        try {
            token = jsonObject.get(JsonKeyEnum.token.name()).getAsString();
        } catch (Exception e) {
            return returnJsonObject.setSuccessAndErrorMessage(ErrorType.cantFindToken.getMessage());
        }

        String tokenId = accountDao.getIdWithToken(token);

        String chatRoomName = null;
        MessageSendReceive messageSendReceive = null;
        try {
            chatRoomName = jsonObject.get(JsonKeyEnum.chatRoomName.name()).getAsString();
            if (!userChatRoomDao.checkHasUserChatRoom(chatRoomName)) {
                return returnJsonObject.setSuccessAndErrorMessage(ErrorType.cantFindChatRoomName.getMessage());
            }
        } catch (Exception e) {
            return returnJsonObject.setSuccessAndErrorMessage(ErrorType.cantFindChatRoomName.getMessage());
        }
        try {
            messageSendReceive = MessageSendReceive.deserialize(jsonObject.getAsJsonObject(JsonKeyEnum.message.name()));
        } catch (Exception e){
            return returnJsonObject.setSuccessAndErrorMessage(ErrorType.cantFindMessage.getMessage());
        }

        UserAndRooms userAndRooms = accountDao.getUserAndRoomsWithUserId(messageSendReceive.getSender());
        if (userAndRooms != null) {
            List<String> chatRoomUserList = userChatRoomDao.getUserChatRoomUsers(chatRoomName);
            if (!Objects.equals(tokenId, userAndRooms.getId()) ||
                    !chatRoomUserList.contains(tokenId)) {
                return returnJsonObject.setSuccessAndErrorMessage(ErrorType.tokenNoPermission.getMessage());
            }
            messageSendReceive.setSender(userAndRooms.getId());
        } else {
            return returnJsonObject.setSuccessAndErrorMessage(ErrorType.cantFindUsers.getMessage());
        }

        if (!messagesDao.userSendMessage(chatRoomName, messageSendReceive)) {
            return returnJsonObject.setSuccessAndErrorMessage(ErrorType.messageSaveFailed.getMessage());
        }

        LocalDateTime lastModify = null;
        try {
            lastModify = LocalDateTime.parse(messageSendReceive.getTime(), DateTimeFormat.formatter);
            if (lastModify.plusMinutes(1).compareTo(LocalDateTime.now()) < 1 ||
                    lastModify.plusMinutes(-1).compareTo(LocalDateTime.now()) > -1) {
                lastModify = LocalDateTime.now();
            }
        } catch (Exception e) {
            return returnJsonObject.setSuccessAndErrorMessage(ErrorType.messageSaveFailed.getMessage());
        }

        if (!userChatRoomDao.setLastModify(chatRoomName, lastModify)) {
            return returnJsonObject.setSuccessAndErrorMessage(ErrorType.messageSaveFailed.getMessage());
        }

        return returnJsonObject.setSuccessAndData("");
    }

    public ReturnJsonObject userReadMessage(String json) {
        ReturnJsonObject returnJsonObject = new ReturnJsonObject();

        JsonObject jsonObject;
        try {
            jsonObject = gson.fromJson(json, JsonObject.class);
        } catch (Exception e) {
            return returnJsonObject.setSuccessAndErrorMessage(ErrorType.dataNotFound.getMessage());
        }

        if (JsonChecker.checkNoKey(jsonObject, JsonKeyEnum.token.name())) {
            return returnJsonObject.setSuccessAndErrorMessage(ErrorType.cantFindToken.getMessage());
        }
        if (JsonChecker.checkNoKey(jsonObject, JsonKeyEnum.chatRoomName.name())) {
            return returnJsonObject.setSuccessAndErrorMessage(ErrorType.cantFindChatRoomName.getMessage());
        }
        String token = null;
        try {
            token = jsonObject.get(JsonKeyEnum.token.name()).getAsString();
        } catch (Exception e) {
            return returnJsonObject.setSuccessAndErrorMessage(ErrorType.cantFindToken.getMessage());
        }
        String tokenId = accountDao.getIdWithToken(token);

        JsonObject chatRooms = null;
        try {
            chatRooms = jsonObject.getAsJsonObject(JsonKeyEnum.chatRoomName.name());
        } catch (Exception e) {
            return returnJsonObject.setSuccessAndErrorMessage(ErrorType.cantFindChatRoomName.getMessage());
        }

        if (chatRooms == null || chatRooms.isEmpty()) {
            return returnJsonObject.setSuccessAndErrorMessage(ErrorType.cantFindChatRoomName.getMessage());
        }

        AccountChatRooms accountChatRooms = accountDao.getAccountChatRooms(tokenId);
        if (accountChatRooms == null) {
            return returnJsonObject.setSuccessAndErrorMessage(ErrorType.tokenIsExpired.getMessage());
        }
        AtomicBoolean successModify = new AtomicBoolean(false);
        chatRooms.asMap().forEach((chatRoomName, chatRoomReadMessageJson) -> {
            try {
                long chatRoomReadMessage = chatRoomReadMessageJson.getAsLong();
                if (accountChatRooms.getChatRooms().containsKey(chatRoomName)) {
                    accountChatRooms.getChatRooms().put(chatRoomName, chatRoomReadMessage);
                }
                successModify.set(true);
            } catch (Exception ignored) {}
        });

        if (!successModify.get()) {
            return returnJsonObject.setSuccessAndErrorMessage(ErrorType.tokenNoPermission.getMessage());
        }
        if (!accountDao.setAccountChatRooms(tokenId, accountChatRooms)) {
            return returnJsonObject.setSuccessAndErrorMessage(ErrorType.sqlUpdateFailed.getMessage());
        }

        return returnJsonObject.setSuccessAndData(accountDao.getAccountChatRooms(tokenId));
    }

    public ReturnJsonObject getUserReceiveMessage(String json) {
        ReturnJsonObject returnJsonObject = new ReturnJsonObject();

        JsonObject jsonObject;
        try {
            jsonObject = gson.fromJson(json, JsonObject.class);
        } catch (Exception e) {
            return returnJsonObject.setSuccessAndErrorMessage(ErrorType.dataNotFound.getMessage());
        }
        if (JsonChecker.checkNoKey(jsonObject, JsonKeyEnum.token.name())) {
            return returnJsonObject.setSuccessAndErrorMessage(ErrorType.cantFindToken.getMessage());
        }
        if (JsonChecker.checkNoKey(jsonObject, JsonKeyEnum.chatRoomName.name())) {
            return returnJsonObject.setSuccessAndErrorMessage(ErrorType.cantFindChatRoomName.getMessage());
        }
        String token = null;
        try {
            token = jsonObject.get(JsonKeyEnum.token.name()).getAsString();
        } catch (Exception e) {
            return returnJsonObject.setSuccessAndErrorMessage(ErrorType.cantFindToken.getMessage());
        }
        String tokenId = accountDao.getIdWithToken(token);

        JsonObject chatRooms = null;
        try {
            chatRooms = jsonObject.getAsJsonObject(JsonKeyEnum.chatRoomName.name());
        } catch (Exception e) {
            return returnJsonObject.setSuccessAndErrorMessage(ErrorType.cantFindChatRoomName.getMessage());
        }

        if (chatRooms == null || chatRooms.isEmpty()) {
            return returnJsonObject.setSuccessAndErrorMessage(ErrorType.cantFindChatRoomName.getMessage());
        }

//        JsonObject dataJsonObject = new JsonObject();
        Map<String, Object> dataMap = new HashMap<>();
        chatRooms.asMap().forEach((chatRoomName, id) -> {
            List<String> userList = userChatRoomDao.getUserChatRoomUsers(chatRoomName);
            if (userList.contains(tokenId)) {
                String idStr = null;
                try {
                    idStr = id.getAsString();
                } catch (Exception ignored) {}
                if (idStr != null) {
                    List<MessageSendReceive> messageSendReceiveList = messagesDao.userReceiveMessageWithId(chatRoomName, idStr);
//                    dataJsonObject.addProperty(chatRoomName, gson.toJson(messageSendReceiveList, new TypeToken<List<MessageSendReceive>>() {
//                    }.getType()));
                    dataMap.put(chatRoomName, messageSendReceiveList);
                }
            }
        });
        if (dataMap.isEmpty() && !chatRooms.isEmpty()) {
            return returnJsonObject.setSuccessAndErrorMessage(ErrorType.tokenNoPermission.getMessage());
        }

        return returnJsonObject.setSuccessAndData(dataMap);
    }

    // todo 設定一次傳輸訊息的數量上限，為未讀訊息由最新開始向上 n 則(n=100)
    public ReturnJsonObject getUserChatRoomChats(String json) {
        ReturnJsonObject returnJsonObject = new ReturnJsonObject();


        JsonObject jsonObject;
        try {
            jsonObject = gson.fromJson(json, JsonObject.class);
        } catch (Exception e) {
            returnJsonObject.setSuccessAndErrorMessage(ErrorType.dataNotFound.getMessage());
            return returnJsonObject;
        }
        if (JsonChecker.checkNoKey(jsonObject, JsonKeyEnum.token.name())) {
            returnJsonObject.setSuccess(false);
            returnJsonObject.setErrorMessage(ErrorType.cantFindToken.getMessage());
            return returnJsonObject;
        }
        if (JsonChecker.checkNoKey(jsonObject, JsonKeyEnum.chatRoomName.name())) {
            returnJsonObject.setSuccess(false);
            returnJsonObject.setErrorMessage(ErrorType.cantFindChatRoomName.getMessage());
            return returnJsonObject;
        }
        String token = null;
        try {
            token = jsonObject.get(JsonKeyEnum.token.name()).getAsString();
        } catch (Exception e) {
            returnJsonObject.setSuccess(false);
            returnJsonObject.setErrorMessage(ErrorType.cantFindToken.getMessage());
            return returnJsonObject;
        }
        String tokenId = accountDao.getIdWithToken(token);

        List<String> chatRoomNames = new ArrayList<>();
        try {
            chatRoomNames.add(jsonObject.get(JsonKeyEnum.chatRoomName.name()).getAsString());
        } catch (Exception e) {
            try {
                jsonObject.get(JsonKeyEnum.chatRoomName.name()).getAsJsonArray().asList().forEach((jsonElement -> {
                    if (!jsonElement.isJsonNull()) {
                        String str = jsonElement.getAsString();
                        if (str != null) chatRoomNames.add(str.toLowerCase());
                    }
                }));
            } catch (Exception e2) {
                if (chatRoomNames.isEmpty()) {
                    returnJsonObject.setSuccess(false);
                    returnJsonObject.setErrorMessage(ErrorType.cantFindChatRoomName.getMessage());
                    return returnJsonObject;
                }
            }
        }

        if (chatRoomNames.isEmpty()) {
            returnJsonObject.setSuccess(false);
            returnJsonObject.setErrorMessage(ErrorType.cantFindChatRoomName.getMessage());
            return returnJsonObject;
        }

        Map<String, List<MessageSendReceive>> chatRoomsChats = new HashMap<>();

        for(String chatRoomName : chatRoomNames) {
            if (!userChatRoomDao.checkHasUserChatRoom(chatRoomName) ||
                    !sqlDao.findTableName(chatRoomName)) {
                continue;
            }
            if (!userChatRoomDao.getUserChatRoomUsers(chatRoomName).contains(tokenId)) continue;

            chatRoomsChats.put(chatRoomName, messagesDao.getUserChatRoomChat(token, chatRoomName));
        }
        if (chatRoomsChats.isEmpty()) {
            returnJsonObject.setSuccess(false);
            returnJsonObject.setErrorMessage(ErrorType.tokenNoPermission.getMessage());
            return returnJsonObject;
        }

        return returnJsonObject.setSuccessAndData(chatRoomsChats);
    }

    public ReturnJsonObject messageReplayAi(String json) {
        ReturnJsonObject returnJsonObject = new ReturnJsonObject();

        JsonObject jsonObject;
        try {
            jsonObject = gson.fromJson(json, JsonObject.class);
        } catch (Exception e) {
            return returnJsonObject.setSuccessAndErrorMessage(ErrorType.dataNotFound.getMessage());
        }

        if (JsonChecker.checkNoKey(jsonObject, JsonKeyEnum.token.name())) {
            return returnJsonObject.setSuccessAndErrorMessage(ErrorType.cantFindToken.getMessage());
        }
        if (JsonChecker.checkNoKey(jsonObject, JsonKeyEnum.chatRoomName.name())) {
            return returnJsonObject.setSuccessAndErrorMessage(ErrorType.cantFindChatRoomName.getMessage());
        }
//        if (JsonChecker.checkNoKey(jsonObject, JsonKeyEnum.messages.name())) {
//            return returnJsonObject.setSuccessAndErrorMessage(ErrorType.cantFindMessages.getMessage());
//        }

        int messageCount = 10;
        if (JsonChecker.checkKey(jsonObject, JsonKeyEnum.messageCount.name())) {
            try {
                messageCount = jsonObject.get(JsonKeyEnum.messageCount.name()).getAsInt();
            } catch (Exception e) {}
        }

        String token = null;
        try {
            token = jsonObject.get(JsonKeyEnum.token.name()).getAsString();
        } catch (Exception e) {
            return returnJsonObject.setSuccessAndErrorMessage(ErrorType.cantFindToken.getMessage());
        }
        String tokenId = accountDao.getIdWithToken(token);
        if (tokenId == null) {
            return returnJsonObject.setSuccessAndErrorMessage(ErrorType.cantFindToken.getMessage());
        }


        String chatRoomName = null;
        try {
            chatRoomName = jsonObject.get(JsonKeyEnum.chatRoomName.name()).getAsString();
        } catch (Exception e) {
            return returnJsonObject.setSuccessAndErrorMessage(ErrorType.cantFindChatRoomName.getMessage());
        }

        MessageSendReceive lastMessage = messagesDao.getUserChatRoomLastChat(token, chatRoomName);
        if (lastMessage == null) {
            return returnJsonObject.setSuccessAndErrorMessage(ErrorType.cantFindChatRoomName.getMessage());
        }
        List<MessageSendReceive> messageList = messagesDao.userReceiveMessageWithId(
                chatRoomName,
                String.valueOf(lastMessage.getId() - messageCount)
        );


        String inputEntryText = null;
        if (JsonChecker.checkKey(jsonObject, JsonKeyEnum.inputEntryText.name())) {
            try {
                inputEntryText = jsonObject.get(JsonKeyEnum.inputEntryText.name()).getAsString();
            } catch (Exception e) {}
        }

        List<Map<String, String>> userMessagesData = new ArrayList<>();
        messageList.forEach(message -> {
            userMessagesData.add(new HashMap<>() {{
                put("speaker", message.getSender());
                put("message", message.getMessage().getOrDefault(MessageKeyEnum.message.name(), ""));
            }});
        });


        AISendMessageData aiSendMessageData = new AISendMessageData();
        aiSendMessageData.inputEntryText = inputEntryText;
        aiSendMessageData.userMessagesData = userMessagesData;

//        return returnJsonObject.setSuccessAndData(aiSendMessageData);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://26.239.119.50:5000/api/v1/messageReplayAi"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(aiSendMessageData)))
                .build();


        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                return returnJsonObject.setSuccessAndErrorMessage(response.statusCode() + "\n" + response.body());
            }
            AIReceiveMessageData aiReceiveMessageData = gson.fromJson(response.body(), AIReceiveMessageData.class);

//            System.out.println("Response Code: " + response.statusCode());
//            System.out.println("Response Body: " + response.body());
            return returnJsonObject.setSuccessAndData(aiReceiveMessageData);
        } catch (Exception e) {
            e.printStackTrace();
            return returnJsonObject.setSuccessAndErrorMessage(e.getMessage());
        }
    }

    static class AISendMessageData {
        public String inputEntryText = null;
        public List<Map<String, String>> userMessagesData = new ArrayList<>();;
    }

    static class AIReceiveMessageData {
        public String completionMessage = null;
        public List<String> possibleReply = new ArrayList<>();;
    }
}

