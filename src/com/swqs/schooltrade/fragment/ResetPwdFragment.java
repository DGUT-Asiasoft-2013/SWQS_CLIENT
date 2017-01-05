package com.swqs.schooltrade.fragment;

import com.swqs.schooltrade.R;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

public class ResetPwdFragment extends Fragment {

	private View view;
	private EditText etPwd;
	private EditText etPwdConfirm;
	private Button btnSubmit;
	private ImageView ivBack;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		if (view == null) {
			view = inflater.inflate(R.layout.fragment_password_recover_step2, null);
			etPwd = (EditText) view.findViewById(R.id.etPwd);
			etPwdConfirm = (EditText) view.findViewById(R.id.etPwdConfirm);
			btnSubmit = (Button) view.findViewById(R.id.btnSubmit);
			ivBack = (ImageView) view.findViewById(R.id.ivBack);
			btnSubmit.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					if (listener != null) {
						listener.onSubmitClick();
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

	public String getPwd() {
		return etPwd.getText().toString();
	}

	public String getPwdConfirm() {
		return etPwdConfirm.getText().toString();
	}

	public interface OnClickListener {

		void onSubmitClick();

		void onBackClick();
	}

	private OnClickListener listener;

	public void setListener(OnClickListener listener) {
		this.listener = listener;
	}

}
