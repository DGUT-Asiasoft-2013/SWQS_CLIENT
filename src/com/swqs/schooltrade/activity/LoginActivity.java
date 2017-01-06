package com.swqs.schooltrade.activity;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.api.BasicCallback;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.swqs.schooltrade.R;
import com.swqs.schooltrade.app.TradeApplication;
import com.swqs.schooltrade.entity.User;
import com.swqs.schooltrade.util.MD5;
import com.swqs.schooltrade.util.Server;

public class LoginActivity extends Activity {

	private EditText etAccount;
	private EditText etPwd;
	private ProgressDialog dlg;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		findViewById(R.id.tvRegister).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				goRegister();
			}
		});

		findViewById(R.id.btnLogin).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				goLogin();
			}
		});

		findViewById(R.id.tvForgetPwd).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				goRecoverPassword();
			}
		});

		etAccount = (EditText) findViewById(R.id.etAccount);
		etPwd = (EditText) findViewById(R.id.etPwd);
		initAccountAndPwd();
	}

	private void initAccountAndPwd() {
		SharedPreferences sp = getSharedPreferences(TradeApplication.SCHOOLTRADE_CONFIGS, Context.MODE_PRIVATE);
		final String account = sp.getString(TradeApplication.ACCOUNT, "");
		final String password = sp.getString(TradeApplication.PASSWORD, "");
		etAccount.setText(account);
		etPwd.setText(password);
	}

	void goRegister() {
		Intent itnt = new Intent(this, RegisterActivity.class);
		startActivity(itnt);
	}

	void goLogin() {
		String account = etAccount.getText().toString().trim();
		String password = etPwd.getText().toString();

		if (account.length() == 0) {
			Toast.makeText(LoginActivity.this, "账号不能为空", Toast.LENGTH_SHORT).show();
		}

		else if (password.length() == 0) {
			Toast.makeText(LoginActivity.this, "密码不能为空", Toast.LENGTH_SHORT).show();
		} else {

			OkHttpClient client = Server.getSharedClient();

			MultipartBody requestBody = new MultipartBody.Builder().addFormDataPart("accountOrEmail", account)
					.addFormDataPart("password", MD5.getMD5(password)).build();

			Request request = Server.requestBuilderWithApi("login").method("post", null).post(requestBody).build();

			dlg = new ProgressDialog(this);
			dlg.setCanceledOnTouchOutside(false);
			dlg.setMessage("loading");
			dlg.show();

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
									dlg.dismiss();
									Toast.makeText(LoginActivity.this, "用户不存在", Toast.LENGTH_SHORT).show();
									return;
								} else if (user.getAccount().equals("passwordIsNotRight")) {
									dlg.dismiss();
									Toast.makeText(LoginActivity.this, "密码错误", Toast.LENGTH_SHORT).show();
									return;
								}
								loginToJpush(user.getAccount());

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
							Toast.makeText(LoginActivity.this, arg1.getLocalizedMessage(), Toast.LENGTH_LONG).show();
						}
					});
				}
			});
		}
	}

	void goRecoverPassword() {
		Intent itnt = new Intent(this, PasswordRecoverActivity.class);
		startActivity(itnt);
	}

	public void loginToJpush(final String account) {
		final String password = etPwd.getText().toString();
		/** ================= 调用SDk登陆接口 ================= */
		JMessageClient.login(account, password, new BasicCallback() {
			@Override
			public void gotResult(int responseCode, String LoginDesc) {
				if (responseCode == 0) {
					dlg.dismiss();
					Toast.makeText(getApplicationContext(), "登录成功", Toast.LENGTH_SHORT).show();
					Log.i("MainActivity", "JMessageClient.login" + ", responseCode = " + responseCode
							+ " ; LoginDesc = " + LoginDesc);
					// 保存用户名和密码
					Editor editor = getSharedPreferences(TradeApplication.SCHOOLTRADE_CONFIGS, Context.MODE_PRIVATE)
							.edit();
					editor.putString(TradeApplication.ACCOUNT, account);
					editor.putString(TradeApplication.PASSWORD, password);
					editor.putBoolean(TradeApplication.IS_AUTO_LOGIN, true);
					editor.commit();
					Intent intent = new Intent();
					intent.setClass(getApplicationContext(), HomeActivity.class);
					startActivity(intent);
					finish();
				} else {
					dlg.dismiss();
					Toast.makeText(getApplicationContext(), "登录失败", Toast.LENGTH_SHORT).show();
					Log.i("MainActivity", "JMessageClient.login" + ", responseCode = " + responseCode
							+ " ; LoginDesc = " + LoginDesc);
				}
			}
		});
	}
}
