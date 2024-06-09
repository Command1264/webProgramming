package com.github.command1264.webProgramming.messages;

import com.github.command1264.webProgramming.accouunt.UserAndRooms;
import com.github.command1264.webProgramming.dao.AccountDao;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class MessageSendReceiveRowMapper implements RowMapper<MessageSendReceive> {
    private AccountDao accountDao = null;
    public MessageSendReceiveRowMapper(AccountDao accountDao) {
        this.accountDao = accountDao;
    }
    @Override
    public MessageSendReceive mapRow(ResultSet rs, int rowNum) throws SQLException {
        MessageSendReceive messageSendReceive = new MessageSendReceive();
        UserAndRooms userAndRooms = accountDao.getUserAndRoomsWithId(String.valueOf(rs.getString("sender")));

        messageSendReceive.setId(rs.getInt("id"));
        if (userAndRooms != null) {
            messageSendReceive.setSender(userAndRooms.getName());
        } else {
            messageSendReceive.setSender("-1");
        }
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
