package cn.foxnickel.findyou.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.util.Date;

public class CallMonitorService extends Service {
    private final String TAG = getClass().getSimpleName();
    private String controlNumber;

    public CallMonitorService() {
    }

    @Override
    public IBinder onBind(final Intent intent) {
        // TODO: Return the communication channel to the service.
        Log.i(TAG, "onBind: start");
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate: start");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "onStartCommand: start");
        controlNumber = intent.getStringExtra("controlNumber");
        Log.i(TAG, "onBind: controlNumber " + controlNumber);
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        PhoneStateListener phoneStateListener = new PhoneStateListener() {
            @Override
            public void onCallStateChanged(int state, String incomingNumber) {
                super.onCallStateChanged(state, incomingNumber);
                switch (state) {
                    case TelephonyManager.CALL_STATE_IDLE:
                        break;
                    case TelephonyManager.CALL_STATE_OFFHOOK:
                        break;
                    case TelephonyManager.CALL_STATE_RINGING:
                        Log.i(TAG, "onCallStateChanged: number: " + incomingNumber);
                        SmsManager smsManager = SmsManager.getDefault();
                        smsManager.sendTextMessage(controlNumber, null, new Date() + " Receive a call from " + incomingNumber, null, null);
                        break;
                    default:
                        break;
                }
            }
        };
        telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
        return super.onStartCommand(intent, flags, startId);
    }
}
