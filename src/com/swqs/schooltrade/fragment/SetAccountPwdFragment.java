package com.swqs.schooltrade.fragment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.swqs.schooltrade.R;
import com.swqs.schooltrade.entity.School;
import com.swqs.schooltrade.util.Server;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SetAccountPwdFragment extends Fragment {

	private View view;
	private EditText etAccount;
	private EditText etPwd;
	private EditText etPwdConfirm;
	private Button btnRegist;
	private ImageView ivBack;
	private Spinner spinnerSchool;
	private List<School> listSchool;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (view == null) {
			view = inflater.inflate(R.layout.fragment_account_pwd, null);
			etAccount = (EditText) view.findViewById(R.id.etAccount);
			etPwd = (EditText) view.findViewById(R.id.etPwd);
			etPwdConfirm = (EditText) view.findViewById(R.id.etPwdConfirm);
			btnRegist = (Button) view.findViewById(R.id.btnRegist);
			ivBack = (ImageView) view.findViewById(R.id.ivBack);

			spinnerSchool = (Spinner) view.findViewById(R.id.spinnerSchool);
			btnRegist.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					if (listener != null) {
						listener.onRegistClick();
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
			getSchoolData();
		}
		return view;
	}

	private void getSchoolData() {
		OkHttpClient client = Server.getSharedClient();
		Request request = Server.requestBuilderWithApi("school").build();
		client.newCall(request).enqueue(new Callback() {
			@Override
			public void onResponse(Call arg0, Response arg1) throws IOException {
				final String jsonString = arg1.body().string();
				final List<String> schoolNameList = new ArrayList<String>();
				listSchool = new ObjectMapper().readValue(jsonString, new TypeReference<List<School>>() {
				});

				for (int i = 0; i < listSchool.size(); i++) {
					schoolNameList.add(listSchool.get(i).getName());
				}
				final ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
						android.R.layout.simple_spinner_item, schoolNameList);
				adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				getActivity().runOnUiThread(new Runnable() {

					@Override
					public void run() {
						spinnerSchool.setAdapter(adapter);
					}
				});
			}

			@Override
			public void onFailure(Call arg0, IOException arg1) {
			}
		});
	}

	public String getAccount() {
		return etAccount.getText().toString().trim();
	}

	public String getPwd() {
		return etPwd.getText().toString();
	}

	public String getPwdConfirm() {
		return etPwdConfirm.getText().toString();
	}

	public int getSelectedSchoolId() {
		return listSchool.get(spinnerSchool.getSelectedItemPosition()).getId();
	}

	public interface OnClickListener {

		void onRegistClick();

		void onBackClick();
	}

	private OnClickListener listener;

	public void setListener(OnClickListener listener) {
		this.listener = listener;
	}
}
