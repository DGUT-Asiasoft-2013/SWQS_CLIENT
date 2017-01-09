package com.swqs.schooltrade.activity;

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.swqs.schooltrade.R;
import com.swqs.schooltrade.app.TradeApplication;
import com.swqs.schooltrade.entity.User;
import com.swqs.schooltrade.util.Server;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MyMoneyActivity extends Activity {

	private Button btnRecharge;
	private TextView tvBalance;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_money);
		tvBalance=(TextView) findViewById(R.id.tvBalance);
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
	}

	@Override
	protected void onResume() {
		super.onResume();
		getMoney();
	}
	private void getMoney() {

		OkHttpClient client = Server.getSharedClient();
		FormBody body=new FormBody.Builder().add("uid", TradeApplication.uid).build();
		Request request = Server.requestBuilderWithApi("me").method("get", null).post(body).build();

		client.newCall(request).enqueue(new Callback() {

			@Override
			public void onResponse(final Call arg0, final Response arg1) throws IOException {
				String jsonString = arg1.body().string();
				ObjectMapper mapper = new ObjectMapper();
				try {
					final User user = mapper.readValue(jsonString, User.class);
					runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							tvBalance.setText(user.getBalance()+"");
						}
					});
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
		Intent intent = new Intent(this, RechargeActivity.class);
		startActivity(intent);
	}
	void goMyBuy(){
		finish();
	}
}
