package com.mugi.peti.kozat.model;

public class Message {
    public String text;
    public String timeStamp;
    public String senderUid;

    public Message() {}

    public Message(String text, String timeStamp, String senderUid) {
        this.text = text;
        this.timeStamp = timeStamp;
        this.senderUid = senderUid;
    }


}
