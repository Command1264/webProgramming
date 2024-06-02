package com.github.command1264.webProgramming.messages;

public enum ErrorType {
    sqlNotConnect("資料庫未連接"),
    sqlUpdateFailed("資料庫添加失敗"),

    cantFindChatRoomName("找不到聊天室名稱"),
    cantFindChatRoom("找不到聊天室"),
    cantFindChatRoomNameAndUsersRaw("找不到chatRoomName或是users"),
    cantFindChatRoomNameRaw("找不到chatRoomName"),
    chatRoomExist("聊天室已存在"),

    findKey(":key已經有人使用"),

    messageSaveFailed("儲存訊息失敗"),

    cantCreateAccount("無法新增帳戶"),
    cantFindId("找不到id"),
    cantFindAccount("找不到帳戶"),
    usersIsZero("人數不能為0"),

    exception("例外");

    private String errorMessage = "";
    ErrorType(String errorMessage) {
        this.errorMessage = errorMessage;
    }
    public String getErrorMessage() {
        return this.errorMessage;
    }
}
