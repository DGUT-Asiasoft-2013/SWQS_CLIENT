package com.swqs.schooltrade.activity;

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.swqs.schooltrade.R;
import com.swqs.schooltrade.app.TradeApplication;
import com.swqs.schooltrade.entity.User;
import com.swqs.schooltrade.util.CustomProgressDialog;
import com.swqs.schooltrade.util.Server;
import com.swqs.schooltrade.util.Util;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Toast;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class RechargeActivity extends Activity implements OnClickListener {

	private RadioGroup rgBankCark;
	private Button btnThird;
	private Button btnMoney100, btnMoney200, btnMoney500, btnMoney1000;
	private EditText etInputMoney;
	private CustomProgressDialog progressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_recharge);

		btnThird = (Button) findViewById(R.id.btnThird);

		rgBankCark = (RadioGroup) findViewById(R.id.rgBankCark);
		rgBankCark.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {

			}
		});

		btnThird.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				goJump();
			}
		});
		btnMoney100 = (Button) findViewById(R.id.btnMoney100);
		btnMoney200 = (Button) findViewById(R.id.btnMoney200);
		btnMoney500 = (Button) findViewById(R.id.btnMoney500);
		btnMoney1000 = (Button) findViewById(R.id.btnMoney1000);
		etInputMoney = (EditText) findViewById(R.id.etInputMoney);

		btnMoney100.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				etInputMoney.setText("100");
			}
		});
		btnMoney200.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				etInputMoney.setText("200");
			}
		});
		btnMoney500.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				etInputMoney.setText("500");
			}
		});
		btnMoney1000.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				etInputMoney.setText("1000");
			}
		});
		findViewById(R.id.ivBack).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

	@Override
	public void onClick(View v) {

	}

	void goJump() {
		String money = etInputMoney.getText().toString();
		if (TextUtils.isEmpty(money)) {
			Toast.makeText(this, "请输入或选择充值金额", Toast.LENGTH_SHORT).show();
			return;
		}
		progressDialog=Util.getProgressDialog(this, R.layout.custom_progressdialog);
		progressDialog.show();
		OkHttpClient client = Server.getSharedClient();
		MultipartBody requestBody = new MultipartBody.Builder().addFormDataPart("money", money)
				.addFormDataPart("uid", TradeApplication.uid).build();

		Request request = Server.requestBuilderWithApi("recharge").method("post", null).post(requestBody).build();
		client.newCall(request).enqueue(new Callback() {

			@Override
			public void onResponse(Call arg0, Response arg1) throws IOException {
				String jsonString = arg1.body().string();
				final ObjectMapper mapper = new ObjectMapper();
				progressDialog.dismiss();
				try {
					User user = mapper.readValue(jsonString, User.class);
					if (user != null) {
						runOnUiThread(new Runnable() {

							@Override
							public void run() {
								Toast.makeText(RechargeActivity.this, "充值成功", Toast.LENGTH_SHORT).show();
								finish();
								return;
							}
						});
					}
				} catch (Exception e) {
					e.printStackTrace();
					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							Toast.makeText(RechargeActivity.this, "充值失败", Toast.LENGTH_SHORT).show();
							return;
						}
					});
				}
			}

			@Override
			public void onFailure(Call arg0, IOException arg1) {
				progressDialog.dismiss();
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						Toast.makeText(RechargeActivity.this, "网络请求失败", Toast.LENGTH_SHORT).show();
						return;
					}
				});
			}
		});
	}
}
