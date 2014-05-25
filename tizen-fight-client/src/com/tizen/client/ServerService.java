package com.tizen.client;

import android.app.Service;
import android.content.Intent;
import android.os.*;
import android.util.Log;
import io.socket.IOAcknowledge;
import io.socket.IOCallback;
import io.socket.SocketIO;
import io.socket.SocketIOException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.util.ArrayList;

public class ServerService extends Service implements IOCallback {

    private final String TAG = "tizen-fight";
    public final static String SERVER_SERVICE = "com.tizen.client.SERVER_SERVICE";

    private SocketIO mServerSocket;

    public static final int CONNECT_MSG = 0;
    public static final int DISCONNECT_MSG = 1;
    public static final int USER_LIST_MSG = 2;

    private final Messenger mMessenger = new Messenger(new IncomingMessageHandler());
    class IncomingMessageHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch(msg.what) {
                case CONNECT_MSG:
                    ServerService.this.connect(msg.getData());
                    break;
                case DISCONNECT_MSG:
                    ServerService.this.disconnect();
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mMessenger.getBinder();
    }

    public void connect(Bundle bundle) {
        String url = bundle.getString("url");
        String nickname = bundle.getString("nickname");
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
        Log.i(TAG, "ServerService said: " + data);
    }

    @Override
    public void onError(SocketIOException e) {
        Log.e(TAG, "Error: " + e.getMessage());
    }

    @Override
    public void onConnect() {
        //mHandler.sendEmptyMessage(CONNECTED);
        Log.i(TAG, "Connection established.");
    }

    @Override
    public void onDisconnect() {
        //mHandler.sendEmptyMessage(DISCONNECTED);
        Log.i(TAG, "Connection terminated.");
    }

    @Override
    public void on(String event, IOAcknowledge ack, Object... args) {
        int what = 0;
        Bundle bundle = new Bundle();

        if (event.equals("user-list")) {
            JSONArray jsonArray = (JSONArray) args[0];
            broadcastUserList(jsonArray);
        }

        Log.i(TAG, "ServerService triggered event '" + event + "'");
    }

    public void broadcastUserList(JSONArray jsonArray) {
        Intent intent = new Intent(UserListActivity.USER_LIST_INTENT);
        ArrayList<String> userList = new ArrayList<String>();
        try {
            for (int i = 0; i < jsonArray.length(); i++)
                userList.add(jsonArray.get(i).toString());
        }
        catch (JSONException e) {
            Log.e(TAG, e.getMessage());
        }
        intent.putStringArrayListExtra("userList", userList);
        sendBroadcast(intent);
    }
}
