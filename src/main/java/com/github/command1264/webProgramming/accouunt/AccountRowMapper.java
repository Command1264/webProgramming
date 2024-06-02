package com.github.command1264.webProgramming.accouunt;

import org.springframework.jdbc.core.RowMapper;

import javax.swing.tree.TreePath;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AccountRowMapper implements RowMapper<Account> {
    @Override
    public Account mapRow(ResultSet rs, int rowNum) throws SQLException {
        Account account = new Account();
        account.setId(rs.getString("id"));
        account.setName(rs.getString("name"));
        account.setLoginAccount(rs.getString("loginAccount"));
        account.setLoginPassword(rs.getString("loginPassword"));
        account.setPhotoStickerBase64(rs.getString("photoStickerBase64"));
        return account;
    }
}
