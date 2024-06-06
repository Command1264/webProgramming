package com.github.command1264.webProgramming.userChatRoom;

import com.github.command1264.webProgramming.util.DateTimeFormat;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.UUID;

public class UsersChatRoom {
    private UUID uuid = null;
    private String users = "";
    private String lastModify = "";
    public UsersChatRoom() {}

    public void setUsers(String users) {
        if (users == null) return;
        this.users = users;
    }
    public void setUUID(String uuid) {
        if (uuid == null) return;
        this.setUUID(UUID.fromString(uuid));
    }
    public void setUUID(UUID uuid) {
        if (uuid == null) return;
        this.uuid = uuid;
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
    public String getUsers() {
        return this.users;
    }

    public String getLastModify() {
        return lastModify;
    }
    public LocalDateTime getLastModifyWithTime() {
        try {
            return LocalDateTime.parse(this.lastModify, DateTimeFormatter.ofPattern(DateTimeFormat.format));
        } catch (DateTimeParseException e) {
            return null;
        }
    }
}
