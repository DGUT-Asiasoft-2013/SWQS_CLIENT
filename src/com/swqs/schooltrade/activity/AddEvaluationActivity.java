package com.swqs.schooltrade.activity;

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.swqs.schooltrade.R;
import com.swqs.schooltrade.entity.Identify;
import com.swqs.schooltrade.entity.Judgement;
import com.swqs.schooltrade.util.CustomProgressDialog;
import com.swqs.schooltrade.util.Server;
import com.swqs.schooltrade.util.Util;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class AddEvaluationActivity extends Activity implements OnClickListener {

	private ImageView ivBack;
	private Button btnLike;
	private Button btnUnlike;
	private EditText etEvaluation;
	private Button btnSubmit;
	private Identify identify;
	private CustomProgressDialog progressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_evaluation);
		identify = (Identify) getIntent().getSerializableExtra("identify");
		ivBack = (ImageView) findViewById(R.id.ivBack);
		btnLike = (Button) findViewById(R.id.btnLike);
		btnUnlike = (Button) findViewById(R.id.btnUnlike);
		etEvaluation = (EditText) findViewById(R.id.etEvaluation);
		btnSubmit = (Button) findViewById(R.id.btnSubmit);
		ivBack.setOnClickListener(this);
		btnLike.setOnClickListener(this);
		btnUnlike.setOnClickListener(this);
		btnSubmit.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ivBack:
			finish();
			break;
		case R.id.btnLike:
			btnLike.setSelected(true);
			btnUnlike.setSelected(false);
			break;
		case R.id.btnUnlike:
			btnLike.setSelected(false);
			btnUnlike.setSelected(true);
			break;
		case R.id.btnSubmit:
			onSubmit();
			break;
		}
	}

	private void onSubmit() {
		if (!btnLike.isSelected() && !btnUnlike.isSelected()) {
			Toast.makeText(this, "请选择好评还是差评", Toast.LENGTH_SHORT).show();
			return;
		}
		String content = etEvaluation.getText().toString();
		if (TextUtils.isEmpty(content)) {
			Toast.makeText(this, "请输入评价内容", Toast.LENGTH_SHORT).show();
			return;
		}
		progressDialog = Util.getProgressDialog(this, R.layout.custom_progressdialog);
		progressDialog.show();
		OkHttpClient client = Server.getSharedClient();
		MultipartBody requestBody = new MultipartBody.Builder().addFormDataPart("identifyId", identify.getId() + "")
				.addFormDataPart("text", content).addFormDataPart("isLike", btnLike.isSelected() + "").build();
		Request request = Server.requestBuilderWithApi("addJudgement").method("post", null).post(requestBody).build();
		client.newCall(request).enqueue(new Callback() {

			@Override
			public void onResponse(Call arg0, Response arg1) throws IOException {
				final String responseSrting = arg1.body().string();
				ObjectMapper mapper = new ObjectMapper();
				progressDialog.dismiss();
				try {
					Judgement judgement = mapper.readValue(responseSrting, Judgement.class);
					if (judgement != null) {
						runOnUiThread(new Runnable() {

							@Override
							public void run() {
								Toast.makeText(AddEvaluationActivity.this, "添加评价成功", Toast.LENGTH_SHORT).show();
								finish();
							}

						});
					}
				} catch (final Exception e) {
					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							Toast.makeText(AddEvaluationActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT)
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
						progressDialog.dismiss();
						Toast.makeText(AddEvaluationActivity.this, arg1.getLocalizedMessage(), Toast.LENGTH_SHORT)
								.show();
					}
				});
			}
		});
	}
}
