package com.example.bus_tracking_admin;

public class Message {
    private String messageId;
    private String messageBody;

    private String date;

    public Message() {
        // Default constructor required for Firebase
    }

    public Message(String messageId, String messageBody, String date) {
        this.messageId = messageId;
        this.messageBody = messageBody;
        this.date = date;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getMessageBody() {
        return messageBody;
    }

    public String getDate() {
        return date;
    }

    public void setMessageBody(String messageBody) {
        this.messageBody = messageBody;
    }
}
