package com.swqs.schooltrade;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.ProgressBar;
import android.widget.Toast;

public class MyCredit extends Activity {
	private ProgressBar PB1;
	private int mProgressStatus=0;
	private Handler mHandler;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_credit);
		PB1=(ProgressBar) findViewById(R.id.progressBar1);
		mHandler= new Handler(){
			public void handleMessage(android.os.Message msg) {
				if(msg.what==0x111){
					PB1.setProgress(mProgressStatus);
				}else{
					Toast.makeText(MyCredit.this, "∫Ù¿≤¿≤∫Ù¿≤¿≤", Toast.LENGTH_SHORT).show();
				}
			}
		};
			
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				while(true){
					mProgressStatus=doWork();
					Message m= new Message();
					if(mProgressStatus<100){
						m.what=0x111;
						mHandler.sendMessage(m);
					}else{
						m.what=0x111;
						mHandler.sendMessage(m);
						break;
					}
				}
				
			}
			private int doWork(){
				mProgressStatus+=Math.random()*10;
				try{
					Thread.sleep(200);
				}catch(InterruptedException e){
					e.printStackTrace();
				}
				return mProgressStatus;
			}
		}).start();
		
	}

	
}
