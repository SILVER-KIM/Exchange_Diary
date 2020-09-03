package com.example.exchangediary;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import static android.support.constraint.Constraints.TAG;

public class MyFriendItemView extends LinearLayout  {
    DatabaseReference joinDatabase = FirebaseDatabase.getInstance().getReference();
    TextView friend_name;
    TextView friend_miniMESSAGE;
    ImageView friend_image;
    Button friend_diary;
    String photoURI;
    ProgressBar pbLogin;


    public MyFriendItemView(Context context){
        super(context);
        init(context);
    }
    public MyFriendItemView(Context context, AttributeSet attrs){
        super(context, attrs);
        init(context);
    }

    public void init(Context context){
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.item_friend, this, true);

        friend_name = (TextView)findViewById(R.id.friend_Name);
        friend_miniMESSAGE = (TextView)findViewById(R.id.friend_miniMESSAGE);
        friend_image = (ImageView)findViewById(R.id.friend_image);
        friend_diary = (Button)findViewById(R.id.diaryBTN);
        pbLogin = (ProgressBar)findViewById(R.id.pbLogin);
    }

    public void setName(String friendName) {
        friend_name.setText(friendName);
    }
    public void setMessage(String friendMessage) {
        friend_miniMESSAGE.setText(friendMessage);
    }
    public void setImage(String friendID) {
        checkURI(friendID);
    }


    public void checkURI(final String myID){
        joinDatabase.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getChildrenCount() != 0) {
                    for (DataSnapshot postSnapShot : dataSnapshot.getChildren()) {
                        if (myID.equals(postSnapShot.getKey())) {
                            if (postSnapShot.child("userProfile").getChildrenCount() != 0) {
                                photoURI = postSnapShot.child("userProfile").child("FileURL").getValue().toString();
                                Picasso.with(getContext())
                                        .load(photoURI)
                                        .fit()
                                        .centerCrop()
                                        .into(friend_image, new Callback.EmptyCallback() {
                                            @Override
                                            public void onSuccess() {
                                                Log.d(TAG, "SUCCESS");
                                                pbLogin.setVisibility(View.GONE);
                                            }
                                        });
                                joinDatabase.removeEventListener(this);
                                break;
                            }
                        }
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}