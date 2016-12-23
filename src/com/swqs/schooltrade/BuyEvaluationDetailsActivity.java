package com.swqs.schooltrade;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class BuyEvaluationDetailsActivity extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_buy_evaluation_details);
		
		findViewById(R.id.btnback).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				goOrderDetails();
				
			}
		});
		
		findViewById(R.id.btnadditional).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				goAdditionalEvaluation();
				
			}
		});
}
	
	void goOrderDetails(){
		Intent itnt = new Intent(this,BuyOrderDetailsActivity.class);
		startActivity(itnt);
	}
	
	void goAdditionalEvaluation(){
		Intent itnt = new Intent(this,BuyAdditionalEvaluationActivity.class);
		startActivity(itnt);
	}
	
}