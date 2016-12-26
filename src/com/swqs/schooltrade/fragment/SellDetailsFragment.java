package com.swqs.schooltrade.fragment;

import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.swqs.schooltrade.R;
import com.swqs.schooltrade.activity.SellEvaluationDetailsActivity;
import com.swqs.schooltrade.activity.SellOrderDetailsActivity;
import com.swqs.schooltrade.entity.Goods;
import com.swqs.schooltrade.util.Server;
import com.swqs.schooltrade.util.Util;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SellDetailsFragment extends Fragment {

	View view;
	ListView listView;

	List<Goods> data;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (view == null) {
			view = inflater.inflate(R.layout.fragment_buy_details, null);

			listView = (ListView) view.findViewById(R.id.list);
			TextView tvTitle = (TextView) view.findViewById(R.id.tvTitle);
			tvTitle.setText("ÎÒÂô³öµÄ");
			listView.setAdapter(listAdapter);

			listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					onItemClicked(position);
				}
			});
		}
		getData();
		return view;
	}

	private void getData() {
		OkHttpClient client = Server.getSharedClient();

		Request request = Server.requestBuilderWithApi("mysell/goodslist").build();

		client.newCall(request).enqueue(new Callback() {

			@Override
			public void onResponse(Call arg0, Response arg1) throws IOException {
				String jsonString = arg1.body().string();
				ObjectMapper mapper = new ObjectMapper();
				List<Goods> goodsList = mapper.readValue(jsonString, new TypeReference<List<Goods>>() {
				});
				data = goodsList;
				getActivity().runOnUiThread(new Runnable() {
					
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
				convertView = inflater.inflate(R.layout.widget_sell_item, null);
				holder = new ViewHolder();
				holder.ivImg = (ImageView) convertView.findViewById(R.id.ivImg);
				holder.tvTitle = (TextView) convertView.findViewById(R.id.tvTitle);
				holder.tvMoney = (TextView) convertView.findViewById(R.id.tvMoney);
				holder.btnLook = (Button) convertView.findViewById(R.id.btnLook);
				holder.btnLook.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						Intent intent = new Intent(getActivity(), SellEvaluationDetailsActivity.class);
						startActivity(intent);
					}
				});
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			Goods goods = data.get(position);
			holder.tvTitle.setText(goods.getTitle());
			holder.tvMoney.setText("£¤" + goods.getCurPrice() + "");
			Util.loadImage(getActivity(), goods.getListImage().get(0).getPictureUrl(), holder.ivImg);
			return convertView;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public Object getItem(int position) {
			return data == null ? null : data.get(position);
		}

		@Override
		public int getCount() {
			return data == null ? 0 : data.size();
		}

		class ViewHolder {
			ImageView ivImg;
			TextView tvTitle;
			TextView tvMoney;
			Button btnLook;
		}
	};

	void onItemClicked(int position) {

		Intent itnt = new Intent(getActivity(), SellOrderDetailsActivity.class);
		itnt.putExtra("goods", data.get(position));

		startActivity(itnt);
	}
}
