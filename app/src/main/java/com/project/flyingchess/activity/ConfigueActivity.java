package com.project.flyingchess.activity;

import android.os.Bundle;
import android.view.View;

import com.project.flyingchess.R;

public class ConfigueActivity extends BaseActivity implements View.OnClickListener{
    private static final String TAG = "ConfigueActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configue);

        findview();
    }

    private void findview() {
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                break;
        }
    }

    public String getTag() {
        return TAG;
    }
}
