package com.example.exchangediary;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

public class FindPwActivity extends AppCompatActivity {

    DatabaseReference joinDatabase;

    EditText editId;
    EditText editEmail2;

    String findPwValue;

    Button btnFindPwOk;

    int newCheck;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_pw);

        joinDatabase = FirebaseDatabase.getInstance().getReference();

        btnFindPwOk = (Button)findViewById(R.id.btnFindPwOk);
        btnFindPwOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                editId = (EditText)findViewById(R.id.editId);
                editEmail2 = (EditText)findViewById(R.id.editEmail2);

                String strID = editId.getText().toString();
                String strEmail2 = editEmail2.getText().toString();

                //모든 칸을 입력하지 않았을 시
                if(strID.isEmpty() || strEmail2.isEmpty() ) {
                    Toast.makeText(getApplicationContext(), "모든 칸을 입력해주세요.", Toast.LENGTH_SHORT).show();
                }
                //이메일 형식이 맞지 않을 때
                if(!strEmail2.isEmpty() && !android.util.Patterns.EMAIL_ADDRESS.matcher(strEmail2).matches()) {
                    Toast.makeText(getApplicationContext(), "올바른 이메일 형식이 아닙니다", Toast.LENGTH_SHORT).show();
                }
                FindPw(strID, strEmail2);
            }
        });
    }
    //찾은 비밀번호 출력 확인 버튼 밑에 출력하기
    private void PrintPw() {
        TextView txtFindPwValue = (TextView)findViewById(R.id.txtFindPwValue);
        TextView text2 = (TextView)findViewById(R.id.text2);

        txtFindPwValue.setText(findPwValue);

        text2.setVisibility(View.VISIBLE);
        txtFindPwValue.setVisibility(View.VISIBLE);
    }

    //비밀번호 찾기
    private void FindPw(final String strID, final String strEmail2) {
        joinDatabase.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                newCheck=0;
                for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    //입력한 아이디와 이메일이 일치하는 비밀번호를 찾기~
                    if (strID.equals(snapshot.getKey()) && strEmail2.equals(snapshot.child("userINFO").child("userEMAIL").getValue())) {
                        findPwValue = snapshot.child("userINFO").child("userPW").getValue().toString();
                        PrintPw();
                        //다시 입력 못하도록 버튼 못누르게 설정
                        btnFindPwOk.setEnabled(false);
                        editId.setEnabled(false);
                        editEmail2.setEnabled(false);
                        newCheck=100;
                    }
                }
                for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if (strID.equals(snapshot.getKey()) && strEmail2.equals(snapshot.child("userINFO").child("userEMAIL").getValue()) && newCheck != 100) {
                        Toast.makeText(getApplicationContext(), "입력하신 정보와 일치하는 비밀번호가 없습니다.", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
