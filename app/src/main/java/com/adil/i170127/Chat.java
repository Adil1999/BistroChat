package com.adil.i170127;

public class Chat {
    String sender, receiver, message;
    boolean isImg;

    public Chat(){}

    public Chat(String sender, String receiver, String message, boolean isImg) {
        super();
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
        this.isImg = isImg;
    }

    public boolean isImg() {
        return isImg;
    }

    public void setImg(boolean img) {
        isImg = img;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
