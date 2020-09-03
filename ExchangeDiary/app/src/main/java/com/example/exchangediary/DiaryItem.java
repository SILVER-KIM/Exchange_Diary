package com.example.exchangediary;

import android.graphics.Bitmap;

public class DiaryItem {
    String diaryName;
    String myID;
    String friendID;

        public DiaryItem(String myID, String friendID, String diaryName) {
            this.diaryName = diaryName;
            this.myID = myID;
            this.friendID = friendID;
        }

        public String getMyID() {
            return myID;
        }
        public String getFriendID() {
            return friendID;
        }
        public String getDiaryName() {
            return diaryName;
        }
    }
