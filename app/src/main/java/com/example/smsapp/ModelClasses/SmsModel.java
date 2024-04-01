package com.example.smsapp.ModelClasses;

public class SmsModel {
    String senderPhone;
    String messageBody;
    String url;
    String time;
    String sentFlag;

    public SmsModel(String senderPhone, String messageBody, String url, String sentFlag) {
        this.senderPhone = senderPhone;
        this.messageBody = messageBody;
        this.url = url;
        this.sentFlag = sentFlag;
    }

    public String getSentFlag() {
        return sentFlag;
    }

    public void setSentFlag(String sentFlag) {
        this.sentFlag = sentFlag;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getSenderPhone() {
        return senderPhone;
    }

    public void setSenderPhone(String senderPhone) {
        this.senderPhone = senderPhone;
    }

    public String getMessageBody() {
        return messageBody;
    }

    public void setMessageBody(String messageBody) {
        this.messageBody = messageBody;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
