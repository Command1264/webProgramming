package com.github.command1264.webProgramming;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class MessageSendReceive {
    private String sender = "";
    private String message = "";
    private String type = "";
    private String time = "";
    public MessageSendReceive(String sender, String message, String type, LocalDateTime time) {
        this.sender = sender;
        this.message = message;
        this.type = type;
        this.time = time.format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss:SSSS"));
    }
    public MessageSendReceive(String sender, String message, String type, String time) {
        this.sender = sender;
        this.message = message;
        this.type = type;
        this.time = time;
    }

    public String getSender() {
        return sender;
    }
    public String getMessage() {
        return message;
    }
    public String getType() {
        return type;
    }
    public String getTime() {
        return time;
    }

}
