package com.swqs.schooltrade.activity;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.swqs.schooltrade.R;
import com.swqs.schooltrade.entity.School;
import com.swqs.schooltrade.entity.User;
import com.swqs.schooltrade.util.RoundImageView;
import com.swqs.schooltrade.util.Server;
import com.swqs.schooltrade.util.Util;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class EditPersonInfoActivity extends Activity implements OnClickListener {

	private LinearLayout layoutNick;
	private LinearLayout layoutSex;
	private LinearLayout layoutBirthday;
	private LinearLayout layoutPhone;
	private LinearLayout layoutEmail;
	private LinearLayout layoutSchool;

	private Button btnBack;
	private TextView tvNick;
	private TextView tvSex;
	private TextView tvBirthday;
	private TextView tvPhone;
	private TextView tvEmail;
	private TextView tvSchool;
	private RoundImageView roundAvatar;
	private TextView tvAccount;
	private TextView tvSaveInfo;

	private User user;
	private List<School> schoolList;
	private School curSchool;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_person_info);
		user = (User) getIntent().getSerializableExtra("user");

		layoutNick = (LinearLayout) findViewById(R.id.layoutNick);
		layoutSex = (LinearLayout) findViewById(R.id.layoutSex);
		layoutBirthday = (LinearLayout) findViewById(R.id.layoutBirthday);
		layoutPhone = (LinearLayout) findViewById(R.id.layoutPhone);
		layoutEmail = (LinearLayout) findViewById(R.id.layoutEmail);
		layoutSchool = (LinearLayout) findViewById(R.id.layoutSchool);

		tvNick = (TextView) findViewById(R.id.tvNick);
		tvSex = (TextView) findViewById(R.id.tvSex);
		tvBirthday = (TextView) findViewById(R.id.tvBirthday);
		tvPhone = (TextView) findViewById(R.id.tvPhone);
		tvEmail = (TextView) findViewById(R.id.tvEmail);
		tvSchool = (TextView) findViewById(R.id.tvSchool);
		btnBack = (Button) findViewById(R.id.btnBack);
		roundAvatar = (RoundImageView) findViewById(R.id.roundAvatar);
		tvAccount = (TextView) findViewById(R.id.tvAccount);
		tvSaveInfo = (TextView) findViewById(R.id.tvSaveInfo);

		layoutNick.setOnClickListener(this);
		layoutSex.setOnClickListener(this);
		layoutBirthday.setOnClickListener(this);
		layoutPhone.setOnClickListener(this);
		// layoutEmail.setOnClickListener(this);
		layoutSchool.setOnClickListener(this);
		btnBack.setOnClickListener(this);
		tvSaveInfo.setOnClickListener(this);

		setPersonInfo();
	}

	private void setPersonInfo() {
		tvNick.setText(user.getName());
		String sex = "δѡ��";
		if (user.getSex() == 1) {
			sex = "��";
		} else if (user.getSex() == 2) {
			sex = "Ů";
		}
		tvSex.setText(sex);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		if (user.getBirthday() != null) {
			tvBirthday.setText(sdf.format(user.getBirthday()));
		}else{
			tvBirthday.setText("");
		}
		if (TextUtils.isEmpty(user.getPhone())) {
			tvPhone.setText("");
		} else {
			tvPhone.setText(user.getPhone());
		}
		tvEmail.setText(user.getEmail());
		tvSchool.setText(user.getSchool().getName());
		tvAccount.setText(user.getAccount());
		Util.loadImage(this, user.getFace_url(), roundAvatar);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.layoutNick:
			showInputDialog("�޸��ǳ�", tvNick.getText().toString(), tvNick);
			break;
		case R.id.layoutSex:
			showSexDialog(tvSex);
			break;
		case R.id.layoutBirthday:
			showBirthdayDialog(tvBirthday);
			break;
		case R.id.layoutPhone:
			showInputDialog("�޸��ֻ�����", tvPhone.getText().toString(), tvPhone);
			break;
		case R.id.layoutEmail:
			showInputDialog("�޸�����", tvEmail.getText().toString(), tvEmail);
			break;
		case R.id.layoutSchool:
			showSchoolDialog();
			break;
		case R.id.btnBack:
			finish();
			break;
		case R.id.tvSaveInfo:
			saveInfo();
			break;
		}
	}

	private void saveInfo() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		long time = 0;
		try {
			time = sdf.parse(tvBirthday.getText().toString()).getTime();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		String sex=tvSex.getText().toString();
		if(sex.equals("δѡ��")){
			sex="0";
		}
		OkHttpClient client = Server.getSharedClient();
		MultipartBody.Builder requestBodyBuilder = new MultipartBody.Builder()
				.addFormDataPart("sex", sex)
				// .addFormDataPart("email", tvEmail.getText().toString())
				.addFormDataPart("name", tvNick.getText().toString()).addFormDataPart("birthday", time + "")
				.addFormDataPart("phone", tvPhone.getText().toString());
		if (curSchool != null) {
			requestBodyBuilder.addFormDataPart("schoolId", curSchool.getId() + "");
		} else {
			requestBodyBuilder.addFormDataPart("schoolId", user.getSchool().getId() + "");
		}

		Request request = Server.requestBuilderWithApi("updateme").method("get", null).post(requestBodyBuilder.build())
				.build();

		client.newCall(request).enqueue(new Callback() {

			@Override
			public void onResponse(Call arg0, Response arg1) throws IOException {
				final String jsonString = arg1.body().string();

				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						ObjectMapper mapper = new ObjectMapper();
						try {
							final User user = mapper.readValue(jsonString, User.class);
							if (user.getAccount().equals("phoneExist")) {
								Toast.makeText(EditPersonInfoActivity.this, "�ֻ������Ѵ���", Toast.LENGTH_SHORT).show();
							} else if (user.getAccount().equals("emailExist")) {
								Toast.makeText(EditPersonInfoActivity.this, "�����ѱ�ע��", Toast.LENGTH_SHORT).show();
							} else if (user.getAccount().equals("nameExist")) {
								Toast.makeText(EditPersonInfoActivity.this, "�ǳ��Ѵ���", Toast.LENGTH_SHORT).show();
							} else {
								finish();
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
			}

			@Override
			public void onFailure(Call arg0, IOException arg1) {
				// TODO Auto-generated method stub

			}
		});
	}

	private void showInputDialog(String title, String content, final TextView textView) {
		final AlertDialog dialog = new AlertDialog.Builder(this).create();
		dialog.setView(LayoutInflater.from(this).inflate(R.layout.input_alert_dialog, null));
		dialog.show();
		dialog.getWindow().setContentView(R.layout.input_alert_dialog);
		TextView tvTitle = (TextView) dialog.findViewById(R.id.tvTitle);
		Button btnConfirm = (Button) dialog.findViewById(R.id.btnConfirm);
		Button btnCancel = (Button) dialog.findViewById(R.id.btnCancel);
		final EditText etContent = (EditText) dialog.findViewById(R.id.etContent);
		etContent.setText(content);
		etContent.setSelection(content.length());
		tvTitle.setText(title);
		btnConfirm.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				String str = etContent.getText().toString();
				if (TextUtils.isEmpty(str)) {
					Toast.makeText(EditPersonInfoActivity.this, "�������ݲ���Ϊ��", Toast.LENGTH_SHORT).show();
				} else {
					if (textView == tvPhone) {
						if (!Util.checkMobileNumber(str)) {
							Toast.makeText(EditPersonInfoActivity.this, "�ֻ������ʽ����ȷ", Toast.LENGTH_SHORT).show();
						} else {
							dialog.dismiss();
							textView.setText(str);
						}
					} else {
						dialog.dismiss();
						textView.setText(str);
					}
				}
			}
		});
		btnCancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				dialog.dismiss();
			}
		});
	}

	private void showSexDialog(final TextView textView) {
		final AlertDialog dialog = new AlertDialog.Builder(this).create();
		dialog.setView(LayoutInflater.from(this).inflate(R.layout.dialog_sex, null));
		dialog.show();
		dialog.getWindow().setContentView(R.layout.dialog_sex);
		TextView tvMan = (TextView) dialog.findViewById(R.id.tvMan);
		TextView tvWoman = (TextView) dialog.findViewById(R.id.tvWoman);
		tvMan.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				dialog.dismiss();
				textView.setText("��");
			}
		});
		tvWoman.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				dialog.dismiss();
				textView.setText("Ů");
			}
		});
	}

	private void showBirthdayDialog(final TextView textView) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		View view = View.inflate(this, R.layout.activity_time, null);

		final DatePicker datePicker = (DatePicker) view.findViewById(R.id.date_picker);
		builder.setView(view);

		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		long time = 0;
		try {
			time = sdf.parse(textView.getText().toString()).getTime();
		} catch (ParseException e) {
			e.printStackTrace();
		} // �õ�ʱ������
		cal.setTimeInMillis(time);
		datePicker.init(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), null);

		builder.setTitle("����");
		builder.setPositiveButton("ȷ  ��", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {

				StringBuffer sb = new StringBuffer();
				sb.append(String.format("%d-%02d-%02d", datePicker.getYear(), datePicker.getMonth() + 1,
						datePicker.getDayOfMonth()));
				textView.setText(sb.toString());
				dialog.cancel();
			}
		});
		Dialog dialog = builder.create();
		dialog.show();
	}

	private void showSchoolDialog() {
		final AlertDialog dialog = new AlertDialog.Builder(this).create();
		dialog.setView(LayoutInflater.from(this).inflate(R.layout.dialog_spinner, null));
		dialog.show();
		dialog.getWindow().setContentView(R.layout.dialog_spinner);
		final Spinner spinnerSchool = (Spinner) dialog.findViewById(R.id.spinnerSchool);
		Button btnConfirm = (Button) dialog.findViewById(R.id.btnConfirm);
		Button btnCancel = (Button) dialog.findViewById(R.id.btnCancel);
		btnConfirm.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				dialog.dismiss();
				curSchool = schoolList.get(spinnerSchool.getSelectedItemPosition());
				tvSchool.setText(curSchool.getName());
			}
		});
		btnCancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				dialog.dismiss();
			}
		});
		if (schoolList == null) {
			OkHttpClient client = Server.getSharedClient();
			Request request = Server.requestBuilderWithApi("school").method("get", null).build();
			client.newCall(request).enqueue(new Callback() {
				@Override
				public void onResponse(Call arg0, Response arg1) throws IOException {
					final String jsonString = arg1.body().string();
					final List<String> schoolNameList = new ArrayList<String>();
					schoolList = new ObjectMapper().readValue(jsonString, new TypeReference<List<School>>() {
					});

					for (int i = 0; i < schoolList.size(); i++) {
						schoolNameList.add(schoolList.get(i).getName());
					}
					// �ڶ�����Ϊ�����б���һ����������������õ���ǰ�涨���list��
					final ArrayAdapter<String> adapter = new ArrayAdapter<String>(EditPersonInfoActivity.this,
							android.R.layout.simple_spinner_item, schoolNameList);
					// ��������Ϊ���������������б�����ʱ�Ĳ˵���ʽ��
					adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
					runOnUiThread(new Runnable() {
						public void run() {
							// ���Ĳ�������������ӵ������б���
							spinnerSchool.setAdapter(adapter);
						}
					});

				}

				@Override
				public void onFailure(Call arg0, IOException arg1) {
				}
			});
		} else {
			List<String> schoolNameList = new ArrayList<String>();
			for (int i = 0; i < schoolList.size(); i++) {
				schoolNameList.add(schoolList.get(i).getName());
			}
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(EditPersonInfoActivity.this,
					android.R.layout.simple_spinner_item, schoolNameList);
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			spinnerSchool.setAdapter(adapter);
		}
	}
}
