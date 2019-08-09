package com.demo.gesturelock;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.library.gesturelock.GestureLockViewGroup;
import com.library.gesturelock.GesturePreference;
import com.library.gesturelock.listener.GestureEventListener;
import com.library.gesturelock.listener.GesturePasswordSettingListener;
import com.library.gesturelock.listener.GestureUnmatchedExceedListener;

public class MainActivity extends AppCompatActivity {

    private GestureLockViewGroup mGestureLockViewGroup;
    private boolean isReset = false;

    RelativeLayout rl_reset;
    TextView tv_state;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rl_reset = findViewById(R.id.rl_reset);
        tv_state = findViewById(R.id.tv_state);
        rl_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isReset = true;
                tv_state.setTextColor(Color.WHITE);
                tv_state.setText("请输入原手势密码");
                mGestureLockViewGroup.resetView();
            }
        });

        initGesture();
    }

    private void initGesture() {
        mGestureLockViewGroup = (GestureLockViewGroup) findViewById(R.id.gesturelock);
        mGestureLockViewGroup.setOnDecryptionListener(new GesturePreference.OnDecryptionListener() {
            @Override
            public String encrypt(String noCipher) {
                return null;
            }

            @Override
            public String decrypt(String cipher) {
                return null;
            }
        });
        mGestureLockViewGroup.startWork();
        gestureEventListener();
        gesturePasswordSettingListener();
        gestureRetryLimitListener();
    }

    private void gestureEventListener() {
        mGestureLockViewGroup.setGestureEventListener(new GestureEventListener() {
            @Override
            public void onGestureEvent(boolean matched) {
                if (!matched) {
                    tv_state.setTextColor(Color.RED);
                    tv_state.setText("手势密码错误,请重新输入");
                } else {
                    if (isReset) {
                        isReset = false;
                        Toast.makeText(MainActivity.this, "清除成功!", Toast.LENGTH_SHORT).show();
                        resetGesturePattern();
                    } else {
                        tv_state.setTextColor(Color.WHITE);
                        tv_state.setText("手势密码正确");
                    }
                }
            }
        });
    }

    private void gesturePasswordSettingListener() {
        mGestureLockViewGroup.setGesturePasswordSettingListener(new GesturePasswordSettingListener() {
            @Override
            public boolean onFirstInputComplete(int len) {
                if (len > 3) {
                    tv_state.setTextColor(Color.WHITE);
                    tv_state.setText("再次绘制手势密码");
                    return true;
                } else {
                    tv_state.setTextColor(Color.RED);
                    tv_state.setText("最少连接4个点，请重新输入!");
                    return false;
                }
            }

            @Override
            public void onSuccess() {
                tv_state.setTextColor(Color.WHITE);
                Toast.makeText(MainActivity.this, "密码设置成功!", Toast.LENGTH_SHORT).show();
                tv_state.setText("请输入手势密码解锁!");
            }

            @Override
            public void onFail() {
                tv_state.setTextColor(Color.RED);
                tv_state.setText("与上一次绘制不一致，请重新绘制");
            }
        });
    }

    private void gestureRetryLimitListener() {
        mGestureLockViewGroup.setGestureUnmatchedExceedListener(3, new GestureUnmatchedExceedListener() {
            @Override
            public void onUnmatchedExceedBoundary() {
                tv_state.setTextColor(Color.RED);
                tv_state.setText("错误次数过多，请稍后再试!");
                startShakeByViewAnim(tv_state, 10f, 1000);
            }
        });
    }

    private void setGestureWhenNoSet() {
        if (!mGestureLockViewGroup.isSetPassword()){
            tv_state.setTextColor(Color.WHITE);
            tv_state.setText("绘制手势密码");
        }
    }

    private void resetGesturePattern() {
        mGestureLockViewGroup.removePassword();
        setGestureWhenNoSet();
        mGestureLockViewGroup.resetView();
    }

    /**
     * 抖动动画
     * @param view
     * @param shakeDegrees
     * @param duration
     */
    private void startShakeByViewAnim(View view, float shakeDegrees, long duration) {
        if (view == null) {
            return;
        }
        //TODO 验证参数的有效性

        //从左向右
        Animation rotateAnim = new RotateAnimation(-shakeDegrees, shakeDegrees, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);

        rotateAnim.setDuration(duration / 10);
        rotateAnim.setRepeatMode(Animation.REVERSE);
        rotateAnim.setRepeatCount(10);

        AnimationSet smallAnimationSet = new AnimationSet(false);
        smallAnimationSet.addAnimation(rotateAnim);

        view.startAnimation(smallAnimationSet);
    }
}
