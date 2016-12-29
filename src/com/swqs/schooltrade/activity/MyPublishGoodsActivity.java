package com.swqs.schooltrade.activity;

import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.swqs.schooltrade.R;
import com.swqs.schooltrade.entity.Goods;
import com.swqs.schooltrade.util.Server;
import com.swqs.schooltrade.util.Util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MyPublishGoodsActivity extends Activity {

	ListView lvGoods;
	List<Goods> data;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mypublish);
		lvGoods = (ListView) findViewById(R.id.lvGoods);
		lvGoods.setAdapter(listAdapter);
		lvGoods.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				onItemClicked(position);
			}
		});
		getData();
	}

	private void onItemClicked(int position) {
		Intent intent=null;
		boolean isSell=data.get(position).isSell();
		if(isSell){
			intent = new Intent(this, BuyOrderDetailsActivity.class);
		}else{
			intent=new Intent(this,GoodsContentActivity.class);
		}
		intent.putExtra("data", data.get(position));
		startActivity(intent);
	}

	private void getData() {
		OkHttpClient client = Server.getSharedClient();

		Request request = Server.requestBuilderWithApi("mypublishment/goodslist").build();

		client.newCall(request).enqueue(new Callback() {

			@Override
			public void onResponse(Call arg0, Response arg1) throws IOException {
				String jsonString = arg1.body().string();
				ObjectMapper mapper = new ObjectMapper();
				List<Goods> goodsList = mapper.readValue(jsonString, new TypeReference<List<Goods>>() {
				});
				data = goodsList;
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						listAdapter.notifyDataSetChanged();
					}
				});
			}

			@Override
			public void onFailure(Call arg0, IOException arg1) {

			}
		});
	}

	BaseAdapter listAdapter = new BaseAdapter() {

		@SuppressLint("InflateParams")
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {
				LayoutInflater inflater = LayoutInflater.from(parent.getContext());
				convertView = inflater.inflate(R.layout.item_mypublish, null);
				holder = new ViewHolder();
				holder.ivImg = (ImageView) convertView.findViewById(R.id.ivImg);
				holder.tvTitle = (TextView) convertView.findViewById(R.id.tvTitle);
				holder.tvMoney = (TextView) convertView.findViewById(R.id.tvMoney);
				holder.tvState = (TextView) convertView.findViewById(R.id.tvState);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			Goods goods = data.get(position);
			holder.tvTitle.setText(goods.getTitle());
			holder.tvMoney.setText("￥" + goods.getCurPrice() + "");
			holder.tvState.setText(goods.isSell() ? "已出售" : "待出售");
			Util.loadImage(MyPublishGoodsActivity.this, goods.getListImage().get(0).getPictureUrl(), holder.ivImg);
			return convertView;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public Object getItem(int position) {
			return data == null ? null : data.size();
		}

		@Override
		public int getCount() {
			return data == null ? 0 : data.size();
		}

		class ViewHolder {
			ImageView ivImg;
			TextView tvTitle;
			TextView tvMoney;
			TextView tvState;
		}
	};
}
