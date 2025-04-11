package com.android.chatpix;

public class ChatConstructor {
    String message;
    String isSender;
    String time;
    String seen;

    public ChatConstructor(String message, String isSender, String time, String seen) {
        this.message = message;
        this.isSender = isSender;
        this.time = time;
        this.seen = seen;
    }
    public ChatConstructor() {
    }

    public String getMessage() {
        return message;
    }

    public String getIsSender() {
        return isSender;
    }

    public String getTime(){
        return time;
    }
    public String getSeen(){return seen;}

}
