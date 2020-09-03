package com.example.exchangediary;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class Message extends FragmentActivity {
    DatabaseReference joinDatabase = FirebaseDatabase.getInstance().getReference();
    private ListView mRequestFriend;
    private FriendList friendListFragment;
    RequestFriendAdapter mRequestAdapter;
    String friendID;
    String myID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        this.setFinishOnTouchOutside(false);

        friendListFragment = new FriendList();

        // 로그인한 나의 ID를 받아와서 저장
        Intent intent = getIntent();
        myID = intent.getStringExtra("id");

        mRequestFriend = (ListView)findViewById(R.id.requestList);

        mRequestAdapter = new RequestFriendAdapter();
        mRequestAdapter.RequestFriendAdapter(getApplicationContext());
        mRequestAdapter.sendDB(myID);
        checkRequest(myID);
        //리스트에 어댑터뷰를 연결
        mRequestFriend.setAdapter(mRequestAdapter);
    }

    // X버튼 클릭 시 액티비티 종료하는 기능
    public void onClick(View v){
        switch (v.getId()){
            case R.id.finishBTN:
                ((FriendListActivity)FriendListActivity.mContext).refresh();
                this.finish();
                break;
        }
    }

    // 데이터베이스의 친구신청(request)을 체크해서 나에게 온 친구신청이 있는지 확인
    public void checkRequest(final String myId) {
        joinDatabase.child("request").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot postSnapShot : dataSnapshot.getChildren()){
                    if(myId.equals(postSnapShot.child("Receiver").getValue())){
                        friendID = postSnapShot.child("Sender").getValue().toString();
                        mRequestAdapter.addItem(new RequestItem(friendID));
                        mRequestAdapter.notifyDataSetChanged();
                        }
                    }
                }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
}
