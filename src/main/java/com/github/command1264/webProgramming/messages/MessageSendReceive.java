package com.github.command1264.webProgramming.messages;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class MessageSendReceive {
    private int id = 0;
    private String sender = "";
    private String message = "";
    private String type = "";
    private String time = "";
    public MessageSendReceive() {}
    public MessageSendReceive(int id, String sender, String message, String type, LocalDateTime time) {
        this.id = id;
        this.sender = sender;
        this.message = message;
        this.type = type;
        this.time = time.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
    public MessageSendReceive(int id, String sender, String message, String type, String time) {
        this.id = id;
        this.sender = sender;
        this.message = message;
        this.type = type;
        this.time = time;
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

}
