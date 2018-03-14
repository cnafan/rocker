package com.example.johnny.rocker;

import android.support.design.widget.Snackbar;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

/**
 * Created by johnny on 2018/2/26.
 */

class ScanIP {

    public int ScanIp() {
        ArrayList<String> arrayList = new ScanIP().getConnectedHotIP();
        if (arrayList != null) {
            if (arrayList.size() > 0) {
                //ip = arrayList.get(0);
                //Snackbar.make(getWindow().getDecorView(),"扫描到设备:" + ip,Snackbar.LENGTH_SHORT).show();
                //Toast.makeText(MainActivity.this, "扫描到设备:" + ip, Toast.LENGTH_SHORT).show();
                return 1;
            }
        }
        //Snackbar.make(getWindow().getDecorView(),"未扫描到设备",Snackbar.LENGTH_SHORT).show();
        //Toast.makeText(MainActivity.this, "未扫描到设备", Toast.LENGTH_SHORT).show();
        return 0;
    }
     ArrayList<String> getConnectedHotIP() {
        ArrayList<String> connectedIP = new ArrayList<String>();
        StringBuilder buffers = new StringBuilder();
        try {
            BufferedReader br = new BufferedReader(new FileReader(
                    "/proc/net/arp"));
            String line;
            while ((line = br.readLine()) != null) {
                String[] splitted = line.split(" +");
//                for (String each : splitted
//                        ) {
//                    buffers.append(" ").append(each);
//                }
//                buffers.append("\n");
                if (splitted != null && splitted.length >= 4) {
                    String ip = splitted[0];
                    String mac= splitted[3];
                    if (mac.equals("b8:27:eb:54:7f:18")) {
                        connectedIP.add(ip);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //Log.i("MainActivity",connectedIP.get(0));
        return connectedIP;
    }
}
