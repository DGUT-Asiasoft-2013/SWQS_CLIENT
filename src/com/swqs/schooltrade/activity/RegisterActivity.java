package com.swqs.schooltrade.activity;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.swqs.schooltrade.R;
import com.swqs.schooltrade.entity.School;
import com.swqs.schooltrade.entity.User;
import com.swqs.schooltrade.fragment.InputcellSimpletextFragment;
import com.swqs.schooltrade.util.MD5;
import com.swqs.schooltrade.util.Server;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Spinner;
import android.widget.Toast;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class RegisterActivity extends Activity implements OnClickListener {
	protected static final String TAG = "RegisterActivity";
	InputcellSimpletextFragment fragInputCellAccount;
	InputcellSimpletextFragment fragInputCellPassword;
	InputcellSimpletextFragment fragInputCellRepassword;

	InputcellSimpletextFragment fragInputCellEmail;
	InputcellSimpletextFragment fragInputCellName;
	InputcellSimpletextFragment fragInputCelltel;

	private Button btnBirthday;
	private RadioGroup rg;
	private Spinner spinnerSchool;
	private List<School> listSchool;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_register);

		fragInputCellAccount = (InputcellSimpletextFragment) getFragmentManager().findFragmentById(R.id.input_account);
		fragInputCellPassword = (InputcellSimpletextFragment) getFragmentManager()
				.findFragmentById(R.id.input_password);
		fragInputCellRepassword = (InputcellSimpletextFragment) getFragmentManager()
				.findFragmentById(R.id.input_repassword);

		fragInputCellEmail = (InputcellSimpletextFragment) getFragmentManager().findFragmentById(R.id.input_email);
		fragInputCellName = (InputcellSimpletextFragment) getFragmentManager().findFragmentById(R.id.input_name);
		fragInputCelltel = (InputcellSimpletextFragment) getFragmentManager().findFragmentById(R.id.input_tel);

		btnBirthday = (Button) findViewById(R.id.btn_birthday);

		btnBirthday.setOnClickListener(this);

		rg = (RadioGroup) findViewById(R.id.rg);
		spinnerSchool = (Spinner) findViewById(R.id.input_school);

		rg.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {

			}
		});// RadioGroup��ѡ�ж�
		findViewById(R.id.btn_submit).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				submit();
			}
		});
		getSchoolData();
	}

	protected void onResume() {
		super.onResume();

		fragInputCellAccount.setLabelText("�ҵ��˻�");
		{
			fragInputCellAccount.setHintText("�������û���");
		}

		fragInputCellPassword.setLabelText("�ҵ�����");
		{
			fragInputCellPassword.setHintText("����������λ����");
			fragInputCellPassword.setIsPassword(true);
			fragInputCellPassword.getText();
		}

		fragInputCellRepassword.setLabelText("�ظ�����");
		{
			fragInputCellRepassword.setHintText("�ٴ���������");
			fragInputCellRepassword.setIsPassword(true);

		}

		fragInputCellEmail.setLabelText("�ҵ�����");
		{
			fragInputCellEmail.setHintText("����������");
			fragInputCellEmail.setIsEmail(true);
		}
		fragInputCelltel.setLabelText("�ֻ�����");
		{
			fragInputCelltel.setHintText("�������ֻ�����");
			fragInputCelltel.setIsNumber(true);
		}
		fragInputCellName.setLabelText("�ҵ��ǳ�");
		{
			fragInputCellName.setHintText("�������ǳ�");
		}
	}

	@Override
	public void onClick(View v) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		View view = View.inflate(this, R.layout.activity_time, null);

		final DatePicker datePicker = (DatePicker) view.findViewById(R.id.date_picker);
		builder.setView(view);

		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(System.currentTimeMillis());
		datePicker.init(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), null);

		builder.setTitle("ѡȡʱ��");
		builder.setPositiveButton("ȷ  ��", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {

				StringBuffer sb = new StringBuffer();
				sb.append(String.format("%d-%02d-%02d", datePicker.getYear(), datePicker.getMonth() + 1,
						datePicker.getDayOfMonth()));
				sb.append("  ");
				btnBirthday.setText(sb);

				dialog.cancel();
			}
		});
		Dialog dialog = builder.create();
		dialog.show();
	}// ����ʱ��ѡ�����Ĺ���ʵ��

	void submit() {
		String account = fragInputCellAccount.getText();
		String name = fragInputCellName.getText();
		String email = fragInputCellEmail.getText();
		String birthday = btnBirthday.getText().toString();
		String phone = fragInputCelltel.getText().toString();
		String password = fragInputCellPassword.getText();
		String passwordRepeat = fragInputCellRepassword.getText();

		if (TextUtils.isEmpty(account)) {
			new AlertDialog.Builder(RegisterActivity.this).setMessage("�������˺�")
					.setIcon(android.R.drawable.ic_dialog_alert).setNegativeButton("��", null).show();

			return;
		}
		if (TextUtils.isEmpty(password)) {
			new AlertDialog.Builder(RegisterActivity.this).setMessage("����������")
					.setIcon(android.R.drawable.ic_dialog_alert).setNegativeButton("��", null).show();

			return;
		}

		if (!password.equals(passwordRepeat)) {

			new AlertDialog.Builder(RegisterActivity.this).setMessage("������������벻һ��")
					.setIcon(android.R.drawable.ic_dialog_alert).setNegativeButton("��", null).show();

			return;
		}

		if (password.length() < 6 || password.length() > 15) {

			new AlertDialog.Builder(RegisterActivity.this).setMessage("���볤��Ϊ6-15λ")
					.setIcon(android.R.drawable.ic_dialog_alert).setNegativeButton("��", null).show();

			return;
		} // �������볤��
		
		if (TextUtils.isEmpty(birthday)) {
			new AlertDialog.Builder(RegisterActivity.this).setMessage("��ѡ������")
					.setIcon(android.R.drawable.ic_dialog_alert).setNegativeButton("��", null).show();

			return;
		}

		password = MD5.getMD5(password);

		int sexId = rg.getCheckedRadioButtonId();
		String sex = (sexId == R.id.male ? "1" : "2");// ���Ա���Ůת����1,2

		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		String time = null;// ���������ʽ����

		try {
			time = dateFormat.parse(birthday).getTime() + "";
		} catch (ParseException e) {
			e.printStackTrace();
		} // �õ�ʱ������

		final ProgressDialog progressDialog = new ProgressDialog(RegisterActivity.this);
		progressDialog.setMessage("���Ժ�");
		progressDialog.setCancelable(false);
		progressDialog.setCanceledOnTouchOutside(false);
		progressDialog.show();

		OkHttpClient client = Server.getSharedClient();
		// ����okHttpClint����
		MultipartBody.Builder requestBodyBuilder = new MultipartBody.Builder().setType(MultipartBody.FORM)
				.addFormDataPart("account", account).addFormDataPart("password", password)
				.addFormDataPart("email", email).addFormDataPart("name", name).addFormDataPart("birthday", "" + time)
				.addFormDataPart("phone", phone)
				.addFormDataPart("schoolId", listSchool.get(spinnerSchool.getSelectedItemPosition()).getId() + "")
				.addFormDataPart("sex", sex);

		Request request = Server.requestBuilderWithApi("register").method("get", null).post(requestBodyBuilder.build())
				.build();
		// ����һ��Request��������������url����server���Ѿ���ӣ���Ҳ����ͨ��requestBuilder���������������method.

		client.newCall(request).enqueue(new Callback() {
			// new call
			// ͨ��request�Ķ���ȥ�����ȵ�һ��Call����,���ƽ������װ�����񣬼�Ȼ������ͻ���execute()��cancel()�ȷ���
			// ���������ȣ���Ϊ�����첽�ķ�ʽȥִ���������Ե��õ���call.enqueue����call����������У�Ȼ��ȴ�����ִ��
			@Override
			public void onResponse(Call arg0, Response arg1) throws IOException {
				progressDialog.dismiss();
				final String jsonString = arg1.body().string();

				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						ObjectMapper mapper = new ObjectMapper();
						try {
							final User user = mapper.readValue(jsonString, User.class);
							if (user.getAccount().equals("accountExist")) {
								Toast.makeText(RegisterActivity.this, "�˺��ѱ�ע��", Toast.LENGTH_SHORT).show();
							} else if (user.getAccount().equals("phoneExist")) {
								Toast.makeText(RegisterActivity.this, "�ֻ������Ѵ���", Toast.LENGTH_SHORT).show();
							} else if (user.getAccount().equals("emailExist")) {
								Toast.makeText(RegisterActivity.this, "�����ѱ�ע��", Toast.LENGTH_SHORT).show();
							} else if (user.getAccount().equals("nameExist")) {
								Toast.makeText(RegisterActivity.this, "�ǳ��Ѵ���", Toast.LENGTH_SHORT).show();
							} else {
								Toast.makeText(RegisterActivity.this, "ע��ɹ�", Toast.LENGTH_SHORT).show();
								finish();
							} // ���Ѳ����ظ������Ѵ��ڵ���Ϣ
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
				Log.e(TAG, jsonString + "g");
			}

			@Override
			public void onFailure(Call arg0, IOException arg1) {

			}
		});

	}// ��ע����Ϣ�ϴ���������

	private void getSchoolData() {
		Log.e(TAG, "h");
		OkHttpClient client = Server.getSharedClient();
		// ����okHttpClint����
		Request request = Server.requestBuilderWithApi("school").method("get", null).build();
		// ����һ��Request��������������url����server���Ѿ���ӣ���Ҳ����ͨ��requestBuilder���������������method.
		// new call
		// ͨ��request�Ķ���ȥ�����ȵ�һ��Call����,���ƽ������װ�����񣬼�Ȼ������ͻ���execute()��cancel()�ȷ���
		client.newCall(request).enqueue(new Callback() {
			// ���������ȣ���Ϊ�����첽�ķ�ʽȥִ���������Ե��õ���call.enqueue����call����������У�Ȼ��ȴ�����ִ��
			@Override
			public void onResponse(Call arg0, Response arg1) throws IOException {
				Log.e(TAG, "g");
				final String jsonString = arg1.body().string();
				final List<String> schoolNameList = new ArrayList<String>();
				listSchool = new ObjectMapper().readValue(jsonString, new TypeReference<List<School>>() {
				});

				for (int i = 0; i < listSchool.size(); i++) {
					schoolNameList.add(listSchool.get(i).getName());
				}
				// �ڶ�����Ϊ�����б���һ����������������õ���ǰ�涨���list��
				final ArrayAdapter<String> adapter = new ArrayAdapter<String>(RegisterActivity.this,
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
				Log.e(TAG, "a");
			}
		});
	}// ѧУ���������ӷ�����

}
