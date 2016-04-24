package com.project.flyingchess.dialog;


import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.ProgressBar;

import com.project.flyingchess.R;

public class WaitingGameStartDialog extends Dialog {
	private ProgressBar pb_loading;

	public WaitingGameStartDialog(Context context, int theme) {
		super(context,theme);
		setContentView(R.layout.dialog_waiting_start_game);

		pb_loading = (ProgressBar) findViewById(R.id.pb_loading);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
}
