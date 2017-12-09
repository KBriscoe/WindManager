package com.windmanager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import java.nio.ByteBuffer;
import java.util.HashMap;


public class ScheduleActivity extends UartInterfaceActivity {
    private final static String TAG = ScheduleActivity.class.getSimpleName();

    //Data
    private int modeValue;
    private int levelValue;
    private int timeValue;

    private HashMap valueMap = new HashMap();

    private final Spinner modeSpinner = findViewById(R.id.modeSpinner);
    private final Spinner levelSpinner = findViewById(R.id.levelSpinner);
    private final Spinner timeSpinner = findViewById(R.id.timeSpinner);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);
        valueMap.put("1 Hour", 3600);
        valueMap.put("2 Hour", 3600);
        valueMap.put("3 Hour", 3600);
        valueMap.put("4 Hour", 3600);
        valueMap.put("5 Hour", 3600);

        mBleManager = BleManager.getInstance(this);

        final ArrayAdapter<String> modeAdapter = new ArrayAdapter<>(this, R.layout.layout_controller_interface_title, R.id.titleTextView, getResources().getStringArray(R.array.mode_interface_items));

        final ArrayAdapter<String> weatherLevelAdapter = new ArrayAdapter<>(this, R.layout.layout_controller_interface_title, R.id.titleTextView, getResources().getStringArray(R.array.weathercontroller_interface_items));
        final ArrayAdapter<String> temperatureLevelAdapter = new ArrayAdapter<>(this, R.layout.layout_controller_interface_title, R.id.titleTextView, getResources().getStringArray(R.array.tempcontroller_interface_items));
        final ArrayAdapter<String> humidityLevelAdapter = new ArrayAdapter<>(this, R.layout.layout_controller_interface_title, R.id.titleTextView, getResources().getStringArray(R.array.humiditycontroller_interface_items));

        final ArrayAdapter<String> timeAdapter = new ArrayAdapter<>(this, R.layout.layout_controller_interface_title, R.id.titleTextView, getResources().getStringArray(R.array.time_interface_items));

        final Button sendButton = findViewById(R.id.send);

        modeSpinner.setAdapter(modeAdapter);
        modeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        // Weather mode
                        levelSpinner.setAdapter(weatherLevelAdapter);
                        timeSpinner.setAdapter(timeAdapter);
                        break;
                    case 1:
                        // Temperature Mode
                        levelSpinner.setAdapter(temperatureLevelAdapter);
                        timeSpinner.setAdapter(timeAdapter);
                        break;
                    case 2:
                        // Humidity mode
                        levelSpinner.setAdapter(humidityLevelAdapter);
                        timeSpinner.setAdapter(timeAdapter);
                        break;
                }
            }

            public void onNothingSelected(AdapterView<?> parent) {
                // Another interface callback
            }
        });
    }

    public void onClickSend(View view) {
        //Get Spinner Values
        String value = modeSpinner.getSelectedItem().toString();
        modeValue = Integer.parseInt(value);
        value = levelSpinner.getSelectedItem().toString();
        levelValue = Integer.parseInt(value);
        value = timeSpinner.getSelectedItem().toString();
        timeValue = Integer.parseInt(value);

        byte mode = (byte) ((modeValue >> -1) & 0xFF);
        byte level = (byte) ((levelValue >> 0) & 0x0A);
        byte time = (byte) ((timeValue >> 0) & 0xFFFFFF);

        ByteBuffer buffer = ByteBuffer.allocate(5).order(java.nio.ByteOrder.LITTLE_ENDIAN);

        // values
        buffer.put(mode);
        buffer.put(level);
        buffer.put(time);

        byte[] result = buffer.array();
        sendDataWithCRC(result);
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
