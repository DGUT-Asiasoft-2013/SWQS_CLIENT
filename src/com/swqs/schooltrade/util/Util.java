package com.swqs.schooltrade.util;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.swqs.schooltrade.R;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.Display;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Util {

	public static void loadImage(final Activity activity, String url, final ImageView image) {
		OkHttpClient client = Server.getSharedClient();

		Request request = new Request.Builder().url(Server.serverAddress+url).method("get", null).build();

		client.newCall(request).enqueue(new Callback() {

			@Override
			public void onResponse(Call arg0, Response arg1) throws IOException {
				try {
					byte[] bytes = arg1.body().bytes();
					final Bitmap bmp = FileUtils.revitionImageSize(bytes);
					activity.runOnUiThread(new Runnable() {

						@Override
						public void run() {
							image.setImageBitmap(bmp);
						}
					});
				} catch (Exception ex) {
					activity.runOnUiThread(new Runnable() {

						@Override
						public void run() {
							image.setImageResource(R.drawable.ic_launcher);
						}
					});
				}
			}

			@Override
			public void onFailure(Call arg0, IOException arg1) {
				activity.runOnUiThread(new Runnable() {

					@Override
					public void run() {
						image.setImageResource(R.drawable.ic_launcher);
					}
				});
			}
		});
	}
	/**
	 * 验证邮箱
	 * 
	 * @param email
	 * @return
	 */
	public static boolean checkEmail(String email) {
		boolean flag = false;
		try {
			String check = "^([a-z0-9A-Z]+[-|_|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
			Pattern regex = Pattern.compile(check);
			Matcher matcher = regex.matcher(email);
			flag = matcher.matches();
		} catch (Exception e) {
			flag = false;
		}
		return flag;
	}

	/**
	 * 验证手机号码
	 * 
	 * @param mobiles
	 * @return
	 */
	public static boolean checkMobileNumber(String mobileNumber) {
		boolean flag = false;
		try {
			Pattern regex = Pattern
					.compile("^(((13[0-9])|(15([0-3]|[5-9]))|(18[0,5-9]))\\d{8})|(0\\d{2}-\\d{8})|(0\\d{3}-\\d{7})$");
			Matcher matcher = regex.matcher(mobileNumber);
			flag = matcher.matches();
		} catch (Exception e) {
			flag = false;
		}
		return flag;
	}
	
	public static CustomProgressDialog getProgressDialog(Activity act,int layoutResID) {
		CustomProgressDialog progressDialog;
		progressDialog = new CustomProgressDialog(act, layoutResID);
		progressDialog.setCanceledOnTouchOutside(false);
		/*
		 * 将对话框的大小按屏幕大小的百分比设置
		 */
		Window dialogWindow = progressDialog.getWindow();
		WindowManager m = act.getWindowManager();
		Display d = m.getDefaultDisplay(); // 获取屏幕宽、高用
		WindowManager.LayoutParams p = dialogWindow.getAttributes(); // 获取对话框当前的参数值
		p.width = (int) (d.getWidth() * 0.9); // 宽度设置为屏幕的0.9
		dialogWindow.setAttributes(p);
		return progressDialog;
	}
}
