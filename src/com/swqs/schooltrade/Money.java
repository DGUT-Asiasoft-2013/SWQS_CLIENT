package com.swqs.schooltrade;

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.swqs.schooltrade.api.Server;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Money extends Activity {

	private Button btnRecharge;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_money);
		findViewById(R.id.btnback).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				goMyBuy();
				
			}
		});
		
		btnRecharge = (Button) findViewById(R.id.btnRecharge);
		btnRecharge.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				goRecharge();
			}
		});
		getMoney();
	}

	private void getMoney() {

		OkHttpClient client = Server.getSharedClient();

		Request request = Server.requestBuilderWithApi("login").method("get", null).build();

		client.newCall(request).enqueue(new Callback() {

			@Override
			public void onResponse(final Call arg0, final Response arg1) throws IOException {
				String jsonString = arg1.body().string();
				ObjectMapper mapper = new ObjectMapper();
				try {
				} catch (Exception e) {
					e.printStackTrace();
					runOnUiThread(new Runnable() {

						@Override
						public void run() {
						}
					});
				}
			}

			@Override
			public void onFailure(Call arg0, IOException arg1) {
			}
		});
			
	}

	void goRecharge() {
		Intent intent = new Intent(this, Recharge.class);
		startActivity(intent);
	}
	void goMyBuy(){
		finish();
	}
}
