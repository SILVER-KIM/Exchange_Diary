package com.example.exchangediary;

import android.graphics.Bitmap;

public class BookShelfItem {
    String diaryName;

    public BookShelfItem(String diaryName) {
        this.diaryName = diaryName;
    }

    public String getDiaryName() {
        return diaryName;
    }

    public void setDiaryName(String diaryName) {
        this.diaryName = diaryName;
    }
}
