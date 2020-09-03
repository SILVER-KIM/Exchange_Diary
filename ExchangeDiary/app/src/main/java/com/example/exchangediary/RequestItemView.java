package com.example.exchangediary;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.jar.Attributes;

public class RequestItemView extends LinearLayout {
    TextView friend_name;

    public RequestItemView(Context context){
        super(context);
        init(context);
    }
    public RequestItemView(Context context, AttributeSet attrs){
        super(context, attrs);
        init(context);
    }

    public void init(Context context){
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.item_request, this, true);

        friend_name = (TextView)findViewById(R.id.friend_name);
    }

    public void setID(String requestID) {
        friend_name.setText(requestID);
    }
}
