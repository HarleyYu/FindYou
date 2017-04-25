package cn.foxnickel.findyou.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.jakewharton.rxbinding2.widget.RxTextView;

import java.util.concurrent.TimeUnit;

import cn.foxnickel.findyou.R;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * A simple {@link Fragment} subclass.
 */
public class ControlFragment extends Fragment implements View.OnClickListener {

    private View mRootView;
    private EditText mControlledNumberText;
    private Button mRecall;
    private Button btCallSmsMonitor;
    private Button btPositionMonitor;
    private Button btPlayMusic;
    private Button btVibration;
    private final String TAG = getClass().getSimpleName();

    public ControlFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_control, container, false);
        initView();
        RxTextView.textChanges(mControlledNumberText).debounce(500, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<CharSequence>() {

                    @Override
                    public void accept(@NonNull CharSequence charSequence) throws Exception {
                        String mobiles = String.valueOf(charSequence);
                        Boolean b = checkPhoneNum(mobiles);
                        if (!b && !mobiles.isEmpty() && !mobiles.equals(""))
                            mControlledNumberText.setError("您输入的手机号有误");
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
        mControlledNumberText = (EditText) mRootView.findViewById(R.id.edit_text_phone);
        /*回拨电话*/
        mRecall = (Button) mRootView.findViewById(R.id.bt_reacll);
        mRecall.setOnClickListener(this);
        /*开启来电短信监控*/
        btCallSmsMonitor = (Button) mRootView.findViewById(R.id.bt_call_sms_monitor);
        btCallSmsMonitor.setOnClickListener(this);
        /*位置监控*/
        btPositionMonitor = (Button) mRootView.findViewById(R.id.bt_position_monitor);
        btPositionMonitor.setOnClickListener(this);
        /*播放音乐*/
        btPlayMusic = (Button) mRootView.findViewById(R.id.bt_play_music);
        btPlayMusic.setOnClickListener(this);
        /*手机振动*/
        btVibration = (Button) mRootView.findViewById(R.id.bt_vibration);
        btVibration.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        String controlledNumber = "";
        if (!TextUtils.equals(mControlledNumberText.getText().toString(), "")) {
            controlledNumber = mControlledNumberText.getText().toString();
        } else {
            Toast.makeText(getContext(), "请输入被控手机号", Toast.LENGTH_SHORT).show();
            return;
        }
        SmsManager smsManager = SmsManager.getDefault();
        switch (v.getId()) {
            case R.id.bt_reacll:
                smsManager.sendTextMessage(controlledNumber, null, "recall", null, null);
                break;
            case R.id.bt_call_sms_monitor:
                Toast.makeText(getContext(), "开启来电短信监控", Toast.LENGTH_SHORT).show();
                break;
            case R.id.bt_position_monitor:
                Toast.makeText(getContext(), "获取到的对方的位置信息将会以短信的形式发送到手机", Toast.LENGTH_SHORT).show();
                smsManager.sendTextMessage(controlledNumber, null, "position", null, null);
                break;
            case R.id.bt_play_music:
                Toast.makeText(getContext(), "成功使对方手机播放铃音", Toast.LENGTH_SHORT).show();
                smsManager.sendTextMessage(controlledNumber, null, "ring", null, null);
                break;
            case R.id.bt_vibration:
                Toast.makeText(getContext(), "成功使对方手机振动", Toast.LENGTH_SHORT).show();
                smsManager.sendTextMessage(controlledNumber, null, "vibration", null, null);
                break;
            default:
                break;
        }
    }

}


