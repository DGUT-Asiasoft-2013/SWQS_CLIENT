package com.swqs.schooltrade.activity;

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.swqs.schooltrade.R;
import com.swqs.schooltrade.app.TradeApplication;
import com.swqs.schooltrade.entity.User;
import com.swqs.schooltrade.util.MD5;
import com.swqs.schooltrade.util.Server;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.model.UserInfo;
import cn.jpush.im.api.BasicCallback;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

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
			@Override
			public void run() {
				SharedPreferences sp = getSharedPreferences(TradeApplication.SCHOOLTRADE_CONFIGS, Context.MODE_PRIVATE);
				boolean isAutoLogin = sp.getBoolean(TradeApplication.IS_AUTO_LOGIN, false);
				if (isAutoLogin) {
					final String account = sp.getString(TradeApplication.ACCOUNT, "");
					final String password = sp.getString(TradeApplication.PASSWORD, "");
					OkHttpClient client = Server.getSharedClient();
					MultipartBody requestBody = new MultipartBody.Builder().addFormDataPart("accountOrEmail", account)
							.addFormDataPart("password", MD5.getMD5(password)).build();
					Request request = Server.requestBuilderWithApi("login").method("post", null).post(requestBody)
							.build();
					client.newCall(request).enqueue(new Callback() {

						@Override
						public void onResponse(Call arg0, Response arg1) throws IOException {
							final String responseSrting = arg1.body().string();
							final ObjectMapper mapper = new ObjectMapper();

							runOnUiThread(new Runnable() {

								@Override
								public void run() {
									try {
										final User user = mapper.readValue(responseSrting, User.class);
										if (user.getAccount().equals("userIsNotExist")) {
											startLoginActivity();
											return;
										} else if (user.getAccount().equals("passwordIsNotRight")) {
											startLoginActivity();
											return;
										}
										TradeApplication.uid=user.getId()+"";
										loginToJpush(user.getAccount(), password);

									} catch (Exception e) {
										startLoginActivity();
									}
								}

							});

						}

						@Override
						public void onFailure(Call arg0, final IOException arg1) {
							runOnUiThread(new Runnable() {
								@Override
								public void run() {
									startLoginActivity();
								}
							});
						}
					});
				} else {
					startLoginActivity();
				}
			}
		}, 1000);

	}

	public void loginToJpush(final String account, final String password) {
		/** ================= 获取个人信息不是null则直接进入type界面 ================= */
		UserInfo myInfo = JMessageClient.getMyInfo();
		if (myInfo != null) {
			Intent intent = new Intent(this, HomeActivity.class);
			startActivity(intent);
			finish();
		} else {
			/** ================= 调用SDk登陆接口 ================= */
			JMessageClient.login(account, password, new BasicCallback() {
				@Override
				public void gotResult(int responseCode, String LoginDesc) {
					if (responseCode == 0) {
						Intent intent = new Intent();
						intent.setClass(getApplicationContext(), HomeActivity.class);
						startActivity(intent);
						finish();
					} else {
						startLoginActivity();
					}
				}
			});
		}
	}

	void startLoginActivity() {
		Intent intent = new Intent(this, LoginActivity.class);
		startActivity(intent);
		finish();
	}
}
