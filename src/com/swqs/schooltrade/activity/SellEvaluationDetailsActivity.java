package com.swqs.schooltrade.activity;

import java.io.IOException;
import java.text.SimpleDateFormat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.swqs.schooltrade.R;
import com.swqs.schooltrade.entity.Judgement;
import com.swqs.schooltrade.util.RoundImageView;
import com.swqs.schooltrade.util.Server;
import com.swqs.schooltrade.util.Util;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SellEvaluationDetailsActivity extends Activity {

	private ImageView ivBack;
	private RoundImageView roundImageAvatar;
	private TextView tvBuyerName;
	private TextView tvEvaluation;
	private TextView tvEvaluateTime;
	private ImageView ivLike;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_evaluation_details);

		ivBack = (ImageView) findViewById(R.id.ivBack);
		roundImageAvatar = (RoundImageView) findViewById(R.id.roundImageAvatar);
		tvBuyerName = (TextView) findViewById(R.id.tvBuyerName);
		tvEvaluation = (TextView) findViewById(R.id.tvEvaluation);
		tvEvaluateTime = (TextView) findViewById(R.id.tvEvaluateTime);
		ivLike = (ImageView) findViewById(R.id.ivLike);
		ivBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
		getEvaluation();
		getLike();
	}

	private void getLike() {
		int goodsId = getIntent().getIntExtra("goods_id", 0);
		int buyerId = getIntent().getIntExtra("buyer_id", 0);
		OkHttpClient client = Server.getSharedClient();
		FormBody body = new FormBody.Builder().add("buyerId", buyerId + "").add("goodsId", goodsId + "").build();
		Request request = Server.requestBuilderWithApi("getGoodsLike").post(body).build();
		client.newCall(request).enqueue(new Callback() {

			@Override
			public void onResponse(Call arg0, Response arg1) throws IOException {
				final String responseSrting = arg1.body().string();
				try {
					final boolean isLike = Boolean.parseBoolean(responseSrting);
					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							if(isLike){
								ivLike.setBackgroundResource(R.drawable.like_press);
							}else{
								ivLike.setBackgroundResource(R.drawable.unlike_press);
							}
						}

					});
				} catch (final Exception e) {
					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							Toast.makeText(SellEvaluationDetailsActivity.this, e.getLocalizedMessage(),
									Toast.LENGTH_SHORT).show();
						}
					});

				}

			}

			@Override
			public void onFailure(Call arg0, final IOException arg1) {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						Toast.makeText(SellEvaluationDetailsActivity.this, arg1.getLocalizedMessage(),
								Toast.LENGTH_SHORT).show();
					}
				});
			}
		});
	}

	private void getEvaluation() {
		int goodsId = getIntent().getIntExtra("goods_id", 0);
		OkHttpClient client = Server.getSharedClient();
		Request request = Server.requestBuilderWithApi("goods/" + goodsId + "/judgement").build();
		client.newCall(request).enqueue(new Callback() {

			@Override
			public void onResponse(Call arg0, Response arg1) throws IOException {
				final String responseSrting = arg1.body().string();
				ObjectMapper mapper = new ObjectMapper();
				try {
					final Judgement judgement = mapper.readValue(responseSrting, Judgement.class);
					if (judgement != null) {
						runOnUiThread(new Runnable() {

							@Override
							public void run() {
								setEvaluationData(judgement);
							}

						});
					}
				} catch (final Exception e) {
					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							Toast.makeText(SellEvaluationDetailsActivity.this, e.getLocalizedMessage(),
									Toast.LENGTH_SHORT).show();
						}
					});

				}

			}

			@Override
			public void onFailure(Call arg0, final IOException arg1) {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						Toast.makeText(SellEvaluationDetailsActivity.this, arg1.getLocalizedMessage(),
								Toast.LENGTH_SHORT).show();
					}
				});
			}
		});
	}

	private void setEvaluationData(Judgement judgement) {
		Util.loadImage(this, judgement.getJudgeAcc().getFace_url(), roundImageAvatar);
		tvBuyerName.setText(judgement.getJudgeAcc().getName());
		tvEvaluation.setText(judgement.getText());
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm");
		tvEvaluateTime.setText(sdf.format(judgement.getCreateDate()));
	}

}