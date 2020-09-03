package com.example.exchangediary;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class DiaryAdapter extends BaseAdapter{
    DatabaseReference joinDatabase = FirebaseDatabase.getInstance().getReference();
    ArrayList<DiaryItem> items = new ArrayList<DiaryItem>();
    private Context context;
    int order;


    public DiaryAdapter(Context context) {
        this.context = context;
    }

    public void addItem(DiaryItem item) {
        items.add(item);
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final DiaryItemView view = new DiaryItemView(parent.getContext());
        final DiaryItem item = items.get(position);
        view.setImage(item.getMyID(), item.getFriendID(), item.getDiaryName());
        return view;
    }
}
