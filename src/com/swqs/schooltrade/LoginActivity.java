package com.swqs.schooltrade;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import inputcell.InputcellSimpletextFragment;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import com.swqs.schooltrade.api.Server;
import com.swqs.schooltrade.api.entity.User;

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.swqs.schooltrade.R;

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

		fragAccount.setLabelText("’À∫≈");
		fragAccount.setHintText("«Î ‰»Î”√ªß√˚");
		fragPassword.setLabelText("√‹¬Î");
		fragPassword.setHintText("«Î ‰»Î√‹¬Î");
		fragPassword.setIsPassword(true);

	}

	void goRegister() {
		Intent itnt = new Intent(this, RegisterActivity.class);
		startActivity(itnt);
	}

	void goLogin() {
		OkHttpClient client = Server.getSharedClient();

		MultipartBody requestBody = new MultipartBody.Builder().addFormDataPart("account", fragAccount.getText())
				.addFormDataPart("password", MD5.getMD5(fragPassword.getText())).build();

		Request request = Server.requestBuilderWithApi("login").method("get", null).post(requestBody).build();

		final ProgressDialog dlg = new ProgressDialog(this);
		dlg.setCancelable(false);
		dlg.setCanceledOnTouchOutside(false);
		dlg.setMessage("loading");
		dlg.show();

		client.newCall(request).enqueue(new Callback() {

			@Override
			public void onResponse(Call arg0, Response arg1) throws IOException {
				final String responseSrting = arg1.body().string();
				ObjectMapper mapper = new ObjectMapper();
				final User user = mapper.readValue(responseSrting, User.class);
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						dlg.dismiss();
						new AlertDialog.Builder(LoginActivity.this).setMessage("seccusse," + user.getName())
								.setPositiveButton("OK", new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog, int which) {
										Intent itnt = new Intent(LoginActivity.this, HomeActivity.class);
										startActivity(itnt);
									}
								}).show();
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

	void goRecoverPassword() {
		Intent itnt = new Intent(this, PasswordRecoverActivity.class);
		startActivity(itnt);
	}
}
