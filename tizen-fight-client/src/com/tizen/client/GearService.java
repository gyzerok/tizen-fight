/*
 * Copyright (c) 2014 Samsung Electronics Co., Ltd.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 *     * Redistributions of source code must retain the above copyright
 *        notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above
 *       copyright notice, this list of conditions and the following disclaimer
 *       in the documentation and/or other materials provided with the
 *       distribution.
 *     * Neither the name of Samsung Electronics Co., Ltd. nor the names of its
 *       contributors may be used to endorse or promote products derived from
 *       this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */


package com.samsung.android.example.helloaccessoryprovider.service;

import java.io.IOException;
import java.util.HashMap;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.text.format.Time;
import android.util.Log;
import android.widget.Toast;
import com.samsung.android.example.helloaccessoryprovider.R;
import com.samsung.android.sdk.SsdkUnsupportedException;
import com.samsung.android.sdk.accessory.SA;
import com.samsung.android.sdk.accessory.SAAgent;
import com.samsung.android.sdk.accessory.SAPeerAgent;
import com.samsung.android.sdk.accessory.SASocket;

public class HelloAccessoryProviderService extends SAAgent {
    public static final String TAG = "HelloAccessoryProviderService";

    public static final int SERVICE_CONNECTION_RESULT_OK = 0;

    public static final int HELLOACCESSORY_CHANNEL_ID = 104;

    HashMap<Integer, HelloAccessoryProviderConnection> mConnectionsMap = null;

    private final IBinder mBinder = new LocalBinder();

    public class LocalBinder extends Binder {
        public HelloAccessoryProviderService getService() {
            return HelloAccessoryProviderService.this;
        }
    }

    public HelloAccessoryProviderService() {
        super(TAG, HelloAccessoryProviderConnection.class);
    }

    public class HelloAccessoryProviderConnection extends SASocket {
        private int mConnectionId;

        public HelloAccessoryProviderConnection() {
            super(HelloAccessoryProviderConnection.class.getName());
        }

        @Override
        public void onError(int channelId, String errorString, int error) {
            Log.e(TAG, "Connection is not alive ERROR: " + errorString + "  "
                    + error);
        }

        @Override
        public void onReceive(int channelId, byte[] data) {
            Log.d(TAG, "onReceive");

            Time time = new Time();

            time.set(System.currentTimeMillis());

            String timeStr = " " + String.valueOf(time.minute) + ":"
                    + String.valueOf(time.second);

            String strToUpdateUI = new String(data);

            final String message = strToUpdateUI.concat(timeStr);

            final HelloAccessoryProviderConnection uHandler = mConnectionsMap.get(Integer
                    .parseInt(String.valueOf(mConnectionId)));
            if(uHandler == null){
                Log.e(TAG,"Error, can not get HelloAccessoryProviderConnection handler");
                return;
            }
            new Thread(new Runnable() {
                public void run() {
                    try {
                        uHandler.send(HELLOACCESSORY_CHANNEL_ID, message.getBytes());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }

        @Override
        protected void onServiceConnectionLost(int errorCode) {
            Log.e(TAG, "onServiceConectionLost  for peer = " + mConnectionId
                    + "error code =" + errorCode);

            if (mConnectionsMap != null) {
                mConnectionsMap.remove(mConnectionId);
            }
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate of smart view Provider Service");

        SA mAccessory = new SA();
        try {
            mAccessory.initialize(this);
        } catch (SsdkUnsupportedException e) {
            // Error Handling
        } catch (Exception e1) {
            Log.e(TAG, "Cannot initialize Accessory package.");
            e1.printStackTrace();
			/*
			 * Your application can not use Accessory package of Samsung
			 * Mobile SDK. You application should work smoothly without using
			 * this SDK, or you may want to notify user and close your app
			 * gracefully (release resources, stop Service threads, close UI
			 * thread, etc.)
			 */
            stopSelf();
        }

    }

    @Override
    protected void onServiceConnectionRequested(SAPeerAgent peerAgent) {
        acceptServiceConnectionRequest(peerAgent);
    }

    @Override
    protected void onFindPeerAgentResponse(SAPeerAgent arg0, int arg1) {
        // TODO Auto-generated method stub
        Log.d(TAG, "onFindPeerAgentResponse  arg1 =" + arg1);
    }

    @Override
    protected void onServiceConnectionResponse(SASocket thisConnection,
                                               int result) {
        if (result == CONNECTION_SUCCESS) {
            if (thisConnection != null) {
                HelloAccessoryProviderConnection myConnection = (HelloAccessoryProviderConnection) thisConnection;

                if (mConnectionsMap == null) {
                    mConnectionsMap = new HashMap<Integer, HelloAccessoryProviderConnection>();
                }

                myConnection.mConnectionId = (int) (System.currentTimeMillis() & 255);

                Log.d(TAG, "onServiceConnection connectionID = "
                        + myConnection.mConnectionId);

                mConnectionsMap.put(myConnection.mConnectionId, myConnection);

                Toast.makeText(getBaseContext(),
                        R.string.ConnectionEstablishedMsg, Toast.LENGTH_LONG)
                        .show();
            } else {
                Log.e(TAG, "SASocket object is null");
            }
        } else if (result == CONNECTION_ALREADY_EXIST) {
            Log.e(TAG, "onServiceConnectionResponse, CONNECTION_ALREADY_EXIST");
        } else {
            Log.e(TAG, "onServiceConnectionResponse result error =" + result);
        }
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return mBinder;
    }
}