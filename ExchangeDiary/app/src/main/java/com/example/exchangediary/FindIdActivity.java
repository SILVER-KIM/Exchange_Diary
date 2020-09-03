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

public class FindIdActivity extends AppCompatActivity {

    DatabaseReference joinDatabase;

    EditText editName;
    EditText editEmail;

    String findIdValue;

    Button btnFindIdOk;

    int newCheck;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_id);

        joinDatabase = FirebaseDatabase.getInstance().getReference();

        //확인 버튼 누르면 아이디 찾기
        btnFindIdOk = (Button)findViewById(R.id.btnFindIdOk);
        btnFindIdOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                editName = (EditText)findViewById(R.id.editName);
                editEmail = (EditText)findViewById(R.id.editEmail);

                String strName = editName.getText().toString();
                String strEmail = editEmail.getText().toString();

                //모든 칸을 입력하지 않았을 시
                if(strName.isEmpty() || strEmail.isEmpty() ) {
                    Toast.makeText(getApplicationContext(), "모든 칸을 입력해주세요.", Toast.LENGTH_SHORT).show();
                }
                //이메일 형식이 맞지 않을 때
                if(!strEmail.isEmpty() && !android.util.Patterns.EMAIL_ADDRESS.matcher(strEmail).matches()) {
                    Toast.makeText(getApplicationContext(), "올바른 이메일 형식이 아닙니다", Toast.LENGTH_SHORT).show();
                }
                FindID(strName, strEmail);

            }
        });
    }
    //찾은 아이디 출력 확인 버튼 밑에 출력하기
    private void PrintID() {
        TextView txtFindIdValue = (TextView)findViewById(R.id.txtFindIdValue);
        TextView text1 = (TextView)findViewById(R.id.text1);

        txtFindIdValue.setText(findIdValue);

        text1.setVisibility(View.VISIBLE);
        txtFindIdValue.setVisibility(View.VISIBLE);
    }

    //아이디 찾기
    private void FindID(final String strName, final String strEmail) {
        joinDatabase.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                newCheck=0;
                for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    //입력한 이름과 데이터베이스에 있는 이름이 같고, 입력한 이메일과 데이터베이스에 있는 이메일이 일치할 때
                    if (strName.equals(snapshot.child("userINFO").child("userNAME").getValue())
                            && strEmail.equals(snapshot.child("userINFO").child("userEMAIL").getValue())) {
                        findIdValue = snapshot.getKey();
                        PrintID();
                        //다시 입력 못하도록 버튼 못누르게 설정
                        btnFindIdOk.setEnabled(false);
                        editName.setEnabled(false);
                        editEmail.setEnabled(false);
                        newCheck = 100;
                    }
                }

                for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if(!strName.equals(snapshot.child("userINFO").child("userNAME").getValue())
                            && !strEmail.equals(snapshot.child("userINFO").child("userEMAIL").getValue()) && newCheck != 100
                            && android.util.Patterns.EMAIL_ADDRESS.matcher(strEmail).matches()){
                        Toast.makeText(getApplicationContext(), "입력하신 정보와 일치하는 아이디가 없습니다.", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
