package com.swqs.schooltrade;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

public class Recharge extends Activity implements OnClickListener {

	private RadioGroup rgBankCark;
	private Button btnThird;
	private Button btnMoney100, btnMoney200, btnMoney500, btnMoney1000;
	private EditText etInputMoney;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_recharge);

		btnThird = (Button) findViewById(R.id.btnThird);

		rgBankCark = (RadioGroup) findViewById(R.id.rgBankCark);
		rgBankCark.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// TODO Auto-generated method stub

			}
		});

		btnThird.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				goJump();
			}
		});
		btnMoney100 = (Button) findViewById(R.id.btnMoney100);
		btnMoney200 = (Button) findViewById(R.id.btnMoney200);
		btnMoney500 = (Button) findViewById(R.id.btnMoney500);
		btnMoney1000 = (Button) findViewById(R.id.btnMoney1000);
		etInputMoney = (EditText) findViewById(R.id.etInputMoney);

		btnMoney100.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				etInputMoney.setText("100");
			}
		});
		btnMoney200.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				etInputMoney.setText("200");
			}
		});
		btnMoney500.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				etInputMoney.setText("500");
			}
		});
		btnMoney1000.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				etInputMoney.setText("1000");
			}
		});
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

	}

	void goJump() {
		Intent itnt1 = new Intent(this, Jump.class);
		startActivity(itnt1);
	}
}
