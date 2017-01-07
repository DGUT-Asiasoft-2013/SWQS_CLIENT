package com.swqs.schooltrade.activity;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.swqs.schooltrade.R;
import com.swqs.schooltrade.util.CustomProgressDialog;
import com.swqs.schooltrade.util.FileUtils;
import com.swqs.schooltrade.util.GridPhotoAdapter;
import com.swqs.schooltrade.util.ImageItem;
import com.swqs.schooltrade.util.Server;
import com.swqs.schooltrade.util.Util;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RelativeLayout;
import android.widget.Toast;
import cn.jmessage.android.uikit.chatting.BaseActivity;
import cn.jmessage.android.uikit.multiselectphotos.AlbumListActivity;
import cn.jmessage.android.uikit.multiselectphotos.BrowserPhotoPagerActivity;
import cn.jmessage.android.uikit.multiselectphotos.BrowserViewPagerActivity;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class NewGoodsActivity extends BaseActivity {
	EditText newTitle, newContent, newOriginalPrice;

	private GridView noScrollgridview;
	private GridPhotoAdapter adapter;
	private PopupWindow pop = null;
	private LinearLayout ll_popup;
	private static final int REQUEST_CODE_TAKE_PICTURE = 0x123;
	private static final int REQUEST_CODE_SELECT_ALBUM = 0x124;
	private static final int REQUEST_CODE_BROWSE_PHOTO = 0x125;
	private List<ImageItem> imageItemList = new ArrayList<ImageItem>();
	private String photoPath = null;
	private View parentView;
	private CustomProgressDialog progressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		parentView = View.inflate(this, R.layout.activity_new_goods, null);
		setContentView(parentView);

		newTitle = (EditText) findViewById(R.id.new_title);
		newContent = (EditText) findViewById(R.id.new_content);
		newOriginalPrice = (EditText) findViewById(R.id.new_originalprice);

		findViewById(R.id.button_publish).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				publishContent();
			}
		});
		init();
	}

	public void init() {

		pop = new PopupWindow(this);

		View view = View.inflate(this, R.layout.item_popupwindows, null);

		ll_popup = (LinearLayout) view.findViewById(R.id.ll_popup);

		pop.setWidth(LayoutParams.MATCH_PARENT);
		pop.setHeight(LayoutParams.WRAP_CONTENT);
		pop.setBackgroundDrawable(new BitmapDrawable());
		pop.setFocusable(true);
		pop.setOutsideTouchable(true);
		pop.setContentView(view);

		RelativeLayout parent = (RelativeLayout) view.findViewById(R.id.parent);
		Button btnCamera = (Button) view.findViewById(R.id.item_popupwindows_camera);
		Button btnAlbum = (Button) view.findViewById(R.id.item_popupwindows_album);
		Button btnCancel = (Button) view.findViewById(R.id.item_popupwindows_cancel);
		parent.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				pop.dismiss();
				ll_popup.clearAnimation();
			}
		});
		btnCamera.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				photo();
				pop.dismiss();
				ll_popup.clearAnimation();
			}
		});
		btnAlbum.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(NewGoodsActivity.this, AlbumListActivity.class);
				startActivityForResult(intent, REQUEST_CODE_SELECT_ALBUM);
				pop.dismiss();
				ll_popup.clearAnimation();
			}
		});
		btnCancel.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				pop.dismiss();
				ll_popup.clearAnimation();
			}
		});

		noScrollgridview = (GridView) findViewById(R.id.input_image);
		noScrollgridview.setSelector(new ColorDrawable(Color.TRANSPARENT));
		adapter = new GridPhotoAdapter(this, imageItemList);
		noScrollgridview.setAdapter(adapter);
		noScrollgridview.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if (position == imageItemList.size()) {
					ll_popup.startAnimation(
							AnimationUtils.loadAnimation(NewGoodsActivity.this, R.anim.activity_translate_in));
					pop.showAtLocation(parentView, Gravity.BOTTOM, 0, 0);
				} else {
					Intent intent = new Intent(NewGoodsActivity.this, BrowserPhotoPagerActivity.class);
					intent.putExtra(BrowserPhotoPagerActivity.CUR_IMG_POSITION, position);
					ArrayList<String> pathArray = new ArrayList<String>();
					for (ImageItem item : imageItemList) {
						pathArray.add(item.getImagePath());
					}
					intent.putStringArrayListExtra(BrowserPhotoPagerActivity.IMG_PATH_ARRAY, pathArray);
					startActivityForResult(intent, REQUEST_CODE_BROWSE_PHOTO);
				}
			}
		});
		noScrollgridview.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				if (position != imageItemList.size()) {
					showLongClickDialog(view, position);
				}
				return true;
			}
		});
	}

	private void showLongClickDialog(View parentView, final int position) {

		final PopupWindow popDialog = new PopupWindow(this);

		View view = View.inflate(this, R.layout.dialog_delete_img, null);

		popDialog.setWidth((int) (0.7 * mWidth));
		popDialog.setHeight(LayoutParams.WRAP_CONTENT);
		popDialog.setBackgroundDrawable(new BitmapDrawable());
		popDialog.setFocusable(true);
		popDialog.setOutsideTouchable(true);
		popDialog.setContentView(view);

		Button btnDel = (Button) view.findViewById(R.id.btnDelImg);
		Button btnCancel = (Button) view.findViewById(R.id.btnCancel);
		btnDel.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				popDialog.dismiss();
				ImageItem takePhoto = imageItemList.get(position);
				imageItemList.remove(takePhoto);
				adapter.notifyDataSetChanged();
			}
		});
		btnCancel.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				popDialog.dismiss();
			}
		});
		popDialog.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss() {
				setBackgroundAlpha(1);
			}
		});
		popDialog.showAtLocation(parentView, Gravity.CENTER, 0, 0);
		setBackgroundAlpha(0.7f);
	}

	private void setBackgroundAlpha(float alpha) {
		WindowManager.LayoutParams lp = getWindow().getAttributes();
		lp.alpha = alpha;
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
		getWindow().setAttributes(lp);
	}

	public void photo() {
		File FileDir = new File(FileUtils.SDPATH);
		if (!FileDir.exists()) {
			FileDir.mkdirs();
		}
		photoPath = FileUtils.SDPATH + "/" + new Date().getTime() + ".jpg";
		Uri photoUri = Uri.fromFile(new File(photoPath));
		Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
		startActivityForResult(openCameraIntent, REQUEST_CODE_TAKE_PICTURE);
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case REQUEST_CODE_TAKE_PICTURE:
			if (imageItemList.size() < adapter.getMax() && resultCode == RESULT_OK) {

				ImageItem takePhoto = new ImageItem();
				try {
					takePhoto.setBitmap(FileUtils.revitionImageSize(photoPath));
				} catch (IOException e) {
					e.printStackTrace();
				}
				takePhoto.setImagePath(photoPath);
				imageItemList.add(takePhoto);
				adapter.notifyDataSetChanged();
			}
			break;
		case REQUEST_CODE_SELECT_ALBUM:
			if (resultCode == AlbumListActivity.RESULT_CODE_SELECT_PICTURE) {
				// 得到图片路径
				ArrayList<String> pathList = data.getStringArrayListExtra(BrowserViewPagerActivity.PICTURE_PATH);
				for (String path : pathList) {
					ImageItem takePhoto = new ImageItem();
					try {
						takePhoto.setBitmap(FileUtils.revitionImageSize(path));
					} catch (IOException e) {
						e.printStackTrace();
					}
					takePhoto.setImagePath(path);
					if (imageItemList.size() < adapter.getMax()) {
						imageItemList.add(takePhoto);
					}
					// if(!imageItemList.contains(takePhoto)&&imageItemList.size()<adapter.getMax()){
					// imageItemList.add(takePhoto);
					// }
				}
				adapter.notifyDataSetChanged();
			}
			break;
		case REQUEST_CODE_BROWSE_PHOTO:
			if (resultCode == BrowserPhotoPagerActivity.RESULT_CODE_SELECT_PICTURE) {
				// TODO 待优化
				// 得到图片路径
				ArrayList<String> pathArray = data.getStringArrayListExtra(BrowserPhotoPagerActivity.IMG_PATH_ARRAY);
				imageItemList.clear();
				for (String path : pathArray) {
					ImageItem takePhoto = new ImageItem();
					try {
						takePhoto.setBitmap(FileUtils.revitionImageSize(path));
					} catch (IOException e) {
						e.printStackTrace();
					}
					takePhoto.setImagePath(path);
					imageItemList.add(takePhoto);
				}
				adapter.notifyDataSetChanged();
			}
			break;
		}
	}

	void publishContent() {
		String title = newTitle.getText().toString();
		String content = newContent.getText().toString();
		String originalprice = newOriginalPrice.getText().toString();
		if (TextUtils.isEmpty(title)) {
			Toast.makeText(this, "请输入标题", Toast.LENGTH_SHORT).show();
			return;
		}
		if (TextUtils.isEmpty(content)) {
			Toast.makeText(this, "请输入商品描述", Toast.LENGTH_SHORT).show();
			return;
		}
		if (TextUtils.isEmpty(originalprice)) {
			Toast.makeText(this, "请输入商品价格", Toast.LENGTH_SHORT).show();
			return;
		}
		if(imageItemList.size()<=0){
			Toast.makeText(this, "请选择商品图片", Toast.LENGTH_SHORT).show();
			return;
		}
		progressDialog=Util.getProgressDialog(this, R.layout.custom_progressdialog);
		progressDialog.show();
		MultipartBody.Builder body = new MultipartBody.Builder()
				.addFormDataPart("title", title)
				.addFormDataPart("content", content)
				.addFormDataPart("originalPrice", originalprice);

		for (int i = 0; i < imageItemList.size(); i++) {
			ImageItem item=imageItemList.get(i);
			File file = new File(item.getImagePath());
			body.addFormDataPart("listImage", "listImage"+i, RequestBody.create(MediaType.parse("image/png"), file));
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

	void onSucceed(String text) {
		progressDialog.dismiss();
		Toast.makeText(this, "发布成功", Toast.LENGTH_SHORT).show();
		finish();
		overridePendingTransition(R.anim.none, R.anim.slide_out_bottom);
		// new AlertDialog.Builder(this).setMessage(text)
		// .setPositiveButton("OK", new DialogInterface.OnClickListener() {
		//
		// @Override
		// public void onClick(DialogInterface dialog, int which) {
		// finish();
		// overridePendingTransition(R.anim.none, R.anim.slide_out_bottom);
		// }
		// }).show();
	}

	void onFailure(Exception e) {
		progressDialog.dismiss();
		new AlertDialog.Builder(this).setMessage(e.getMessage()).show();
	}
}
