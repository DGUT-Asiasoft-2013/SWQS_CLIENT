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
	 * ��֤����
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
	 * ��֤�ֻ�����
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
		 * ���Ի���Ĵ�С����Ļ��С�İٷֱ�����
		 */
		Window dialogWindow = progressDialog.getWindow();
		WindowManager m = act.getWindowManager();
		Display d = m.getDefaultDisplay(); // ��ȡ��Ļ������
		WindowManager.LayoutParams p = dialogWindow.getAttributes(); // ��ȡ�Ի���ǰ�Ĳ���ֵ
		p.width = (int) (d.getWidth() * 0.9); // �������Ϊ��Ļ��0.9
		dialogWindow.setAttributes(p);
		return progressDialog;
	}
}
