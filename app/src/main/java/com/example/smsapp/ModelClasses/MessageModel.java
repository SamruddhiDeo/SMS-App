package com.example.smsapp.ModelClasses;

public class MessageModel {
    String message;
    String url;
    String sent;

    public String getSent() {
        return sent;
    }

    public MessageModel(String message, String url, String sent) {
        this.message = message;
        this.url = url;
        this.sent = sent;
    }

    public String getMessage() {
        return message;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
