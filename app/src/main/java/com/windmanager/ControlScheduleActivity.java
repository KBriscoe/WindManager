package com.windmanager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.nio.ByteBuffer;
import java.util.HashMap;


public class ControlScheduleActivity extends UartInterfaceActivity{
    private final static String TAG = ControlScheduleActivity.class.getSimpleName();

    // Constants
    private final static String kPreferences = "ControllerActivity_prefs";
    private final static String kPreferences_uartToolTip = "uarttooltip";

    private ViewGroup mUartTooltipViewGroup;

    //Data
    private int modeValue;
    private int levelValue;
    private int timeValue;
    private HashMap valueMap = new HashMap();

    Spinner modeSpinner;
    Spinner levelSpinner;
    Spinner timeSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);
        mBleManager = BleManager.getInstance(this);

        modeSpinner = findViewById(R.id.modeSpinner);
        levelSpinner = findViewById(R.id.levelSpinner);
        timeSpinner = findViewById(R.id.timeSpinner);
        //Mode Values
        valueMap.put("Weather", 0);
        valueMap.put("Humidity", 1);
        valueMap.put("Temperature", 2);
        //Weather Level Values
        valueMap.put("Sunny", 0);
        valueMap.put("Rainy", 1);
        valueMap.put("Snow", 2);
        //Humidity Level Values
        valueMap.put("0%", 0);
        valueMap.put("25%", 1);
        valueMap.put("50%", 2);
        valueMap.put("75%", 3);
        valueMap.put("100%", 4);
        //Temperature Level Values
        valueMap.put("0", 0);
        valueMap.put("10", 1);
        valueMap.put("20", 2);
        valueMap.put("30", 3);
        valueMap.put("40", 4);
        valueMap.put("50", 5);
        valueMap.put("60", 6);
        valueMap.put("70", 7);
        valueMap.put("80", 8);
        valueMap.put("90+", 9);
        //Time Values
        valueMap.put("1 Hour", 1);
        valueMap.put("2 Hours", 2);
        valueMap.put("3 Hours", 3);
        valueMap.put("4 Hours", 4);
        valueMap.put("5 Hours", 5);

        final ArrayAdapter<String> modeAdapter = new ArrayAdapter<>(this, R.layout.layout_controller_interface_title, R.id.titleTextView, getResources().getStringArray(R.array.mode_interface_items));

        final ArrayAdapter<String> weatherLevelAdapter = new ArrayAdapter<>(this, R.layout.layout_controller_interface_title, R.id.titleTextView, getResources().getStringArray(R.array.weathercontroller_interface_items));
        final ArrayAdapter<String> temperatureLevelAdapter = new ArrayAdapter<>(this, R.layout.layout_controller_interface_title, R.id.titleTextView, getResources().getStringArray(R.array.tempcontroller_interface_items));
        final ArrayAdapter<String> humidityLevelAdapter = new ArrayAdapter<>(this, R.layout.layout_controller_interface_title, R.id.titleTextView, getResources().getStringArray(R.array.humiditycontroller_interface_items));

        final ArrayAdapter<String> timeAdapter = new ArrayAdapter<>(this, R.layout.layout_controller_interface_title, R.id.titleTextView, getResources().getStringArray(R.array.time_interface_items));

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
        onServicesDiscovered();
    }

    public void onClickSet(View view) {
        //Get Spinner Values
        String value = modeSpinner.getSelectedItem().toString();
        modeValue = (Integer)valueMap.get(value);
        value = levelSpinner.getSelectedItem().toString();
        levelValue = (Integer)valueMap.get(value);
        value = timeSpinner.getSelectedItem().toString();
        timeValue = (Integer)valueMap.get(value);

        byte mode = (byte) (modeValue);
        byte level = (byte) (levelValue);
        byte time = (byte) (timeValue);
        ByteBuffer buffer = ByteBuffer.allocate(2 + 1 + 1 + 1).order(java.nio.ByteOrder.LITTLE_ENDIAN);


        // prefix
        String prefix = "!S";
        buffer.put(prefix.getBytes());


        // values
        buffer.put(mode);
        buffer.put(level);
        buffer.put(time);

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
