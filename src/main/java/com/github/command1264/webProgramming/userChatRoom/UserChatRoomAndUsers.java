package com.github.command1264.webProgramming.userChatRoom;

import com.github.command1264.webProgramming.accouunt.User;

import java.util.ArrayList;
import java.util.List;

public class UserChatRoomAndUsers extends UserChatRoom {
    protected List<User> usersObject;

    public UserChatRoomAndUsers() {
        super();
        this.usersObject = new ArrayList<>();
    }

    public List<User> getUsersObject() {
        return this.usersObject;
    }

    public void setUsersObject(List<User> setUsersObject) {
        this.usersObject = setUsersObject;
    }
}
