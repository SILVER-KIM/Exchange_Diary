package com.example.exchangediary;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

//커스텀 어댑터(내가 만든) friendlist자바에서 다 표현하기 싫어서 따로 뺀 어댑터..몰라 이렇게 하는게 좋대
public class MyFriendAdapter extends BaseAdapter {
    DatabaseReference joinDatabase = FirebaseDatabase.getInstance().getReference();
    ArrayList<MyFriendItem> items = new ArrayList<MyFriendItem>();
    Context context;

    public void MyFriendAdapter(Context context) {
        this.context = context;
    }

    public int getCount() {
        return items.size();
    }

    public void addItem(MyFriendItem item) {
        items.add(item);
    }

    public Object getItem(int position) {
        return items.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public void removeItem(int position) {
        items.remove(position);
    }

    public View getView(final int position, final View convertView, final ViewGroup viewGroup) {
        final MyFriendItemView view = new MyFriendItemView(viewGroup.getContext());
        final MyFriendItem item = items.get(position);
        view.setName(item.getFriendNAME());
        view.setMessage(item.getFriendMESSAGE());
        view.setImage(item.getFriendID());

        Button GTDiary = (Button)view.findViewById(R.id.diaryBTN);
        GTDiary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, NewBookshelf.class);
                intent.putExtra("myID",item.getMyID());
                intent.putExtra("friendID",item.getFriendID());
                context.startActivity(intent);
            }
        });
        return view;
    }
}

