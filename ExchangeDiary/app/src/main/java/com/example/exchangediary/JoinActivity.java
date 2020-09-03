package com.example.exchangediary;

import android.content.Intent;
import android.provider.ContactsContract;
import android.renderscript.Sampler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Iterator;

public class JoinActivity extends AppCompatActivity {
    DatabaseReference joinDatabase = FirebaseDatabase.getInstance().getReference();
    EditText makeId;
    EditText makeName;
    EditText makeEmail;
    EditText makePassword;
    EditText okPassword;
    int newcheck = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);

        FirebaseApp.initializeApp(this);

        Button btnJoin = (Button) findViewById(R.id.btnJoin);

        btnJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                makeId = (EditText) findViewById(R.id.makeId);
                makeName = (EditText) findViewById(R.id.makeName);
                makeEmail = (EditText) findViewById(R.id.makeEmail);
                makePassword = (EditText) findViewById(R.id.makePassword);
                okPassword = (EditText) findViewById(R.id.okPassword);
                CheckBox checkbox = (CheckBox)findViewById(R.id.checkbox);

                String newId = makeId.getText().toString();
                String newName = makeName.getText().toString();
                String newEmail = makeEmail.getText().toString();
                String newPassword = makePassword.getText().toString();
                String newOk = okPassword.getText().toString();


                if(!checkbox.isChecked()){
                    Toast.makeText(getApplicationContext(), "중복체크를 해주세요.", Toast.LENGTH_SHORT).show();
                }
                else if (newPassword.equals(newOk) && !newId.isEmpty() && !newName.isEmpty() && !newEmail.isEmpty() && !newPassword.isEmpty() && !newOk.isEmpty()) {
                    if(newcheck == 100 && checkbox.isChecked() && android.util.Patterns.EMAIL_ADDRESS.matcher(newEmail).matches())
                        writeNewUser(newId, newName, newEmail, newPassword, newOk);
                    //이메일 형식이 맞지 않을 때
                    if(!android.util.Patterns.EMAIL_ADDRESS.matcher(newEmail).matches())
                        Toast.makeText(getApplicationContext(),"올바른 이메일 형식이 아닙니다",Toast.LENGTH_SHORT).show();
                }
                else if(!newPassword.equals(newOk))
                    Toast.makeText(getApplicationContext(), "비밀번호와 비밀번호 확인란이 다릅니다.", Toast.LENGTH_SHORT).show();
                else if(newId.isEmpty() || newPassword.isEmpty() || newOk.isEmpty() || newName.isEmpty() || newEmail.isEmpty())
                    Toast.makeText(getApplicationContext(), "모든 칸을 입력해주세요.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void GoBackLogin(View v) {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    public void overlapCheck(View v) {
        makeId = (EditText) findViewById(R.id.makeId);
        String newId = makeId.getText().toString();
        CheckBox checkbox = (CheckBox)findViewById(R.id.checkbox);

        if(!newId.isEmpty())
            checkID(newId, 0);
        else if(newId.isEmpty())
            Toast.makeText(getApplicationContext(), "아이디를 입력하세요", Toast.LENGTH_SHORT).show();
    }

    public void writeNewUser(String id, String name, String email, String password, String check) {
        User user = new User(id, name, email, password, check, "");
        //joinDatabase.push().setValue(user)
        joinDatabase.child("users").child(id).child("userINFO").setValue(user);

        Toast.makeText(getApplicationContext(), "회원가입 완료", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    public void checkID(final String id, final int num) {
        joinDatabase.child("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                CheckBox checkbox = (CheckBox)findViewById(R.id.checkbox);
                newcheck = num;

                if(dataSnapshot.getChildrenCount() != 0) {
                    for (DataSnapshot postSnapShot : dataSnapshot.getChildren()) {
                        if (id.equals(postSnapShot.getKey())) {
                            if (checkbox.isChecked())
                                break;
                            else {
                                makeId.setText(null);
                                Toast.makeText(getApplicationContext(), "존재하는 아이디 입니다.", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }
                    }
                    for (DataSnapshot postSnapShot : dataSnapshot.getChildren()) {
                        if (!id.equals(postSnapShot.getKey())) {
                            if(!checkbox.isChecked() && newcheck == 0) {
                                Toast.makeText(getApplicationContext(), "사용 가능한 아이디 입니다.", Toast.LENGTH_SHORT).show();
                                newcheck = 100;
                                checkbox.setChecked(true);
                                makeId.setEnabled(false);
                            }
                            else if (checkbox.isChecked() && newcheck == 0) {
                                newcheck = 100;
                                joinDatabase.removeEventListener(this);
                                break;
                            }
                        }
                    }
                }
                else if(dataSnapshot.getChildrenCount() == 0){
                    Toast.makeText(getApplicationContext(), "사용 가능한 아이디 입니다.", Toast.LENGTH_SHORT).show();
                    newcheck = 100;
                    checkbox.setChecked(true);
                    makeId.setEnabled(false);
                    joinDatabase.removeEventListener(this);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
}
