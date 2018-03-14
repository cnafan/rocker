package com.example.johnny.rocker;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;


/**
 * Created by johnny on 2018/1/20.
 */

public class MySurfaceView extends SurfaceView implements SurfaceHolder.Callback, Runnable {

    //是否扫描到ip
    static int scanIP = 0;
    Paint paint;
    Canvas canvas;
    // 创建一个线程启动绘图
    Thread mThread;
    // SurfaceView管理者
    SurfaceHolder holder;

    // flag用于判断重绘是否继续进行
    boolean flag;
    int logicType;
    float trayRadius;
    double angle, radian;

    float rockCenterX, rockCenterY, rockRadius;
    float baseCenterX, baseCenterY, baseRadius;

    final byte LOGIC_STOP = 0;
    final byte LOGIC_FORWARD = 1;
    final byte LOGIC_BACKWARD = 2;
    final byte LOGIC_LEFT = 3;
    final byte LOGIC_RIGHT = 4;
    String str_direction;
    static CtrlSocketClient ctrlSocketClient;
    private HandlerThread myHandlerThread;
    private Handler handlerss;

    private int lastDirection = 0;

    int ScanIp() {
        ArrayList<String> arrayList = new ScanIP().getConnectedHotIP();
        if (arrayList != null) {
            if (arrayList.size() > 0) {
                MainActivity.ip = arrayList.get(0);
                //Snackbar.make(this,"扫描到设备:" + MainActivity.ip,Snackbar.LENGTH_SHORT).show();
                //Toast.makeText(getContext(), "扫描到设备:" + MainActivity.ip, Toast.LENGTH_SHORT).show();
                return 1;
            }
        }
        //Snackbar.make(getRootView(),"扫描到设备:",Snackbar.LENGTH_SHORT).show();
        //Toast.makeText(getContext(), "未扫描到设备", Toast.LENGTH_SHORT).show();
        return 0;
    }

    // 当在代码中创建MySurfaceView对象的时候，会使用该构造器的方法
    public MySurfaceView(Context context) {
        super(context);
        // 初始化
        init();
    }// 当在代码中创建MySurfaceView对象的时候，会使用该构造器的方法


