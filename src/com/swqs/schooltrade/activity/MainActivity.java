package com.swqs.schooltrade.activity;

import com.swqs.schooltrade.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	@Override
	protected void onResume() {
		super.onResume();
		
		Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
			private int abcd = 0;

			@Override
			public void run() {
				startLoginActivity();// TODO Auto-generated method stub
			}
		}, 1000);// TODO Auto-generated method stub

	}


	void startLoginActivity() {
		Intent itnt = new Intent(this, LoginActivity.class);// TODO
		startActivity(itnt);
		finish();
	}
}
