package com.example.exchangediary;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Adapter;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class FriendListActivity extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;
    DatabaseReference joinDatabase = FirebaseDatabase.getInstance().getReference();
    public static Context mContext;
    private FriendList friendListFragment;
    private MyProfile myProfileFragment;
    private AddFriend addFriendFragment;
    String id;
    String saveNAME;
    String saveMESSAGE;
    String signal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_list);
        mContext = this;

        // Login에서 보낸 intent를 받음
        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        saveNAME = intent.getStringExtra("name");
        saveMESSAGE = intent.getStringExtra("message");
        signal = intent.getStringExtra("signal");

        friendListFragment = new FriendList();
        myProfileFragment = new MyProfile();
        addFriendFragment = new AddFriend();


        sendBundle(friendListFragment);
        initFragment();

        bottomNavigationView = (BottomNavigationView)findViewById(R.id.bottomNavigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

            switch (item.getItemId()) {
                case R.id.friendList:
                    loadNAME();
                    sendBundle(friendListFragment);
                    transaction.replace(R.id.fragment_container, friendListFragment).commit();
                    return true;
                case R.id.myProfile:
                    loadNAME();
                    sendBundle(myProfileFragment);
                    transaction.replace(R.id.fragment_container, myProfileFragment).commit();
                    return true;
                case R.id.addFriend:
                    loadNAME();
                    sendBundle(addFriendFragment);
                    transaction.replace(R.id.fragment_container, addFriendFragment).commit();
                    return true;
            }
            return false;
        }
    };

    public void refresh(){
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.detach(friendListFragment).attach(friendListFragment).commitAllowingStateLoss();
    }

    public void refresh2(){
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.detach(myProfileFragment).attach(myProfileFragment).commitAllowingStateLoss();
    }

    public void initFragment(){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.fragment_container, friendListFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    // 프래그먼트에서는 intent가 아닌 Bundle을 사용해서 데이터를 전송(아이디, 닉네임, 상태메세지를 보냄)
    public  void sendBundle(Fragment fragment){
        Bundle bundle = new Bundle(1);
        bundle.putString("id", id);
        bundle.putString("name", saveNAME);
        bundle.putString("message", saveMESSAGE);
        fragment.setArguments(bundle);
    }

    // 프로필에서 닉네임과 상태메세지를 변경할 때, 다시 로드해주는 기능
    public void loadNAME(){
        joinDatabase.child("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount() != 0) {
                    for (DataSnapshot postSnapShot : dataSnapshot.getChildren()) {
                        if (id.equals(postSnapShot.getKey())) {
                            saveNAME = postSnapShot.child("userINFO").child("userNAME").getValue().toString();
                            saveMESSAGE = postSnapShot.child("userINFO").child("userMESSAGE").getValue().toString();
                        }
                    }
                }
            }
            @Override
            public void onCancelled (@NonNull DatabaseError databaseError){
            }
        });

    }
}
