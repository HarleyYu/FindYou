package cn.foxnickel.findyou.fragment;


import android.content.Intent;
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

import com.jakewharton.rxbinding2.widget.RxTextView;

import java.util.concurrent.TimeUnit;

import cn.foxnickel.findyou.R;
import cn.foxnickel.findyou.Receiver.SmsReceiver.SmsReceiver;
import cn.foxnickel.findyou.service.CallMonitorService;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

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
        RxTextView.textChanges(mControlNumberText).debounce(500, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<CharSequence>() {

                    @Override
                    public void accept(@NonNull CharSequence charSequence) throws Exception {
                        String mobiles = String.valueOf(charSequence);
                        Boolean b = checkPhoneNum(mobiles);
                        if (!b && !mobiles.isEmpty() && !mobiles.equals(""))
                            mControlNumberText.setError("您输入的手机号有误");
                    }
                });
        return mRootView;
    }

    private Boolean checkPhoneNum(String mobiles) {
        String telRegex = "[1][358]\\d{9}";//"[1]"代表第1位为数字1，"[358]"代表第二位可以为3、5、8中的一个，"\\d{9}"代表后面是可以是0～9的数字，有9位。
        if (TextUtils.isEmpty(mobiles)) {
            return false;
        } else {
            if (!mobiles.matches(telRegex)) {
                return false;
            } else return true;
        }
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
        String controlNumber = "";
        if (!TextUtils.equals(mControlNumberText.getText().toString(), "")) {
            controlNumber = mControlNumberText.getText().toString();
        } else {
            Toast.makeText(getContext(), "请输入主控手机号", Toast.LENGTH_SHORT).show();
            return;
        }
        switch (v.getId()) {
            case R.id.bt_start_monitor:
                Toast.makeText(getContext(), "开始监控", Toast.LENGTH_SHORT).show();
                setMonitorItem();
                /*短信监控*/
                IntentFilter receiveFilter = new IntentFilter();
                receiveFilter.addAction("android.provider.Telephony.SMS_RECEIVED");
                mSmsReceiver = new SmsReceiver(controlNumber);
                getActivity().registerReceiver(mSmsReceiver, receiveFilter);
                /*来电监控*/
                if (preferences.getBoolean("call", false)) {
                    Intent intent = new Intent(getActivity(), CallMonitorService.class);
                    intent.putExtra("controlNumber", controlNumber);
                    getActivity().startService(intent);
                }
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
