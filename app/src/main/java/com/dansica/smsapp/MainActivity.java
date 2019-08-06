package com.dansica.smsapp;

import android.Manifest;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    Button sendBtn, viaMsgBtn;
    EditText toEt, contentEt;
    SmsManager smsManager;
    MessageReceiver messageReceiver = new MessageReceiver();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sendBtn = findViewById(R.id.sendBtn);
        viaMsgBtn = findViewById(R.id.viaMsgBtn);

        toEt = findViewById(R.id.toEt);
        contentEt = findViewById(R.id.contentEt);

        checkPermission();

        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        intentFilter.addAction("android.provider.Telephony.SMS_RECEIVED");
        this.registerReceiver(messageReceiver, intentFilter);

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String to = toEt.getText().toString();
                String content = contentEt.getText().toString();

                String[] split = toEt.getText().toString().split(",");

                for (String i : split) {
                    smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(i, null, content, null, null);

                    Toast.makeText(MainActivity.this, "Message Sent", Toast.LENGTH_LONG).show();
                }
            }
        });

        viaMsgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String to = toEt.getText().toString();
                String content = contentEt.getText().toString();

                Uri sms = Uri.parse("smsto:" + to);
                Intent intent = new Intent(Intent.ACTION_SENDTO, sms);
                intent.putExtra("sms_body", content);
                startActivity(intent);

            }
        });

    }

    public void checkPermission() {
        int permissionSendSMS = ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS);
        int permissionReceiveSMS = ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS);

        if (permissionSendSMS != PackageManager.PERMISSION_GRANTED && permissionReceiveSMS != PackageManager.PERMISSION_GRANTED) {
            String[] permissionNeeded = new String[]{
                    Manifest.permission.SEND_SMS, Manifest.permission.RECEIVE_SMS};

            ActivityCompat.requestPermissions(this, permissionNeeded, 1);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.unregisterReceiver(messageReceiver);
    }
}
