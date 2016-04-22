package com.project.flyingchess.dialog;


import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.project.flyingchess.R;

public class StartGameDialog extends Dialog {
	private Button btn_create_game,btn_join_game,btn_cancel;

	public StartGameDialog(Context context, int theme) {
		super(context,theme);
		setContentView(R.layout.dialog_start_game);

		btn_create_game = (Button) findViewById(R.id.btn_create_game);
		btn_join_game = (Button) findViewById(R.id.btn_join_game);
		btn_cancel= (Button) findViewById(R.id.btn_cancel);
	}

	public void setCreateGameListener(View.OnClickListener onClickListener){
		btn_create_game.setOnClickListener(onClickListener);
	}

	public void setJoinGameListener(View.OnClickListener onClickListener){
		btn_join_game.setOnClickListener(onClickListener);
	}

	public void setCancelListener(View.OnClickListener onClickListener){
		btn_cancel.setOnClickListener(onClickListener);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
}
