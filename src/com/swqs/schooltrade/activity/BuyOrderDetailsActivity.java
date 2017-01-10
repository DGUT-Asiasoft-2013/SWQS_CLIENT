package com.swqs.schooltrade.activity;

import java.io.IOException;
import java.text.SimpleDateFormat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.swqs.schooltrade.R;
import com.swqs.schooltrade.app.TradeApplication;
import com.swqs.schooltrade.entity.Goods;
import com.swqs.schooltrade.entity.Identify;
import com.swqs.schooltrade.util.Server;
import com.swqs.schooltrade.util.Util;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
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

public class BuyOrderDetailsActivity extends Activity {

	private TextView tvSeller;
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

	private TextView tvBuyer;

	Goods goods = null;
	Identify identify = null;
	private Button btnConfirm;
	private Button btnComment;
	private Button btnGotoComment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_buy_order_details);
		goods = (Goods) getIntent().getSerializableExtra("data");
		tvSeller = (TextView) findViewById(R.id.tvSeller);
		tvBuyer = (TextView) findViewById(R.id.tvBuyer);
		tvTradeTime = (TextView) findViewById(R.id.tvTradeTime);
		tvPhone = (TextView) findViewById(R.id.tvPhone);
		tvTitle = (TextView) findViewById(R.id.tvTitle);
		tvMoney = (TextView) findViewById(R.id.tvMoney);
		tvState = (TextView) findViewById(R.id.tvState);
		tvState1 = (TextView) findViewById(R.id.tvState1);
		tvState2 = (TextView) findViewById(R.id.tvState2);
		tvState3 = (TextView) findViewById(R.id.tvState3);
		tvState4 = (TextView) findViewById(R.id.tvState4);
		ivImg = (ImageView) findViewById(R.id.ivImg);
		btnConfirm = (Button) findViewById(R.id.btnConfirm);
		btnComment = (Button) findViewById(R.id.btnComment);
		btnGotoComment = (Button) findViewById(R.id.btnGotoComment);
		btnGotoComment.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				gotoComment();
			}
		});
		findViewById(R.id.btnCommunicate).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				goChatSeller();
			}
		});

		findViewById(R.id.btnback).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});

		findViewById(R.id.btnComment).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				goEvaluationDetails();

			}
		});
		btnConfirm.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				AlertDialog.Builder builder = new AlertDialog.Builder(BuyOrderDetailsActivity.this);
				builder.setMessage("点击确认后,钱款将会打到卖家账号中");
				builder.setNegativeButton("确认", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						confirmReceive();
					}
				});
				builder.setPositiveButton("取消", null);
				builder.show();
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		getOrderDetail();
	}
	private void gotoComment() {
		Intent intent=new Intent(this,AddEvaluationActivity.class);
		intent.putExtra("identify", identify);
		startActivity(intent);
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
		goods = (Goods) getIntent().getSerializableExtra("data");
		getOrderDetail();
	}

	protected void confirmReceive() {
		OkHttpClient client = Server.getSharedClient();

		MultipartBody requestBody = new MultipartBody.Builder().addFormDataPart("identifyId", identify.getId() + "")
				.addFormDataPart("flag", "3").addFormDataPart("uid", TradeApplication.uid).build();

		Request request = Server.requestBuilderWithApi("settradestate").method("post", null).post(requestBody).build();

		final ProgressDialog dlg = new ProgressDialog(this);
		dlg.setCancelable(true);
		dlg.setCanceledOnTouchOutside(false);
		dlg.setMessage("确认中...");
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
							int flag = Integer.parseInt(responseSrting);
							if (flag == 1) {
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
						Toast.makeText(BuyOrderDetailsActivity.this, arg1.getLocalizedMessage(), Toast.LENGTH_LONG)
								.show();
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

	private void setOrderDetailData() {
		tvSeller.setText("卖家：" + identify.getSeller().getAccount());
		tvBuyer.setText("收货人：" + identify.getBuyer().getAccount());
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm");
		tvTradeTime.setText("交易时间：" + sdf.format(identify.getCreateDate()));
		tvPhone.setText("手机号码：" + identify.getBuyer().getPhone());
		Util.loadImage(this, goods.getListImage().get(0).getPictureUrl(), ivImg);
		tvTitle.setText(goods.getTitle());
		tvMoney.setText("￥" + goods.getOriginalPrice() + "");
		int state = identify.getTradeState();
		String strState;
		if (state == 1) {
			strState = "未发货";
			tvState1.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_bright));
			btnComment.setVisibility(View.GONE);
			btnConfirm.setVisibility(View.GONE);
			btnGotoComment.setVisibility(View.GONE);
		} else if (state == 2) {
			strState = "已发货";
			tvState1.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_bright));
			tvState2.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_bright));
			btnComment.setVisibility(View.GONE);
			btnConfirm.setVisibility(View.VISIBLE);
			btnGotoComment.setVisibility(View.GONE);
		} else if (state == 3) {
			strState = "已收货";
			tvState1.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_bright));
			tvState2.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_bright));
			tvState3.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_bright));
			btnComment.setVisibility(View.GONE);
			btnConfirm.setVisibility(View.GONE);
			btnGotoComment.setVisibility(View.VISIBLE);
		} else {
			strState = "已评价";
			tvState1.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_bright));
			tvState2.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_bright));
			tvState3.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_bright));
			tvState4.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_bright));
			btnComment.setVisibility(View.VISIBLE);
			btnConfirm.setVisibility(View.GONE);
			btnGotoComment.setVisibility(View.GONE);
		}
		tvState.setText("销售状态：" + strState);

	}

	void goEvaluationDetails() {
		Intent intent = new Intent(this, BuyEvaluationDetailsActivity.class);
		intent.putExtra("goods_id", identify.getGoods().getId());
		intent.putExtra("buyer_id", identify.getBuyer().getId());
		startActivity(intent);
	}

	void goChatSeller() {
		Intent intent = new Intent(this, ChatActivity.class);
		intent.putExtra(TradeApplication.TARGET_ID, identify.getSeller().getAccount());
		startActivity(intent);
	}

}
