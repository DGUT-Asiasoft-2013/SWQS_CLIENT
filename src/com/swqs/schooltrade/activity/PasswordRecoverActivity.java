package com.swqs.schooltrade.activity;

import java.io.IOException;

import com.swqs.schooltrade.R;
import com.swqs.schooltrade.fragment.GetEmailCodeFragment;
import com.swqs.schooltrade.fragment.ResetPwdFragment;
import com.swqs.schooltrade.util.CustomProgressDialog;
import com.swqs.schooltrade.util.MD5;
import com.swqs.schooltrade.util.Server;
import com.swqs.schooltrade.util.Util;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class PasswordRecoverActivity extends Activity {

	private GetEmailCodeFragment fragmentEmailCode;
	private CustomProgressDialog progressDialog;
	private CustomProgressDialog alertDialog;
	private String email;
	ResetPwdFragment resetPwdFragment = new ResetPwdFragment();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_password_recover);
		fragmentEmailCode = new GetEmailCodeFragment();
		fragmentEmailCode.setListener(new GetEmailCodeFragment.OnClickListener() {

			@Override
			public void onSendCodeClick() {
				getCode();
			}

			@Override
			public void onNextClick() {
				goResetPwdFragment();
			}

			@Override
			public void onBackClick() {
				finish();
			}
		});
		resetPwdFragment.setListener(new ResetPwdFragment.OnClickListener() {

			@Override
			public void onSubmitClick() {
				goSubmit();
			}

			@Override
			public void onBackClick() {
				finish();
			}
		});

		getFragmentManager().beginTransaction().replace(R.id.container, fragmentEmailCode).commit();
	}

	private void goResetPwdFragment() {
		showProgress(R.layout.custom_progressdialog);
		// ���������Ч
		// TODO
		if (!Util.checkEmail(fragmentEmailCode.getEmail())) {
			progressDialog.dismiss();
			showAlertDialog(R.layout.custom_alertdialog);
			return;
		}
		MultipartBody requestBodyNext = new MultipartBody.Builder().addFormDataPart("code", fragmentEmailCode.getCode())
				.build();
		Request requestNext = Server.requestBuilderWithApi("checkcode").method("post", null).post(requestBodyNext)
				.build();
		Server.getSharedClient().newCall(requestNext).enqueue(new Callback() {
			@Override
			public void onResponse(Call arg0, Response arg1) throws IOException {
				final String responseSrting = arg1.body().string();
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						try {
							progressDialog.dismiss();
							boolean flag = Boolean.parseBoolean(responseSrting);
							if (!flag) {
								Toast.makeText(PasswordRecoverActivity.this, "��֤�����", Toast.LENGTH_SHORT).show();
								return;
							}
							goStep2();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}

				});

			}

			@Override
			public void onFailure(Call arg0, final IOException arg1) {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						progressDialog.dismiss();
						Toast.makeText(PasswordRecoverActivity.this, arg1.getLocalizedMessage(), Toast.LENGTH_LONG)
								.show();
					}
				});
			}
		});
	}

	private void getCode() {
		showProgress(R.layout.custom_progressdialog);
		// ���������Ч
		if (!Util.checkEmail(fragmentEmailCode.getEmail())) {
			progressDialog.dismiss();
			showAlertDialog(R.layout.custom_alertdialog);
			return;
		}
		email = fragmentEmailCode.getEmail();
		OkHttpClient client = Server.getSharedClient();
		MultipartBody requestBody = new MultipartBody.Builder().addFormDataPart("email", email).build();
		Request request = Server.requestBuilderWithApi("passwordCheckEmail").method("post", null).post(requestBody)
				.build();
		client.newCall(request).enqueue(new Callback() {
			@Override
			public void onResponse(Call arg0, Response arg1) throws IOException {
				final String responseSrting = arg1.body().string();
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						try {
							progressDialog.dismiss();
							boolean flag = Boolean.parseBoolean(responseSrting);
							if (!flag) {
								Toast.makeText(PasswordRecoverActivity.this, "�����仹ûע��", Toast.LENGTH_SHORT).show();
								return;
							}
							Toast.makeText(PasswordRecoverActivity.this, "�ѷ�����֤�뵽������", Toast.LENGTH_SHORT).show();
							fragmentEmailCode.setLeftTime();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}

				});

			}

			@Override
			public void onFailure(Call arg0, final IOException arg1) {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						progressDialog.dismiss();
						Toast.makeText(PasswordRecoverActivity.this, arg1.getLocalizedMessage(), Toast.LENGTH_LONG)
								.show();
					}
				});
			}
		});
	}

	public void showProgress(int layoutResID) {
		if (progressDialog != null) {
			progressDialog.cancel();
		}
		progressDialog = new CustomProgressDialog(this, layoutResID);
		/*
		 * ���Ի���Ĵ�С����Ļ��С�İٷֱ�����
		 */
		Window dialogWindow = progressDialog.getWindow();
		WindowManager m = getWindowManager();
		Display d = m.getDefaultDisplay(); // ��ȡ��Ļ������
		WindowManager.LayoutParams p = dialogWindow.getAttributes(); // ��ȡ�Ի���ǰ�Ĳ���ֵ
		p.width = (int) (d.getWidth() * 0.9); // �������Ϊ��Ļ��0.9
		dialogWindow.setAttributes(p);
		progressDialog.show();
	}

	private void showAlertDialog(int layoutResID) {
		if (alertDialog != null) {
			alertDialog.show();
		} else {
			alertDialog = new CustomProgressDialog(this, layoutResID);
			alertDialog.findViewById(R.id.btn_know).setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					alertDialog.cancel();
				}
			});
			Window dialogWindow = alertDialog.getWindow();
			WindowManager m = getWindowManager();
			Display d = m.getDefaultDisplay(); // ��ȡ��Ļ������
			WindowManager.LayoutParams p = dialogWindow.getAttributes(); // ��ȡ�Ի���ǰ�Ĳ���ֵ
			p.width = (int) (d.getWidth() * 0.9); // �������Ϊ��Ļ��0.9
			dialogWindow.setAttributes(p);
			alertDialog.show();
		}
	}

	private void goStep2() {

		getFragmentManager()
				.beginTransaction().setCustomAnimations(R.animator.slide_in_right, R.animator.slide_out_left,
						R.animator.slide_in_left, R.animator.slide_out_right)
				.replace(R.id.container, resetPwdFragment).commit();
	}

	private void goSubmit() {
		showProgress(R.layout.custom_progressdialog);
		String newPwd = resetPwdFragment.getPwd();
		String newPwdConfirm = resetPwdFragment.getPwdConfirm();
		if (TextUtils.isEmpty(newPwd)) {
			progressDialog.dismiss();
			Toast.makeText(this, "���벻��Ϊ��", Toast.LENGTH_SHORT).show();
			return;
		}
		if (!newPwd.equals(newPwdConfirm)) {
			progressDialog.dismiss();
			Toast.makeText(this, "������������벻һ��", Toast.LENGTH_SHORT).show();
			return;
		}
		if (newPwd.length() < 6 || newPwd.length() > 16) {
			progressDialog.dismiss();
			Toast.makeText(this, "���볤����6��16֮��", Toast.LENGTH_SHORT).show();
			return;
		}
		OkHttpClient client = Server.getSharedClient();
		MultipartBody body = new MultipartBody.Builder().addFormDataPart("email", email)
				.addFormDataPart("password", MD5.getMD5(newPwd)).build();

		Request request = Server.requestBuilderWithApi("updatePwd").post(body).build();

		client.newCall(request).enqueue(new Callback() {

			@Override
			public void onResponse(Call arg0, Response arg1) throws IOException {
				final String responseString = arg1.body().string();
				progressDialog.dismiss();
				try {
					final int flag=Integer.parseInt(responseString);
					runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							if(flag==1){
								Toast.makeText(PasswordRecoverActivity.this, "�һ�����ɹ�", Toast.LENGTH_SHORT)
								.show();
								finish();
							}else{
								Toast.makeText(PasswordRecoverActivity.this, "�һ�����ʧ��", Toast.LENGTH_SHORT)
								.show();
							}
						}
					});
				} catch (Exception e) {
					runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							Toast.makeText(PasswordRecoverActivity.this, "�����쳣", Toast.LENGTH_SHORT)
							.show();
						}
					});
				}
			}

			@Override
			public void onFailure(Call arg0, final IOException arg1) {
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						Toast.makeText(PasswordRecoverActivity.this, arg1.getLocalizedMessage(), Toast.LENGTH_SHORT)
								.show();
					}
				});
			}
		});
	}
}
