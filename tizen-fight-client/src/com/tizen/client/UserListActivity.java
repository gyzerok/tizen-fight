package com.tizen.client;

import android.app.ListActivity;
import android.content.*;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Messenger;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

public class UserListActivity extends ListActivity {

    public static final String USER_LIST_INTENT = "user-list";

    private UserListAdapter mAdapter;

    private final Intent mServerServiceIntent = new Intent(ServerService.SERVER_SERVICE);

    private BroadcastReceiver mReceiver;


    private Messenger mMessenger;
    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mMessenger = new Messenger(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mMessenger = null;
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(USER_LIST_INTENT)) onUserListEvent(intent);
            }
        };

        mAdapter = new UserListAdapter(getApplicationContext());
        getListView().setAdapter(mAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();

        bindService(mServerServiceIntent, mConnection,
                Context.BIND_AUTO_CREATE);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(USER_LIST_INTENT);

        registerReceiver(mReceiver, intentFilter);
    }

    @Override
    public void onPause() {
        unbindService(mConnection);
        unregisterReceiver(mReceiver);

        super.onPause();
    }

    @Override
    protected void onListItemClick(ListView listView, View v, int position, long id) {
        String item = (String) getListAdapter().getItem(position);
        // TODO: Implement this
        Toast.makeText(this, item + " selected", Toast.LENGTH_LONG).show();
    }

    public void onUserListEvent(Intent intent) {
        mAdapter.fill(intent.getStringArrayListExtra("userList"));
    }
}
