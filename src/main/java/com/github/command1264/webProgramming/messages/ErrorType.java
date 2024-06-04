package com.github.command1264.webProgramming.messages;

public enum ErrorType {
    sqlNotConnect("資料庫未連接"),
    sqlUpdateFailed("資料庫添加失敗"),

    cantFindChatRoomName("找不到聊天室名稱"),
    cantFindChatRoom("找不到聊天室"),
    cantFindChatRoomNameRaw("找不到chatRoomName"),
    chatRoomExist("聊天室已存在"),

    cantFindUsers("找不到使用者"),
    usersHasMulti("找到多組使用者"),
    usersSaveFailed("使用者儲存失敗"),

    cantFindChatRoomNameAndUsersRaw("找不到chatRoomName或是users"),

    findKey(":key已經有人使用"),

    cantFindMessage("找不到訊息"),
    messageHasMulti("找到多組訊息"),
    messageSaveFailed("儲存訊息失敗"),
    messageDeleteFailed("儲存刪除失敗"),

    cantCreateAccount("無法新增帳戶"),
    cantFindLoginDataOrSession("找不到帳戶資訊或是Session"),
    cantFindLoginAccount("找不到登入帳號"),
    cantFindId("找不到id"),
    wrongPassword("密碼錯誤"),
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
