package com.github.command1264.messages;

import java.util.ArrayList;
import java.util.List;

public class MessageGetAll {
    List<MessageSendReceive> messages = new ArrayList<>();
    public MessageGetAll() {

    }
    public MessageGetAll(List<MessageSendReceive> messages) {
        this.messages = messages;
    }
}
