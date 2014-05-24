package com.tizen.client;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class UserListAdapter extends BaseAdapter {

    private List<String> mItems = new ArrayList<String>();

    private final Context mContext;

    public UserListAdapter(Context context) {
        mContext = context;
    }

    public void add(String nickname) {
        mItems.add(nickname);
        notifyDataSetChanged();
    }

    public void remove(String nickname) {
        mItems.remove(nickname);
        notifyDataSetChanged();
    }

    public void fill(ArrayList<String> list) {
        mItems = list;
        notifyDataSetChanged();
    }

    @Override
    public String getItem(int position) {
        return mItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final String nickname = mItems.get(position);

        LayoutInflater inflater = LayoutInflater.from(mContext);
        LinearLayout itemLayout = (LinearLayout) inflater.inflate(R.layout.item, null);

        final TextView titleView = (TextView) itemLayout.findViewById(R.id.player);
        titleView.setText(nickname);

        // Return the View you just created
        return itemLayout;

    }
}
