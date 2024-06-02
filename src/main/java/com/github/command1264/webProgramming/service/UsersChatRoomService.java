package com.github.command1264.webProgramming.service;

import com.github.command1264.webProgramming.accouunt.User;
import com.github.command1264.webProgramming.dao.AccountDao;
import com.github.command1264.webProgramming.dao.SqlDao;
import com.github.command1264.webProgramming.dao.UsersChatRoomDao;
import com.github.command1264.webProgramming.messages.ErrorType;
import com.github.command1264.webProgramming.messages.ReturnJsonObject;
import com.github.command1264.webProgramming.util.RoomNameConverter;
import com.github.command1264.webProgramming.util.UsersSorter;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;


@Component
public class UsersChatRoomService {
    @Autowired
    private SqlDao sqlDao;
    @Autowired
    private AccountDao accountDao;
    @Autowired
    private UsersChatRoomDao usersChatRoomDao;
    private Gson gson = new Gson();

    public ReturnJsonObject getUsersChatRoom(String json) {
        ReturnJsonObject returnJsonObject = new ReturnJsonObject();
        if(sqlDao.checkNotConnect()) {
            returnJsonObject.setSuccess(false);
            returnJsonObject.setErrorMessage(ErrorType.sqlNotConnect.getErrorMessage());
            return returnJsonObject;
        }

        JsonObject jsonObject = gson.fromJson(json, JsonObject.class);
        JsonArray users = jsonObject.getAsJsonArray("users");
        return usersChatRoomDao.getUsersChatRoom(UsersSorter.sortUsersIdList(users));
    }

    public ReturnJsonObject createUsersChatRoom(String json) {
        ReturnJsonObject returnJsonObject = new ReturnJsonObject();
        if(sqlDao.checkNotConnect()) {
            returnJsonObject.setSuccess(false);
            returnJsonObject.setErrorMessage(ErrorType.sqlNotConnect.getErrorMessage());
            return returnJsonObject;
        }


        JsonObject jsonObject = gson.fromJson(json, JsonObject.class);
        if (!jsonObject.has("users")) {
            returnJsonObject.setSuccess(false);
            returnJsonObject.setErrorMessage(ErrorType.usersIsZero.getErrorMessage());
            return returnJsonObject;
        }

        JsonArray users = jsonObject.getAsJsonArray("users");

        String usersIdListJsonStr = UsersSorter.sortUsersIdList(users);

        if (usersIdListJsonStr == null) {
            returnJsonObject.setSuccess(false);
            returnJsonObject.setErrorMessage(ErrorType.usersIsZero.getErrorMessage());
            return returnJsonObject;
        }

        return usersChatRoomDao.createUsersChatRoom(usersIdListJsonStr);
    }

//    private @Nullable String sortUsersIdList(JsonArray users) {
//        List<String> usersIdList = new ArrayList<>();
//
//        for (JsonElement jsonElement : users.asList()) {
//            try {
//                User user = accountDao.getUser(jsonElement.getAsString());
//                if (user == null) continue;
//                usersIdList.add(user.getId());
//            } catch (Exception e) {
//                e.printStackTrace();
//                continue;
//            }
//        }
//
//        if (usersIdList.isEmpty()) {
//            return null;
//        }
//        // 排序，讓之後的聊天室更好判斷
////        usersList.sort(Comparator.comparing((User user) -> user.id));
//        usersIdList.sort(Comparator.naturalOrder());
//        return gson.toJson(gson.toJsonTree(usersIdList, new TypeToken<List<String>>() {
//        }.getType()).getAsJsonArray(), JsonArray.class);
//    }

    public ReturnJsonObject getUsersChatRoomChat(String json) {
        ReturnJsonObject returnJsonObject = new ReturnJsonObject();
        if(sqlDao.checkNotConnect()) {
            returnJsonObject.setSuccess(false);
            returnJsonObject.setErrorMessage(ErrorType.sqlNotConnect.getErrorMessage());
            return returnJsonObject;
        }


        JsonObject jsonObject = gson.fromJson(json, JsonObject.class);
        String chatRoomName = null;
        String usersIdListJsonStr = null;
        if (jsonObject.has("chatRoomName")) {
            chatRoomName = jsonObject.get("chatRoomName").getAsString();
        }

        if (jsonObject.has("users") && chatRoomName == null) {
            usersIdListJsonStr = UsersSorter.sortUsersIdList(jsonObject.getAsJsonArray("users"));

            if (usersIdListJsonStr == null) {
                returnJsonObject.setSuccess(false);
                returnJsonObject.setErrorMessage(ErrorType.cantFindChatRoomNameAndUsersRaw.getErrorMessage());
                return returnJsonObject;
            }
            chatRoomName = RoomNameConverter.convertChatRoomName(usersChatRoomDao.getChatRoomUUID(usersIdListJsonStr));
        }
        if (chatRoomName == null) {
            returnJsonObject.setSuccess(false);
            returnJsonObject.setErrorMessage(ErrorType.cantFindChatRoomNameRaw.getErrorMessage());
            return returnJsonObject;
        }
        return usersChatRoomDao.getUsersChatRoomChat(chatRoomName);
    }


}
