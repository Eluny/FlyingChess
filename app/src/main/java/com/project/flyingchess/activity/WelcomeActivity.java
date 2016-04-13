package com.project.flyingchess.activity;

import android.os.Bundle;
import android.view.View;

import com.project.flyingchess.R;

public class WelcomeActivity extends BaseActivity implements View.OnClickListener{
    private static final String TAG = "WelcomeActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

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
