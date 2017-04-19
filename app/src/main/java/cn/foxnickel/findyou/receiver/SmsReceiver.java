package cn.foxnickel.findyou.receiver;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.ActivityCompat;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.text.TextUtils;
import android.util.Log;

import java.io.IOException;

public class SmsReceiver extends BroadcastReceiver {

    private String address;//发件地址
    private StringBuilder content;//内容
    private final String TAG = getClass().getSimpleName();
    private String controlNumber;
    SharedPreferences mPreferences;

    public SmsReceiver(String controlNumber) {
        this.controlNumber = controlNumber;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        mPreferences = context.getSharedPreferences("monitor_item", Context.MODE_PRIVATE);
        /*获取收到的短信*/
        Bundle bundle = intent.getExtras();
        Object[] pdus = (Object[]) bundle.get("pdus");
        SmsMessage[] messages = new SmsMessage[pdus.length];
        for (int i = 0; i < messages.length; i++) {
            messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
        }
        address = messages[0].getOriginatingAddress();
        content = new StringBuilder();
        for (SmsMessage message : messages) {
            content.append(message.getMessageBody());
        }

        if (!TextUtils.isEmpty(address) && !TextUtils.equals(content.toString(), "")) {
            Log.i(TAG, "onReceive: address=" + address + " content= " + content);
            Log.i(TAG, "onReceive: controlNumber: " + controlNumber);
            Log.i(TAG, "onReceive: recall: " + mPreferences.getBoolean("recall", false));
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            if (TextUtils.equals(address, controlNumber) && TextUtils.equals(content.toString(), "recall") && mPreferences.getBoolean("recall", false)) {
                context.startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + controlNumber)));
            } else if (TextUtils.equals(address, controlNumber) && TextUtils.equals(content.toString(), "ring") && mPreferences.getBoolean("ring", false)) {
                Uri uri = Uri.parse("http://www.foxnickel.cn/123.mp3");
                MediaPlayer mediaPlayer = new MediaPlayer();
                try {
                    mediaPlayer.setDataSource(context, uri);
                    mediaPlayer.prepare();
                    mediaPlayer.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (TextUtils.equals(address, controlNumber) && TextUtils.equals(content.toString(), "vibration") && mPreferences.getBoolean("vibration", false)) {
                Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
                vibrator.vibrate(1000);
            }
            if (mPreferences.getBoolean("sms", false)) {
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(controlNumber, null, "He/She received a message,content is:\n" + content, null, null);
            }
        } else {
            Log.i(TAG, "onReceive: 短信数据为空");
        }

    }
}
