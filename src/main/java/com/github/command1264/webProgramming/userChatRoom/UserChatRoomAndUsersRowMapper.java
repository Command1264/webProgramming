package com.github.command1264.webProgramming.userChatRoom;

import com.github.command1264.webProgramming.accouunt.User;
import com.github.command1264.webProgramming.dao.AccountDao;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserChatRoomAndUsersRowMapper implements RowMapper<UserChatRoomAndUsers> {
    private final AccountDao accountDao;
    public UserChatRoomAndUsersRowMapper(AccountDao accountDao) {
        this.accountDao = accountDao;
    }
    @Override
    public UserChatRoomAndUsers mapRow(ResultSet rs, int rowNum) throws SQLException {
        UserChatRoomAndUsers userChatRoomAndUsers = new UserChatRoomAndUsers();
        userChatRoomAndUsers.setUUID(rs.getString("uuid"));
        userChatRoomAndUsers.setName(rs.getString("name"));
        userChatRoomAndUsers.setUsers(rs.getString("users"));
        userChatRoomAndUsers.setLastModify(rs.getString("lastModify"));

        List<User> users = new ArrayList<>();
        for(String id : userChatRoomAndUsers.getUserList()) {
            User user = accountDao.getUserWithId(id);
            if (user == null) continue;
            users.add(user);
        }
        userChatRoomAndUsers.setUsersObject(users);

        return userChatRoomAndUsers;
    }
}
