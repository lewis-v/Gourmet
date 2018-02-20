package com.yw.gourmet.utils;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

/**
 * auth: lewis-v
 * time: 2018/2/19.
 */

public class ShakeUtils implements SensorEventListener {
    private static ShakeUtils instance;
    private SensorManager mSensorManager = null;
    private OnShakeListener mOnShakeListener = null;
    private static final int SENSOR_VALUE = 19;

    public static ShakeUtils getInstance(Context context){
        if (instance == null){
            instance = new ShakeUtils(context);
        }
        return instance;
    }

    public ShakeUtils(Context context){
        mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
    }

    public void setOnShakeListener( OnShakeListener onShakeListener ){
        mOnShakeListener = onShakeListener;
    }

    public void onResume(){
        mSensorManager.registerListener(this,
                mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void onPause(){
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        int sensorType = event.sensor.getType();
        //values[0]:X轴，values[1]：Y轴，values[2]：Z轴
        float[] values = event.values;
        if (sensorType == Sensor.TYPE_ACCELEROMETER){
            //这里可以调节摇一摇的灵敏度
            if ((Math.abs(values[0]) > SENSOR_VALUE || Math.abs(values[1]) > SENSOR_VALUE )){
                System.out.println("sensor value == " + " " + values[ 0 ] + " " + values[ 1 ] + " " +  values[ 2 ] );
                if( null != mOnShakeListener ){
                    mOnShakeListener.onShake( );
                }
            }
        }
    }

    public interface OnShakeListener{
        void onShake();
    }

}
