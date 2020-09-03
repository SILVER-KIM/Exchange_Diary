package com.example.exchangediary;

import android.app.Fragment;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class RequestFriendAdapter extends BaseAdapter{
    DatabaseReference joinDatabase = FirebaseDatabase.getInstance().getReference();
    ArrayList<RequestItem> items = new ArrayList<RequestItem>();
    private FriendList friendListFragment;
    String myId;
    String friendId;
    Context context;

    public void RequestFriendAdapter(Context context){
        this.context = context;
    }

    public int getCount(){
        return items.size();
    }

    public void addItem(RequestItem item){
        if(!items.contains(item))
            items.add(item);
    }

    public Object getItem(int position){
        return items.get(position);
    }

    public long getItemId(int position){
        return position;
    }

    public void removeItem(int position){
        items.remove(position);
        removeDB(myId, friendId);
    }

    public View getView(final int position, final View convertView, final ViewGroup viewGroup){
        RequestItemView view = new RequestItemView(viewGroup.getContext());
        final RequestItem item = items.get(position);
        view.setID(item.getRequestID());

        // 취소 버튼 클릭했을 때
        Button noBTN = (Button)view.findViewById(R.id.no);
        noBTN.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                friendId = item.getRequestID();
                removeItem(position);
                notifyDataSetChanged();
                Toast.makeText(context, "친구신청을 거부했습니다.", Toast.LENGTH_SHORT).show();
            }
        });

        // 수락 버튼 클릭했을 때
        Button yesBTN = (Button)view.findViewById(R.id.yes);
        yesBTN.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                friendId = item.getRequestID();
                removeItem(position);
                addDB(myId, friendId);
                notifyDataSetChanged();
                Toast.makeText(context, "친구가 되었습니다:)", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    public void sendDB( String myID){
        myId = myID;
    }

    // request 데이터베이스에서 해당 데이터를 삭제하는 기능
    // 취소를 클릭했을 때는 삭제만! 수락을 클릭했을 때는 각각의 userFriend에 서로를 추가!
    public void removeDB(final String myID, final String friendID){
        joinDatabase.child("request").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getChildrenCount() != 0) {
                    // 상대방이 나에게 친구신청을 보낸 적이 있는지 검사하는 기능
                    for (DataSnapshot postSnapShot : dataSnapshot.getChildren()) {
                        if (myID.equals(postSnapShot.child("Receiver").getValue())) {
                            if (friendID.equals(postSnapShot.child("Sender").getValue())) {
                                String position = postSnapShot.getKey();
                                joinDatabase.child("request").child(position).removeValue();
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

    public void addDB(final String myID, final String friendID){
        joinDatabase.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getChildrenCount() != 0) {
                    for (DataSnapshot postSnapShot : dataSnapshot.getChildren()) {
                        if (myID.equals(postSnapShot.getKey())) {
                            if(!friendID.equals(postSnapShot.child("userFriend").getKey())) {
                                joinDatabase.child("users").child(myID).child("userFriend").child(friendID).setValue(friendID);
                                joinDatabase.child("users").child(friendID).child("userFriend").child(myID).setValue(myID);
                                joinDatabase.removeEventListener(this);
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