    // 在xml布局中，如果使用该对象布局，则使用该构造器方法
    public MySurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    // 初始化方法
    @SuppressLint("HandlerLeak")
    public void init() {
        scanIP=ScanIp();
        switch (scanIP) {
            case 1:
                //socket init
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Log.i("MySurfaceView", "ip:" + MainActivity.ip);
                        ctrlSocketClient = new CtrlSocketClient(MainActivity.ip);
                    }
                });
                thread.start();
                try {
                    thread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                break;
            default:
                break;
        }


        holder = getHolder();
        // 添加当前SurfaceView的状态回调
        holder.addCallback(this);
        this.setZOrderOnTop(true);
        // 允许背景透明
        holder.setFormat(PixelFormat.TRANSLUCENT);
        // 将图层置顶
        paint = new Paint();
        paint.setAntiAlias(true);
        //340/740/540/1140
        rockCenterX = baseCenterX = 540;
        rockCenterY = baseCenterY = 400;
        rockRadius = 90;
        trayRadius = 90;
        baseRadius = 250;
        //创建一个线程,线程名字：handler-thread
        myHandlerThread = new HandlerThread("handler-thread");
        //开启一个线程
        myHandlerThread.start();
        //在这个线程中创建一个handler对象
        handlerss = new Handler(myHandlerThread.getLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                //这个方法是运行在 handler-thread 线程中的 ，可以执行耗时操作
                //Log.i("handler ", "消息： " + msg.what + "  线程： " + Thread.currentThread().getName());
                switch (msg.arg1) {
                    case 3:
                        //direction
                        //Log.i("handler ", "case:" + (String) msg.obj);
                        switch (scanIP){
                            case 1:
                                ctrlSocketClient.sendmsg((String) msg.obj, 0);
                                break;
                        default:
                            break;
                        }
                        break;
                }
            }
        };
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //1080/1480
        setMeasuredDimension(1080, 1480);
    }

    // 绘图
    public void myDraw() {
        // 从管理者holder中获取画布对象
        canvas = holder.lockCanvas();
        // 每画一次就覆盖上一次绘制的图像
        if (null != canvas) {
            canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
            paint.setColor(Color.GRAY);
            paint.setAlpha(50);
            canvas.drawCircle(baseCenterX, baseCenterY, baseRadius, paint);
//            paint.setColor(Color.GRAY);
//            paint.setAlpha(10);
//            canvas.drawCircle(baseCenterX, baseCenterY, trayRadius, paint);
//		      RectF oval = new RectF(340, 340, 740, 740);
//		      canvas.drawArc(oval, 0, 30, true, paint);
            paint.setColor(Color.BLACK);
            canvas.drawCircle(rockCenterX, rockCenterY, rockRadius, paint);
            // 向管理者holder提交绘制好的对象
            holder.unlockCanvasAndPost(canvas);
        } else {

            Log.i("MySurface", "MySurface canvas null");
        }
    }

    // 时间监听
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // 对事件进行分别监听
        if (event.getAction() == MotionEvent.ACTION_UP) {
            rockCenterX = baseCenterX;
            rockCenterY = baseCenterY;
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            logicType = LOGIC_STOP;
        } else {
            float distanceX = event.getX() - baseCenterX;
            float distanceY = event.getY() - baseCenterY;
            double distanceCenter = Math.sqrt(distanceX * distanceX + distanceY * distanceY);
            if (distanceCenter <= baseRadius) {
                rockCenterX = event.getX();
                rockCenterY = event.getY();
            } else {
                rockCenterX = (float) (baseCenterX + baseRadius * distanceX / distanceCenter);
                rockCenterY = (float) (baseCenterY + baseRadius * distanceY / distanceCenter);
            }
            radian = Math.acos(distanceX / distanceCenter);
            //Log.i("johnnyme","radian:"+radian);
            if (event.getY() > baseCenterY) {
                angle = Math.toDegrees(-radian);
                logicType = setLogicType(angle);
            } else {
                angle = Math.toDegrees(radian);
                logicType = setLogicType(angle);
            }
        }
        //重复检测
        //上1后2左3右4 stop0
        if (logicType != lastDirection) {
            DirectionMethod(logicType);
            lastDirection = logicType;
        }
        return true;
    }

    int setLogicType(double angle) {
        int type;
        if (angle > 45 && angle <= 135) {
            type = LOGIC_FORWARD;
        } else if ((angle > 135 && angle <= 180) || (angle > -180 && angle <= -135)) {
            type = LOGIC_LEFT;
        } else if ((angle > -45 && angle <= 0) || (angle > 0 && angle <= 45)) {
            type = LOGIC_RIGHT;
        } else {
            type = LOGIC_BACKWARD;
        }
        return type;
    }

    // 方向控制
    public void DirectionMethod(final int direction) {
        switch (direction) {
            case 0:
                str_direction = "t_stop";
                break;
            case 1:
                str_direction = "t_up";
                break;
            case 2:
                str_direction = "t_down";
                break;
            case 3:
                str_direction = "t_left";
                break;
            case 4:
                str_direction = "t_right";
                break;
            default:
                str_direction = "t_stop";
                break;
        }
        Message message = handlerss.obtainMessage();
        message.arg1 = 3;
        message.obj = str_direction;
        handlerss.sendMessage(message);
//        try {
//            ctl.join();
//        } catch (InterruptedException e) {
//            Log.i("MySurface", "InterruptedException1:" + e);
//            e.printStackTrace();
//        }
    }

    // 实现Runnable接口后重写的run方法，此时整个类可以被作为一个线程
    // Thread类本身需要实现Runnable接口
    // 线程：执行耗时操作时间较长，重复次数特别多
    @Override
    public void run() {
        while (flag) {
            myDraw();
            // 每个10ms绘制一次图像
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                Log.i("MySurface", "InterruptedException:" + e);
                e.printStackTrace();
            }
        }
    }

    // 当SurfaceView被创建时，会使用该方法
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        flag = true;
        // 通过向Thread中传入Runnable创建对象
        mThread = new Thread(this);
        // 启动线程
        mThread.start();

        Log.i("MySurface", "thread start");
    }

    // 当SurfacView页面的状态发生改变时，会使用该方法（大小，形状……）
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

        Log.i("MySurface", "thread change");
    }

    // 当SurfaceView页面被销毁（返回），会使用该方法
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        flag = false;
        //mThread.stop();
        Log.i("MySurface", "thread interrupt");
        ctrlSocketClient.destroy();

    }

}
