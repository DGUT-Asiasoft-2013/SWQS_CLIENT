package com.swqs.schooltrade.util;

import java.io.IOException;

import com.swqs.schooltrade.R;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
					final Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
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
}
