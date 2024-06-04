package com.github.command1264.webProgramming.accouunt;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

public class SessionRowMapper implements RowMapper<Session> {
    @Override
    public Session mapRow(ResultSet rs, int rowNum) throws SQLException {
        Session session = new Session();
        session.setId(rs.getString("id"));
        session.setSession(rs.getString("session"));
        session.setExpiredTime(LocalDateTime.parse(rs.getString("expiredTime")));
        return session;
    }
}
