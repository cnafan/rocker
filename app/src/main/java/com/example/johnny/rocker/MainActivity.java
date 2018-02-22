package com.example.johnny.rocker;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import static com.example.johnny.rocker.MySurfaceView.ctrlSocketClient;

public class MainActivity extends Activity {
    public Switch ledSwitch;
    public TextView temperatureTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //状态栏黑色字符
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        init();

    }

    void init() {
//        ledSwitch = (Switch) findViewById(R.id.led_switch);
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                ctrlSocketClient.sendmsg("get_led_status",1);
//            }
//        }).start();
//        ledSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                String check_info;
//                if (isChecked) {
//                    check_info = "led_turnon";
//                } else {
//                    check_info = "led_turnoff";
//                }
//                final String finalCheck_info = check_info;
//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        ctrlSocketClient.sendmsg(finalCheck_info, 1);
//                    }
//                }).start();
//            }
//        });
//        temperatureTextView = (TextView) findViewById(R.id.temperature);
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                mhandle.sendEmptyMessage(2);
//                //ctrlSocketClient.sendmsg("get_temperature", 2);
//            }
//        }).start();

    }

    @SuppressLint("HandlerLeak")
    public Handler mhandle = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.arg1) {
                case 0:
                    //Log.d("MainActivity", "send成功.");
                    break;
                case 1:
                    //Log.d("MainActivity", "obj?" + msg.obj);
                    if (msg.obj == null)
                        break;
                    ledSwitch.setChecked(Integer.parseInt(String.valueOf(msg.obj)) == 1);
                    //Log.d("MainActivity", "set成功" + (Integer.parseInt(String.valueOf(msg.obj)) == 1));
                    break;
                case 2:
                    //Log.d("MainActivity", "obj?" + msg.obj);
                    if (msg.obj == null)
                        break;
                    String text = temperatureTextView.getText().toString();
                    text = text + msg.obj + "°C";
                    temperatureTextView.setText(text);
                    break;
            }
        }
    };


}