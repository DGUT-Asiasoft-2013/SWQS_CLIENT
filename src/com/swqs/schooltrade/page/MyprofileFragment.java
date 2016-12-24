package com.swqs.schooltrade.page;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.swqs.schooltrade.R;
import com.swqs.schooltrade.activity.BuyAndSelledActivity;
import com.swqs.schooltrade.activity.MyCollection;
import com.swqs.schooltrade.activity.MyCredit;
import com.swqs.schooltrade.activity.MyInforActivity;
import com.swqs.schooltrade.activity.MyMoneyActivity;
import com.swqs.schooltrade.activity.MyPublishGoodsActivity;
import com.swqs.schooltrade.entity.User;
import com.swqs.schooltrade.util.AvatarView;
import com.swqs.schooltrade.util.Server;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MyprofileFragment extends Fragment {

	View view = null;
	AvatarView avatar;
	TextView tvUsername;
	public static final int REQUESTCODE_CAMERA = 0x123;
	public static final int REQUESTCODE_ALBUM = 0x124;
	private byte[] pngData;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (view == null) {
			view = inflater.inflate(R.layout.fragment_page_myprofile, null);
			avatar = (AvatarView) view.findViewById(R.id.avatar);
			avatar.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					updateImage();
				}
			});
			view.findViewById(R.id.tvPublish).setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					myPublish();

				}
			});
			view.findViewById(R.id.tvBuyed).setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					hasBuyed();
				}
			});
			view.findViewById(R.id.tvSelled).setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					hasSelled();
				}
			});
			view.findViewById(R.id.tvData).setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					myInformation();
				}
			});
			view.findViewById(R.id.tvCollection).setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					goCollection();
				}
			});
			view.findViewById(R.id.tvReputation).setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					goReputation();
				}
			});
			view.findViewById(R.id.tvUser).setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					goUser();
				}
			});
			tvUsername = (TextView) view.findViewById(R.id.tvUsername);
			getUser();
		}
		return view;
	}

	private void myInformation() {
		Intent intent = new Intent(getActivity(), MyInforActivity.class);
		startActivity(intent);
	}

	private void updateImage() {
		String[] items = { "拍照", "相册" };

		new AlertDialog.Builder(getActivity()).setTitle("选择方式").setItems(items, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
				case 0:
					takePhoto();
					break;

				case 1:
					pickFromAlbum();
					break;

				default:
					break;
				}
			}
		}).show();
	}

	void takePhoto() {
		Intent itnt = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		startActivityForResult(itnt, REQUESTCODE_CAMERA);
	}

	void pickFromAlbum() {
		Intent itnt = new Intent(Intent.ACTION_GET_CONTENT);
		itnt.setType("image/*");
		startActivityForResult(itnt, REQUESTCODE_ALBUM);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == Activity.RESULT_CANCELED)
			return;
		if (requestCode == REQUESTCODE_CAMERA) {
			Bitmap bmp = (Bitmap) data.getExtras().get("data");
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			bmp.compress(CompressFormat.PNG, 100, baos);
			pngData = baos.toByteArray();
			uploadImage();
		} else if (requestCode == REQUESTCODE_ALBUM) {
			try {
				Bitmap bmp = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), data.getData());
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				bmp.compress(CompressFormat.PNG, 100, baos);
				pngData = baos.toByteArray();
				uploadImage();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void uploadImage() {
		if (pngData != null) {
			MultipartBody.Builder body = new MultipartBody.Builder();
			body.addFormDataPart("avatar", "avatar", RequestBody.create(MediaType.parse("image/png"), pngData));
			Request request = Server.requestBuilderWithApi("updateFace").post(body.build()).build();

			Server.getSharedClient().newCall(request).enqueue(new Callback() {
				
				@Override
				public void onResponse(Call arg0, Response arg1) throws IOException {
					final String responseSrting = arg1.body().string();
					final ObjectMapper mapper = new ObjectMapper();
					final User user = mapper.readValue(responseSrting, User.class);
					getActivity().runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							avatar.load(user.getFace_url());
						}
					});
				}
				
				@Override
				public void onFailure(Call arg0, IOException arg1) {
					
				}
			});
		}
		
	}

	private void myPublish() {
		Intent intent = new Intent(getActivity(), MyPublishGoodsActivity.class);
		startActivity(intent);
	}

	private void getUser() {
		OkHttpClient client = Server.getSharedClient();
		Request request = Server.requestBuilderWithApi("me").method("GET", null).build();
		// 异步发起请求
		client.newCall(request).enqueue(new Callback() {

			@Override
			public void onResponse(final Call arg0, final Response arg1) throws IOException {

				String jsonString = arg1.body().string();
				ObjectMapper mapper = new ObjectMapper();
				try {
					final User user = mapper.readValue(jsonString, User.class);
					if (user != null) {
						getActivity().runOnUiThread(new Runnable() {

							@Override
							public void run() {
								tvUsername.setText(user.getName());
								avatar.load(user);
							}
						});
					}
				} catch (Exception e) {
					e.printStackTrace();
					getActivity().runOnUiThread(new Runnable() {

						@Override
						public void run() {
							tvUsername.setText("解析失败");
						}
					});
					return;
				}

			}

			@Override
			public void onFailure(Call arg0, IOException arg1) {
				getActivity().runOnUiThread(new Runnable() {

					@Override
					public void run() {
						tvUsername.setText("請求失败");
					}
				});
			}
		});
	}

	private void hasSelled() {
		Intent intent = new Intent(getActivity(), BuyAndSelledActivity.class);
		intent.putExtra("flag", "sell");
		startActivity(intent);
	}

	private void hasBuyed() {
		Intent intent = new Intent(getActivity(), BuyAndSelledActivity.class);
		intent.putExtra("flag", "buy");
		startActivity(intent);
	}

	private void goCollection() {
		Intent itnt = new Intent(getActivity(), MyCollection.class);
		startActivity(itnt);
	}

	private void goReputation() {
		Intent itnt = new Intent(getActivity(), MyCredit.class);
		startActivity(itnt);
	}

	private void goUser() {
		Intent itnt = new Intent(getActivity(), MyMoneyActivity.class);
		startActivity(itnt);
	}

}
