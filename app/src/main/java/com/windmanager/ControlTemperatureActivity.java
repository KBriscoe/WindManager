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

public class ControlTemperatureActivity extends UartInterfaceActivity{
    // Log
    private final static String TAG = WindControllerActivity.class.getSimpleName();

    // Activity request codes (used for onActivityResult)
    private static final int kActivityRequestCode_0TempColorPickerActivity = 0;
    private static final int kActivityRequestCode_10TempColorPickerActivity = 1;
    private static final int kActivityRequestCode_20TempColorPickerActivity = 2;
    private static final int kActivityRequestCode_30TempColorPickerActivity = 3;
    private static final int kActivityRequestCode_40TempColorPickerActivity = 4;
    private static final int kActivityRequestCode_50TempColorPickerActivity = 5;
    private static final int kActivityRequestCode_60TempColorPickerActivity = 6;
    private static final int kActivityRequestCode_70TempColorPickerActivity = 7;
    private static final int kActivityRequestCode_80TempColorPickerActivity = 8;
    private static final int kActivityRequestCode_90TempColorPickerActivity = 9;

    // Constants
    private final static String kPreferences = "ControllerActivity_prefs";
    private final static String kPreferences_uartToolTip = "uarttooltip";

    // UI
    private ViewGroup mUartTooltipViewGroup;

    //Data
    private String prefix;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_windcontroller);

        //Log.d(TAG, "onCreate");

        mBleManager = BleManager.getInstance(this);

        ExpandableHeightListView interfaceListView = findViewById(R.id.interfaceListView);
        ArrayAdapter<String> interfaceListAdapter = new ArrayAdapter<>(this, R.layout.layout_controller_interface_title, R.id.titleTextView, getResources().getStringArray(R.array.tempcontroller_interface_items));
        assert interfaceListView != null;
        interfaceListView.setAdapter(interfaceListAdapter);
        interfaceListView.setExpanded(true);
        interfaceListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    prefix = "!T0";
                    Intent intent = new Intent(ControlTemperatureActivity.this, ColorPickerActivity.class);
                    intent.putExtra("PREFIX", prefix);
                    startActivityForResult(intent, kActivityRequestCode_0TempColorPickerActivity);
                }else if(position == 1) {
                    prefix = "!T1";
                    Intent intent = new Intent(ControlTemperatureActivity.this, ColorPickerActivity.class);
                    intent.putExtra("PREFIX", prefix);
                    startActivityForResult(intent, kActivityRequestCode_10TempColorPickerActivity);
                }else if(position == 2) {
                    prefix = "!T2";
                    Intent intent = new Intent(ControlTemperatureActivity.this, ColorPickerActivity.class);
                    intent.putExtra("PREFIX", prefix);
                    startActivityForResult(intent, kActivityRequestCode_20TempColorPickerActivity);
                }else if(position == 3) {
                    prefix = "!T3";
                    Intent intent = new Intent(ControlTemperatureActivity.this, ColorPickerActivity.class);
                    intent.putExtra("PREFIX", prefix);
                    startActivityForResult(intent, kActivityRequestCode_30TempColorPickerActivity);
                }else if(position == 4) {
                    prefix = "!T4";
                    Intent intent = new Intent(ControlTemperatureActivity.this, ColorPickerActivity.class);
                    intent.putExtra("PREFIX", prefix);
                    startActivityForResult(intent, kActivityRequestCode_40TempColorPickerActivity);
                }else if(position == 5) {
                    prefix = "!T5";
                    Intent intent = new Intent(ControlTemperatureActivity.this, ColorPickerActivity.class);
                    intent.putExtra("PREFIX", prefix);
                    startActivityForResult(intent, kActivityRequestCode_50TempColorPickerActivity);
                }else if(position == 6) {
                    prefix = "!T6";
                    Intent intent = new Intent(ControlTemperatureActivity.this, ColorPickerActivity.class);
                    intent.putExtra("PREFIX", prefix);
                    startActivityForResult(intent, kActivityRequestCode_60TempColorPickerActivity);
                }else if(position == 7) {
                    prefix = "!T7";
                    Intent intent = new Intent(ControlTemperatureActivity.this, ColorPickerActivity.class);
                    intent.putExtra("PREFIX", prefix);
                    startActivityForResult(intent, kActivityRequestCode_70TempColorPickerActivity);
                }else if(position == 8) {
                    prefix = "!T8";
                    Intent intent = new Intent(ControlTemperatureActivity.this, ColorPickerActivity.class);
                    intent.putExtra("PREFIX", prefix);
                    startActivityForResult(intent, kActivityRequestCode_80TempColorPickerActivity);
                }else if(position == 9) {
                    prefix = "!T9";
                    Intent intent = new Intent(ControlTemperatureActivity.this, ColorPickerActivity.class);
                    intent.putExtra("PREFIX", prefix);
                    startActivityForResult(intent, kActivityRequestCode_90TempColorPickerActivity);
                }
            }
        });

        mUartTooltipViewGroup = findViewById(R.id.uartTooltipViewGroup);
        SharedPreferences preferences = getSharedPreferences(kPreferences, Context.MODE_PRIVATE);
        final boolean showUartTooltip = preferences.getBoolean(kPreferences_uartToolTip, true);
        mUartTooltipViewGroup.setVisibility(showUartTooltip ? View.VISIBLE : View.GONE);

        // Start services
        onServicesDiscovered();
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