package com.github.command1264.webProgramming.messages;

import com.google.gson.*;

public class ReturnJsonObject {
    private boolean success = false;
    private String errorMessage = "";
    private String exception = "";
    private Object data = "";

    public ReturnJsonObject(){}

    public ReturnJsonObject(boolean success, String errorMessage, String exception, Object data) {
        this.success = success;
        if (errorMessage != null) this.errorMessage = errorMessage;
        if (exception != null) this.exception = exception;
        if (data != null) this.data = data;
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
    public Object getData() {
        return data;
    }
//    public Object getData() {
//        return data;
//    }

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
    public void setData(Object data) {
        if (data == null) return;
        this.data = data;
    }
    public ReturnJsonObject setSuccessAndData(Object data) {
        this.success = true;
        this.data = data;
        this.errorMessage = "";
        this.exception = "";
        return this;
    }
    public ReturnJsonObject setSuccessAndErrorMessage(String errorMessage) {
        return this.setSuccessAndErrorMessage(errorMessage, "");
    }
    public ReturnJsonObject setSuccessAndErrorMessage(String errorMessage, String exception) {
        this.success = false;
        this.data = "";
        this.errorMessage = errorMessage;
        this.exception = exception;
        return this;
    }


    public String serialize() {
        try {
            return new Gson().toJson(this, ReturnJsonObject.class);
        } catch (JsonSyntaxException e) {
            return null;
        }
    }

    public static ReturnJsonObject deserialize(String json) {
        try {
            return new Gson().fromJson(json, ReturnJsonObject.class);
        } catch (JsonSyntaxException e) {
            return null;
        }
    }
}
