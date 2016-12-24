package com.swqs.schooltrade.activity;

import com.swqs.schooltrade.R;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class SellOrderDetailsActivity extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sell_order_details);
		
		findViewById(R.id.btncell).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				goChatbuyer();
				
			}
		});
		
		findViewById(R.id.btnback).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				goMySell();
				
			}
		});
		
		findViewById(R.id.btnlook).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				goSellEvaluationDetails();
				
			}
		});
}
	
	void goSellEvaluationDetails(){
		Intent itnt = new Intent(this,SellEvaluationDetailsActivity.class);
		startActivity(itnt);
	}
	
	void goChatbuyer(){
		Intent itnt = new Intent(this,BuyEvaluationDetailsActivity.class);
		startActivity(itnt);
	}
	
	void goMySell(){
		finish();
	}
}
