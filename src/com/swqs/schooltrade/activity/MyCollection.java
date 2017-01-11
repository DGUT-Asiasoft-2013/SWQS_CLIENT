package com.swqs.schooltrade.activity;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.swqs.schooltrade.R;
import com.swqs.schooltrade.app.TradeApplication;
import com.swqs.schooltrade.entity.Collection;
import com.swqs.schooltrade.entity.Goods;
import com.swqs.schooltrade.util.RoundImageView;
import com.swqs.schooltrade.util.Server;
import com.swqs.schooltrade.util.Util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.Response;

public class MyCollection extends Activity {

	ListView listView;
	List<Collection> data;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_collection);
		listView = (ListView) findViewById(R.id.lvMyCollection);
		listView.setAdapter(listAdapter);

		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

				onItemClicked(position);
			}
		});
		getMyCollection();
	}

	void onItemClicked(int position) {
		Goods goods = data.get(position).getId().getGoods();
		if (goods.isSell()) {
			Toast.makeText(MyCollection.this, "该商品已出售", Toast.LENGTH_SHORT).show();
			return;
		}
			Intent itnt = new Intent(this, GoodsContentActivity.class);
			itnt.putExtra("data", goods);
			startActivity(itnt);
		
	}

	BaseAdapter listAdapter = new BaseAdapter() {

		@SuppressLint("InflateParams")
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = null;
			ViewHolder viewHolder;

			if (convertView == null) {
				viewHolder = new ViewHolder();
				LayoutInflater inflater = LayoutInflater.from(parent.getContext());
				view = inflater.inflate(R.layout.widget_goods_item, null);
				viewHolder.textTitle = (TextView) view.findViewById(R.id.title_content);
				viewHolder.textPrice = (TextView) view.findViewById(R.id.originalprice_content);
				viewHolder.imageGoods = (ImageView) view.findViewById(R.id.goods_image);
				viewHolder.tvName = (TextView) view.findViewById(R.id.tvName);
				viewHolder.roundAvatar = (RoundImageView) view.findViewById(R.id.roundAvatar);
				viewHolder.tvTime = (TextView) view.findViewById(R.id.tvTime);
				view.setTag(viewHolder);
			} else {
				view = convertView;
				viewHolder = (ViewHolder) view.getTag();
			}

			Goods goods = data.get(position).getId().getGoods();
			viewHolder.tvName.setText(goods.getAccount().getAccount());
			viewHolder.textTitle.setText(goods.getTitle());
			if (goods.isSell()) {
				viewHolder.textPrice.setText(goods.getCurPrice() + "    已出售");
			} else {
				viewHolder.textPrice.setText(goods.getCurPrice() + "    未出售");
			}
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm");
			viewHolder.tvTime.setText(sdf.format(goods.getCreateDate()));
			Util.loadImage(MyCollection.this, goods.getAccount().getFace_url(), viewHolder.roundAvatar);
			if (goods.getListImage() != null && goods.getListImage().size() > 0) {
				Util.loadImage(MyCollection.this, goods.getListImage().get(0).getPictureUrl(), viewHolder.imageGoods);
			}

			return view;
		}

		class ViewHolder {
			public TextView tvName;
			public RoundImageView roundAvatar;
			public TextView tvTime;
			public TextView textTitle;
			public TextView textPrice;
			public ImageView imageGoods;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public Object getItem(int position) {
			return data.get(position);
		}

		@Override
		public int getCount() {
			return data == null ? 0 : data.size();
		}
	};

	void getMyCollection() {
		FormBody formBody = new FormBody.Builder().add("uid", TradeApplication.uid).build();
		Request request = Server.requestBuilderWithApi("myCollection").post(formBody).build();

		Server.getSharedClient().newCall(request).enqueue(new Callback() {
			@Override
			public void onResponse(Call arg0, Response arg1) throws IOException {
				try {
					String jsonString = arg1.body().string();
					final List<Collection> collectionData = new ObjectMapper().readValue(jsonString,
							new TypeReference<List<Collection>>() {
							});
					if (collectionData != null) {
						runOnUiThread(new Runnable() {
							public void run() {
								MyCollection.this.data = collectionData;
								listAdapter.notifyDataSetChanged();
							}
						});
					}
				} catch (final Exception e) {
					runOnUiThread(new Runnable() {
						public void run() {
							new AlertDialog.Builder(MyCollection.this).setMessage(e.getMessage()).show();
						}
					});
				}
			}

			@Override
			public void onFailure(Call arg0, final IOException e) {
				runOnUiThread(new Runnable() {
					public void run() {
						new AlertDialog.Builder(MyCollection.this).setMessage(e.getMessage()).show();
					}
				});
			}
		});
	}

}
