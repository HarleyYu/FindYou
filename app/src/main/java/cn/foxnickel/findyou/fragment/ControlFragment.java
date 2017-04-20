package cn.foxnickel.findyou.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.jakewharton.rxbinding2.widget.RxTextView;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.foxnickel.findyou.R;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * A simple {@link Fragment} subclass.
 */
public class ControlFragment extends Fragment {

   @BindView(R.id.edit_text_phone)
    EditText mControlPhone;
    public ControlFragment() {
        // Required empty public constructor

    }

    private Boolean checkPhoneNum(String s) {
        String telRegex = "[1][358]\\d{9}";//"[1]"代表第1位为数字1，"[358]"代表第二位可以为3、5、8中的一个，"\\d{9}"代表后面是可以是0～9的数字，有9位。
        if (TextUtils.isEmpty(s)) {
            return false;
        } else {
            if (!s.matches(telRegex)) {
                return false;
            } else return true;
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_control, container, false);
        ButterKnife.bind(this,view);
        RxTextView.textChanges(mControlPhone).debounce(500, TimeUnit.MILLISECONDS)
                .map(new Function<CharSequence, Boolean>() {
                    @Override
                    public Boolean apply(@NonNull CharSequence charSequence) throws Exception {
                        return checkPhoneNum(String.valueOf(charSequence));
                    }
                }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(@NonNull Boolean aBoolean) throws Exception {
                        if(!aBoolean)
                            mControlPhone.setError("您输入的密码有误");
                    }
                });
               return view;
    }

}
