package com.swqs.schooltrade;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class SellEvaluationDetailsActivity extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sell_evaluation_details);
		
		findViewById(R.id.btnback).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				goSellOrderDetails();
				
			}
		});
		
		findViewById(R.id.btnadditional).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				goSellAdditionalEvaluation();
				
			}
		});
}
	
	void goSellOrderDetails(){
		finish();
	}
	
	void goSellAdditionalEvaluation(){
		Intent itnt = new Intent(this,SellAdditionalEvaluationActivity.class);
		startActivity(itnt);
	}
	
}