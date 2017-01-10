package com.swqs.schooltrade.activity;

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.swqs.schooltrade.R;
import com.swqs.schooltrade.app.TradeApplication;
import com.swqs.schooltrade.entity.Comment;
import com.swqs.schooltrade.util.CustomProgressDialog;
import com.swqs.schooltrade.util.Server;
import com.swqs.schooltrade.util.Util;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.Response;

public class CommentToOthersActivity extends Activity {

	private ImageView ivBack;
	private Button btnSendComment;
	private EditText etComment;
	private Comment comment;
	private CustomProgressDialog progressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_comment_others);
		comment = (Comment) getIntent().getSerializableExtra("comment");
		ivBack = (ImageView) findViewById(R.id.ivBack);
		btnSendComment = (Button) findViewById(R.id.btnSendComment);
		etComment = (EditText) findViewById(R.id.etComment);
		ivBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
		btnSendComment.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				sendComment();
			}
		});
		etComment.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				String content = s.toString();
				if (TextUtils.isEmpty(content)) {
					btnSendComment.setEnabled(false);
				} else {
					btnSendComment.setEnabled(true);
				}
			}
		});
		etComment.setHint("回复@" + comment.getAccount().getName());
	}

	private void sendComment() {
		progressDialog=Util.getProgressDialog(this, R.layout.custom_progressdialog);
		progressDialog.show();
		String text = etComment.getText().toString();
		MultipartBody body = new MultipartBody.Builder().addFormDataPart("text", text)
				.addFormDataPart("uid", TradeApplication.uid).build();
		Request request = Server
				.requestBuilderWithApi(
						"/goods/" + comment.getGoods().getId() + "/parentcomments/" + comment.getId() + "/addComments")
				.post(body).build();

		Server.getSharedClient().newCall(request).enqueue(new Callback() {
			@Override
			public void onResponse(Call arg0, Response arg1) throws IOException {
				final String responseBody = arg1.body().string();
				ObjectMapper mapper = new ObjectMapper();
				progressDialog.dismiss();
				try {
					Comment c = mapper.readValue(responseBody, Comment.class);
					if (c != null) {
						runOnUiThread(new Runnable() {
							public void run() {
								Toast.makeText(CommentToOthersActivity.this, "评论成功", Toast.LENGTH_SHORT).show();
								finish();
							}
						});
					}
				} catch (Exception e) {
					e.printStackTrace();
					Toast.makeText(CommentToOthersActivity.this, "系统异常，解析失败", Toast.LENGTH_SHORT).show();
				}
			}

			@Override
			public void onFailure(Call arg0, final IOException arg1) {
				runOnUiThread(new Runnable() {
					public void run() {
						progressDialog.dismiss();
						Toast.makeText(CommentToOthersActivity.this, arg1.getLocalizedMessage(), Toast.LENGTH_SHORT)
								.show();
					}
				});
			}
		});
	}
}
