package com.joy.freightgo;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

/**
 * Created by Joy on 2016/8/20.
 */
public class SettingActivity extends Activity {
    private static final String joytag = "joy.settingactivity.";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(joytag+"", "onCreate");
        setContentView(R.layout.activity_setting);
    }
}
