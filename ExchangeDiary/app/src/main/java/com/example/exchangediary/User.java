package com.example.exchangediary;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class User{

        public String userID;
        public String userNAME;
        public String userEMAIL;
        public String userPW;
        public String checkPW;
        public String userMESSAGE;

        public User(String userID, String userNAME, String userEMAIL, String userPW, String checkPW, String userMESSAGE){
            this.userID=userID;
            this.userNAME=userNAME;
            this.userEMAIL=userEMAIL;
            this.userPW=userPW;
            this.checkPW=checkPW;
            this.userMESSAGE=userMESSAGE;
        }

    }

