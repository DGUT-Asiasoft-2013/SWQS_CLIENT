package com.swqs.schooltrade.util;

import com.swqs.schooltrade.R;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;

public class CustomProgressDialog extends Dialog {
	public CustomProgressDialog(Context context,int layoutResID) {
		this(context, R.style.CustomProgressDialog,layoutResID);
	}

	public CustomProgressDialog(Context context, int theme,int layoutResID) {
		super(context, theme);
		this.setContentView(layoutResID);
		this.getWindow().getAttributes().gravity = Gravity.CENTER;
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {

		if (!hasFocus) {
			dismiss();
		}
	}
}
