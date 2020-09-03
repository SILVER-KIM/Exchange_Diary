package com.example.exchangediary;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {
    DatabaseReference joinDatabase = FirebaseDatabase.getInstance().getReference();
    EditText id;
    EditText password;
    int newCheck;
    int goCheck;
    String name;
    String message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Button login = (Button)findViewById(R.id.login);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                id = (EditText)findViewById(R.id.id);
                password = (EditText)findViewById(R.id.password);

                String input_id = id.getText().toString();
                String input_pw = password.getText().toString();

                // 아이디 비밀번호 공란 체크하기
                if(input_id.isEmpty() && !input_pw.isEmpty())
                    Toast.makeText(getApplicationContext(), "아이디를 입력해주세요.", Toast.LENGTH_SHORT).show();
                else if(!input_id.isEmpty() && input_pw.isEmpty())
                    Toast.makeText(getApplicationContext(), "비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show();
                else if(input_id.isEmpty() && input_pw.isEmpty())
                    Toast.makeText(getApplicationContext(), "모든 칸을 입력해주세요.", Toast.LENGTH_SHORT).show();
                else{
                    checkLogin(input_id, input_pw);
                }
            }
        });

        //아이디 찾기화면으로 이동
        Button findID = (Button)findViewById(R.id.findID);
        findID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), FindIdActivity.class);
                startActivity(intent);
            }
        });

        //비밀번호 찾기화면으로 이동
        Button findPW = (Button)findViewById(R.id.findPW);
        findPW.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), FindPwActivity.class);
                startActivity(intent);
            }
        });
    }

    public void GotoJoin(View v){
        Button goJoin = (Button)findViewById(R.id.goJoin);

        Intent intent = new Intent(this, JoinActivity.class);
        startActivity(intent);
    }

    public int checkLogin(final String id, final String pw){
        joinDatabase.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                newCheck = 0;
                for(DataSnapshot postSnapShot : dataSnapshot.getChildren()){
                    if(id.equals(postSnapShot.getKey())){
                       if(pw.equals(postSnapShot.child("userINFO").child("userPW").getValue())){
                           Toast.makeText(getApplicationContext(), "로그인 성공", Toast.LENGTH_SHORT).show();
                           newCheck = 100;
                           name = postSnapShot.child("userINFO").child("userNAME").getValue().toString();
                           message = postSnapShot.child("userINFO").child("userMESSAGE").getValue().toString();
                           //아이디, 닉네임, 상태메세지를 intent로 전송
                           Intent intent = new Intent(getApplicationContext(), FriendListActivity.class);
                           intent.putExtra("id", id);
                           intent.putExtra("name", name);
                           intent.putExtra("message", message);
                           startActivity(intent);
                           return;
                       }
                       else{
                           newCheck = 100;
                           Toast.makeText(getApplicationContext(), "로그인 실패", Toast.LENGTH_SHORT).show();
                           return;
                       }
                    }
                }
                for (DataSnapshot postSnapShot : dataSnapshot.getChildren()) {
                    if (!id.equals(postSnapShot.getKey()) && newCheck != 100) {
                        Toast.makeText(getApplicationContext(), "존재하지 않는 아이디입니다.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return newCheck;
    }

}
