package com.github.command1264.webProgramming.dao;

import com.github.command1264.webProgramming.util.Printer;
import com.github.command1264.webProgramming.util.SqlTableEnum;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

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

    public void initTable() {
        if (jdbcTemplate == null) return;

        Printer.println("初始化 MySQL 資料表...");

        String createAccountInfoSql = """
            create table if not exists :tableName(
                id bigint unsigned NOT NULL AUTO_INCREMENT,
                userId varchar(64) NOT NULL,
                name varchar(256) not null,
                createTime datetime not null,
                loginAccount varchar(256) not null,
                loginPassword varchar(64) not null,
                photoStickerBase64 text,
                deleted boolean not null default false,
                PRIMARY KEY (`id`),
                UNIQUE KEY `loginAccount` (`loginAccount`),
                UNIQUE KEY `userId` (`userId`)
            );
        """.replaceAll(":tableName", SqlTableEnum.accountInfo.name());
        String createAccountChatRoomsSql = """
            create table if not exists :tableName(
                id bigint unsigned NOT NULL,
                chatRooms text not null,
                PRIMARY KEY (`id`)
            );
        """.replaceAll(":tableName", SqlTableEnum.accountChatRooms.name());

//        String initAccountInfoSql = """
//            INSERT INTO ignored `accountInfo`(id, name, createTime, loginAccount, loginPassword, photoStickerBase64, chatRooms)
//            VALUES('Command1','指令 Command1', '2004-01-06 20:07:09.2200','command1264@gmail.com','25d55ad283aa400af464c76d713c07ad', NULL, '[]'),
//            ('Taiwan_PingLord','台灣Ping霸主', '2007-09-22 20:04:01.0600','command2882@gmail.com','5f4dcc3b5aa765d61d8327deb882cf99', NULL, '[]');
//        """;
        Map<String, Object> map = new HashMap<>();
        jdbcTemplate.update(createAccountInfoSql, map);
//        jdbcTemplate.update(initAccountInfoSql, map);

        String createUserChatRoomSql = """
            create table if not exists :tableName(
                uuid varchar(36) not null primary key,
                name text not null,
                users text not null,
                lastModify datetime not null,
                deleted boolean not null default false
            );
        """.replaceAll(":tableName", SqlTableEnum.userChatRooms.name());
        jdbcTemplate.update(createUserChatRoomSql, map);

        String createCookieSessionSql = """
            create table if not exists :tableName(
                id varchar(64) primary key not null,
                token text not null,
                expiredTime datetime not null
            );
        """.replaceAll(":tableName", SqlTableEnum.loginTokens.name());
        jdbcTemplate.update(createCookieSessionSql, map);

        Printer.println("MySQL 資料表初始化完成");
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

}
