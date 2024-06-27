package com.github.command1264.webProgramming.messages;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class MessageSendReceiveRowMapper implements RowMapper<MessageSendReceive> {
    private boolean showDeleted = false;
    public MessageSendReceiveRowMapper() {
        this(false);
    }
    public MessageSendReceiveRowMapper(boolean showDeleted) {
        this.showDeleted = showDeleted;
    }
    @Override
    public MessageSendReceive mapRow(ResultSet rs, int rowNum) throws SQLException {
        MessageSendReceive messageSendReceive = new MessageSendReceive();
        messageSendReceive.setId(rs.getInt("id"));
        try {
            if ("0".equals(rs.getString("primaryId"))) {
                messageSendReceive.setSender("system");
                messageSendReceive.setSenderId("system");
            } else {
                messageSendReceive.setSender(rs.getString("name"));
                messageSendReceive.setSenderId(rs.getString("userId"));
            }
        } catch (Exception e) {
            messageSendReceive.setSender(rs.getString("name"));
            messageSendReceive.setSenderId(rs.getString("userId"));
        }
        messageSendReceive.setTime(rs.getString("time"));
        messageSendReceive.setModify(rs.getBoolean("modify"));
        messageSendReceive.setDeleted(rs.getBoolean("deleted"));
        if (!showDeleted && messageSendReceive.getDeleted()) {
            messageSendReceive.setMessage("{}");
            messageSendReceive.setType("space");
        } else {
            messageSendReceive.setMessage(rs.getString("message"));
            messageSendReceive.setType(rs.getString("type"));
        }
        return messageSendReceive;
    }
}
