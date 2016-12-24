package com.swqs.schooltrade.activity;

import com.swqs.schooltrade.R;
import com.swqs.schooltrade.fragment.BuyDetailsFragment;
import com.swqs.schooltrade.fragment.SellDetailsFragment;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.os.Bundle;

public class BuyAndSelledActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_buy_sell);
		String flag=getIntent().getStringExtra("flag");
		FragmentTransaction ft=getFragmentManager().beginTransaction();
		if(flag.equals("buy")){
			
			ft.replace(R.id.container, new BuyDetailsFragment());
			ft.commit();
		}else if(flag.equals("sell")){
			ft.replace(R.id.container, new SellDetailsFragment());
			ft.commit();
		}
	}
}
