package com.swqs.schooltrade.activity;

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.swqs.schooltrade.R;
import com.swqs.schooltrade.app.TradeApplication;
import com.swqs.schooltrade.entity.Goods;
import com.swqs.schooltrade.util.Server;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.Response;

public class EditGoodsActivity extends Activity {   
    private Goods goods;
	
    EditText editTitle, editContent, editCurPrice;
    Button buttonCompleteEdit;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	
    	setContentView(R.layout.activity_edit_goods);
    	
    	goods = (Goods) getIntent().getSerializableExtra("data");
		
    	editTitle = (EditText) findViewById(R.id.edit_title);
    	editContent = (EditText) findViewById(R.id.edit_content);
    	editCurPrice = (EditText) findViewById(R.id.edit_curprice);
    	buttonCompleteEdit = (Button) findViewById(R.id.btnCompleteEdit);
    	
    	editTitle.setText(goods.getTitle());
    	editContent.setText(goods.getContent());
    	editCurPrice.setText(goods.getCurPrice() + "");
    	
    	buttonCompleteEdit.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				CompleteEdit();				
			}
		});
    }
    
    void CompleteEdit(){
    	String title = editTitle.getText().toString();
		String content = editContent.getText().toString();
		String curprice = editCurPrice.getText().toString();
		
		MultipartBody.Builder body = new MultipartBody.Builder()
				.addFormDataPart("title", title)
				.addFormDataPart("content", content)
				.addFormDataPart("curPrice", curprice)
				.addFormDataPart("uid", TradeApplication.uid);
		
		Request request = Server.requestBuilderWithApi("/updategoods/"+goods.getId()).post(body.build()).build();

		Server.getSharedClient().newCall(request).enqueue(new Callback() {
			@Override
			public void onResponse(Call arg0, Response arg1) throws IOException {
				final String responseBody = arg1.body().string();

				runOnUiThread(new Runnable() {
					public void run() {
						ObjectMapper mapper=new ObjectMapper();
						try {
							goods=mapper.readValue(responseBody, Goods.class);
							if(goods!=null){
								EditGoodsActivity.this.onSucceed(goods);
							}
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				});
			}

			@Override
			public void onFailure(Call arg0, final IOException arg1) {
				runOnUiThread(new Runnable() {
					public void run() {
						EditGoodsActivity.this.onFailure(arg1);
					}
				});
			}
		});
	}

	void onSucceed(Goods goods) {
		Toast.makeText(this, "±à¼­³É¹¦", Toast.LENGTH_SHORT).show();
		Intent intent=new Intent(this,GoodsContentActivity.class);
		intent.putExtra("goods", goods);
		setResult(RESULT_OK, intent);
		finish();
		overridePendingTransition(R.anim.none, R.anim.slide_out_bottom);
	}

	void onFailure(Exception e) {
		new AlertDialog.Builder(this).setMessage(e.getMessage()).show();
	}
}
