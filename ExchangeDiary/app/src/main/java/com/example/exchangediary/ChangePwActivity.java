package com.example.exchangediary;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ChangePwActivity extends AppCompatActivity {

    DatabaseReference joinDatabase;

    EditText presentPw;
    EditText newPw;
    EditText reInputPw;

    String idValue;

    private AddFriend addFriendFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_pw);

        joinDatabase = FirebaseDatabase.getInstance().getReference();

        addFriendFragment = new AddFriend();

        Intent intent = getIntent();
        idValue = intent.getStringExtra("id");

        Button btnChange = (Button)findViewById(R.id.btnChange);
        btnChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                presentPw = (EditText)findViewById(R.id.presentPw);
                newPw = (EditText)findViewById(R.id.newPw);
                reInputPw = (EditText)findViewById(R.id.reInputPw);

                String password1 = presentPw.getText().toString();
                String password2 = newPw.getText().toString();
                String password3 = reInputPw.getText().toString();

                //모든 칸을 입력하지 않았을 시
                if(password1.isEmpty() || password2.isEmpty() || password3.isEmpty())
                    Toast.makeText(getApplicationContext(), "모든 칸을 입력해주세요.", Toast.LENGTH_SHORT).show();
                    // 새로 입력한 비밀번호와 비밀번호 확인란이 다를 때
                else if(!password2.equals(password3))
                    Toast.makeText(getApplicationContext(), "비밀번호와 비밀번호 확인란이 다릅니다.", Toast.LENGTH_SHORT).show();
                else if(!password1.isEmpty() && !password2.isEmpty() && !password3.isEmpty())
                    PasswordChange(password1, password2, password3);

            }
        });
        //취소 버튼 누르면 설정화면으로 다시 돌아가기
    }

    public void cancel(View v){
        this.finish();
    }


    //비밀번호 변경 기능
    public void PasswordChange(final String pw1, final String pw2, final String pw3) {
        joinDatabase.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    //입력한 비밀번호와 현재 입력한 비밀번호가 같고
                    if (idValue.equals(snapshot.getKey())) {
                        if (pw1.equals(snapshot.child("userINFO").child("userPW").getValue())) {
                            // 새로 입력한 비밀번호와 비밀번호 확인란이 같을 때
                            if (pw2.equals(pw3)) {
                                //새로운 비밀번호로 변경하기
                                joinDatabase.child("users").child(idValue).child("userINFO").child("userPW").setValue(pw2);
                                joinDatabase.child("users").child(idValue).child("userINFO").child("checkPW").setValue(pw3);

                                //비밀번호 변경완료 대화상자 띄우기
                                new AlertDialog.Builder(ChangePwActivity.this)
                                        .setTitle("변경 완료")
                                        .setMessage("\n비밀번호 변경을 완료하였습니다.\n로그인을 다시 해주세요.")
                                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                                                startActivity(intent);
                                            }
                                        }).show();
                                break;
                            }
                        }
                        //입력한 비밀번호와 현재 비밀번호가 다를 때 (첫째 칸)
                        else
                            Toast.makeText(getApplicationContext(), "현재 비밀번호가 틀립니다. 비밀번호를 다시 입력해주세요.", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}