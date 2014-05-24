package com.tizen.client;

import android.app.ListActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

public class UserListActivity extends ListActivity {

    private static final String USER_LIST_INTENT = "user-list";
    private static final String USER_CONNECTED_INTENT = "user-connected";
    private static final String USER_DISCONNECTED_INTENT = "user-disconnected";

    private UserListAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(USER_LIST_INTENT)) updateList(intent);
                if (intent.getAction().equals(USER_CONNECTED_INTENT)) onUserConnected(intent);
                if (intent.getAction().equals(USER_DISCONNECTED_INTENT)) onUserDisconnected(intent);
            }
        }, new IntentFilter(USER_LIST_INTENT));

        mAdapter = new UserListAdapter(getApplicationContext());
        getListView().setAdapter(mAdapter);
    }

    @Override
    protected void onListItemClick(ListView listView, View v, int position, long id) {
        String item = (String) getListAdapter().getItem(position);
        // TODO: Implement this
        Toast.makeText(this, item + " selected", Toast.LENGTH_LONG).show();
    }

    public void updateList(Intent intent) {
        mAdapter.fill(intent.getStringArrayListExtra("userList"));
    }

    public void onUserConnected(Intent intent) {
        mAdapter.add(intent.getStringExtra("nickname"));
    }

    public void onUserDisconnected(Intent intent) {
        mAdapter.remove(intent.getStringExtra("nickname"));
    }
}
