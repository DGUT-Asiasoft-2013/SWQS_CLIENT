package com.swqs.schooltrade.activity;

import java.io.IOException;
import java.text.SimpleDateFormat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.swqs.schooltrade.R;
import com.swqs.schooltrade.app.TradeApplication;
import com.swqs.schooltrade.entity.Goods;
import com.swqs.schooltrade.entity.Identify;
import com.swqs.schooltrade.entity.User;
import com.swqs.schooltrade.util.MD5;
import com.swqs.schooltrade.util.Server;
import com.swqs.schooltrade.util.Util;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import cn.jmessage.android.uikit.chatting.ChatActivity;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SellOrderDetailsActivity extends Activity {

	private TextView tvReceiver;
	private TextView tvTradeTime;
	private TextView tvPhone;
	private ImageView ivImg;
	private TextView tvTitle;
	private TextView tvMoney;
	private TextView tvState;
	
	private TextView tvState1;
	private TextView tvState2;
	private TextView tvState3;
	private TextView tvState4;
	
	Goods goods = null;
	Identify identify = null;
	private Button btnSendGoods;
	private Button btnComment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sell_order_details);
		goods = (Goods) getIntent().getSerializableExtra("data");
		tvReceiver=(TextView) findViewById(R.id.tvReceiver);
		tvTradeTime=(TextView) findViewById(R.id.tvTradeTime);
		tvPhone=(TextView) findViewById(R.id.tvPhone);
		tvTitle=(TextView) findViewById(R.id.tvTitle);
		tvMoney=(TextView) findViewById(R.id.tvMoney);
		tvState=(TextView) findViewById(R.id.tvState);
		tvState1=(TextView) findViewById(R.id.tvState1);
		tvState2=(TextView) findViewById(R.id.tvState2);
		tvState3=(TextView) findViewById(R.id.tvState3);
		tvState4=(TextView) findViewById(R.id.tvState4);
		ivImg=(ImageView) findViewById(R.id.ivImg);
		btnSendGoods=(Button) findViewById(R.id.btnSendGoods);
		btnComment=(Button) findViewById(R.id.btnComment);
		findViewById(R.id.btnCommunicate).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				goChatbuyer();

			}
		});

		findViewById(R.id.btnback).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				goBack();

			}
		});

		btnComment.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				goSellEvaluationDetails();

			}
		});
		btnSendGoods.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				sendGoods();
			}
		});
		getOrderDetail();
	}

	protected void sendGoods() {


			OkHttpClient client = Server.getSharedClient();

			MultipartBody requestBody = new MultipartBody.Builder().addFormDataPart("identifyId", identify.getId()+"")
					.addFormDataPart("flag", "2").build();

			Request request = Server.requestBuilderWithApi("settradestate").method("post", null).post(requestBody).build();

			final ProgressDialog dlg = new ProgressDialog(this);
			dlg.setCancelable(true);
			dlg.setCanceledOnTouchOutside(false);
			dlg.setMessage("sending goods...");
			dlg.show();

			client.newCall(request).enqueue(new Callback() {

				@Override
				public void onResponse(Call arg0, Response arg1) throws IOException {
					final String responseSrting = arg1.body().string();

					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							try {
								dlg.dismiss();
								int flag=Integer.parseInt(responseSrting);
								if(flag==1){
									getOrderDetail();
								}
							} catch (Exception e) {

							}
						}

					});

				}

				@Override
				public void onFailure(Call arg0, final IOException arg1) {
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							dlg.dismiss();
							Toast.makeText(SellOrderDetailsActivity.this, arg1.getLocalizedMessage(), Toast.LENGTH_LONG).show();
						}
					});
				}
			});
			
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
							setOrderDetailData();
						}
					});
				}
			}

			@Override
			public void onFailure(Call arg0, IOException arg1) {

			}
		});

	}
	
	private void setOrderDetailData(){
		tvReceiver.setText("收货人："+identify.getBuyer().getAccount());
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd hh:mm");
		tvTradeTime.setText("交易时间："+sdf.format(identify.getCreateDate()));
		tvPhone.setText("手机号码："+identify.getBuyer().getPhone());
		Util.loadImage(this, goods.getListImage().get(0).getPictureUrl(), ivImg);
		tvTitle.setText(goods.getTitle());
		tvMoney.setText("￥"+goods.getOriginalPrice()+"");
		int state=identify.getTradeState();
		String strState;
		if(state==1){
			strState="未发货";
			tvState1.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_bright));
			btnComment.setVisibility(View.GONE);
			btnSendGoods.setVisibility(View.VISIBLE);
		}else if(state==2){
			strState="已发货";
			tvState1.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_bright));
			tvState2.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_bright));
			btnComment.setVisibility(View.GONE);
			btnSendGoods.setVisibility(View.GONE);
		}else if(state==3){
			strState="已收货";
			tvState1.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_bright));
			tvState2.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_bright));
			tvState3.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_bright));
			btnComment.setVisibility(View.VISIBLE);
			btnSendGoods.setVisibility(View.GONE);
		}else{
			strState="已评价";
			tvState1.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_bright));
			tvState2.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_bright));
			tvState3.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_bright));
			tvState4.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_bright));
			btnComment.setVisibility(View.VISIBLE);
			btnSendGoods.setVisibility(View.GONE);
		}
		tvState.setText("销售状态："+strState);
		
	}
	
	void goSellEvaluationDetails() {
		Intent itnt = new Intent(this, SellEvaluationDetailsActivity.class);
		startActivity(itnt);
	}

	void goChatbuyer() {
		Intent intent = new Intent(this, ChatActivity.class);
		intent.putExtra(TradeApplication.TARGET_ID, identify.getBuyer().getAccount());
		startActivity(intent);
	}

	void goBack() {
		finish();
	}
}
