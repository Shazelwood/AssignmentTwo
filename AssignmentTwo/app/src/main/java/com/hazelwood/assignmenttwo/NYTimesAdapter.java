package com.hazelwood.assignmenttwo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Hazelwood on 8/27/14.
 */
public class NYTimesAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<NYTimes> mObjects;

    private static final long ID_CONSTANT = 123456789;

    public NYTimesAdapter(Context c, ArrayList<NYTimes> objects){
        mContext = c;
        mObjects = objects;
    }

    @Override
    public int getCount() {
        return mObjects.size();
    }

    @Override
    public NYTimes getItem(int position) {
        return mObjects.get(position);
    }

    @Override
    public long getItemId(int position) {
        return ID_CONSTANT + position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null){
            convertView = LayoutInflater.from(mContext).inflate(R.layout.listayout, parent, false);
        }

        NYTimes item = getItem(position);
//        SmartImageView imageView = (SmartImageView)convertView.findViewById(R.id.newsImage);
        TextView textView1 = (TextView)convertView.findViewById(R.id.newsTitle);
        TextView textView2 = (TextView)convertView.findViewById(R.id.newsDescription);

        textView1.setText(item.title);
        textView2.setText(item.description);
//        imageView.setImageUrl(item.imageURL);


        return convertView;
    }

}
