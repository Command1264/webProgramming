package com.github.command1264.webProgramming.messages;

public class ReturnJsonObject {
    private boolean success = false;
    private String errorMessage = "";
    private String exception = "";
    private String data = "";

    public ReturnJsonObject(){}

    public ReturnJsonObject(boolean success, String errorMessage, String exception, String data) {
        this.success = success;
        this.errorMessage = errorMessage;
        this.exception = exception;
        this.data = data;
    }

    public boolean isSuccess() {
        return success;
    }
    public String getErrorMessage() {
        return errorMessage;
    }
    public String getException() {
        return exception;
    }
    public String getData() {
        return data;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
    public void setErrorMessage(String errorMessage) {
        if (errorMessage == null) return;
        this.errorMessage = errorMessage;
    }
    public void setException(String exception) {
        if (exception == null) return;
        this.exception = exception;
    }
    public void setData(String data) {
        if (data == null) return;
        this.data = data;
    }
}
