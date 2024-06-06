package com.github.command1264.webProgramming.messages;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class MessageSendReceiveRowMapper implements RowMapper<MessageSendReceive> {

    @Override
    public MessageSendReceive mapRow(ResultSet rs, int rowNum) throws SQLException {
        MessageSendReceive messageSendReceive = new MessageSendReceive();
        messageSendReceive.setId(rs.getInt("id"));
        messageSendReceive.setSender(rs.getString("sender"));
        messageSendReceive.setMessage(rs.getString("message"));
        messageSendReceive.setType(rs.getString("type"));
        messageSendReceive.setType(rs.getString("time"));
        messageSendReceive.setModify(rs.getBoolean("modify"));
        messageSendReceive.setDeleted(rs.getBoolean("deleted"));
        if (messageSendReceive.getDeleted()) {
            messageSendReceive.setType("space");
            messageSendReceive.setMessage("");
        }
        return messageSendReceive;
    }
}
