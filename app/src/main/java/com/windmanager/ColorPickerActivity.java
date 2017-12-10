package com.windmanager;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.larswerkman.holocolorpicker.ColorPicker;
import com.larswerkman.holocolorpicker.SaturationBar;
import com.larswerkman.holocolorpicker.ValueBar;

import java.nio.ByteBuffer;

public class ColorPickerActivity extends UartInterfaceActivity implements ColorPicker.OnColorChangedListener {
    // Log
    private final static String TAG = ColorPickerActivity.class.getSimpleName();

    // Constants
    private final static boolean kPersistValues = true;
    private final static String kPreferences = "ColorPickerActivity_prefs";
    private final static String kPreferences_color = "color";

    private final static int kFirstTimeColor = 0x0000ff;

    // UI
    private ColorPicker mColorPicker;
    private View mRgbColorView;
    private TextView mRgbTextView;

    private int mSelectedColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_color_picker);

        mBleManager = BleManager.getInstance(this);

        // UI
        mRgbColorView = findViewById(R.id.rgbColorView);
        mRgbTextView = (TextView) findViewById(R.id.rgbTextView);

        SaturationBar mSaturationBar = (SaturationBar) findViewById(R.id.saturationbar);
        ValueBar mValueBar = (ValueBar) findViewById(R.id.valuebar);
        mColorPicker = (ColorPicker) findViewById(R.id.colorPicker);
        if (mColorPicker != null) {
            mColorPicker.addSaturationBar(mSaturationBar);
            mColorPicker.addValueBar(mValueBar);
            mColorPicker.setOnColorChangedListener(this);
        }

        if (kPersistValues) {
            SharedPreferences preferences = getSharedPreferences(kPreferences, Context.MODE_PRIVATE);
            mSelectedColor = preferences.getInt(kPreferences_color, kFirstTimeColor);
        } else {
            mSelectedColor = kFirstTimeColor;
        }

        mColorPicker.setOldCenterColor(mSelectedColor);
        mColorPicker.setColor(mSelectedColor);
        onColorChanged(mSelectedColor);

        // Start services
        onServicesDiscovered();
    }

    @Override
    public void onStop() {
        // Preserve values
        if (kPersistValues) {
            SharedPreferences settings = getSharedPreferences(kPreferences, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = settings.edit();
            editor.putInt(kPreferences_color, mSelectedColor);
            editor.apply();
        }

        super.onStop();
    }

    @Override
    public void onDisconnected() {
        super.onDisconnected();
        Log.d(TAG, "Disconnected. Back to previous activity");
        setResult(-1);      // Unexpected Disconnect
        finish();
    }

    @Override
    public void onColorChanged(int color) {
        // Save selected color
        mSelectedColor = color;

        // Update UI
        mRgbColorView.setBackgroundColor(color);

        final int r = (color >> 16) & 0xFF;
        final int g = (color >> 8) & 0xFF;
        final int b = (color >> 0) & 0xFF;
        final String text = String.format(getString(R.string.colorpicker_rgbformat), r, g, b);
        mRgbTextView.setText(text);
    }

    public void onClickSend(View view) {
        // Set the old color
        mColorPicker.setOldCenterColor(mSelectedColor);

        // Send selected color !Crgb
        byte r = (byte) ((mSelectedColor >> 16) & 0xFF);
        byte g = (byte) ((mSelectedColor >> 8) & 0xFF);
        byte b = (byte) ((mSelectedColor >> 0) & 0xFF);

        ByteBuffer buffer = ByteBuffer.allocate(3 + 3).order(java.nio.ByteOrder.LITTLE_ENDIAN);

        // prefix
        String prefix = getPrefix();
        buffer.put(prefix.getBytes());

        // values
        buffer.put(r);
        buffer.put(g);
        buffer.put(b);

        byte[] result = buffer.array();
        sendDataWithCRC(result);
    }

    private String getPrefix() {
        Bundle extras = getIntent().getExtras();
        return extras.getString("PREFIX");
    }
}
