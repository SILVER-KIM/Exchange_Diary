package com.example.exchangediary;

//친구 데이터를 표현하기위한 모델 클래스(데이터 클래스) 입니다.
//friendlist자바에서 다 표현하기 싫어서 따로 뺀 클래스
public class Friend {
    private int icon;       //친구 프로필 사진
    private String name;    //친구 프로필 닉네임
    private String miniMESSAGE; //친구 프로필 상태메세지

    public Friend(int icon, String name, String miniMESSAGE) {
        this.icon = icon;
        this.name = name;
        this.miniMESSAGE = miniMESSAGE;
    }
    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMiniMESSAGE() {
        return miniMESSAGE;
    }

    public void setMiniMESSAGE(String miniMESSAGE) {
        this.miniMESSAGE = miniMESSAGE;
    }
}
