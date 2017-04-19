package cn.foxnickel.findyou.fragment;


import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import cn.foxnickel.findyou.R;
import cn.foxnickel.findyou.receiver.SmsReceiver;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 */
public class ControlledFragment extends Fragment implements View.OnClickListener {

    private View mRootView;
    private EditText mControlNumberText;
    private Switch mSwitchSmsMonitor;
    private Switch mSwitchCallMonitor;
    private Switch mSwitchPositionMonitor;
    private Switch mSwitchRecallMonitor;
    private Switch mSwitchVibrationMonitor;
    private Switch mSwitchRingtoneMonitor;
    private Button mBtStartMonitor;
    private Button mBtStopMonitor;
    private final String TAG = getClass().getSimpleName();
    private SharedPreferences preferences;
    private SmsReceiver mSmsReceiver;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_controlled, container, false);
        preferences = getActivity().getSharedPreferences("monitor_item", MODE_PRIVATE);
        initView();
        return mRootView;
    }

    private void initView() {
        mControlNumberText = (EditText) mRootView.findViewById(R.id.edit_text_phone);
        mSwitchSmsMonitor = (Switch) mRootView.findViewById(R.id.switch_sms_monitor);
        mSwitchCallMonitor = (Switch) mRootView.findViewById(R.id.switch_call_monitor);
        mSwitchPositionMonitor = (Switch) mRootView.findViewById(R.id.switch_position_monitor);
        mSwitchRecallMonitor = (Switch) mRootView.findViewById(R.id.switch_recall_monitor);
        mSwitchVibrationMonitor = (Switch) mRootView.findViewById(R.id.switch_vibration_monitor);
        mSwitchRingtoneMonitor = (Switch) mRootView.findViewById(R.id.switch_ringtone_monitor);
        mBtStartMonitor = (Button) mRootView.findViewById(R.id.bt_start_monitor);
        mBtStartMonitor.setOnClickListener(this);
        mBtStopMonitor = (Button) mRootView.findViewById(R.id.bt_stop_monitor);
        mBtStopMonitor.setOnClickListener(this);
        setDefaultMonitorState();
    }

    private void setMonitorItem() {
        SharedPreferences.Editor editor = getActivity().getSharedPreferences("monitor_item",
                MODE_PRIVATE).edit();
        editor.putBoolean("sms", mSwitchSmsMonitor.isChecked());
        editor.putBoolean("call", mSwitchCallMonitor.isChecked());
        editor.putBoolean("position", mSwitchPositionMonitor.isChecked());
        editor.putBoolean("recall", mSwitchRecallMonitor.isChecked());
        editor.putBoolean("ring", mSwitchRingtoneMonitor.isChecked());
        editor.putBoolean("vibration", mSwitchVibrationMonitor.isChecked());
        editor.commit();
    }

    /*private void getData() {
        Log.i(TAG, "getData: sms=" + preferences.getBoolean("sms", false));
        Log.i(TAG, "getData: call=" + preferences.getBoolean("call", false));
        Log.i(TAG, "getData: position=" + preferences.getBoolean("position", false));
        Log.i(TAG, "getData: recall=" + preferences.getBoolean("recall", false));
        Log.i(TAG, "getData: ring=" + preferences.getBoolean("ring", false));
        Log.i(TAG, "getData: vibration=" + preferences.getBoolean("vibration", false));
    }*/

    private void setDefaultMonitorState() {
        mSwitchSmsMonitor.setChecked(preferences.getBoolean("sms", false));
        mSwitchCallMonitor.setChecked(preferences.getBoolean("call", false));
        mSwitchPositionMonitor.setChecked(preferences.getBoolean("position", false));
        mSwitchRecallMonitor.setChecked(preferences.getBoolean("recall", false));
        mSwitchRingtoneMonitor.setChecked(preferences.getBoolean("ring", false));
        mSwitchVibrationMonitor.setChecked(preferences.getBoolean("vibration", false));
    }

    @Override
    public void onClick(View v) {
        String controllNumber = "";
        if (!TextUtils.equals(mControlNumberText.getText().toString(), "")) {
            controllNumber = mControlNumberText.getText().toString();
        } else {
            Toast.makeText(getContext(), "请输入主控手机号", Toast.LENGTH_SHORT).show();
            return;
        }
        switch (v.getId()) {
            case R.id.bt_start_monitor:
                Toast.makeText(getContext(), "开始监控", Toast.LENGTH_SHORT).show();
                setMonitorItem();
                IntentFilter receiveFilter = new IntentFilter();
                receiveFilter.addAction("android.provider.Telephony.SMS_RECEIVED");
                mSmsReceiver = new SmsReceiver(controllNumber);
                getActivity().registerReceiver(mSmsReceiver, receiveFilter);
                break;
            case R.id.bt_stop_monitor:
                Toast.makeText(getContext(), "停止监控", Toast.LENGTH_SHORT).show();
                getActivity().unregisterReceiver(mSmsReceiver);
                break;
            default:
                break;
        }
    }
}
