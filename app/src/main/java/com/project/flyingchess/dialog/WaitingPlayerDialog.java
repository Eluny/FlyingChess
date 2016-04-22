package com.project.flyingchess.dialog;


import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.project.flyingchess.R;

public class WaitingPlayerDialog extends Dialog {
	private TextView tv_content;
	private Button btn_begin,btn_cancel;

	public WaitingPlayerDialog(Context context, int theme) {
		super(context,theme);
		setContentView(R.layout.dialog_waiting_player);

		tv_content = (TextView) findViewById(R.id.tv_content);
		btn_begin = (Button) findViewById(R.id.btn_begin);
		btn_cancel= (Button) findViewById(R.id.btn_cancel);
	}

	public void setBeginListener(View.OnClickListener onClickListener){
		btn_begin.setOnClickListener(onClickListener);
	}

	public void setCancelListener(View.OnClickListener onClickListener){
		btn_cancel.setOnClickListener(onClickListener);
	}

	public void setContent(String content){
		tv_content.setText(content);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
}
