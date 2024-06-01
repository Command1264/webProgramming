package com.github.command1264.webProgramming.dao;

import com.github.command1264.webProgramming.accouunt.Account;
import com.github.command1264.webProgramming.accouunt.AccountRowMapper;
import com.github.command1264.webProgramming.accouunt.User;
import com.github.command1264.webProgramming.messages.MessageSendReceive;
import com.github.command1264.webProgramming.messages.ReturnJsonObject;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import jakarta.annotation.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.*;
import java.util.*;

@Component
public class SqlDao {
    private Connection conn = null;
    private Gson gson = new Gson();
    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    public SqlDao(String url, String user, String password) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("找不到JDBC！");
            e.printStackTrace();
        }
        try {
            conn = DriverManager.getConnection(url, user,password);
            conn.setAutoCommit(false);
        } catch (SQLException e) {
            System.err.println("無法連接資料庫！");
            e.printStackTrace();
        }
    }

    public SqlDao() {
        this("jdbc:mysql://localhost:3306/webprogramming?serverTimezone=UTC", "root", "Margaret20070922");
    }

    public String test(String id) {
        String sql = "select * from accountInfo where id=:id";
        Map<String, Object> map = new HashMap<>();
        map.put("id", id);
        AccountRowMapper accountRowMapper = new AccountRowMapper();
//        Account account = jdbcTemplate.queryForObject(sql, new Object[] {id}, new int[] {1}, accountRowMapper);
        Account account = jdbcTemplate.queryForObject(sql, map, new BeanPropertyRowMapper<Account>(Account.class));
        return gson.toJson(account, Account.class);

    }


    public ReturnJsonObject createAccount(Account account) {
        ReturnJsonObject returnJsonObject = new ReturnJsonObject();
        if (checkNotConnect()) {
            returnJsonObject.setSuccess(false);
            returnJsonObject.setErrorMessage("未連接資料庫");
            return returnJsonObject;
        }

        try (Statement stmt = conn.createStatement()) {
            for (String key : new String[]{"id", "loginAccount"}) {
                if (checkRepeat("accountInfo", key, account.get(key))) {
                    returnJsonObject.setSuccess(false);
                    returnJsonObject.setErrorMessage(key + "已經有人使用");
                    return returnJsonObject;
                }
            }
            int count = stmt.executeUpdate(
                    String.format("insert into accountInfo (id, name, loginAccount, loginPassword, photoStickerBase64) values('%s', '%s', '%s', '%s', '%s')",
                            account.getId(),
                            account.getName(),
                            account.getLoginAccount(),
                            account.getLoginPassword(),
                            account.getPhotoStickerBase64()
                    ));
            if (count == 1) {
                returnJsonObject.setSuccess(true);
                conn.commit();
            } else {
                returnJsonObject.setSuccess(false);
                returnJsonObject.setErrorMessage("無法新增帳戶");
                conn.rollback();
            }
        } catch (Exception e) {
            returnJsonObject.setSuccess(false);
            returnJsonObject.setErrorMessage("例外");
            returnJsonObject.setException(e.getMessage());
        }
        return returnJsonObject;
    }


    public @Nullable User getUser(String id) {
        return (User) getAccount(id);
    }

    public @Nullable Account getAccount(String id) {
        if (checkNotConnect()) return null;

        try (Statement stmt = conn.createStatement()) {
            ResultSet set = stmt.executeQuery(String.format("select * from accountInfo where id='%s'", id));
            int size = 0;
            Account account = null;
            while(set.next()) {
                account = new Account(
                        id,
                        set.getString("name"),
                        set.getString("loginAccount"),
                        set.getString("loginPassword"),
                        set.getString("photoStickerBase64")
                );
                ++size;
            }
            if (size == 1) return account;
            else return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // todo
    public boolean modifyAccount(String oldId, Account account) {
        if (checkNotConnect()) return false;

        try (Statement stmt = conn.createStatement()) {
            ResultSet set = stmt.executeQuery(String.format("select * from accountInfo where id='%s'", oldId));
            int size = 0;
            while (set.next()) ++size;
            if (size != 1) return false;

            int executeCount = stmt.executeUpdate(String.format("""
                    update accountInfo set id='%s',
                    name='%s',
                    loginAccount='%s',
                    loginPassword='%s',
                    photoStickerBase64='%s'
                    where id='%s'
                """,
                account.getId(),
                account.getName(),
                account.getLoginAccount(),
                account.getLoginPassword(),
                account.getPhotoStickerBase64(),
                oldId
            ));
            if (executeCount == 1) return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean modifyUser(User user) {
        return true;
    }

    // todo
    public boolean deleteAccount(Account account) {
        return true;
    }

    public boolean deleteUser(User user) {
        return true;
    }


    public UUID getChatRoomUUID(String users) {
        if (checkNotConnect()) return null;

        try (Statement stmt = conn.createStatement()){
            UUID chatUUID = null;
            ResultSet set = stmt.executeQuery(String.format("select * from userchatrooms where users='%s';", users));
            int size = 0;
            while (set.next()) {
                chatUUID = UUID.fromString(set.getString("uuid"));
                ++size;
            }
            if (size != 1) return null;
            return chatUUID;
        } catch (SQLException e) {
            return null;
        }
    }
    public ReturnJsonObject getUsersChatRoom(String usersIdListJsonStr) {
        ReturnJsonObject returnJsonObject = new ReturnJsonObject();
        try (Statement stmt = conn.createStatement()){
            ResultSet set = stmt.executeQuery(String.format("select * from userchatrooms where users='%s'", usersIdListJsonStr));
            int size = 0;
            UUID chatUUID = null;
            while (set.next()) {
                try {
                    chatUUID = UUID.fromString(set.getString("uuid"));
                } catch (IllegalArgumentException ignored) {
                }
                ++size;
            }
            if (size == 0 || chatUUID == null) {
                returnJsonObject.setSuccess(false);
                returnJsonObject.setErrorMessage("找不到聊天室");
                return returnJsonObject;
            } else if (size == 1) {
                returnJsonObject.setSuccess(true);
                returnJsonObject.setData(chatUUID.toString());
                return returnJsonObject;
            } else {
                returnJsonObject.setSuccess(false);
                returnJsonObject.setErrorMessage("此使用者群組有多個聊天室");
                return returnJsonObject;
            }
        } catch (SQLException e) {
            returnJsonObject.setSuccess(false);
            returnJsonObject.setErrorMessage("例外");
            returnJsonObject.setException(e.getMessage());
        }
        return returnJsonObject;
    }

    public ReturnJsonObject createUsersChatRoom(String usersIdListJsonStr) {
        ReturnJsonObject returnJsonObject = new ReturnJsonObject();
        try (Statement stmt = conn.createStatement()) {

            // 檢查是否有重複的聊天室
            if (checkRepeat("userchatrooms", "users", usersIdListJsonStr)) {
                returnJsonObject.setSuccess(false);
                returnJsonObject.setErrorMessage("已有聊天室");
                return returnJsonObject;
            }

            UUID uuid;
            do {
                uuid = UUID.randomUUID();
            } while (checkRepeat("userchatrooms", "uuid", uuid.toString()));
            String roomName = String.format("room_%s", uuid.toString().replaceAll("-", "_"));


            int updateRoom = stmt.executeUpdate(String.format(
                    "insert into userChatRooms (uuid, users)  values('%s', '%s')",
                    uuid.toString(),
                    usersIdListJsonStr
            ));
            stmt.executeUpdate(String.format("""
                        create table %s(
                            id int not null primary key auto_increment,
                            sender varchar(64) not null,
                            message text not null,
                            type varchar(20) not null default 'text',
                            time text not null
                        );
                    """,
                    roomName
            ));
            if (updateRoom != 1 || !findTableName(roomName)) {
                conn.rollback();
                returnJsonObject.setSuccess(false);
                returnJsonObject.setErrorMessage("資料庫添加失敗，請稍後再試");
                return returnJsonObject;
            } else {
                returnJsonObject.setSuccess(true);
                conn.commit();
            }

        } catch (SQLException e) {
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
            returnJsonObject.setSuccess(false);
            returnJsonObject.setErrorMessage("例外");
            returnJsonObject.setException(e.getMessage());
        }

        return returnJsonObject;
    }

    public ReturnJsonObject getUsersChatRoomChat(String chatRoomName) {
        ReturnJsonObject returnJsonObject = new ReturnJsonObject();
        if (chatRoomName == null) {
            returnJsonObject.setSuccess(false);
            returnJsonObject.setErrorMessage("找不到房間名稱");
            return returnJsonObject;
        }

        try (Statement stmt = conn.createStatement()) {
            ResultSet set = stmt.executeQuery(String.format("select * from %s", chatRoomName));
            List<MessageSendReceive> messageList = new ArrayList<>();
            while (set.next()) {
                messageList.add(new MessageSendReceive(
                        set.getInt("id"),
                        set.getString("sender"),
                        set.getString("message"),
                        set.getString("type"),
                        set.getString("time")
                ));
            }
            String messageListStr = gson.toJson(gson.toJsonTree(messageList,
                            new TypeToken<List<MessageSendReceive>>() {
                            }.getType())
                    .getAsJsonArray(), JsonArray.class);
            returnJsonObject.setSuccess(true);
            returnJsonObject.setData(messageListStr);
        } catch (SQLException e) {
            returnJsonObject.setSuccess(false);
            returnJsonObject.setErrorMessage("例外");
            returnJsonObject.setException(e.getMessage());
        }

        return returnJsonObject;
    }

    public ReturnJsonObject userSendMessage(String usersIdListJsonStr, String chatRoomName, JsonObject jsonObject) {
        ReturnJsonObject returnJsonObject = new ReturnJsonObject();
        try (Statement stmt = conn.createStatement()) {
            MessageSendReceive messageSendReceive = gson.fromJson(jsonObject.getAsJsonObject("message"), MessageSendReceive.class);
            // 這裡不需要新增 MessageSendReceive#getId() ，因為 id 會自己生成
            int num = stmt.executeUpdate(
                    String.format("insert into %s (sender, message, type, time) values('%s', '%s', '%s', '%s')",
                            chatRoomName,
                            messageSendReceive.getSender(),
                            messageSendReceive.getMessage(),
                            messageSendReceive.getType(),
                            messageSendReceive.getTime()
                    ));
            if (num != 1) {
                returnJsonObject.setSuccess(false);
                returnJsonObject.setErrorMessage("訊息新增失敗");
                conn.rollback();
            } else {
                returnJsonObject.setSuccess(true);
                conn.commit();
            }
            returnJsonObject.setSuccess(true);
        } catch (Exception e) {
            e.printStackTrace();
            returnJsonObject.setSuccess(false);
            returnJsonObject.setErrorMessage("例外");
            returnJsonObject.setException(e.getMessage());
        }
        return returnJsonObject;
    }


    public boolean checkConnect() {
        return (conn != null);
    }

    public boolean checkNotConnect() {
        return !checkConnect();
    }

    public boolean findTableName(String tableName) {
        if (checkNotConnect() || tableName == null) return false;

        try (Statement stmt = conn.createStatement()) {
            ResultSet set = stmt.getConnection().getMetaData().getTables(null, null, tableName, null);
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
    public boolean checkRepeat(String table, String key, String value) {
        if (checkNotConnect()) return true;

        try (Statement stmt = conn.createStatement()) {
            ResultSet set = stmt.executeQuery(String.format("select * from %s where %s='%s'", table, key, value));
            if (set != null) {
                int size = 0;
                while(set.next()) ++size;
                if (size != 0) return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return true;
        }
        return false;
    }
}
