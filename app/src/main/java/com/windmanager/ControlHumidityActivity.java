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

public class ControlHumidityActivity extends UartInterfaceActivity{
    // Log
    private final static String TAG = WindControllerActivity.class.getSimpleName();

    // Activity request codes (used for onActivityResult)
    private static final int kActivityRequestCode_0ColorPickerActivity = 0;
    private static final int kActivityRequestCode_25ColorPickerActivity = 1;
    private static final int kActivityRequestCode_50ColorPickerActivity = 2;
    private static final int kActivityRequestCode_75ColorPickerActivity = 3;
    private static final int kActivityRequestCode_100ColorPickerActivity = 4;

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
        ArrayAdapter<String> interfaceListAdapter = new ArrayAdapter<>(this, R.layout.layout_controller_interface_title, R.id.titleTextView, getResources().getStringArray(R.array.humiditycontroller_interface_items));
        assert interfaceListView != null;
        interfaceListView.setAdapter(interfaceListAdapter);
        interfaceListView.setExpanded(true);
        interfaceListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    prefix = "!H0";
                    Intent intent = new Intent(ControlHumidityActivity.this, ColorPickerActivity.class);
                    intent.putExtra("PREFIX", prefix);
                    startActivityForResult(intent, kActivityRequestCode_0ColorPickerActivity);
                }else if(position == 1) {
                    prefix = "!H1";
                    Intent intent = new Intent(ControlHumidityActivity.this, ColorPickerActivity.class);
                    intent.putExtra("PREFIX", prefix);
                    startActivityForResult(intent, kActivityRequestCode_25ColorPickerActivity);
                }else if(position == 2) {
                    prefix = "!H2";
                    Intent intent = new Intent(ControlHumidityActivity.this, ColorPickerActivity.class);
                    intent.putExtra("PREFIX", prefix);
                    startActivityForResult(intent, kActivityRequestCode_50ColorPickerActivity);
                }else if(position == 3) {
                    prefix = "!H3";
                    Intent intent = new Intent(ControlHumidityActivity.this, ColorPickerActivity.class);
                    intent.putExtra("PREFIX", prefix);
                    startActivityForResult(intent, kActivityRequestCode_75ColorPickerActivity);
                }else if(position == 4) {
                    prefix = "!H4";
                    Intent intent = new Intent(ControlHumidityActivity.this, ColorPickerActivity.class);
                    intent.putExtra("PREFIX", prefix);
                    startActivityForResult(intent, kActivityRequestCode_100ColorPickerActivity);
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

    public void onClickCloseTooltip(View view) {
        SharedPreferences settings = getSharedPreferences(kPreferences, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(kPreferences_uartToolTip, false);
        editor.apply();

        mUartTooltipViewGroup.setVisibility(View.GONE);

    }

    @Override
    public void onDisconnected() {
        super.onDisconnected();
        Log.d(TAG, "Disconnected. Back to previous activity");
        setResult(-1);      // Unexpected Disconnect
        finish();
    }

}