package com.swqs.schooltrade.fragment;

import java.util.Timer;
import java.util.TimerTask;

import com.swqs.schooltrade.R;
import com.swqs.schooltrade.util.Util;

import android.app.Fragment;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

public class GetEmailCodeFragment extends Fragment {

	private View view;
	public static final String TAG = "OK";
	private EditText etEmail;
	private EditText etCode;
	private Button btnSendCode;
	private Button btnNext;
	private ImageView ivBack;
	private String email;
	private String identifyCode;

	/**
	 * 定时器
	 */
	private Timer timer;
	/**
	 * 定时任务
	 */
	private TimerTask task;
	private int TOTAL_LEFT_TIME = 1000 * 60;
	private int leftTime = TOTAL_LEFT_TIME;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (view == null) {
			view = inflater.inflate(R.layout.fragment_email_code, null);
			etEmail = (EditText) view.findViewById(R.id.etEmail);
			etCode = (EditText) view.findViewById(R.id.etCode);
			btnSendCode = (Button) view.findViewById(R.id.btnSendCode);
			btnNext = (Button) view.findViewById(R.id.btnNext);
			ivBack = (ImageView) view.findViewById(R.id.ivBack);

			etEmail.addTextChangedListener(new TextWatcher() {

				@Override
				public void onTextChanged(CharSequence s, int start,
						int before, int count) {
				}

				@Override
				public void beforeTextChanged(CharSequence s, int start,
						int count, int after) {
				}

				@Override
				public void afterTextChanged(Editable s) {
					email = etEmail.getText().toString().trim();
					identifyCode = etCode.getText().toString().trim();
					// 如果邮箱格式正确
					// TODO 验证邮箱格式
					if (Util.checkEmail(email)) {
						btnSendCode.setEnabled(true);
						btnSendCode
								.setBackgroundResource(R.drawable.selector_btn_send_code);
						// 如果已经输入了验证码
						if (identifyCode.length() > 0) {
							btnNext.setEnabled(true);
							btnNext.setBackgroundResource(R.drawable.selector_btn_send_code);
						}
					} else {
						btnSendCode.setEnabled(false);
						btnSendCode.setBackgroundResource(R.color.forbit_color);
						// 如果邮箱格式不正确，那么就不能进行下一步
						btnNext.setEnabled(false);
						btnNext.setBackgroundResource(R.color.forbit_color);
					}
				}
			});
			etCode.addTextChangedListener(new TextWatcher() {

				@Override
				public void onTextChanged(CharSequence s, int start,
						int before, int count) {
				}

				@Override
				public void beforeTextChanged(CharSequence s, int start,
						int count, int after) {
				}

				@Override
				public void afterTextChanged(Editable s) {
					identifyCode = etCode.getText().toString().trim();
					// TODO 验证邮箱
					if (email != null && Util.checkEmail(email)
							&& identifyCode.length() == 6) {
						btnNext.setEnabled(true);
						btnNext.setBackgroundResource(R.drawable.selector_btn_send_code);
					} else {
						btnNext.setEnabled(false);
						btnNext.setBackgroundResource(R.color.forbit_color);
					}
				}
			});
			btnSendCode.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					if (listener != null) {
						listener.onSendCodeClick();
					}
				}
			});
			btnNext.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					if (listener != null) {
						listener.onNextClick();
					}
				}
			});
			ivBack.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					if (listener != null) {
						listener.onBackClick();
					}
				}
			});
		}

		return view;
	}

	public interface OnClickListener {
		void onSendCodeClick();

		void onNextClick();

		void onBackClick();
	}

	private OnClickListener listener;

	public void setListener(OnClickListener listener) {
		this.listener = listener;
	}

	public String getEmail() {
		return etEmail.getText().toString();
	}

	public String getCode() {
		return etCode.getText().toString();
	}

	public void setLeftTime() {
		task = new TimerTask() {
			@Override
			public void run() {
				getActivity().runOnUiThread(new Runnable() {

					@Override
					public void run() {
						if (leftTime == 0) {
							btnSendCode.setText("获取验证码");
							btnSendCode.setEnabled(true);
							btnSendCode
									.setBackgroundResource(R.drawable.selector_btn_send_code);
							leftTime = TOTAL_LEFT_TIME;
							timer.cancel();
							task.cancel();
							timer = null;
							task = null;
							return;
						}
						btnSendCode.setText("重新发送");
						// 这里不能直接除以1000，因为剩余秒数不一定是1000的倍数
						btnSendCode.append("(" + (leftTime / 1000) + ")");
						leftTime -= 1000;
					}
				});
			}
		};
		timer = new Timer();
		btnSendCode.setEnabled(false);
		btnSendCode.setBackgroundResource(R.color.forbit_color);
		timer.schedule(task, 0, 1000);
	}
	
	@Override
	public void onDetach() {
		if (timer != null) {
			timer.cancel();
			timer = null;
		}
		if (task != null) {
			task.cancel();
			task = null;
		}
		super.onDetach();
	}
}
