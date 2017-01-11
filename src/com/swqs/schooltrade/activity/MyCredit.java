package com.swqs.schooltrade.activity;

import java.io.IOException;
import java.util.Random;

import org.json.JSONArray;

import com.swqs.schooltrade.R;
import com.swqs.schooltrade.app.TradeApplication;
import com.swqs.schooltrade.util.Server;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.Response;

public class MyCredit extends Activity {
	private ProgressBar pbCredit;
	private int mProgressStatus = 0;
	private Handler mHandler;
	private TextView tvLike;
	private TextView tvUnlike;
	private int likeGrade;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_credit);
		findViewById(R.id.ivBack).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});

		pbCredit = (ProgressBar) findViewById(R.id.pbCredit);
		mHandler = new Handler() {
			public void handleMessage(android.os.Message msg) {
				if (msg.what == 0x111) {
					pbCredit.setProgress(mProgressStatus);
				} else {
					Toast.makeText(MyCredit.this, "∫Ù¿≤¿≤∫Ù¿≤¿≤", Toast.LENGTH_SHORT).show();
				}
			}
		};
		tvLike=(TextView) findViewById(R.id.tvLike);
		tvUnlike=(TextView) findViewById(R.id.tvUnlike);
		getMyCredit();
	}

	private void getMyCredit() {
		FormBody formBody = new FormBody.Builder().add("uid", TradeApplication.uid).build();
		Request request = Server.requestBuilderWithApi("myCredit").post(formBody).build();

		Server.getSharedClient().newCall(request).enqueue(new Callback() {
			@Override
			public void onResponse(Call arg0, Response arg1) throws IOException {
				try {
					String jsonString = arg1.body().string();
					JSONArray obj = new JSONArray(jsonString);
					final int like = (Integer) obj.get(0);
					final int unlike = (Integer) obj.get(1);
					likeGrade=(int)((((double)like)/(like+unlike))*100);
					
					runOnUiThread(new Runnable() {
						public void run() {
							Toast.makeText(MyCredit.this, ""+likeGrade, 0).show();
							tvLike.setText(like+"");
							tvUnlike.setText(unlike+"");
							myThread.start();
						}
					});
				} catch (final Exception e) {
					runOnUiThread(new Runnable() {
						public void run() {
							Toast.makeText(MyCredit.this, e.getLocalizedMessage(), 1).show();
						}
					});
				}
			}

			@Override
			public void onFailure(Call arg0, final IOException e) {
				runOnUiThread(new Runnable() {
					public void run() {
						Toast.makeText(MyCredit.this, e.getLocalizedMessage(), 1).show();
					}
				});
			}
		});
	}

	private Thread myThread = new Thread(new Runnable() {

		@Override
		public void run() {
			while (true) {
				mProgressStatus = doWork();
				Message m = new Message();
				if (mProgressStatus < likeGrade) {
					m.what = 0x111;
					mHandler.sendMessage(m);
				} else {
					m.what = 0x111;
					mHandler.sendMessage(m);
					break;
				}
			}

		}

		private int doWork() {
			mProgressStatus += new Random().nextInt(1)+1;
			try {
				Thread.sleep(20);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return mProgressStatus;
		}
	});
}
