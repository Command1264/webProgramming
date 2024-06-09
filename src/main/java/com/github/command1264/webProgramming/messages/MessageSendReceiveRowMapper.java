package com.github.command1264.webProgramming.messages;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class MessageSendReceiveRowMapper implements RowMapper<MessageSendReceive> {
    private boolean rawId = false;
//    private AccountDao accountDao = null;
    public MessageSendReceiveRowMapper() {
        this(false);
    }
    public MessageSendReceiveRowMapper(boolean rawId) {
        this.rawId = rawId;
    }
//    public MessageSendReceiveRowMapper(AccountDao accountDao) {
//        this.accountDao = accountDao;
//    }
    @Override
    public MessageSendReceive mapRow(ResultSet rs, int rowNum) throws SQLException { // todo sql inner join
        MessageSendReceive messageSendReceive = new MessageSendReceive();
//        UserAndRooms userAndRooms = accountDao.getUserAndRoomsWithId(String.valueOf(rs.getString("sender")));

        messageSendReceive.setId(rs.getInt("id"));
        if (rawId) {
            messageSendReceive.setSender(rs.getString("sender"));
        } else {
//            String name = rs.getString("name");
//            messageSendReceive.setSender((name == null || Objects.equals(name, "null")) ? "-1" : name);
            messageSendReceive.setSender(rs.getString("name"));
        }
        messageSendReceive.setMessage(rs.getString("message"));
        messageSendReceive.setType(rs.getString("type"));
        messageSendReceive.setTime(rs.getString("time"));
        messageSendReceive.setModify(rs.getBoolean("modify"));
        messageSendReceive.setDeleted(rs.getBoolean("deleted"));
        if (messageSendReceive.getDeleted()) {
            messageSendReceive.setType("space");
            messageSendReceive.setMessage("");
        }
        return messageSendReceive;
    }
}
