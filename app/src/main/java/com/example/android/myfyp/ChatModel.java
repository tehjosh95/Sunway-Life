package com.example.android.myfyp;

import com.firebase.client.ServerValue;
import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class ChatModel {

    private String user;
    private String message;
    private Object timestamp;

    private String imgUrl;
    public ChatModel(){}

    public ChatModel(String user, String message, Object timestamp, String imgUrl){
        this.user = user;
        this.message = message;
        this.timestamp = timestamp;
        this.imgUrl = imgUrl;
    }

    public String getMessage() { return message; }

    public void setMessage(String message) { this.message = message; }

    public String getUser() { return user; }

    public void setUser(String user) { this.user = user; }

    public Object getTimestamp() { return timestamp; }

    public void setTimestamp(Object timestamp) { this.timestamp = timestamp; }

    public String getImgUrl() { return imgUrl; }

    public void setImgUrl(String imgUrl) { this.imgUrl = imgUrl; }
}
