package com.swqs.schooltrade.activity;

import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.swqs.schooltrade.R;
import com.swqs.schooltrade.entity.Goods;
import com.swqs.schooltrade.entity.Identify;
import com.swqs.schooltrade.util.Server;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class BuyOrderDetailsActivity extends Activity {

	Goods goods = null;
	Identify identify = null;
	TextView tvSeller;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_buy_order_details);
		goods = (Goods) getIntent().getSerializableExtra("data");
		findViewById(R.id.btnCommunicate).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				goChatSeller();

			}
		});

		findViewById(R.id.btnback).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				goMyBuy();

			}
		});

		findViewById(R.id.btnlook).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				goEvaluationDetails();

			}
		});
		getOrderDetail();
	}

	private void getOrderDetail() {

		OkHttpClient client = Server.getSharedClient();

		Request request = Server.requestBuilderWithApi("/orderdetails/" + goods.getId()).build();

		client.newCall(request).enqueue(new Callback() {

			@Override
			public void onResponse(Call arg0, Response arg1) throws IOException {
				String jsonString = arg1.body().string();
				ObjectMapper mapper = new ObjectMapper();
				identify = mapper.readValue(jsonString, Identify.class);
				if (identify != null) {
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

	void goEvaluationDetails() {
		Intent itnt = new Intent(this, BuyEvaluationDetailsActivity.class);
		startActivity(itnt);
	}

	void goChatSeller() {
		Intent itnt = new Intent(this, BuyEvaluationDetailsActivity.class);
		startActivity(itnt);
	}

	void goMyBuy() {
		finish();
	}
}
