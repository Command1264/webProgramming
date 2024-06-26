package com.github.command1264.webProgramming.messages;

import com.github.command1264.webProgramming.util.DateTimeFormat;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class MessageSendReceive {
    private int id = 0;
    private String sender = "";
    private String senderId = "";
    private Map<String, String> message = new HashMap<>();
    private String type = "";
    private String time = "";
    private boolean modify = false;
    private boolean deleted = false;
    public MessageSendReceive() {}
    public MessageSendReceive(int id, String sender, String senderId, Map<String, String> message, String type, LocalDateTime time, boolean modify, boolean deleted) {
        this(id, sender, senderId, message, type, time.format(DateTimeFormatter.ofPattern(DateTimeFormat.format)), modify, deleted);
    }
    public MessageSendReceive(int id, String sender, String senderId, Map<String, String> message, String type, String time, boolean modify, boolean deleted) {
        this.id = id;
        this.sender = sender;
        this.senderId = senderId;
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
    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }
    public void setMessage(String message) {
        try {
            this.message = new Gson().fromJson(message, new TypeToken<Map<String, String>>(){}.getType());
        } catch (Exception e) {
            try {
                this.message.put(MessageKeyEnum.message.name(), message);
            } catch (Exception e2) {}
        }
    }
    public void setMessage(Map<String, String> message) {
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
    public String getSenderId() {
        return this.senderId;
    }
    public Map<String, String> getMessage() {
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
