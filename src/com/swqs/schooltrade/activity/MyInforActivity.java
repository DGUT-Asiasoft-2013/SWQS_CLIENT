package com.swqs.schooltrade.activity;




import java.io.IOException;
import java.text.SimpleDateFormat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.swqs.schooltrade.R;
import com.swqs.schooltrade.entity.User;
import com.swqs.schooltrade.util.AvatarView;
import com.swqs.schooltrade.util.Server;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MyInforActivity extends Activity {

	
	TextView textView;
	ProgressBar progress;
	AvatarView avatar;
	
	TextView nickName;
	TextView schoolName;
	TextView sex ;
	TextView telNo;
	TextView emailNo;
	TextView id;
	TextView birthday;
	TextView balance;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_info);
		
		textView = (TextView)findViewById(R.id.text);
		progress = (ProgressBar)findViewById(R.id.progress);
		avatar = (AvatarView)findViewById(R.id.avatar);
		nickName = (TextView) findViewById(R.id.nickName);
		schoolName = (TextView) findViewById(R.id.schoolName);
		sex= (TextView) findViewById(R.id.sex);
		telNo =(TextView) findViewById(R.id.telNo);
		emailNo=(TextView) findViewById(R.id.emailNo);
		id=(TextView) findViewById(R.id.idNo);
		birthday=(TextView) findViewById(R.id.birthdayTime);
		balance=(TextView)findViewById(R.id.balance);
		         
				
	}
	
	@Override
	protected void onResume() {
		textView.setVisibility(View.GONE);
		progress.setVisibility(View.VISIBLE);	
		
		OkHttpClient client = Server.getSharedClient();
		Request request = Server.requestBuilderWithApi("me")
				.method("get", null)
				.build();
		
		client.newCall(request).enqueue(new Callback() {
			
			@Override
			public void onResponse(Call arg0, Response arg1) throws IOException {
				final User user = new ObjectMapper().readValue(arg1.body().bytes(), User.class);
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						nickName.setText(user.getName());				
						schoolName.setText(user.getSchool().getName());
						sex.setText(user.getSex()==1?"ÄÐ":"Å®");
						telNo.setText(user.getPhone());
						emailNo.setText(user.getEmail());	
						SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
						birthday.setText(sdf.format(user.getBirthday()));
					}
				});
				
			}
			
			@Override
			public void onFailure(Call arg0, IOException arg1) {
				
				
			}
		});
		
		super.onResume();
	}
}
