package com.swqs.schooltrade;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

public class Jump extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		setContentView(R.layout.activity_recharge);
		super.onCreate(savedInstanceState);
		Dialog alertDialog = new AlertDialog.Builder(this).
			    setTitle("��ʾ��").
			    setMessage("��ֵ�ɹ���").
			    setIcon(R.drawable.ic_launcher).
			    setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
			     
			     @Override
			     public void onClick(DialogInterface dialog, int which) {
			       goMoney();
			    	 
			     }
			    }).
			    setNegativeButton("û��", new DialogInterface.OnClickListener() {
			     
			     @Override
			     public void onClick(DialogInterface dialog, int which) {
			      // TODO Auto-generated method stub
			     }
			    }).
			    
			    create();
			  alertDialog.show();
			 }
	void goMoney(){
		Intent it2=new Intent(this,Money.class);
		startActivity(it2);
	}
	}
	

