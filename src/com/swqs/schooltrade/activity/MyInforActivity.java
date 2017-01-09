package com.swqs.schooltrade.activity;

import java.io.IOException;
import java.text.SimpleDateFormat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.swqs.schooltrade.R;
import com.swqs.schooltrade.app.TradeApplication;
import com.swqs.schooltrade.entity.User;
import com.swqs.schooltrade.util.RoundImageView;
import com.swqs.schooltrade.util.Server;
import com.swqs.schooltrade.util.Util;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MyInforActivity extends Activity implements OnClickListener {

	private Button btnBack;
	private TextView tvNick;
	private TextView tvSex;
	private TextView tvBirthday;
	private TextView tvPhone;
	private TextView tvEmail;
	private TextView tvSchool;
	private RoundImageView roundAvatar;
	private TextView tvAccount;
	private TextView tvEditPersonInfo;

	private User user;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_info);

		tvNick = (TextView) findViewById(R.id.tvNick);
		tvSex = (TextView) findViewById(R.id.tvSex);
		tvBirthday = (TextView) findViewById(R.id.tvBirthday);
		tvPhone = (TextView) findViewById(R.id.tvPhone);
		tvEmail = (TextView) findViewById(R.id.tvEmail);
		tvSchool = (TextView) findViewById(R.id.tvSchool);
		btnBack = (Button) findViewById(R.id.btnBack);
		roundAvatar = (RoundImageView) findViewById(R.id.roundAvatar);
		tvAccount = (TextView) findViewById(R.id.tvAccount);
		tvEditPersonInfo = (TextView) findViewById(R.id.tvEditPersonInfo);

		btnBack.setOnClickListener(this);
		tvEditPersonInfo.setOnClickListener(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		getPersonInfo();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnBack:
			finish();
			break;
		case R.id.tvEditPersonInfo:
			Intent intent = new Intent(this, EditPersonInfoActivity.class);
			intent.putExtra("user", user);
			startActivity(intent);
			break;
		}
	}

	private void getPersonInfo() {
		OkHttpClient client = Server.getSharedClient();
		FormBody body=new FormBody.Builder().add("uid", TradeApplication.uid).build();
		Request request = Server.requestBuilderWithApi("me").method("get", null).post(body).build();

		client.newCall(request).enqueue(new Callback() {

			@Override
			public void onResponse(Call arg0, Response arg1) throws IOException {
				user = new ObjectMapper().readValue(arg1.body().bytes(), User.class);
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						setPersonInfo();
					}
				});

			}

			@Override
			public void onFailure(Call arg0, IOException arg1) {

			}
		});
	}

	private void setPersonInfo() {
		tvNick.setText(user.getName());
		String sex = "Î´Ñ¡Ôñ";
		if (user.getSex() == 1) {
			sex = "ÄÐ";
		} else if (user.getSex() == 2) {
			sex = "Å®";
		}
		tvSex.setText(sex);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		if (user.getBirthday() != null) {
			tvBirthday.setText(sdf.format(user.getBirthday()));
		}else{
			tvBirthday.setText("");
		}
		if (TextUtils.isEmpty(user.getPhone())) {
			tvPhone.setText("");
		} else {
			tvPhone.setText(user.getPhone());
		}
		tvEmail.setText(user.getEmail());
		tvSchool.setText(user.getSchool().getName());
		tvAccount.setText(user.getAccount());
		Util.loadImage(this, user.getFace_url(), roundAvatar);
	}

}
