package com.github.command1264.webProgramming.dao;

import com.github.command1264.webProgramming.messages.*;
import com.github.command1264.webProgramming.userChatRoom.UserChatRoom;
import com.github.command1264.webProgramming.userChatRoom.UserChatRoomRowMapper;
import com.github.command1264.webProgramming.util.RoomNameConverter;
import com.github.command1264.webProgramming.util.SqlTableEnum;
import com.google.gson.Gson;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.*;

@Component
public class MessagesDao { // todo mybatis
    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;
    @Autowired
    private AccountDao accountDao;
    private final Gson gson = new Gson();

    public @NotNull List<MessageSendReceive> getUserChatRoomChat(String token, String chatRoomName) {
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

    public @NotNull ReturnJsonObject modifyUserChatRoomChat(
            UUID chatRoomUUID,
            MessageSendReceive oldMessage,
            MessageSendReceive newMessage) {
        return this.modifyUserChatRoomChat(RoomNameConverter.convertChatRoomName(chatRoomUUID), oldMessage, newMessage);
    }

    public @NotNull ReturnJsonObject modifyUserChatRoomChat(
            String chatRoomName,
            MessageSendReceive oldMessage,
            MessageSendReceive newMessage) {
        ReturnJsonObject returnJsonObject = new ReturnJsonObject();
        if (jdbcTemplate == null) {
            returnJsonObject.setSuccess(false);
            returnJsonObject.setErrorMessage(ErrorType.sqlNotConnect.getMessage());
            return returnJsonObject;
        }
        if (oldMessage == null || newMessage == null) {
            returnJsonObject.setSuccess(false);
            returnJsonObject.setErrorMessage(ErrorType.cantFindMessage.getMessage());
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
            returnJsonObject.setSuccessAndErrorMessage(ErrorType.cantFindMessage.getMessage());
            return returnJsonObject;
        }
        if (messagesList.isEmpty()) {
            returnJsonObject.setSuccess(false);
            returnJsonObject.setErrorMessage(ErrorType.cantFindMessage.getMessage());
            return returnJsonObject;
        } else if (messagesList.size() != 1) {
            returnJsonObject.setSuccess(false);
            returnJsonObject.setErrorMessage(ErrorType.messageHasMulti.getMessage());
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
            returnJsonObject.setSuccessAndErrorMessage(ErrorType.messageSaveFailed.getMessage());
            return returnJsonObject;
        }
        if (count != 1) {
            returnJsonObject.setSuccessAndErrorMessage(ErrorType.messageSaveFailed.getMessage());
            return returnJsonObject;
        }
        returnJsonObject.setSuccess(true);
        return returnJsonObject;
    }

    public @NotNull ReturnJsonObject deleteUserChatRoomChat(String chatRoomName, MessageSendReceive message) {
        ReturnJsonObject returnJsonObject = new ReturnJsonObject();
        if (jdbcTemplate == null) {
            returnJsonObject.setSuccess(false);
            returnJsonObject.setErrorMessage(ErrorType.sqlNotConnect.getMessage());
            return returnJsonObject;
        }
        if (chatRoomName == null || message == null) {
            returnJsonObject.setSuccess(false);
            returnJsonObject.setErrorMessage(ErrorType.cantFindMessage.getMessage());
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
            returnJsonObject.setSuccessAndErrorMessage(ErrorType.cantFindMessage.getMessage());
            return returnJsonObject;
        }
        if (messagesList.isEmpty()) {
            returnJsonObject.setSuccessAndErrorMessage(ErrorType.cantFindMessage.getMessage());
            return returnJsonObject;
        } else if (messagesList.size() != 1) {
            returnJsonObject.setSuccessAndErrorMessage(ErrorType.messageHasMulti.getMessage());
            return returnJsonObject;
        }
        String deleteSql = "update :chatRoomName set deleted=true where id=:id;"
                .replaceAll(":chatRoomName", chatRoomName);

        int count;
        try {
            count = jdbcTemplate.update(deleteSql, map);
        } catch (Exception e) {
            returnJsonObject.setSuccessAndErrorMessage(ErrorType.messageDeleteFailed.getMessage());
            return returnJsonObject;
        }
        if (count != 1) {
            returnJsonObject.setSuccessAndErrorMessage(ErrorType.messageDeleteFailed.getMessage());
            return returnJsonObject;
        }
        returnJsonObject.setSuccessAndData("");
        return returnJsonObject;
    }

    public boolean systemSendMessage(String chatRoomName, String message, String type) {
        return userSendMessage(chatRoomName, new MessageSendReceive(
                0, "system", "0", new HashMap<>() {{
                    put(MessageKeyEnum.message.name(), "message");
                }}, type, LocalDateTime.now(), false, false
        ));
    }

    public boolean userSendMessage(String chatRoomName, MessageSendReceive messageSendReceive) {
        if (jdbcTemplate == null || chatRoomName == null || messageSendReceive == null) return false;

        String sql = "insert ignore into :tableName (id, sender, message, type, time) values(:id, :sender, :message, :type, :time);"
                .replaceAll(":tableName", chatRoomName);

        Map<String, Object> map = new HashMap<>() {{
            put("id", messageSendReceive.getId());
            put("sender", messageSendReceive.getSender());
            put("message", gson.toJson(messageSendReceive.getMessage()));
            put("type", messageSendReceive.getType());
            put("time", messageSendReceive.getTime());
        }};
        int count;
        try {
            count = jdbcTemplate.update(sql, map);
        } catch (Exception e) {
            return false;
        }
        return count > 0;
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

        Map<String, Object> map = new HashMap<>() {{
            put("id", id);
        }};
        try {
            return jdbcTemplate.query(selectMessageSql, map, new MessageSendReceiveRowMapper());
        } catch (Exception e ){
            return new ArrayList<>();
        }
    }

    public @Nullable MessageSendReceive getUserChatRoomLastChat(String token, String chatRoomName) {
        if (token == null || chatRoomName == null) return null;
        List<MessageSendReceive> messageSendReceiveList = this.getUserChatRoomChat(token, chatRoomName);
        return (messageSendReceiveList.isEmpty()) ? null : messageSendReceiveList.get(messageSendReceiveList.size() - 1);
    }

    public boolean fixMessages() {
        String findAllUserChatRoomsSql = """
                select * from :tableName;
                """.replaceAll(":tableName", SqlTableEnum.userChatRooms.name());
        List<UserChatRoom> userChatRoomList;
        try {
            userChatRoomList = jdbcTemplate.query(findAllUserChatRoomsSql, new HashMap<>(), new UserChatRoomRowMapper());
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        String findAllChatsSql = """
                select room.id,
                    info.name, room.sender as userId, room.message,
                    room.type, room.time,
                    room.modify, room.deleted
                    from :tableRoomName room left join :tableInfo info
                on room.sender=info.id;
                """.replaceAll(":tableInfo", SqlTableEnum.accountInfo.name());
        String replaceChatsSql = """
                update :tableName set id=:id, sender=:sender, message=:message, type=:type, time=:time, modify=:modify, deleted=:deleted
                where id=:id;
            """;
//        String replaceChatsSql = """
//                replace into :tableName(id, sender, message, type, time, modify, deleted)
//                values(:id, :sender, :message, :type, :time, :modify, :deleted);
//            """;
        for (UserChatRoom userChatRoom : userChatRoomList) {
            try {
                String userChatRoomName = RoomNameConverter.convertChatRoomName(userChatRoom.getUUID());
                List<MessageSendReceive> messageSendReceiveList = jdbcTemplate.query(
                        StringUtils.replace(findAllChatsSql,
                                ":tableRoomName",
                                userChatRoomName
                        ), new HashMap<>(), new MessageSendReceiveRowMapper(true));

                for (MessageSendReceive messageSendReceive : messageSendReceiveList) {
                    try {
                        Map<String, Object> messageReplaceMap = new HashMap<>() {{
                            put("id", messageSendReceive.getId());
                            put("sender", messageSendReceive.getSenderId());
                            put("message", gson.toJson(messageSendReceive.getMessage()));
                            put("type", messageSendReceive.getType());
                            put("time", messageSendReceive.getTime());
                            put("modify", messageSendReceive.getModify());
                            put("deleted", messageSendReceive.getDeleted());
                        }};
                        jdbcTemplate.update(
                                StringUtils.replace(replaceChatsSql,
                                        ":tableName",
                                        userChatRoomName
                                        ), messageReplaceMap);
                    } catch (Exception e2) {
                        e2.printStackTrace();
                        return false;
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }
}
