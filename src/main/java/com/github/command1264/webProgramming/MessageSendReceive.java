package com.github.command1264.webProgramming;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class MessageSendReceive {
    String user = "";
    String receiver = "";
    String message = "";
    String type = "";
    String time = "";
    public MessageSendReceive() {

    }
    public MessageSendReceive(String user, String receiver, String message, String type, LocalDateTime time) {
        this.user = user;
        this.receiver = receiver;
        this.message = message;
        this.type = type;
        this.time = time.format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss:SSSS"));
    }
    public MessageSendReceive(String user, String receiver, String message, String type, String time) {
        this.user = user;
        this.receiver = receiver;
        this.message = message;
        this.type = type;
        this.time = time;
    }
}
