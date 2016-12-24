package com.swqs.schooltrade;

import java.io.IOException;

import com.swqs.schooltrade.api.Server;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import inputcell.ImageInputCellFragment;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class NewGoodsActivity extends Activity {
    EditText editTitle, editContent, editOriginalPrice;
    ImageInputCellFragment fragInputImage;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	setContentView(R.layout.activity_new_goods);
    	
    	editTitle = (EditText) findViewById(R.id.edit_title);
    	editContent = (EditText) findViewById(R.id.edit_content);
    	editOriginalPrice = (EditText) findViewById(R.id.edit_originalprice);
   
    	fragInputImage = (ImageInputCellFragment) getFragmentManager().findFragmentById(R.id.input_image);
    	
    	findViewById(R.id.button_publish).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				publishContent();
			}
		});
    }
    
    void publishContent(){
    	String title = editTitle.getText().toString();
    	String content = editContent.getText().toString();
    	String originalPrice = editOriginalPrice.getText().toString();    	
    	
    	MultipartBody.Builder body = new MultipartBody.Builder()
				.addFormDataPart("title", title)
				.addFormDataPart("content", content)
				.addFormDataPart("originalPrice", originalPrice);			
    	
    	if(fragInputImage.getPngData()!=null){
			body.addFormDataPart(
					"listImage",
					"listImage",
					RequestBody.create(MediaType.parse("image/png"),
					fragInputImage.getPngData()));
		} 	
    	
    	Request request = Server.requestBuilderWithApi("addgoods").post(body.build()).build();

		Server.getSharedClient().newCall(request).enqueue(new Callback() {
			@Override
			public void onResponse(Call arg0, Response arg1) throws IOException {
				final String responseBody = arg1.body().string();

				runOnUiThread(new Runnable() {
					public void run() {
						NewGoodsActivity.this.onSucceed(responseBody);
					}
				});
			}

			@Override
			public void onFailure(Call arg0, final IOException arg1) {
				runOnUiThread(new Runnable() {
					public void run() {
						NewGoodsActivity.this.onFailure(arg1);
					}
				});
			}
		});
	}

	void onSucceed(String text){
		Toast.makeText(this, "发布成功", Toast.LENGTH_SHORT).show();
		finish();
		overridePendingTransition(R.anim.none, R.anim.slide_out_bottom);
//		new AlertDialog.Builder(this).setMessage(text)
//		.setPositiveButton("OK", new DialogInterface.OnClickListener() {
//
//			@Override
//			public void onClick(DialogInterface dialog, int which) {
//				finish();
//				overridePendingTransition(R.anim.none, R.anim.slide_out_bottom);
//			}
//		}).show();
	}

	void onFailure(Exception e){
		new AlertDialog.Builder(this).setMessage(e.getMessage()).show();
	}
}
