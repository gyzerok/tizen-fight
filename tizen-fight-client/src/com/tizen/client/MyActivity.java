package com.tizen.client;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MyActivity extends Activity {

    private final String TAG = "tizen-fight";

    private EditText mIpEdit;
    private EditText mNicknameEdit;
    private Button mLoginBtn;

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
                // TODO: Ask server for connect
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
