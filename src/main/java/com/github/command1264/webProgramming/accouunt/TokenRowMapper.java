package com.github.command1264.webProgramming.accouunt;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class TokenRowMapper implements RowMapper<Token> {
    @Override
    public Token mapRow(ResultSet rs, int rowNum) throws SQLException {
        Token token = new Token();
        token.setId(rs.getString("id"));
        token.setToken(rs.getString("token"));
        token.setExpiredTime(rs.getString("expiredTime"));
        return token;
    }
}
