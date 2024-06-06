package com.github.command1264.webProgramming.messages;

import com.github.command1264.webProgramming.util.DateTimeFormat;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class MessageSendReceive {
    private int id = 0;
    private String sender = "";
    private String message = "";
    private String type = "";
    private String time = "";
    private boolean modify = false;
    private boolean deleted = false;
    public MessageSendReceive() {}
    public MessageSendReceive(int id, String sender, String message, String type, LocalDateTime time, boolean modify, boolean deleted) {
        this(id, sender, message, type, time.format(DateTimeFormatter.ofPattern(DateTimeFormat.format)), modify, deleted);
    }
    public MessageSendReceive(int id, String sender, String message, String type, String time, boolean modify, boolean deleted) {
        this.id = id;
        this.sender = sender;
        this.message = message;
        this.type = type;
        this.time = time;
        this.modify = modify;
        this.deleted = deleted;
    }
    public void setId(int id) {
        this.id = id;
    }
    public void setSender(String sender) {
        this.sender = sender;
    }
    public void setMessage(String message) {
        this.message = message;
    }
    public void setType(String type) {
        this.type = type;
    }
    public void setTime(String time) {
        this.time = time;
    }
    public void setModify(boolean modify) {
        this.modify = modify;
    }
    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public int getId() {
        return this.id;
    }
    public String getSender() {
        return this.sender;
    }
    public String getMessage() {
        return this.message;
    }
    public String getType() {
        return this.type;
    }
    public String getTime() {
        return this.time;
    }
    public boolean getModify() {
        return this.modify;
    }
    public boolean getDeleted() {
        return this.deleted;
    }

    public String serialize() {
        try {
            return new Gson().toJson(this, MessageSendReceive.class);
        } catch (JsonSyntaxException e) {
            return null;
        }
    }

    public static MessageSendReceive deserialize(JsonObject jsonObject) {
        try {
            return new Gson().fromJson(jsonObject, MessageSendReceive.class);
        } catch (JsonSyntaxException e) {
            return null;
        }
    }
    public static MessageSendReceive deserialize(String json) {
        try {
            return new Gson().fromJson(json, MessageSendReceive.class);
        } catch (JsonSyntaxException e) {
            return null;
        }
    }

}
