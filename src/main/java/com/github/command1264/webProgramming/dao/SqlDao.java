package com.github.command1264.webProgramming.dao;

import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.*;
import java.util.*;

@Component
public class SqlDao {
    private final Gson gson = new Gson();
    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;
    @Autowired
    private DataSource dataSource;

    public SqlDao() {
        this.initTable();
    }

    public String test(String id) {
        return "";
    }

    public void initTable() {
        if (jdbcTemplate == null) return;
        System.out.println("initing Sql...");
        String createAccountInfoSql = """
            create table if not exists :tableName(
                id varchar(64) primary key not null,
                name varchar(256) not null,
                createTime datetime(4) not null,
                loginAccount varchar(256) not null,
                loginPassword varchar(64) not null,
                photoStickerBase64 text,
                chatRooms text not null,
                unique key `loginAccount` (`loginAccount`)
            );
        """.replaceAll(":tableName", SqlTableEnum.accountInfo.getName());
        String initAccountInfoSql = """
            INSERT INTO `accountinfo`(id, name, createTime, loginAccount, loginPassword, photoStickerBase64, chatRooms)
            VALUES('Command1','指令 Command1', '2004-01-06 20:07:09.2200','command1264@gmail.com','25d55ad283aa400af464c76d713c07ad', NULL, '[]'),
            ('Taiwan_PingLord','台灣Ping霸主', '2007-09-22 20:04:01.0600','command2882@gmail.com','5f4dcc3b5aa765d61d8327deb882cf99', NULL, '[]');
        """;
        Map<String, Object> map = new HashMap<>();
        jdbcTemplate.update(createAccountInfoSql, map);
        jdbcTemplate.update(initAccountInfoSql, map);

        String createUsersChatRoomSql = """
            create table if not exists :tableName(
                uuid varchar(36) not null primary key,
                users text not null,
                lastModify datetime(4) not null
            );
        """.replaceAll(":tableName", SqlTableEnum.usersChatRooms.getName());
        jdbcTemplate.update(createUsersChatRoomSql, map);

        String createCookieSessionSql = """
            create table if not exists :tableName(
                id varchar(64) primary key not null,
                session text not null,
                expiredTime datetime(4) not null
            );
        """.replaceAll(":tableName", SqlTableEnum.cookieSessions.getName());
        jdbcTemplate.update(createCookieSessionSql, map);
        System.out.println("Sql init successful");

    }


    public boolean findTableName(String tableName) {
        if (dataSource == null || tableName == null) return false;

        try (Connection conn = dataSource.getConnection()){
            ResultSet set = conn.getMetaData().getTables(null, null, tableName, null);
            while(set.next()) {
                String name = set.getString("TABLE_NAME");
                if (tableName.equalsIgnoreCase(name)) return true;
            }
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    public boolean checkRepeat(String tableName, String key, String value) {
        if (jdbcTemplate == null) return true;
        String sql = "select * from :tableName where :key=:value"
                .replaceAll(":tableName", tableName)
                .replaceAll(":key", key);
        Map<String, Object> map = new HashMap<>() {{
            put("value", value);
        }};
        return (!jdbcTemplate.queryForList(sql, map).isEmpty());
    }

    public boolean checkConnect() {
        if (jdbcTemplate == null) return false;
        if (dataSource == null) return false;
        try {
            if (dataSource.getConnection() == null) return false;
        } catch (SQLException e) {
            return false;
        }
        return true;
    }
    public boolean checkNotConnect() {
        return !checkConnect();
    }
}