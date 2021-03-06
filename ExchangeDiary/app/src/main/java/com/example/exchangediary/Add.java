package com.example.exchangediary;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static com.google.android.gms.common.internal.safeparcel.SafeParcelable.NULL;

public class Add extends AppCompatActivity {
    DatabaseReference joinDatabase = FirebaseDatabase.getInstance().getReference();
    private ShowUser showUserFragment;
    EditText userID;
    Button searchUSER;
    String saveNAME = NULL;
    String saveID;
    String inputID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        Intent intent = getIntent();
        saveID = intent.getStringExtra("id");

        searchUSER = (Button)findViewById(R.id.btnUserSearch_add);

        showUserFragment = new ShowUser();

        searchUSER.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userID = (EditText)findViewById(R.id.userEdit_add);
                inputID = userID.getText().toString();
                if(!saveID.equals(inputID))
                    checkUSER(inputID);
                else
                    Toast.makeText(getApplicationContext(), "너 스스로를 찾지마!", Toast.LENGTH_SHORT).show();
            }
        });
    }
    public  void sendBundle(Fragment fragment){
        Bundle bundle = new Bundle(1);
        bundle.putString("myID", saveID);
        bundle.putString("friendID",inputID);
        bundle.putString("name", saveNAME);
        bundle.putString("type", "add");
        fragment.setArguments(bundle);
    }

    public void onBackPressed(){
        ((FriendListActivity)FriendListActivity.mContext).refresh();
        this.finish();
    }

    public void showFrag(){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        sendBundle(showUserFragment);
        transaction.replace(R.id.add_fragment_container, showUserFragment).commitAllowingStateLoss();
        getSupportFragmentManager().beginTransaction().detach(showUserFragment).attach(showUserFragment).commitAllowingStateLoss();
    }

    public void checkUSER(final String id) {
        joinDatabase.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int newCheck = 0;
                for(DataSnapshot postSnapShot : dataSnapshot.getChildren()){
                    if(id.equals(postSnapShot.getKey())){
                        saveNAME = postSnapShot.child("userINFO").child("userNAME").getValue().toString();
                        newCheck = 100;
                        showFrag();
                    }
                }
                for(DataSnapshot postSnapShot : dataSnapshot.getChildren()){
                    if (!id.equals(postSnapShot.getKey()) && newCheck != 100) {
                        Toast.makeText(getApplicationContext(), "일치하는 사용자가 없습니다.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
}