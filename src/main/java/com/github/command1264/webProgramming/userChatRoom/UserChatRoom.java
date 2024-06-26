package com.github.command1264.webProgramming.userChatRoom;

import com.github.command1264.webProgramming.util.DateTimeFormat;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.UUID;

public class UserChatRoom {
    protected UUID uuid = null;
    protected String name = "";
    protected String users = "";
    protected String lastModify = "";

    public UserChatRoom() {
        this(null);
    }
    public UserChatRoom(UUID uuid) {
        this(uuid, "[]");
    }
    public UserChatRoom(UUID uuid, String users) {
        this(uuid, (uuid == null) ? null : uuid.toString(), users);
    }
    public UserChatRoom(UUID uuid, String name, String users) {
        this(uuid, name, users, LocalDateTime.now());
    }
    public UserChatRoom(UUID uuid, String name, String users, LocalDateTime lastModify) {
        this(uuid, name, users, lastModify.format(DateTimeFormat.formatter));
    }
    public UserChatRoom(UUID uuid, String name, String users, String lastModify) {
        this.uuid = uuid;
        this.name = name;
        this.users = users;
        this.lastModify = lastModify;
    }

    public void setUUID(String uuid) {
        if (uuid == null) return;
        try {
            this.setUUID(UUID.fromString(uuid));
        } catch (Exception ignored) {}
    }
    public void setUUID(UUID uuid) {
        if (uuid == null) return;
        this.uuid = uuid;
    }
    public void setName(String name) {
        if (name == null) return;
        this.name = name;
    }
    public void setUsers(String users) {
        if (users == null) return;
        this.users = users;
    }
    public void setLastModify(LocalDateTime lastModify) {
        if (lastModify == null) return;
        this.setLastModify(lastModify.format(DateTimeFormatter.ofPattern(DateTimeFormat.format)));
    }
    public void setLastModify(String lastModify) {
        if (lastModify == null) return;
        this.lastModify = lastModify;
    }

    public UUID getUUID() {
        return this.uuid;
    }
    public String getName() {
        return this.name;
    }
    public String getUsers() {
        return this.users;
    }
    public List<String> getUserList() {
        return new Gson().fromJson(this.users, new TypeToken<List<String>>(){}.getType());
    }

    public String getLastModify() {
        return lastModify;
    }
    public LocalDateTime getLastModifyWithTime() {
        try {
            return LocalDateTime.parse(this.lastModify, DateTimeFormatter.ofPattern(DateTimeFormat.format));
        } catch (DateTimeParseException e) {
            return LocalDateTime.now();
        }
    }
}
