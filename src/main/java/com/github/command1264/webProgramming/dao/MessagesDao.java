package com.github.command1264.webProgramming.dao;

import com.github.command1264.webProgramming.messages.ErrorType;
import com.github.command1264.webProgramming.messages.MessageSendReceive;
import com.github.command1264.webProgramming.messages.ReturnJsonObject;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class MessagesDao {
    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;
    private final Gson gson = new Gson();
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
