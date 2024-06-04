package com.github.command1264.webProgramming.dao;

import com.github.command1264.webProgramming.messages.ErrorType;
import com.github.command1264.webProgramming.messages.MessageSendReceive;
import com.github.command1264.webProgramming.messages.MessageSendReceiveRowMapper;
import com.github.command1264.webProgramming.messages.ReturnJsonObject;
import com.github.command1264.webProgramming.util.RoomNameConverter;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
public class MessagesDao {
    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;
    private final Gson gson = new Gson();

    public ReturnJsonObject getUsersChatRoomChat(String chatRoomName) {
        ReturnJsonObject returnJsonObject = new ReturnJsonObject();
        if (jdbcTemplate == null) {
            returnJsonObject.setSuccess(false);
            returnJsonObject.setErrorMessage(ErrorType.sqlNotConnect.getErrorMessage());
            return returnJsonObject;
        }
        if (chatRoomName == null) {
            returnJsonObject.setSuccess(false);
            returnJsonObject.setErrorMessage(ErrorType.cantFindChatRoomName.getErrorMessage());
            return returnJsonObject;
        }
        String sql = "select * from :tableName;".replaceAll(":tableName", chatRoomName);
        List<MessageSendReceive> messagesList = jdbcTemplate.query(sql, new HashMap<>(), new MessageSendReceiveRowMapper());

        String messageListStr = gson.toJson(gson.toJsonTree(messagesList,
                        new TypeToken<List<MessageSendReceive>>() {
                        }.getType())
                .getAsJsonArray(), JsonArray.class);
        returnJsonObject.setSuccess(true);
        returnJsonObject.setData(messageListStr);
        return returnJsonObject;
    }

    public ReturnJsonObject modifyUsersChatRoomChat(
            UUID chatRoomUUID,
            MessageSendReceive oldMessage,
            MessageSendReceive newMessage) {
        return this.modifyUsersChatRoomChat(RoomNameConverter.convertChatRoomName(chatRoomUUID), oldMessage, newMessage);
    }

    public ReturnJsonObject modifyUsersChatRoomChat(
            String chatRoomName,
            MessageSendReceive oldMessage,
            MessageSendReceive newMessage) {
        ReturnJsonObject returnJsonObject = new ReturnJsonObject();
        if (jdbcTemplate == null) {
            returnJsonObject.setSuccess(false);
            returnJsonObject.setErrorMessage(ErrorType.sqlNotConnect.getErrorMessage());
            return returnJsonObject;
        }
        if (oldMessage == null || newMessage == null) {
            returnJsonObject.setSuccess(false);
            returnJsonObject.setErrorMessage(ErrorType.cantFindMessage.getErrorMessage());
            return returnJsonObject;
        }

        String selectSql = "select * from :chatRoom where id=:id;"
                .replaceAll(":chatRoom", chatRoomName);
        Map<String, Object> map = new HashMap<>() {{
            put("id", oldMessage.getId());
        }};
        List<MessageSendReceive> messagesList = jdbcTemplate.query(selectSql, map, new MessageSendReceiveRowMapper());
        if (messagesList.isEmpty()) {
            returnJsonObject.setSuccess(false);
            returnJsonObject.setErrorMessage(ErrorType.cantFindMessage.getErrorMessage());
            return returnJsonObject;
        } else if (messagesList.size() != 1) {
            returnJsonObject.setSuccess(false);
            returnJsonObject.setErrorMessage(ErrorType.messageHasMulti.getErrorMessage());
            return returnJsonObject;
        }

        String updateSql = "update :chatRoomName set message=:message, type=:type, modify=true where id=:id;"
                .replaceAll(":chatRoomName", chatRoomName);
        map.put("message", newMessage.getMessage());
        map.put("type", newMessage.getType());

        int count = jdbcTemplate.update(updateSql, map);
        if (count != 1) {
            returnJsonObject.setSuccess(false);
            returnJsonObject.setErrorMessage(ErrorType.messageSaveFailed.getErrorMessage());
            return returnJsonObject;
        }
        returnJsonObject.setSuccess(true);
        return returnJsonObject;
    }

