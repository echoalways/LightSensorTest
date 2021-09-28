package com.example.lightsensortest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements SensorEventListener, View.OnClickListener {
    private SensorManager sensorManager;
    private Sensor mLightSensor;
    public static final String TAG = "MainActivity";
    private float mStartTime;
    private boolean mEnabled;
    private MyHandler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mLightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        mHandler = new MyHandler();
        Button button = findViewById(R.id.button1);
        button.setOnClickListener(this);
        registerBroadcast();
    }

    private void registerBroadcast() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        registerReceiver(mMyReceiver, filter);
    }

    private BroadcastReceiver mMyReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "received screen off broadcast....: ");
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG, "postDelayed ");
                    registerSensor();
                }
            }, 10000);
        }
    };

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        long now = SystemClock.uptimeMillis();
        Log.d(TAG, "received light sensor data,interval:" + (now - mStartTime) + "ms");
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button1:
                registerSensor();
                break;
            default:
        }
    }

    static class MyHandler extends Handler {
        @Override
        public void handleMessage(@NonNull Message msg) {

        }
    }

    private void registerSensor() {
        if (!mEnabled) {
            mEnabled = true;
            mStartTime = SystemClock.uptimeMillis();
            Log.d(TAG, "register light sensor, time:" + mStartTime);
            sensorManager.registerListener(this, mLightSensor, SensorManager.SENSOR_DELAY_NORMAL);
            Toast.makeText(MainActivity.this, "开始注册light sensor...", Toast.LENGTH_SHORT).show();
        } else {
            mEnabled = false;
            sensorManager.unregisterListener(this);
            Toast.makeText(MainActivity.this, "解除注册light sensor...", Toast.LENGTH_SHORT).show();
        }
    }
}