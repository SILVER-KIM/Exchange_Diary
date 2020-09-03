package com.example.exchangediary;

public class RequestItem {
    String requestID;

    public RequestItem(String requestID) {
        this.requestID = requestID;
    }
    public void setRequestID(String requestID){
        this.requestID = requestID;
    }
    public String getRequestID(){
        return requestID;
    }
}
