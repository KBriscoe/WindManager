package com.windmanager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import java.nio.ByteBuffer;

public class ControlModeActivity extends UartInterfaceActivity{
    // Log
    private final static String TAG = ControlModeActivity.class.getSimpleName();

    // Constants
    private final static String kPreferences = "ControllerActivity_prefs";
    private final static String kPreferences_uartToolTip = "uarttooltip";

    //UI
    private ViewGroup mUartTooltipViewGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modecontroller);

        mBleManager = BleManager.getInstance(this);

        // Start services
        onServicesDiscovered();
    }

    public void onClickWeather(View view) {
        ByteBuffer buffer = ByteBuffer.allocate(3).order(java.nio.ByteOrder.LITTLE_ENDIAN);

        // prefix
        String prefix = "!M";
        byte mode = 0;
        buffer.put(prefix.getBytes());
        buffer.put(mode);


        byte[] result = buffer.array();
        sendDataWithCRC(result);
    }

    public void onClickHumidity(View view) {
        ByteBuffer buffer = ByteBuffer.allocate(3).order(java.nio.ByteOrder.LITTLE_ENDIAN);

        // prefix
        String prefix = "!M";
        byte mode = 1;
        buffer.put(prefix.getBytes());
        buffer.put(mode);


        byte[] result = buffer.array();
        sendDataWithCRC(result);
    }

    public void onClickTemperature(View view) {
        ByteBuffer buffer = ByteBuffer.allocate(3).order(java.nio.ByteOrder.LITTLE_ENDIAN);

        // prefix
        String prefix = "!M";
        byte mode = 2;
        buffer.put(prefix.getBytes());
        buffer.put(mode);


        byte[] result = buffer.array();
        sendDataWithCRC(result);
    }

    public void onClickStandby(View view) {
        ByteBuffer buffer = ByteBuffer.allocate(3).order(java.nio.ByteOrder.LITTLE_ENDIAN);

        // prefix
        String prefix = "!M";
        byte mode = 3;
        buffer.put(prefix.getBytes());
        buffer.put(mode);


        byte[] result = buffer.array();
        sendDataWithCRC(result);
    }

    public void onClickCloseTooltip(View view) {
        SharedPreferences settings = getSharedPreferences(kPreferences, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(kPreferences_uartToolTip, false);
        editor.apply();

        mUartTooltipViewGroup.setVisibility(View.GONE);

    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");
    }

    @Override
    protected void onStop() {
        Log.d(TAG, "onStop");
        super.onStop();
    }

    @Override
    public void onResume() {
        Log.d(TAG, "onResume");
        super.onResume();
        // Setup listeners
        mBleManager.setBleListener(this);
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "onPause");
        super.onPause();
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 0) {
            if (resultCode < 0) {       // Unexpected disconnect
                setResult(resultCode);
                finish();
            }
        }
    }

    @Override
    public void onDisconnected() {
        super.onDisconnected();
        Log.d(TAG, "Disconnected. Back to previous activity");
        setResult(-1);      // Unexpected Disconnect
        finish();
    }

}

