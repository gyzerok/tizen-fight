package com.tizen.client;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import io.socket.IOAcknowledge;
import io.socket.IOCallback;
import io.socket.SocketIO;
import io.socket.SocketIOException;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;

public class Server implements IOCallback {

    private final String TAG = "tizen-fight";

    private SocketIO mServerSocket;

    private static final int CONNECTED = 0;
    private static final int DISCONNECTED = 1;
    private static final int USER_LIST = 2;

    private static Server instance = null;

    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch(msg.what) {
                case CONNECTED:
                    // TODO: Send start UserListActivity Intent
                case DISCONNECTED:
                    // TODO: ?
                    break;
                case USER_LIST:
                    // TODO: Send user-list update Intent
                    break;
            }
        }
    };

    private Server() {}

    public static Server getInstance() {
        if (instance == null) instance = new Server();
        return instance;
    }

    public void connect(String url, String nickname) {
        try {
            mServerSocket = new SocketIO(url);
            mServerSocket.connect(this);
            try {
                mServerSocket.emit("handshake", new JSONObject("{username: " + nickname + "}"));
            }
            catch (JSONException e) {
                Log.e(TAG, e.getMessage());
            }
        }
        catch (MalformedURLException e) {
            Log.e(TAG, e.getMessage());
        }
    }

    public void disconnect() {
        mServerSocket.disconnect();
    }

    @Override
    public void onMessage(JSONObject json, IOAcknowledge ack) {
        Log.i(TAG, "Message received");
    }

    @Override
    public void onMessage(String data, IOAcknowledge ack) {
        Log.i(TAG, "Server said: " + data);
    }

    @Override
    public void onError(SocketIOException e) {
        Log.e(TAG, "Error: " + e.getMessage());
    }

    @Override
    public void onConnect() {
        mHandler.sendEmptyMessage(CONNECTED);
        Log.i(TAG, "Connection established.");
    }

    @Override
    public void onDisconnect() {
        mHandler.sendEmptyMessage(DISCONNECTED);
        Log.i(TAG, "Connection terminated.");
    }

    @Override
    public void on(String event, IOAcknowledge ack, Object... args) {
        int what = 0;

        if (event.equals("user-list")) what = USER_LIST;

        Message msg = mHandler.obtainMessage(what, args[0]);
        mHandler.sendMessage(msg);
        Log.i(TAG, "Server triggered event '" + event + "'");
    }
}
