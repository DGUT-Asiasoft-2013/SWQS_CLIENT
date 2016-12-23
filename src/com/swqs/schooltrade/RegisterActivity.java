package com.swqs.schooltrade;

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
import com.swqs.schooltrade.api.Server;
import com.swqs.schooltrade.api.entity.School;
import com.swqs.schooltrade.api.entity.User;

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
import inputcell.InputcellSimpletextFragment;
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
		});// RadioGroup单选判断
		findViewById(R.id.btn_submit).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				submit();
			}
		});
		getSchoolData();
	}

	private void getSchoolData() {
		Log.e(TAG, "h");
		OkHttpClient client = Server.getSharedClient();

		Request request = Server.requestBuilderWithApi("school").method("get", null).build();

		client.newCall(request).enqueue(new Callback() {

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
				// 第二步：为下拉列表定义一个适配器，这里就用到里前面定义的list。
				final ArrayAdapter<String> adapter = new ArrayAdapter<String>(RegisterActivity.this,
						android.R.layout.simple_spinner_item, schoolNameList);
				// 第三步：为适配器设置下拉列表下拉时的菜单样式。
				adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				runOnUiThread(new Runnable() {
					public void run() {
						// 第四步：将适配器添加到下拉列表上
						spinnerSchool.setAdapter(adapter);
					}
				});

			}

			@Override
			public void onFailure(Call arg0, IOException arg1) {
				Log.e(TAG, "a");
			}
		});
	}

	protected void onResume() {
		super.onResume();

		fragInputCellAccount.setLabelText("我的账户");
		{
			fragInputCellAccount.setHintText("请输入用户名");
		}

		fragInputCellPassword.setLabelText("我的密码");
		{
			fragInputCellPassword.setHintText("输入至少六位密码");
			fragInputCellPassword.setIsPassword(true);
			fragInputCellPassword.getText();
		}

		fragInputCellRepassword.setLabelText("重复密码");
		{
			fragInputCellRepassword.setHintText("再次输入密码");
			fragInputCellRepassword.setIsPassword(true);

		}

		fragInputCellEmail.setLabelText("我的邮箱");
		{
			fragInputCellEmail.setHintText("请输入邮箱");
			fragInputCellEmail.setIsEmail(true);
		}
		fragInputCelltel.setLabelText("手机号码");
		{
			fragInputCelltel.setHintText("请输入手机号码");
			fragInputCelltel.setIsNumber(true);
		}
		fragInputCellName.setLabelText("我的昵称");
		{
			fragInputCellName.setHintText("请输入昵称");
		}
	}

	void submit() {
		String password = fragInputCellPassword.getText();
		String passwordRepeat = fragInputCellRepassword.getText();

		if (!password.equals(passwordRepeat)) {

			new AlertDialog.Builder(RegisterActivity.this).setMessage("重复密码与密码不一致")
					.setIcon(android.R.drawable.ic_dialog_alert).setNegativeButton("好", null).show();

			return;
		}

		if (password.length() < 6 || password.length() > 15) {

			new AlertDialog.Builder(RegisterActivity.this).setMessage("请输入长度为6-15之间的数字或字母")
					.setIcon(android.R.drawable.ic_dialog_alert).setNegativeButton("好", null).show();

			return;
		}//限制密码长度
		
		password = MD5.getMD5(password);

		String account = fragInputCellAccount.getText();
		String name = fragInputCellName.getText();
		String email = fragInputCellEmail.getText();
		String birthday = btnBirthday.getText().toString();
		String phone = fragInputCelltel.getText().toString();

		int sexId = rg.getCheckedRadioButtonId();
		String sex = (sexId == R.id.male ? "1" : "2");

		final ProgressDialog progressDialog = new ProgressDialog(RegisterActivity.this);
		progressDialog.setMessage("请稍候");
		progressDialog.setCancelable(false);
		progressDialog.setCanceledOnTouchOutside(false);
		progressDialog.show();

		OkHttpClient client = Server.getSharedClient();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		String time = null;
		
		try {
			time = dateFormat.parse(birthday).getTime() + "";
		} catch (ParseException e) {
			e.printStackTrace();
		}
		MultipartBody.Builder requestBodyBuilder = new MultipartBody.Builder().setType(MultipartBody.FORM)
				.addFormDataPart("account", account).addFormDataPart("password", password)
				.addFormDataPart("email", email).addFormDataPart("name", name).addFormDataPart("birthday", "" + time)
				.addFormDataPart("phone", phone)
				.addFormDataPart("schoolId", listSchool.get(spinnerSchool.getSelectedItemPosition()).getId() + "")
				.addFormDataPart("sex", sex);

		Request request = Server.requestBuilderWithApi("register").method("get", null).post(requestBodyBuilder.build())
				.build();

		client.newCall(request).enqueue(new Callback() {

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
							if(user.getAccount().equals("accountExist")){
								Toast.makeText(RegisterActivity.this, "账号已被注册", Toast.LENGTH_SHORT).show();
							}else if(user.getAccount().equals("phoneExist")){
								Toast.makeText(RegisterActivity.this, "手机号码已存在", Toast.LENGTH_SHORT).show();
							}else if(user.getAccount().equals("emailExist")){
								Toast.makeText(RegisterActivity.this, "邮箱已被注册", Toast.LENGTH_SHORT).show();
							}else if(user.getAccount().equals("nameExist")){
								Toast.makeText(RegisterActivity.this, "昵称已存在", Toast.LENGTH_SHORT).show();
							}else {
								Toast.makeText(RegisterActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
								finish();
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
				Log.e(TAG, jsonString + "g");
			}

			@Override
			public void onFailure(Call arg0, IOException arg1) {
				// TODO Auto-generated method stub

			}
		});

	}// 密码输入限制

	@Override
	public void onClick(View v) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		View view = View.inflate(this, R.layout.activity_time, null);
		
		final DatePicker datePicker = (DatePicker) view.findViewById(R.id.date_picker);
		builder.setView(view);

		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(System.currentTimeMillis());
		datePicker.init(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), null);

		builder.setTitle("选取时间");
		builder.setPositiveButton("确  定", new DialogInterface.OnClickListener() {

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
	}

	
}
