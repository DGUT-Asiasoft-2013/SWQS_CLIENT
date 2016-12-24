package com.swqs.schooltrade.activity;

import java.io.IOException;

import com.swqs.schooltrade.R;
import com.swqs.schooltrade.entity.Goods;
import com.swqs.schooltrade.util.Server;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.Toast;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class BuyAdditionalEvaluationActivity extends Activity {
	private EditText etComment;
	private Goods goods;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_buy_additional_evaluation);
		etComment=(EditText) findViewById(R.id.etComment);
		goods = (Goods) getIntent().getSerializableExtra("goods");
		
		findViewById(R.id.btnback).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				goEvaluationDetails();
				
			}
		});
		
		findViewById(R.id.btnSubmit).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				onSubmit();
			}
		});
	}

	private void onSubmit() {
		String comment = etComment.getText().toString();
		if (TextUtils.isEmpty(comment)) {
			Toast.makeText(this, "请输入评论内容", Toast.LENGTH_SHORT).show();
			return;
		}
		OkHttpClient client = Server.getSharedClient();
		MultipartBody body = new MultipartBody.Builder().addFormDataPart("text", comment).build();
		Request request = Server.requestBuilderWithApi("goods/" + goods.getId() + "/comments").post(body)
				.build();
		client.newCall(request).enqueue(new Callback() {

			@Override
			public void onResponse(Call arg0, Response arg1) throws IOException {
				final String jsonString = arg1.body().string();
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						onSuccess(jsonString);
					}
				});
			}

			@Override
			public void onFailure(Call arg0, final IOException arg1) {
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						onFailed(arg1);
					}
				});
			}
		});
	}

	private void onSuccess(String jsonString) {
		finish();
		Toast.makeText(this, "发表评论成功", Toast.LENGTH_SHORT).show();
	}

	private void onFailed(Exception e) {
		new AlertDialog.Builder(this).setTitle("结果").setMessage("发表失败")
				.setNegativeButton("", null).show();
	}
	
	void goEvaluationDetails(){
		Intent itnt = new Intent(this,BuyEvaluationDetailsActivity.class);
		startActivity(itnt);
	}
	
	void goOrderDetails(){
		Intent itnt = new Intent(this,BuyOrderDetailsActivity.class);
		startActivity(itnt);
	}
	
}
