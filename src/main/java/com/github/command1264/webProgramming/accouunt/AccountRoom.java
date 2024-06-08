package com.github.command1264.webProgramming.accouunt;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AccountRoom {
    protected String id;
    protected List<Map<String, Long>> chatRooms;
    public static final Type CHAT_ROOMS_TYPE = new TypeToken<List<Map<String, Long>>>() {}.getType();

    public AccountRoom() {
        this("", new ArrayList<>());
    }

    public AccountRoom(String id, List<Map<String, Long>> chatRooms) {
        this.id = id;
        this.chatRooms = chatRooms;
    }

    public void setId(@Nullable String id) {
        if (id == null) return;
        this.id = id;
    }
    public void setChatRooms(@Nullable String chatRooms) {
        if (chatRooms == null) return;
        this.chatRooms = new Gson().fromJson(chatRooms, AccountAndRooms.CHAT_ROOMS_TYPE);
    }
    public void setChatRooms(@Nullable List<Map<String, Long>> chatRooms) {
        if (chatRooms == null) return;
        this.chatRooms = chatRooms;
    }


    public String getId() {
        return this.id;
    }
    public List<Map<String, Long>> getChatRooms() {
        return this.chatRooms;
    }

    public String getChatRoomsSerialize() {
        return new Gson().toJson(this.chatRooms, new TypeToken<List<Map<String, Long>>>(){}.getType());
    }

    public String serialize() {
        try {
            return new Gson().toJson(this, Account.class);
        } catch (JsonSyntaxException e) {
            return null;
        }
    }
}
