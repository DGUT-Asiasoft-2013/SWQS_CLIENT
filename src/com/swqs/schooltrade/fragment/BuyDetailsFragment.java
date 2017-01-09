package com.swqs.schooltrade.fragment;

import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.swqs.schooltrade.R;
import com.swqs.schooltrade.activity.BuyOrderDetailsActivity;
import com.swqs.schooltrade.app.TradeApplication;
import com.swqs.schooltrade.entity.Identify;
import com.swqs.schooltrade.util.Server;
import com.swqs.schooltrade.util.Util;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class BuyDetailsFragment extends Fragment {

	protected static final String TAG = "BuyDetailsFragment";
	View view;
	ListView listView;
	
	List<Identify> data;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (view==null){
			view = inflater.inflate(R.layout.fragment_buy_details, null);
			
			listView = (ListView) view.findViewById(R.id.list);
			listView.setAdapter(listAdapter);
			
			listView.setOnItemClickListener(new OnItemClickListener() {

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
		FormBody body=new FormBody.Builder().add("uid", TradeApplication.uid).build();
		Request request = Server.requestBuilderWithApi("mybuy/goodslist").post(body).build();

		client.newCall(request).enqueue(new Callback() {
			
			@Override
			public void onResponse(Call arg0, Response arg1) throws IOException {
				String jsonString=arg1.body().string();
				ObjectMapper mapper = new ObjectMapper();
				List<Identify> goodsList=mapper.readValue(jsonString, new TypeReference<List<Identify>>() {
				});
				data=goodsList;
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
			ViewHolder holder=null;
			if(convertView==null){
				LayoutInflater inflater = LayoutInflater.from(parent.getContext());
				convertView = inflater.inflate(R.layout.widget_buy_item, null);	
				holder=new ViewHolder();
				holder.ivImg=(ImageView) convertView.findViewById(R.id.ivImg);
				holder.tvTitle=(TextView) convertView.findViewById(R.id.tvTitle);
				holder.tvMoney=(TextView) convertView.findViewById(R.id.tvMoney);
				holder.tvState=(TextView) convertView.findViewById(R.id.tvState);
				convertView.setTag(holder);
			}else{
				holder=(ViewHolder) convertView.getTag();
			}
			Identify identify=data.get(position);
			holder.tvTitle.setText(identify.getGoods().getTitle());
			holder.tvMoney.setText("￥"+identify.getGoods().getCurPrice());
			Util.loadImage(getActivity(),identify.getGoods().getListImage().get(0).getPictureUrl() , holder.ivImg);
			int state=identify.getTradeState();
			String strState="";
			if(state==1){
				strState="卖家未发货";
			}else if(state==2){
				strState="卖家已发货";
			}else if(state==3){
				strState="已收货";
			}
			holder.tvState.setText(strState);
			return convertView;
		}
		
		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}
		
		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return data==null?null:data.size();
		}
		
		@Override
		public int getCount() {
			return data==null ? 0 : data.size();
		}
		
		class ViewHolder{
			ImageView ivImg;
			TextView tvTitle;
			TextView tvMoney;
			TextView tvState;
		}
	};
	
	void onItemClicked(int position){
		
		Intent itnt = new Intent(getActivity(), BuyOrderDetailsActivity.class);
		itnt.putExtra("data", data.get(position).getGoods());
		startActivity(itnt);
	}
}