    public ReturnJsonObject deleteUsersChatRoomChat(String chatRoomName, MessageSendReceive message) {
        ReturnJsonObject returnJsonObject = new ReturnJsonObject();
        if (jdbcTemplate == null) {
            returnJsonObject.setSuccess(false);
            returnJsonObject.setErrorMessage(ErrorType.sqlNotConnect.getErrorMessage());
            return returnJsonObject;
        }
        if (chatRoomName == null || message == null) {
            returnJsonObject.setSuccess(false);
            returnJsonObject.setErrorMessage(ErrorType.cantFindMessage.getErrorMessage());
            return returnJsonObject;
        }

        String selectSql = "select * from :chatRoomName where id=:id;"
                .replaceAll(":chatRoomName", chatRoomName);
        Map<String, Object> map = new HashMap<>() {{
            put("id", message.getId());
        }};
        List<MessageSendReceive> messagesList = jdbcTemplate.query(selectSql, map, new MessageSendReceiveRowMapper());
        if (messagesList.isEmpty()) {
            returnJsonObject.setSuccess(false);
            returnJsonObject.setErrorMessage(ErrorType.cantFindMessage.getErrorMessage());
            return returnJsonObject;
        } else if (messagesList.size() != 1) {
            returnJsonObject.setSuccess(false);
            returnJsonObject.setErrorMessage(ErrorType.messageHasMulti.getErrorMessage());
            return returnJsonObject;
        }
        String deleteSql = "delete from :chatRoomName where id=:id;"
                .replaceAll(":chatRoomName", chatRoomName);

        int count = jdbcTemplate.update(deleteSql, map);
        if (count != 1) {
            returnJsonObject.setSuccess(false);
            returnJsonObject.setErrorMessage(ErrorType.messageDeleteFailed.getErrorMessage());
            return returnJsonObject;
        }
        returnJsonObject.setSuccess(true);
        return returnJsonObject;
    }



    public ReturnJsonObject userSendMessage(String chatRoomName, JsonObject jsonObject) {
        ReturnJsonObject returnJsonObject = new ReturnJsonObject();
        if (jdbcTemplate == null) {
            returnJsonObject.setSuccess(false);
            returnJsonObject.setErrorMessage(ErrorType.sqlNotConnect.getErrorMessage());
            return returnJsonObject;
        }
        String sql = "insert into :tableName (sender, message, type, time) values(:sender, :message, :type, :time);"
                .replaceAll(":tableName", chatRoomName);
        MessageSendReceive messageSendReceive = gson.fromJson(jsonObject.getAsJsonObject("message"), MessageSendReceive.class);
        Map<String, Object> map = new HashMap<>() {{
            put("sender", messageSendReceive.getSender());
            put("message", messageSendReceive.getMessage());
            put("type", messageSendReceive.getType());
            put("time", messageSendReceive.getTime());
        }};
        int runCount = jdbcTemplate.update(sql, map);
        if (runCount != 1) {
            returnJsonObject.setSuccess(false);
            returnJsonObject.setErrorMessage(ErrorType.messageSaveFailed.getErrorMessage());
            return returnJsonObject;
        }
        returnJsonObject.setSuccess(true);
        return returnJsonObject;

//        try (Statement stmt = conn.createStatement()) {
//            MessageSendReceive messageSendReceive = gson.fromJson(jsonObject.getAsJsonObject("message"), MessageSendReceive.class);
//            // 這裡不需要新增 MessageSendReceive#getId() ，因為 id 會自己生成
//            int num = stmt.executeUpdate(
//                    String.format("insert into %s (sender, message, type, time) values('%s', '%s', '%s', '%s')",
//                            chatRoomName,
//                            messageSendReceive.getSender(),
//                            messageSendReceive.getMessage(),
//                            messageSendReceive.getType(),
//                            messageSendReceive.getTime()
//                    ));
//            if (num != 1) {
//                returnJsonObject.setSuccess(false);
//                returnJsonObject.setErrorMessage("訊息新增失敗");
//                conn.rollback();
//            } else {
//                returnJsonObject.setSuccess(true);
//                conn.commit();
//            }
//            returnJsonObject.setSuccess(true);
//        } catch (Exception e) {
//            e.printStackTrace();
//            returnJsonObject.setSuccess(false);
//            returnJsonObject.setErrorMessage("例外");
//            returnJsonObject.setException(e.getMessage());
//        }
//        return returnJsonObject;
    }

}