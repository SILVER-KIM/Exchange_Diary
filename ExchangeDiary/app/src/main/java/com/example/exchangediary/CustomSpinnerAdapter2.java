package com.example.exchangediary;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;


public class CustomSpinnerAdapter2 extends ArrayAdapter<String> {

    String[] spinnerNames2;
    int[] spinnerImages2;
    Context mContext;

    public CustomSpinnerAdapter2(@NonNull Context context, String[] names, int[] images) {
        super(context, R.layout.spinner_row);

        this.spinnerNames2 = names;
        this.spinnerImages2 = images;
        this.mContext = context;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return getView(position, convertView, parent);
    }

    @Override
    public int getCount() {
        return spinnerNames2.length;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        ViewHolder mViewHolder = new ViewHolder();

        if (convertView == null) {

            LayoutInflater mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.spinner_row, parent, false);

            mViewHolder.mImage = (ImageView) convertView.findViewById(R.id.imageview_spinner_image);
            mViewHolder.mName = (TextView) convertView.findViewById(R.id.textview_spinner_name);
            convertView.setTag(mViewHolder);

        } else {

            mViewHolder = (ViewHolder) convertView.getTag();
        }

        mViewHolder.mImage.setImageResource(spinnerImages2[position]);
        mViewHolder.mName.setText(spinnerNames2[position]);

        return convertView;
    }

    private static class ViewHolder {

        ImageView mImage;
        TextView mName;
    }
}

