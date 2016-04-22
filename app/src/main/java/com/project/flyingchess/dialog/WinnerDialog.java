package com.project.flyingchess.dialog;


import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.project.flyingchess.R;

public class WinnerDialog extends Dialog {
	private Button btn_restart;
	private TextView tv_rank_1,tv_rank_2,tv_rank_3;

	public WinnerDialog(Context context, int theme) {
		super(context,theme);
		setContentView(R.layout.dialog_winner);

		btn_restart = (Button) findViewById(R.id.btn_restart);
		tv_rank_1 = (TextView) findViewById(R.id.tv_rank_1);
		tv_rank_2 = (TextView) findViewById(R.id.tv_rank_2);
		tv_rank_3 = (TextView) findViewById(R.id.tv_rank_3);
	}

	public void setRestartListener(View.OnClickListener listener){
		btn_restart.setOnClickListener(listener);
	}

	public void setFirst(String content){
		tv_rank_1.setText(content);
		tv_rank_1.setVisibility(View.VISIBLE);
		if(content.equals("")) tv_rank_1.setVisibility(View.GONE);
	}

	public void setSecond(String content){
		tv_rank_2.setText(content);
		tv_rank_2.setVisibility(View.VISIBLE);
		if(content.equals("")) tv_rank_2.setVisibility(View.GONE);
	}

	public void setThird(String content){
		tv_rank_3.setText(content);
		tv_rank_3.setVisibility(View.VISIBLE);
		if(content.equals("")) tv_rank_3.setVisibility(View.GONE);
	}

	public void setBtnVisible(boolean isVisible){
		btn_restart.setVisibility(isVisible ? View.VISIBLE : View.GONE);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
}
