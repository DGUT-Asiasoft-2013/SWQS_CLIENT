package com.swqs.schooltrade.activity;

import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.swqs.schooltrade.R;
import com.swqs.schooltrade.app.TradeApplication;
import com.swqs.schooltrade.entity.Comment;
import com.swqs.schooltrade.entity.Goods;
import com.swqs.schooltrade.entity.User;
import com.swqs.schooltrade.util.CustomProgressDialog;
import com.swqs.schooltrade.util.MD5;
import com.swqs.schooltrade.util.RoundImageView;
import com.swqs.schooltrade.util.Server;
import com.swqs.schooltrade.util.Util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import cn.jmessage.android.uikit.chatting.ChatActivity;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class GoodsContentActivity extends Activity {
	protected static final String TAG = "GoodsContentActivity";
	private Goods goods;

	List<Comment> comments;
	ListView lvImage;
	ListView lvComment;
	User user;
	TextView textTitle, textContent, textAccount, textPrice, textDate;
	EditText editComment;
	Button buttonBack, buttonSendComment;
	RelativeLayout layoutOthers;
	RelativeLayout layoutMe;
	RelativeLayout layoutComment;
	private CustomProgressDialog progressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.widget_goods_detail);

		goods = (Goods) getIntent().getSerializableExtra("data");

		textTitle = (TextView) findViewById(R.id.content_title);
		textContent = (TextView) findViewById(R.id.content_item);
		textAccount = (TextView) findViewById(R.id.account_id);
		textPrice = (TextView) findViewById(R.id.content_originalprice);
		textDate = (TextView) findViewById(R.id.content_createDate);

		lvImage = (ListView) findViewById(R.id.image_goods);
		RoundImageView avatar = (RoundImageView) findViewById(R.id.avatar);

		textTitle.setText(goods.getTitle());
		textContent.setText(goods.getContent());
		textAccount.setText(goods.getAccount().getAccount());
		textPrice.setText(goods.getOriginalPrice() + "");

		Util.loadImage(this, goods.getAccount().getFace_url(), avatar);

		String dateStr = DateFormat.format("yyyy-MM-dd hh:mm", goods.getCreateDate()).toString();
		textDate.setText(dateStr);

		findViewById(R.id.btnCommentOthers).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				makeComment();
			}
		});
		findViewById(R.id.btnCommentMe).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				makeComment();
			}
		});

		findViewById(R.id.btnContact).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				chatToSeller();
			}
		});

		findViewById(R.id.btnBuy).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				gotoBuy();
			}
		});

		findViewById(R.id.btnEdit).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(GoodsContentActivity.this, EditGoodsActivity.class);
				intent.putExtra("data", goods);
				startActivityForResult(intent, 1);
			}
		});

		lvComment = (ListView) findViewById(R.id.commentlist);
		lvComment.setAdapter(adapter);
		lvImage.setAdapter(goodsImgaeAdapter);

		layoutOthers = (RelativeLayout) findViewById(R.id.layoutOthers);
		layoutMe = (RelativeLayout) findViewById(R.id.layoutMe);

		layoutComment = (RelativeLayout) findViewById(R.id.layoutComment);
		editComment = (EditText) findViewById(R.id.editComment);
		buttonBack = (Button) findViewById(R.id.btnBack);
		buttonSendComment = (Button) findViewById(R.id.btnSendComment);
		lvComment.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent intent = new Intent(GoodsContentActivity.this, CommentToOthersActivity.class);
				intent.putExtra("comment", comments.get(position));
				startActivity(intent);
			}
		});
		getUser();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			goods = (Goods) data.getSerializableExtra("goods");
			textTitle.setText(goods.getTitle());
			textContent.setText(goods.getContent());
			textPrice.setText(goods.getCurPrice() + "");
		}
	}

	protected void chatToSeller() {
		Intent intent = new Intent(this, ChatActivity.class);
		intent.putExtra(TradeApplication.TARGET_ID, goods.getAccount().getAccount());
		// intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
		// Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
	}

	public static void setListViewHeightBasedOnChildren(ListView listView) {
		ListAdapter listAdapter = listView.getAdapter();
		if (listAdapter == null) {
			return;
		}
		int totalHeight = 0;
		for (int i = 0; i < listAdapter.getCount(); i++) {
			View listItem = listAdapter.getView(i, null, listView);
			listItem.measure(0, 0);
			totalHeight = totalHeight + listItem.getMeasuredHeight() + listItem.getPaddingTop()
					+ listItem.getPaddingBottom();
		}

		ViewGroup.LayoutParams params = listView.getLayoutParams();
		params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
		listView.setLayoutParams(params);
	}

	BaseAdapter goodsImgaeAdapter = new BaseAdapter() {

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {
				convertView = View.inflate(GoodsContentActivity.this, R.layout.item_goods_image, null);
				holder = new ViewHolder();
				holder.ivGoodsImage = (ImageView) convertView.findViewById(R.id.image_goods);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			Util.loadImage(GoodsContentActivity.this, goods.getListImage().get(position).getPictureUrl(),
					holder.ivGoodsImage);
			return convertView;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public int getCount() {
			return goods.getListImage() == null ? 0 : goods.getListImage().size();
		}

		class ViewHolder {
			ImageView ivGoodsImage;
		}
	};

	// 留言区列表
	BaseAdapter adapter = new BaseAdapter() {

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = null;
			ViewHolder viewHolder;

			if (convertView == null) {
				viewHolder = new ViewHolder();
				view = View.inflate(GoodsContentActivity.this, R.layout.widget_comment_item, null);
				viewHolder.Comment = (TextView) view.findViewById(R.id.comment);
				viewHolder.Account = (TextView) view.findViewById(R.id.id_account);
				viewHolder.CreateDate = (TextView) view.findViewById(R.id.createdate);
				viewHolder.Avatar = (RoundImageView) view.findViewById(R.id.account_avatar);
				viewHolder.tvNumFloor = (TextView) view.findViewById(R.id.tvNumFloor);
				view.setTag(viewHolder);
			} else {
				view = convertView;
				viewHolder = (ViewHolder) view.getTag();
			}

			Comment comment = comments.get(position);
			if (comment.getParentComment() != null) {
				viewHolder.Comment
						.setText("回复@" + comment.getParentComment().getAccount().getName() + ":" + comment.getText());
			} else {
				viewHolder.Comment.setText(comment.getText());
			}
			if (position == 0) {
				viewHolder.tvNumFloor.setText("沙发");
			} else if (position == 1) {
				viewHolder.tvNumFloor.setText("板凳");
			} else if (position == 2) {
				viewHolder.tvNumFloor.setText("地板");
			} else {
				viewHolder.tvNumFloor.setText(position + 1 + "楼");
			}
			viewHolder.Account.setText(comment.getAccount().getAccount());
			// viewHolder.Avatar.load(comment.getAccount().getFace_url());
			Util.loadImage(GoodsContentActivity.this, comment.getAccount().getFace_url(), viewHolder.Avatar);

			String dateStr = DateFormat.format("yyyy-MM-dd hh:mm", comment.getCreateDate()).toString();
			viewHolder.CreateDate.setText(dateStr);

			return view;
		};

		class ViewHolder {
			public TextView Comment;
			public TextView Account;
			public TextView CreateDate;
			public RoundImageView Avatar;
			public TextView tvNumFloor;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public Object getItem(int position) {
			return comments.get(position);
		}

		@Override
		public int getCount() {
			return comments == null ? 0 : comments.size();
		}
	};

	// 发表留言
	void makeComment() {
		if (user.getId().equals(goods.getAccount().getId())) {
			layoutMe.setVisibility(View.GONE);
			layoutComment.setVisibility(View.VISIBLE);
		} else {
			layoutOthers.setVisibility(View.GONE);
			layoutComment.setVisibility(View.VISIBLE);
		}

		buttonSendComment.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				sendComment();
			}
		});

		buttonBack.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (user.getId().equals(goods.getAccount().getId())) {
					layoutComment.setVisibility(View.GONE);
					layoutMe.setVisibility(View.VISIBLE);
				} else {
					layoutComment.setVisibility(View.GONE);
					layoutOthers.setVisibility(View.VISIBLE);
				}
			}
		});
	}

	void sendComment() {
		String text = editComment.getText().toString();

		MultipartBody body = new MultipartBody.Builder().addFormDataPart("text", text)
				.addFormDataPart("uid", TradeApplication.uid).build();

		Request request = Server.requestBuilderWithApi("/goods/" + goods.getId() + "/addParentComments").post(body)
				.build();

		Server.getSharedClient().newCall(request).enqueue(new Callback() {
			@Override
			public void onResponse(Call arg0, Response arg1) throws IOException {
				final String responseBody = arg1.body().string();

				runOnUiThread(new Runnable() {
					public void run() {
						editComment.setText("");
						reload();
						if (user.getId().equals(goods.getAccount().getId())) {
							layoutComment.setVisibility(View.GONE);
							layoutMe.setVisibility(View.VISIBLE);
						} else {
							layoutComment.setVisibility(View.GONE);
							layoutOthers.setVisibility(View.VISIBLE);
						}
					}
				});
			}

			@Override
			public void onFailure(Call arg0, final IOException arg1) {
				runOnUiThread(new Runnable() {
					public void run() {
						GoodsContentActivity.this.onFailure(arg1);
					}
				});
			}
		});
	}

	// 购买
	void gotoBuy() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		final View dialogView = View.inflate(this, R.layout.dialog_input_password, null);
		builder.setView(dialogView);
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				EditText etPwd = (EditText) dialogView.findViewById(R.id.etPwd);
				String pwd = etPwd.getText().toString();
				if (TextUtils.isEmpty(pwd)) {
					Toast.makeText(GoodsContentActivity.this, "请输入支付密码", Toast.LENGTH_SHORT).show();
					return;
				}
				if (user != null && user.getBalance() < goods.getCurPrice()) {
					Toast.makeText(GoodsContentActivity.this, "余额不足，请充值", Toast.LENGTH_SHORT).show();
					return;
				}
				dialog.dismiss();
				// TODO 去服务器验证支付密码是否正确
				buyGoods(pwd);
			}
		}).show();
	}

	private void buyGoods(String pwd) {
		progressDialog = Util.getProgressDialog(this, R.layout.custom_progressdialog);
		progressDialog.show();
		MultipartBody.Builder requestBodyBuilder = new MultipartBody.Builder()
				.addFormDataPart("password", MD5.getMD5(pwd)).addFormDataPart("uid", TradeApplication.uid);

		Request request = Server.requestBuilderWithApi("buygoods/" + goods.getId()).method("get", null)
				.post(requestBodyBuilder.build()).build();

		Server.getSharedClient().newCall(request).enqueue(new Callback() {

			@Override
			public void onResponse(Call arg0, Response arg1) throws IOException {
				String responseString = arg1.body().string();
				progressDialog.dismiss();
				if ("passwordIsNotRight".equals(responseString)) {
					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							Toast.makeText(GoodsContentActivity.this, "支付密码错误", Toast.LENGTH_SHORT).show();
						}
					});

				} else if ("Success".equals(responseString)) {
					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							Toast.makeText(GoodsContentActivity.this, "购买成功", Toast.LENGTH_SHORT).show();
							finish();
						}
					});
				} else {
					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							Toast.makeText(GoodsContentActivity.this, "服务器错误", Toast.LENGTH_SHORT).show();
							finish();
						}
					});
				}
			}

			@Override
			public void onFailure(Call arg0, IOException arg1) {
				progressDialog.dismiss();
				Toast.makeText(GoodsContentActivity.this, arg1.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
			}
		});
	}

	private void getUser() {
		OkHttpClient client = Server.getSharedClient();
		FormBody requestBody = new FormBody.Builder().add("uid", TradeApplication.uid).build();
		Request request = Server.requestBuilderWithApi("me").method("post", null).post(requestBody).build();
		// 异步发起请求
		client.newCall(request).enqueue(new Callback() {

			@Override
			public void onResponse(final Call arg0, final Response arg1) throws IOException {

				String jsonString = arg1.body().string();
				ObjectMapper mapper = new ObjectMapper();
				try {
					user = mapper.readValue(jsonString, User.class);
					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							if (user.getId().equals(goods.getAccount().getId())) {
								layoutOthers.setVisibility(View.GONE);
								layoutMe.setVisibility(View.VISIBLE);
							} else {
								layoutMe.setVisibility(View.GONE);
								layoutOthers.setVisibility(View.VISIBLE);
							}
						}
					});
				} catch (Exception e) {
					e.printStackTrace();
				}

			}

			@Override
			public void onFailure(Call arg0, IOException arg1) {
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		setListViewHeightBasedOnChildren(lvImage);
		reload();
	}

	void reload() {

		Request request = Server.requestBuilderWithApi("/goods/" + goods.getId() + "/comments").get().build();
		Server.getSharedClient().newCall(request).enqueue(new Callback() {
			@Override
			public void onResponse(Call arg0, Response arg1) throws IOException {
				try {
					final List<Comment> data = new ObjectMapper().readValue(arg1.body().string(),
							new TypeReference<List<Comment>>() {
							});

					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							GoodsContentActivity.this.reloadData(data);
						}
					});
				} catch (final Exception e) {
					runOnUiThread(new Runnable() {
						public void run() {
							GoodsContentActivity.this.onFailure(e);
						}
					});
				}
			}

			@Override
			public void onFailure(Call arg0, final IOException e) {
				runOnUiThread(new Runnable() {
					public void run() {
						GoodsContentActivity.this.onFailure(e);
					}
				});
			}
		});
	}

	protected void reloadData(List<Comment> data) {
		comments = data;
		adapter.notifyDataSetChanged();
		setListViewHeightBasedOnChildren(lvComment);
	}

	protected void appendData(List<Comment> data) {
		adapter.notifyDataSetChanged();
	}

	void onFailure(Exception e) {
		new AlertDialog.Builder(this).setMessage(e.getMessage()).show();
	}
}
