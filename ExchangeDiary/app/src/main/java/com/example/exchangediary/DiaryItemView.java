package com.example.exchangediary;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import static android.support.constraint.Constraints.TAG;

public class DiaryItemView extends LinearLayout {
    DatabaseReference joinDatabase = FirebaseDatabase.getInstance().getReference();
    ImageView diary_image;
    String photoURI;

    public DiaryItemView(Context context){
        super(context);
        init(context);
    }

    public DiaryItemView(Context context, AttributeSet attrs){
        super(context, attrs);
        init(context);
    }

    public void init(Context context){
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.item_diary, this, true);

        diary_image = (ImageView)findViewById(R.id.viewDiary);
    }

    public void setImage(String myID, String friendID, String diaryName) {
        checkURI(myID, friendID, diaryName);
    }

    public void checkURI(final String myID, final String friendID, final String diaryName){
        joinDatabase.child("BookShelf").child(myID).child("Friends").child(friendID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot postSnapShot : dataSnapshot.getChildren()) {
                        if(diaryName.equals(postSnapShot.getKey())){
                            photoURI = postSnapShot.child("diaryURI").getValue().toString();
                            Picasso.with(getContext())
                                    .load(photoURI)
                                    .fit()
                                    .centerCrop()
                                    .into(diary_image, new Callback.EmptyCallback() {
                                        @Override
                                        public void onSuccess() {
                                            Log.d(TAG, "SUCCESS");
                                            }
                                    });
                                }
                            }
                        }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
