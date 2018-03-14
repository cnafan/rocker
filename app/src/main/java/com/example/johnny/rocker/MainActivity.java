package com.example.johnny.rocker;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.ArrayList;

import static com.example.johnny.rocker.MySurfaceView.ctrlSocketClient;

public class MainActivity extends Activity implements CompoundButton.OnCheckedChangeListener {
    public Switch ledSwitch;
    public TextView temperatureTextView;
    static String ip = "";
    public Switch button;
    private static int scanIP = 0;
    private TextView textView_ip;

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

        //扫描树莓派ip
        final ProgressDialog progressDialog = ProgressDialog.show(this, getString(R.string.please_wait), getString(R.string.scaning),false,false);
//        final ProgressDialog alert = new ProgressDialog().Builder(MainActivity.this)
//                .create();
//        alert.setTitle(getString(R.string.please_wait));
//        alert.setMessage(getString(R.string.scaning));
//        alert.setCancelable(false);
//        alert.show();
        init();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                progressDialog.dismiss();
                scanIP = ScanIp();
                textView_ip.setText(ip);
            }
        }, 3000);

    }

   public int ScanIp() {
        ArrayList<String> arrayList = new ScanIP().getConnectedHotIP();
        if (arrayList != null) {
            if (arrayList.size() > 0) {
                ip = arrayList.get(0);
                Snackbar.make(getWindow().getDecorView(),"扫描到设备:" + ip,Snackbar.LENGTH_SHORT).show();
                //Toast.makeText(MainActivity.this, "扫描到设备:" + ip, Toast.LENGTH_SHORT).show();
                return 1;
            }
        }
        Snackbar.make(getWindow().getDecorView(),"未扫描到设备",Snackbar.LENGTH_SHORT).show();
        //Toast.makeText(MainActivity.this, "未扫描到设备", Toast.LENGTH_SHORT).show();
        return 0;
    }

    void init() {
        ledSwitch = (Switch) findViewById(R.id.led_switch);
        ledSwitch.setOnCheckedChangeListener(this);
        button = (Switch) findViewById(R.id.scanip);
        button.setOnCheckedChangeListener(this);
        textView_ip=findViewById(R.id.ip);
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
                    ledSwitch.setChecked(false);
                    //ledSwitch.setChecked(Integer.parseInt(String.valueOf(msg.obj)) == 1);
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
                case 3:
                    //Log.i("MainActivity","isSelected:"+button.isSelected());
                    button.setChecked(false);
                    break;
            }
        }
    };

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

        switch (buttonView.getId()) {
            case R.id.led_switch:
                if (isChecked) {
                    Log.i("MainActivity", "led_on");
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            switch (scanIP) {
                                case 1:
                                    ctrlSocketClient.sendmsg("led_on", 0);
                                    break;
                                default:
                                    break;
                            }
                        }
                    }).start();
                } else {
                    Log.i("MainActivity", "led_off");
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            switch (scanIP) {
                                case 1:
                                    ctrlSocketClient.sendmsg("led_off", 0);
                                    break;
                                default:
                                    break;
                            }
                        }
                    }).start();
                }
                break;
            case R.id.scanip:
                Log.i("MainActivity", "ischecked:" + isChecked);
                if (isChecked) {
                    //扫描树莓派ip
                    final ProgressDialog progressDialog = ProgressDialog.show(this, getString(R.string.please_wait), getString(R.string.scaning),false,false);

//                    final AlertDialog alert = new AlertDialog.Builder(MainActivity.this)
//                            .create();
//                    alert.setTitle(getString(R.string.please_wait));
//                    alert.setMessage(getString(R.string.scaning));
//                    alert.setCancelable(false);
//                    alert.show();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.dismiss();
                            switch (ScanIp()){
                                case 1:
                                    textView_ip.setText(ip);
                                    break;
                                default:
                                    break;
                            }
                            Message message = mhandle.obtainMessage();
                            message.arg1 = 3;
                            mhandle.sendMessage(message);
                        }
                    }, 3000);

                }
                break;
        }
    }

}