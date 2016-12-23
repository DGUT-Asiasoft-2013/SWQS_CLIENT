package com.swqs.schooltrade;

import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.swqs.schooltrade.api.Server;
import com.swqs.schooltrade.api.entity.Comment;
import com.swqs.schooltrade.api.entity.Goods;
import com.swqs.schooltrade.api.entity.User;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class GoodsContentActivity extends Activity {
	protected static final String TAG = "GoodsContentActivity";
	private Goods goods;
	// private View loadMoreView;
	private Button btnLikes;

	List<Comment> comments;
	User user;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.widget_goods_detail);

		goods = (Goods) getIntent().getSerializableExtra("data");

		TextView textContent = (TextView) findViewById(R.id.content_item);
		TextView textTitle = (TextView) findViewById(R.id.content_title);
		TextView textAccount = (TextView) findViewById(R.id.account_id);
		TextView textPrice = (TextView) findViewById(R.id.content_originalprice);
		TextView textDate = (TextView) findViewById(R.id.content_createDate);

		ImageView imageGoods = (ImageView) findViewById(R.id.image_goods);
		AvatarView avatar = (AvatarView) findViewById(R.id.avatar);

		textContent.setText(goods.getContent());
		textTitle.setText(goods.getTitle());
		textAccount.setText(goods.getAccount().getAccount());
		textPrice.setText(goods.getOriginalPrice() + "");

		Util.loadImage(this, goods.getListImage().get(0).getPictureUrl(), imageGoods);
		avatar.load(goods.getAccount());

		String dateStr = DateFormat.format("yyyy-MM-dd", goods.getCreateDate()).toString();
		textDate.setText(dateStr);

		findViewById(R.id.button_comment).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				makeComment();
			}
		});

		findViewById(R.id.button_like).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				toggleLikes();
			}
		});

		findViewById(R.id.button_buy).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				gotoBuy();
			}
		});

		ListView list = (ListView) findViewById(R.id.commentlist);
		list.setAdapter(adapter);
		getUser();
	}

	// 留言区列表
	BaseAdapter adapter = new BaseAdapter() {

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = null;
			ViewHolder viewHolder;

			if (convertView == null) {
				viewHolder = new ViewHolder();
				LayoutInflater inflater = LayoutInflater.from(parent.getContext());
				view = inflater.inflate(R.layout.widget_comment_item, null);
				viewHolder.ParentComment = (TextView) view.findViewById(R.id.parentcomment);
				viewHolder.Account = (TextView) view.findViewById(R.id.id_account);
				viewHolder.CreateDate = (TextView) view.findViewById(R.id.createdate);
				viewHolder.Avatar = (AvatarView) view.findViewById(R.id.account_avatar);
				view.setTag(viewHolder);
			} else {
				view = convertView;
				viewHolder = (ViewHolder) view.getTag();
			}

			Comment comment = comments.get(position);
			viewHolder.ParentComment.setText(comment.getText());
			viewHolder.Account.setText(comment.getAccount().getAccount());
			viewHolder.Avatar.load(comment.getAccount().getFace_url());

			String dateStr = DateFormat.format("yyyy-MM-dd", comment.getCreateDate()).toString();
			viewHolder.CreateDate.setText(dateStr);

			return view;
		};

		class ViewHolder {
			public TextView ParentComment;
			public TextView Account;
			public TextView CreateDate;
			public AvatarView Avatar;
		}

		@Override
		public long getItemId(int position) {
			return comments.get(position).getId();
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

	// 发表留言方法
	void makeComment() {
		Intent itnt = new Intent(this, NewCommentActivity.class);
		itnt.putExtra("data", goods);
		startActivity(itnt);
		overridePendingTransition(R.anim.slide_in_bottom, R.anim.none);
	}

	// 点赞方法
	void toggleLikes() {
		MultipartBody body = new MultipartBody.Builder().addFormDataPart("likes", String.valueOf(!isLiked)).build();

		Request request = Server.requestBuilderWithApi("goods/" + goods.getId() + "/likes").post(body).build(); // 接口未正确设置

		Server.getSharedClient().newCall(request).enqueue(new Callback() {

			@Override
			public void onResponse(Call arg0, Response arg1) throws IOException {
				runOnUiThread(new Runnable() {
					public void run() {
						reload();
					}
				});
			}

			@Override
			public void onFailure(Call arg0, IOException arg1) {
				runOnUiThread(new Runnable() {
					public void run() {
						reload();
					}
				});
			}
		});
	}

	private boolean isLiked;

	void checkLiked() {
		Request request = Server.requestBuilderWithApi("goods/" + goods.getId() + "/isliked").get().build(); // 接口未正确设置

		Server.getSharedClient().newCall(request).enqueue(new Callback() {
			@Override
			public void onResponse(Call arg0, Response arg1) throws IOException {
				try {
					final String responseString = arg1.body().string();
					final Boolean result = new ObjectMapper().readValue(responseString, Boolean.class);

					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							onCheckLikedResult(result);
						}
					});
				} catch (final Exception e) {
					e.printStackTrace();
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							onCheckLikedResult(false);
						}
					});
				}
			}

			@Override
			public void onFailure(Call arg0, IOException e) {
				e.printStackTrace();
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						onCheckLikedResult(false);
					}
				});
			}
		});
	}

	void onCheckLikedResult(boolean result) {
		isLiked = result;
		btnLikes.setTextColor(result ? Color.BLUE : Color.BLACK);
	}

	void reloadLikes() {
		Request request = Server.requestBuilderWithApi("/goods/" + goods.getId() + "/likes").get().build(); // 接口未正确设置

		Server.getSharedClient().newCall(request).enqueue(new Callback() {

			@Override
			public void onResponse(Call arg0, Response arg1) throws IOException {
				try {
					String responseString = arg1.body().string();
					final Integer count = new ObjectMapper().readValue(responseString, Integer.class);

					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							onReloadLikesResult(count);
						}
					});
				} catch (Exception e) {
					e.printStackTrace();
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							onReloadLikesResult(0);
						}
					});
				}
			}

			@Override
			public void onFailure(Call arg0, IOException e) {
				e.printStackTrace();
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						onReloadLikesResult(0);
					}
				});
			}
		});
	}

	void onReloadLikesResult(int count) {
		if (count > 0) {
			btnLikes.setText("已赞");
		} else {
			btnLikes.setText("点赞");
		}
	}

	// 购买方法
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
		MultipartBody.Builder requestBodyBuilder = new MultipartBody.Builder().addFormDataPart("password",
				MD5.getMD5(pwd));

		Request request = Server.requestBuilderWithApi("buygoods/" + goods.getId()).method("get", null)
				.post(requestBodyBuilder.build()).build();

		Server.getSharedClient().newCall(request).enqueue(new Callback() {

			@Override
			public void onResponse(Call arg0, Response arg1) throws IOException {
				String responseString = arg1.body().string();
				Log.e(TAG, responseString + " g");
				if ("passwordIsNotRight".equals(responseString)) {
					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							Toast.makeText(GoodsContentActivity.this, "支付密码错误", Toast.LENGTH_SHORT).show();
						}
					});

				} else if("Success".equals(responseString)){
					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							Toast.makeText(GoodsContentActivity.this, "购买成功", Toast.LENGTH_SHORT).show();
							finish();
						}
					});
				}else{
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
				// TODO Auto-generated method stub

			}
		});
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
					user = mapper.readValue(jsonString, User.class);
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

		reload();
	}

	void reload() {
		// reloadLikes();
		// checkLiked();

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

	// void loadmore(){
	//
	// Request request =
	// Server.requestBuilderWithApi("/goods/"+goods.getId()+"/comments/").get().build();
	// //接口未正确设置
	//
	// Server.getSharedClient().newCall(request).enqueue(new Callback() {
	// @Override
	// public void onResponse(Call arg0, Response arg1) throws IOException {
	// try{
	// final List<Comment> data = new
	// ObjectMapper().readValue(arg1.body().string(), new
	// TypeReference<List<Comment>>() {
	// });
	//
	// runOnUiThread(new Runnable() {
	//
	// @Override
	// public void run() {
	// GoodsContentActivity.this.appendData(data);
	// }
	// });
	// }catch(final Exception e){
	// runOnUiThread(new Runnable() {
	// public void run() {
	// GoodsContentActivity.this.onFailure(e);
	// }
	// });
	// }
	// }
	//
	// @Override
	// public void onFailure(Call arg0, final IOException e) {
	// runOnUiThread(new Runnable() {
	// public void run() {
	// GoodsContentActivity.this.onFailure(e);
	// }
	// });
	// }
	// });
	// }

	protected void reloadData(List<Comment> data) {
		comments = data;
		adapter.notifyDataSetInvalidated();
	}

	protected void appendData(List<Comment> data) {
		adapter.notifyDataSetChanged();
	}

	void onFailure(Exception e) {
		new AlertDialog.Builder(this).setMessage(e.getMessage()).show();
	}
}
