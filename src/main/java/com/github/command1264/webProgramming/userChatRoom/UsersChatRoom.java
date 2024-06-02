package com.github.command1264.webProgramming.userChatRoom;

import java.util.UUID;

public class UsersChatRoom {
    private UUID uuid = null;
    private String users = "";
    public UsersChatRoom() {}

    public void setUsers(String users) {
        this.users = users;
    }
    public void setUUID(String uuid) {
        this.setUUID(UUID.fromString(uuid));
    }
    public void setUUID(UUID uuid) {
        this.uuid = uuid;
    }

    public UUID getUUID() {
        return this.uuid;
    }
    public String getUsers() {
        return this.users;
    }
}
