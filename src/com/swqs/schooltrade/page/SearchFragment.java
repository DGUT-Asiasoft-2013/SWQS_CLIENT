package com.swqs.schooltrade.page;

import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.swqs.schooltrade.R;
import com.swqs.schooltrade.activity.GoodsContentActivity;
import com.swqs.schooltrade.entity.Goods;
import com.swqs.schooltrade.entity.Page;
import com.swqs.schooltrade.util.Server;
import com.swqs.schooltrade.util.Util;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SearchFragment extends Fragment {

	private View view;
	private EditText etSearch;
	private ListView lvSearchGoods;
	List<Goods> data;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (view == null) {
			view = inflater.inflate(R.layout.fragment_page_search, null);
			etSearch = (EditText) view.findViewById(R.id.etSearch);
			view.findViewById(R.id.ivSearch).setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					search();
				}
			});
			lvSearchGoods = (ListView) view.findViewById(R.id.listview);
			lvSearchGoods.setAdapter(listAdapter);
			lvSearchGoods.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					onItemSelected(position);
				}
			});
			etSearch.addTextChangedListener(new TextWatcher() {

				@Override
				public void onTextChanged(CharSequence s, int start, int before, int count) {
					// TODO Auto-generated method stub

				}

				@Override
				public void beforeTextChanged(CharSequence s, int start, int count, int after) {
					// TODO Auto-generated method stub

				}

				@Override
				public void afterTextChanged(Editable s) {
					search();
				}
			});
		}
		return view;
	}

	private void onItemSelected(int position) {

		Goods goods = data.get(position);

		Intent itnt = new Intent(getActivity(), GoodsContentActivity.class);
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
				view.setTag(viewHolder);
			} else {
				view = convertView;
				viewHolder = (ViewHolder) view.getTag();
			}

			Goods goods = data.get(position);

			viewHolder.textTitle.setText(goods.getTitle());
			viewHolder.textPrice.setText(goods.getOriginalPrice() + "");
			Util.loadImage(getActivity(), goods.getListImage().get(0).getPictureUrl(), viewHolder.imageGoods);

			return view;
		}

		class ViewHolder {
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

	private void search() {
		String keyword = etSearch.getText().toString();
		OkHttpClient client = Server.getSharedClient();
		Request request = Server.requestBuilderWithApi("goods/search/" + keyword).build();
		// 异步发起请求
		client.newCall(request).enqueue(new Callback() {

			@Override
			public void onResponse(Call arg0, Response arg1) throws IOException {

				final String jsonString = arg1.body().string();
				getActivity().runOnUiThread(new Runnable() {

					@Override
					public void run() {
						try {
							Page<Goods> page= new ObjectMapper().readValue(jsonString, new TypeReference<Page<Goods>>() {
							});
							data=page.getContent();
							listAdapter.notifyDataSetChanged();
						} catch (Exception e) {
							e.printStackTrace();
							data.clear();
							listAdapter.notifyDataSetChanged();
						}
					}
				});

			}

			@Override
			public void onFailure(Call arg0, IOException arg1) {

			}
		});
	}
}
