package com.swqs.schooltrade.page;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.swqs.schooltrade.R;
import com.swqs.schooltrade.activity.GoodsContentActivity;
import com.swqs.schooltrade.entity.Goods;
import com.swqs.schooltrade.util.AvatarView;
import com.swqs.schooltrade.util.RoundImageView;
import com.swqs.schooltrade.util.Server;
import com.swqs.schooltrade.util.Util;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.ReferenceType;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

public class GoodsListFragment extends Fragment {

	View view;
	ListView listView;

	List<Goods> data;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (view == null) {
			view = inflater.inflate(R.layout.fragment_page_goods_list, null);

			listView = (ListView) view.findViewById(R.id.goodslist);

			listView.setAdapter(listAdapter);

			listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					onItemClicked(position);
				}
			});
		}

		return view;
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

			Goods goods = data.get(position);
			viewHolder.tvName.setText(goods.getAccount().getAccount());
			viewHolder.textTitle.setText(goods.getTitle());
			viewHolder.textPrice.setText(goods.getOriginalPrice() + "");
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm");
			viewHolder.tvTime.setText(sdf.format(goods.getCreateDate()));
			Util.loadImage(getActivity(), goods.getAccount().getFace_url(), viewHolder.roundAvatar);
			if (goods.getListImage() != null && goods.getListImage().size() > 0) {
				Util.loadImage(getActivity(), goods.getListImage().get(0).getPictureUrl(), viewHolder.imageGoods);
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

	void onItemClicked(int position) {
		Goods goods = data.get(position);

		Intent itnt = new Intent(getActivity(), GoodsContentActivity.class);
		itnt.putExtra("data", goods);

		startActivity(itnt);
	}

	@Override
	public void onResume() {
		super.onResume();
		reload();
	}

	void reload() {
		Request request = Server.requestBuilderWithApi("getlistgoods").get().build();

		Server.getSharedClient().newCall(request).enqueue(new Callback() {
			@Override
			public void onResponse(Call arg0, Response arg1) throws IOException {
				try {
					final List<Goods> data = new ObjectMapper().readValue(arg1.body().string(),
							new TypeReference<List<Goods>>() {
							});

					getActivity().runOnUiThread(new Runnable() {
						public void run() {
							GoodsListFragment.this.data = data;
							listAdapter.notifyDataSetInvalidated();
						}
					});
				} catch (final Exception e) {
					getActivity().runOnUiThread(new Runnable() {
						public void run() {
							new AlertDialog.Builder(getActivity()).setMessage(e.getMessage()).show();
						}
					});
				}
			}

			@Override
			public void onFailure(Call arg0, final IOException e) {
				getActivity().runOnUiThread(new Runnable() {
					public void run() {
						new AlertDialog.Builder(getActivity()).setMessage(e.getMessage()).show();
					}
				});
			}
		});
	}
}