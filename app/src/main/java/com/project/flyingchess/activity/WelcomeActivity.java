package com.project.flyingchess.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

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
        Button btn_single_game = (Button) findViewById(R.id.btn_single_game);
        Button btn_net_game = (Button) findViewById(R.id.btn_net_game);

        btn_single_game.setOnClickListener(this);
        btn_net_game.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_single_game:
                startActivity(new Intent(WelcomeActivity.this,ConfigueActivity.class));
                break;
            case R.id.btn_net_game:
                startActivity(new Intent(WelcomeActivity.this,GameActivity.class)
                        .putExtra(GameActivity.KEY_MODE,GameActivity.NET_SERVER));
                break;
            default:
                break;
        }
    }

    public String getTag() {
        return TAG;
    }
}
