package com.swqs.schooltrade.activity;

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.swqs.schooltrade.R;
import com.swqs.schooltrade.entity.User;
import com.swqs.schooltrade.fragment.InputcellSimpletextFragment;
import com.swqs.schooltrade.util.MD5;
import com.swqs.schooltrade.util.Server;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class LoginActivity extends Activity {

	InputcellSimpletextFragment fragAccount, fragPassword;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		findViewById(R.id.btn_register).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				goRegister();
			}
		});

		findViewById(R.id.btn_login).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				goLogin();
			}
		});

		findViewById(R.id.btn_forgot_password).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				goRecoverPassword();
			}
		});

		fragAccount = (InputcellSimpletextFragment) getFragmentManager().findFragmentById(R.id.input_account);
		fragPassword = (InputcellSimpletextFragment) getFragmentManager().findFragmentById(R.id.input_password);
	}

	protected void onResume() {
		super.onResume();

		fragAccount.setLabelText("账号");
		fragAccount.setHintText("请输入用户名");
		fragPassword.setLabelText("密码");
		fragPassword.setHintText("请输入密码");
		fragPassword.setIsPassword(true);

	}

	void goRegister() {
		Intent itnt = new Intent(this, RegisterActivity.class);
		startActivity(itnt);
	}

	void goLogin() {
		String account = fragAccount.getText();
		String password = fragPassword.getText();

		if (account.length() == 0) {
			Toast.makeText(LoginActivity.this, "账号不能为空", Toast.LENGTH_SHORT).show();
			// new AlertDialog.Builder(LoginActivity.this).setMessage("账号不能为空")
			// .setIcon(android.R.drawable.ic_dialog_alert).setNegativeButton("好",
			// null).show();
			//
			// return;
		}

		else if (password.length() == 0) {
			Toast.makeText(LoginActivity.this, "密码不能为空", Toast.LENGTH_SHORT).show();
			// new AlertDialog.Builder(LoginActivity.this).setMessage("密码不能为空")
			// .setIcon(android.R.drawable.ic_dialog_alert).setNegativeButton("好",
			// null).show();
			//
			// return;
		} else {

			OkHttpClient client = Server.getSharedClient();

			MultipartBody requestBody = new MultipartBody.Builder().addFormDataPart("account", fragAccount.getText())
					.addFormDataPart("password", MD5.getMD5(fragPassword.getText())).build();

			Request request = Server.requestBuilderWithApi("login").method("post", null).post(requestBody).build();

			final ProgressDialog dlg = new ProgressDialog(this);
			dlg.setCancelable(false);
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
								dlg.dismiss();
								if (user.getAccount().equals("accountIsNotExist")) {
									Toast.makeText(LoginActivity.this, "账号不存在", Toast.LENGTH_SHORT).show();
									return;
								}
								else if (user.getAccount().equals("passwordIsNotRight")) {
									Toast.makeText(LoginActivity.this, "密码错误", Toast.LENGTH_SHORT).show();
									return;
								}
								Toast.makeText(LoginActivity.this, "登陆成功", Toast.LENGTH_SHORT).show();
								Intent itnt = new Intent(LoginActivity.this, HomeActivity.class);
								startActivity(itnt);
								finish();
								
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
}
