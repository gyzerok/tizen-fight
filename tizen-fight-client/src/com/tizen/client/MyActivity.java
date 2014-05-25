package com.tizen.client;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.*;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MyActivity extends Activity {

    private final String TAG = "tizen-fight";

    private EditText mIpEdit;
    private EditText mNicknameEdit;
    private Button mLoginBtn;

    private final Intent mServerServiceIntent = new Intent(ServerService.SERVER_SERVICE);

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
        setContentView(R.layout.main);

        mIpEdit = (EditText) findViewById(R.id.ip);
        mNicknameEdit = (EditText) findViewById(R.id.nickname);
        mLoginBtn = (Button) findViewById(R.id.login);

        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendConnectMessage();
                startActivity(new Intent(MyActivity.this, UserListActivity.class));
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        bindService(mServerServiceIntent, mConnection,
                Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onPause() {
        unbindService(mConnection);

        super.onPause();
    }

    public void sendConnectMessage() {
        Message msg = Message.obtain(null, ServerService.CONNECT_MSG);
        Bundle bundle = new Bundle();
        bundle.putString("url", "http://" + mIpEdit.getText().toString() + "/");
        bundle.putString("nickname", mNicknameEdit.getText().toString());
        msg.setData(bundle);

        try {
            mMessenger.send(msg);
        }
        catch (RemoteException e) {
            Log.e(TAG, e.toString());
        }
    }
}
