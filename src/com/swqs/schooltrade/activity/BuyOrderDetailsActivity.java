package com.swqs.schooltrade.activity;

import com.swqs.schooltrade.R;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class BuyOrderDetailsActivity extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_buy_order_details);
		
		findViewById(R.id.btncell).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				goChatSeller();
				
			}
		});
		
		findViewById(R.id.btnback).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				goMyBuy();
				
			}
		});
		
		findViewById(R.id.btnlook).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				goEvaluationDetails();
				
			}
		});
}
	
	void goEvaluationDetails(){
		Intent itnt = new Intent(this,BuyEvaluationDetailsActivity.class);
		startActivity(itnt);
	}
	
	void goChatSeller(){
		Intent itnt = new Intent(this,BuyEvaluationDetailsActivity.class);
		startActivity(itnt);
	}
	
	void goMyBuy(){
		finish();
	}
}
