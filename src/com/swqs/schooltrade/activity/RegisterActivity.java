package com.swqs.schooltrade.activity;

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.swqs.schooltrade.R;
import com.swqs.schooltrade.entity.User;
import com.swqs.schooltrade.fragment.GetEmailCodeFragment;
import com.swqs.schooltrade.fragment.SetAccountPwdFragment;
import com.swqs.schooltrade.util.CustomProgressDialog;
import com.swqs.schooltrade.util.MD5;
import com.swqs.schooltrade.util.Server;
import com.swqs.schooltrade.util.Util;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;
import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.api.BasicCallback;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class RegisterActivity extends Activity{
	protected static final String TAG = "RegisterActivity";
	private GetEmailCodeFragment fragmentEmailCode;
	private SetAccountPwdFragment fragmentAccountPwd;
	private CustomProgressDialog progressDialog;
	private CustomProgressDialog alertDialog;
	private String email;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);

		fragmentEmailCode = new GetEmailCodeFragment();
		fragmentAccountPwd = new SetAccountPwdFragment();
		getFragmentManager().beginTransaction()
				.replace(R.id.container, fragmentEmailCode).commit();
		fragmentEmailCode.setListener(new GetEmailCodeFragment.OnClickListener() {
			
			@Override
			public void onSendCodeClick() {
				getCode();
			}
			
			@Override
			public void onNextClick() {
				goRegistFragment();
			}
			
			@Override
			public void onBackClick() {
				finish();
			}
		});
		fragmentAccountPwd.setListener(new SetAccountPwdFragment.OnClickListener() {
			
			@Override
			public void onRegistClick() {
				goRegist();
			}
			
			@Override
			public void onBackClick() {
				finish();
			}
		});
	}
	
	private void goRegist(){
		showProgress(R.layout.custom_progressdialog);
		String account=fragmentAccountPwd.getAccount();
		String pwd=fragmentAccountPwd.getPwd();
		String pwdConfirm=fragmentAccountPwd.getPwdConfirm();
		if(TextUtils.isEmpty(account)){
			progressDialog.dismiss();
			Toast.makeText(this, "请输入账号", Toast.LENGTH_SHORT).show();
			return;
		}
		if(TextUtils.isEmpty(pwd)){
			progressDialog.dismiss();
			Toast.makeText(this, "请输入密码", Toast.LENGTH_SHORT).show();
			return;
		}
		if(!pwd.equals(pwdConfirm)){
			progressDialog.dismiss();
			Toast.makeText(this, "两次输入的密码不一致", Toast.LENGTH_SHORT).show();
			return;
		}
		if(account.length()<4){
			progressDialog.dismiss();
			Toast.makeText(this, "账号长度至少4位", Toast.LENGTH_SHORT).show();
			return;
		}
		if(pwd.length()<6||pwd.length()>16){
			progressDialog.dismiss();
			Toast.makeText(this, "密码长度在6到16之间", Toast.LENGTH_SHORT).show();
			return;
		}
		
		OkHttpClient client = Server.getSharedClient();
		// 创建okHttpClint对象
		MultipartBody.Builder requestBodyBuilder = new MultipartBody.Builder()
				.addFormDataPart("account", account).addFormDataPart("password", MD5.getMD5(pwd))
				.addFormDataPart("schoolId", fragmentAccountPwd.getSelectedSchoolId() + "")
				.addFormDataPart("email", email);

		Request request = Server.requestBuilderWithApi("register").method("get", null).post(requestBodyBuilder.build())
				.build();

		client.newCall(request).enqueue(new Callback() {
			@Override
			public void onResponse(Call arg0, Response arg1) throws IOException {
				final String jsonString = arg1.body().string();
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						ObjectMapper mapper = new ObjectMapper();
						try {
							final User user = mapper.readValue(jsonString, User.class);
							if (user.getAccount().equals("accountExist")) {
								progressDialog.dismiss();
								Toast.makeText(RegisterActivity.this, "账号已被注册", Toast.LENGTH_SHORT).show();
							} else {
								registToJpush();
							} 
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
			}

			@Override
			public void onFailure(Call arg0, IOException arg1) {

			}
		});
	}

	public void registToJpush() {
		final String account=fragmentAccountPwd.getAccount();
		final String password=fragmentAccountPwd.getPwd();
		/**=================     调用SDK注册接口    =================*/
        JMessageClient.register(account, password, new BasicCallback() {
            @Override
            public void gotResult(int responseCode, String registerDesc) {
                if (responseCode == 0) {
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "注册成功", Toast.LENGTH_SHORT).show();
                    finish();
                    Log.i("RegisterActivity", "JMessageClient.register " + ", responseCode = " + responseCode + " ; registerDesc = " + registerDesc);
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "注册失败", Toast.LENGTH_SHORT).show();
                    Log.i("RegisterActivity", "JMessageClient.register " + ", responseCode = " + responseCode + " ; registerDesc = " + registerDesc);
                }
            }
        });
	}
	private void goRegistFragment(){
		showProgress(R.layout.custom_progressdialog);
		// 如果邮箱无效
		// TODO
		if (!Util.checkEmail(fragmentEmailCode.getEmail())) {
			progressDialog.dismiss();
			showAlertDialog(R.layout.custom_alertdialog);
			return;
		}
		MultipartBody requestBodyNext = new MultipartBody.Builder()
				.addFormDataPart("code", fragmentEmailCode.getCode())
				.build();
		Request requestNext = Server
				.requestBuilderWithApi("checkcode")
				.method("post", null).post(requestBodyNext).build();
		Server.getSharedClient().newCall(requestNext)
				.enqueue(new Callback() {
					@Override
					public void onResponse(Call arg0, Response arg1)
							throws IOException {
						final String responseSrting = arg1.body().string();
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								try {
									progressDialog.dismiss();
									boolean flag = Boolean
											.parseBoolean(responseSrting);
									if (!flag) {
										Toast.makeText(RegisterActivity.this,
												"验证码错误", Toast.LENGTH_SHORT)
												.show();
										return;
									}
									getFragmentManager()
											.beginTransaction()
											.replace(R.id.container,
													fragmentAccountPwd)
											.commit();
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
								Toast.makeText(RegisterActivity.this,
										arg1.getLocalizedMessage(),
										Toast.LENGTH_LONG).show();
							}
						});
					}
				});
	}
	private void getCode(){
		showProgress(R.layout.custom_progressdialog);
		// 如果邮箱无效
		// TODO
		if (!Util.checkEmail(fragmentEmailCode.getEmail())) {
			progressDialog.dismiss();
			showAlertDialog(R.layout.custom_alertdialog);
			return;
		}
		email=fragmentEmailCode.getEmail();
		OkHttpClient client = Server.getSharedClient();
		MultipartBody requestBody = new MultipartBody.Builder()
				.addFormDataPart("email", email)
				.build();
		Request request = Server.requestBuilderWithApi("registerCehckEmail")
				.method("post", null).post(requestBody).build();
		client.newCall(request).enqueue(new Callback() {
			@Override
			public void onResponse(Call arg0, Response arg1)
					throws IOException {
				final String responseSrting = arg1.body().string();
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						try {
							progressDialog.dismiss();
							boolean flag = Boolean
									.parseBoolean(responseSrting);
							if (!flag) {
								Toast.makeText(RegisterActivity.this,
										"该邮箱已被注册", Toast.LENGTH_SHORT)
										.show();
								return;
							}
							Toast.makeText(RegisterActivity.this, "已发送验证码到该邮箱",
									Toast.LENGTH_SHORT).show();
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
						Toast.makeText(RegisterActivity.this,
								arg1.getLocalizedMessage(),
								Toast.LENGTH_LONG).show();
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
		 * 将对话框的大小按屏幕大小的百分比设置
		 */
		Window dialogWindow = progressDialog.getWindow();
		WindowManager m = getWindowManager();
		Display d = m.getDefaultDisplay(); // 获取屏幕宽、高用
		WindowManager.LayoutParams p = dialogWindow.getAttributes(); // 获取对话框当前的参数值
		p.width = (int) (d.getWidth() * 0.9); // 宽度设置为屏幕的0.9
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
			Display d = m.getDefaultDisplay(); // 获取屏幕宽、高用
			WindowManager.LayoutParams p = dialogWindow.getAttributes(); // 获取对话框当前的参数值
			p.width = (int) (d.getWidth() * 0.9); // 宽度设置为屏幕的0.9
			dialogWindow.setAttributes(p);
			alertDialog.show();
		}
	}
}
