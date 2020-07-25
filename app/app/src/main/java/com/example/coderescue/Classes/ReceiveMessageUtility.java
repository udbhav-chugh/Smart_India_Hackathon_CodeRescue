package com.example.coderescue.Classes;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class ReceiveMessageUtility extends BroadcastReceiver {
    public static final int REQUEST_CODE_RECEIVE_MESSAGE_PERMISSION = 2000;

    public static void checkPermissions(Context context, Activity activity){
        int permissionCheck = ContextCompat.checkSelfPermission(context, Manifest.permission.RECEIVE_SMS);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                Log.d("message util", "Receive message permission granted");
            } else {
                Log.i("message util", "Requesting SMS receive permissions");
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.RECEIVE_SMS}, REQUEST_CODE_RECEIVE_MESSAGE_PERMISSION);
            }
        }
        else{
            Toast.makeText(context, "Build version less than " + Integer.toString(Build.VERSION_CODES.M), Toast.LENGTH_SHORT).show();
            Log.d("message util", "Build version less than " + Integer.toString(Build.VERSION_CODES.M));
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent != null && intent.getAction() != null && intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")){
            Bundle bundle = intent.getExtras();
            SmsMessage[] msgs;
            String msg_from;
            if(bundle != null){
                try{
                    Object[] pdus = (Object[]) bundle.get("pdus");
                    msgs = new SmsMessage[pdus.length];
                    for (int i = 0; i < msgs.length ; i++) {
                        msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                        msg_from = msgs[i].getOriginatingAddress();
                        String msgBody = msgs[i].getMessageBody();

                        Toast.makeText(context, "From: " + msg_from + ", Body: " + msgBody, Toast.LENGTH_SHORT).show();
                    }
                }catch (Exception e){
                    Log.e("message util", "Error in receiving messages: " + e.getMessage());
                }
            }
            else{
                Log.d("message util", "bundle in onReceive intent extras empty");
            }
        }
        else{
            Log.d("message util", "onReceive intent incorrect");
        }
    }
}
