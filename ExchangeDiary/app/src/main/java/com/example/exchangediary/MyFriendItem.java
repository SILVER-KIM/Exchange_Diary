package com.example.exchangediary;

public class MyFriendItem {
    String myID;
    String friendNAME;
    String friendMESSAGE;
    String friendID;

    public MyFriendItem(String friendNAME, String friendMESSAGE, String friendID,String myID) {
        this.friendNAME = friendNAME;
        this.friendMESSAGE = friendMESSAGE;
        this.friendID = friendID;
        this.myID = myID;
    }
    public void setFriendNAME(String friendNAME){
        this.friendNAME = friendNAME;
    }
    public String getFriendNAME(){
        return friendNAME;
    }
    public void setFriendMESSAGE(String friendMESSAGE){
        this.friendMESSAGE = friendMESSAGE;
    }
    public String getFriendMESSAGE(){
        return friendMESSAGE;
    }
    public String getFriendID(){
        return friendID;
    }
    public String getMyID() {return myID;}
}

