package com.example.johnny.rocker;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * h
 * Created by johnny on 2017/11/8.
 */

class CtrlSocketClient {
    private Socket socket = null;
    private static String IpAddress = "192.168.0.119";
    private static int Port = 20000;
    private String info;
    private Message message;
    private Handler handler;
    private String msg = "";


    CtrlSocketClient(Handler handler) {
        this.handler = handler;

        try {
            socket = new Socket(IpAddress, Port);
            //Log.i("CtrlSocket", "socket:" + socket);
        } catch (IOException e) {
            e.printStackTrace();
            Log.i("CtrlSocket", "exception:" + e);
        }
    }

    CtrlSocketClient(String ip) {

        try {
            socket = new Socket(ip, Port);
            //Log.i("CtrlSocket", "socket:" + socket);
        } catch (IOException e) {
            e.printStackTrace();
            Log.i("CtrlSocket", "exception:" + e);
        }
    }

    CtrlSocketClient() {

        try {
            socket = new Socket(IpAddress, Port);
            //Log.i("CtrlSocket", "socket:" + socket);
        } catch (IOException e) {
            e.printStackTrace();
            Log.i("CtrlSocket", "exception:" + e);
        }
    }

    boolean checksocket() {
        return false;
    }

    void sendmsg(String info, int id) {
        if (socket == null) {
            return;
        }


        if (socket.isConnected()) {
            // 获取 Client 端的输出/输入流
            PrintWriter out = null;
            try {
                //out=new PrintWriter(socket.getOutputStream(),true);
                out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8")), true);
            } catch (IOException e) {
                Log.i("CtrlSocketClient", "outexcept:" + e);
                e.printStackTrace();
            }
            // 填充信息
            assert out != null;
            out.println(info);
            Log.i("CtrlSocketClient", "send :" + info);
            BufferedReader br;
            try {
                br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                msg = br.readLine();
                //Log.i("CtrlSocketClient", "recv :" + msg);


            } catch (IOException e) {
                e.printStackTrace();
            }

            switch (id) {
                case 0:
                    break;
                case 1:
                    //led
                    message = handler.obtainMessage();
                    message.arg1 = 1;
                    message.obj = msg;
                    handler.sendMessage(message);
                    //Log.i("CtrlSocketClient", "handle send");
                    break;
                case 2:
                    //direction
                    message = handler.obtainMessage();
                    message.arg1 = 2;
                    message.obj = msg;
                    handler.sendMessage(message);
                    //Log.i("CtrlSocketClient", "handle2 send");
                    break;
                default:
                    break;
            }

        } else {
            Log.i("CtrlSocketClient", "not socket");
        }
    }

    void destroy() {
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
