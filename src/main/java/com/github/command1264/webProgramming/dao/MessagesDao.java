package com.github.command1264.webProgramming.dao;

import com.github.command1264.webProgramming.messages.ErrorType;
import com.github.command1264.webProgramming.messages.MessageSendReceive;
import com.github.command1264.webProgramming.messages.MessageSendReceiveRowMapper;
import com.github.command1264.webProgramming.messages.ReturnJsonObject;
import com.github.command1264.webProgramming.util.RoomNameConverter;
import com.github.command1264.webProgramming.util.SqlTableEnum;
import com.google.gson.Gson;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class MessagesDao {
    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;
    @Autowired
    AccountDao accountDao;
    private final Gson gson = new Gson();

    public @NotNull List<MessageSendReceive> getUsersChatRoomChat(String token, String chatRoomName) {
        if (jdbcTemplate == null  || token == null || chatRoomName == null) return new ArrayList<>();

        String selectMessageSql = StringUtils.replaceEach("""
                select room.id, info.userId,
                    info.name, room.message,
                    room.type, room.time,
                    room.modify, room.deleted
                    from :tableRoomName room left join :tableInfo info
                on room.sender=info.id;
                """,
            new String [] {":tableRoomName", ":tableInfo"},
            new String [] {chatRoomName, SqlTableEnum.accountInfo.name()}
        );

//        String selectMessageSql = "select * from :tableName;"
//                .replaceAll(":tableName", chatRoomName);
        try {
            return jdbcTemplate.query(selectMessageSql, new HashMap<>(), new MessageSendReceiveRowMapper());
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public @NotNull ReturnJsonObject modifyUsersChatRoomChat(
            UUID chatRoomUUID,
            MessageSendReceive oldMessage,
            MessageSendReceive newMessage) {
        return this.modifyUsersChatRoomChat(RoomNameConverter.convertChatRoomName(chatRoomUUID), oldMessage, newMessage);
    }

    public @NotNull ReturnJsonObject modifyUsersChatRoomChat(
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
        String selectSql = StringUtils.replaceEach("""
                select room.id, info.userId,
                    info.name, room.message,
                    room.type, room.time,
                    room.modify, room.deleted
                    from :tableRoomName room left join :tableInfo info
                on room.sender=info.id
                where room.id=:id;
                """,
                new String [] {":tableRoomName", ":tableInfo"},
                new String [] {chatRoomName, SqlTableEnum.accountInfo.name()}
        );
//        String selectSql = "select * from :chatRoom where id=:id;"
//                .replaceAll(":chatRoom", chatRoomName);
        Map<String, Object> map = new HashMap<>() {{
            put("id", oldMessage.getId());
        }};
        List<MessageSendReceive> messagesList;
        try {
            messagesList = jdbcTemplate.query(selectSql, map, new MessageSendReceiveRowMapper());
        } catch (Exception e){
            returnJsonObject.setSuccessAndErrorMessage(ErrorType.cantFindMessage.getErrorMessage());
            return returnJsonObject;
        }
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

        int count;
        try {
            count = jdbcTemplate.update(updateSql, map);
        } catch (Exception e) {
            returnJsonObject.setSuccessAndErrorMessage(ErrorType.messageSaveFailed.getErrorMessage());
            return returnJsonObject;
        }
        if (count != 1) {
            returnJsonObject.setSuccessAndErrorMessage(ErrorType.messageSaveFailed.getErrorMessage());
            return returnJsonObject;
        }
        returnJsonObject.setSuccess(true);
        return returnJsonObject;
    }

    public @NotNull ReturnJsonObject deleteUsersChatRoomChat(String chatRoomName, MessageSendReceive message) {
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

        String selectSql = StringUtils.replaceEach("""
                select room.id, info.userId,
                    info.name, room.message,
                    room.type, room.time,
                    room.modify, room.deleted
                    from :tableRoomName room left join :tableInfo info
                on room.sender=info.id
                where room.id=:id;
                """,
                new String [] {":tableRoomName", ":tableInfo"},
                new String [] {chatRoomName, SqlTableEnum.accountInfo.name()}
        );
//        String selectSql = "select * from :chatRoomName where id=:id;"
//                .replaceAll(":chatRoomName", chatRoomName);
        Map<String, Object> map = new HashMap<>() {{
            put("id", message.getId());
        }};
        List<MessageSendReceive> messagesList;
        try {
            messagesList = jdbcTemplate.query(selectSql, map, new MessageSendReceiveRowMapper());
        } catch (Exception e) {
            returnJsonObject.setSuccessAndErrorMessage(ErrorType.cantFindMessage.getErrorMessage());
            return returnJsonObject;
        }
        if (messagesList.isEmpty()) {
            returnJsonObject.setSuccessAndErrorMessage(ErrorType.cantFindMessage.getErrorMessage());
            return returnJsonObject;
        } else if (messagesList.size() != 1) {
            returnJsonObject.setSuccessAndErrorMessage(ErrorType.messageHasMulti.getErrorMessage());
            return returnJsonObject;
        }
        String deleteSql = "update :chatRoomName set deleted=true where id=:id;"
                .replaceAll(":chatRoomName", chatRoomName);

        int count;
        try {
            count = jdbcTemplate.update(deleteSql, map);
        } catch (Exception e) {
            returnJsonObject.setSuccessAndErrorMessage(ErrorType.messageDeleteFailed.getErrorMessage());
            return returnJsonObject;
        }
        if (count != 1) {
            returnJsonObject.setSuccessAndErrorMessage(ErrorType.messageDeleteFailed.getErrorMessage());
            return returnJsonObject;
        }
        returnJsonObject.setSuccessAndData("");
        return returnJsonObject;
    }



    public @NotNull ReturnJsonObject userSendMessage(String chatRoomName, MessageSendReceive messageSendReceive) {
        ReturnJsonObject returnJsonObject = new ReturnJsonObject();
        if (jdbcTemplate == null) {
            returnJsonObject.setSuccess(false);
            returnJsonObject.setErrorMessage(ErrorType.sqlNotConnect.getErrorMessage());
            return returnJsonObject;
        }
        String sql = "insert into :tableName (sender, message, type, time) values(:sender, :message, :type, :time);"
                .replaceAll(":tableName", chatRoomName);

        Map<String, Object> map = new HashMap<>() {{
            put("sender", messageSendReceive.getSender());
            put("message", messageSendReceive.getMessage());
            put("type", messageSendReceive.getType());
            put("time", messageSendReceive.getTime());
        }};
        int count;
        try {
            count = jdbcTemplate.update(sql, map);
        } catch (Exception e) {
            returnJsonObject.setSuccessAndErrorMessage(ErrorType.messageSaveFailed.getErrorMessage());
            return returnJsonObject;
        }
        if (count != 1) {
            returnJsonObject.setSuccessAndErrorMessage(ErrorType.messageSaveFailed.getErrorMessage());
            return returnJsonObject;
        }
        returnJsonObject.setSuccess(true);
        return returnJsonObject;

    }

    public @NotNull List<MessageSendReceive> userReceiveMessageWithId(String chatRoomName, String id) {
        if (jdbcTemplate == null || chatRoomName == null || id == null) {
            return new ArrayList<>();
        }
        String selectMessageSql = StringUtils.replaceEach("""
                select room.id, info.userId,
                    info.name, room.message,
                    room.type, room.time,
                    room.modify, room.deleted
                    from :tableRoomName room left join :tableInfo info
                on room.sender=info.id
                where room.id>:id;
                """,
                new String [] {":tableRoomName", ":tableInfo"},
                new String [] {chatRoomName, SqlTableEnum.accountInfo.name()}
        );
//        String selectMessageSql = "select * from :tableName where id>:id;"
//                .replaceAll(":tableName", chatRoomName);

        Map<String, Object> map = new HashMap<>() {{
            put("id", id);
        }};
        try {
            return jdbcTemplate.query(selectMessageSql, map, new MessageSendReceiveRowMapper());
        } catch (Exception e ){
            return new ArrayList<>();
        }
    }
}
