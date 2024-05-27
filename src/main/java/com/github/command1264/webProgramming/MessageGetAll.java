package com.github.command1264.webProgramming;

import java.util.ArrayList;
import java.util.List;

public class MessageGetAll {
    String user = "";
    String receiver = "";
    List<MessageSendReceive> messages = new ArrayList<>();
    public MessageGetAll() {

    }
    public MessageGetAll(String user, String receiver, List<MessageSendReceive> messages) {
        this.user = user;
        this.receiver = receiver;
        this.messages = messages;
    }
}
