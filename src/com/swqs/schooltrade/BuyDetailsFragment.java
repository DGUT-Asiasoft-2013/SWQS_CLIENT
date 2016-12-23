package com.swqs.schooltrade;

import java.io.IOException;
import java.util.List;
import java.util.Random;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.swqs.schooltrade.api.Server;
import com.swqs.schooltrade.api.entity.Goods;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class BuyDetailsFragment extends Fragment {

	protected static final String TAG = "BuyDetailsFragment";
	View view;
	ListView listView;
	
	List<Goods> data;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (view==null){
			view = inflater.inflate(R.layout.fragment_buy_details, null);
			
			listView = (ListView) view.findViewById(R.id.list);
			listView.setAdapter(listAdapter);
			
			listView.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					Log.e(TAG, "a");
					onItemClicked(position);
				}
			});
		}
		getData();
		return view;
	}
	
	private void getData() {
		OkHttpClient client = Server.getSharedClient();

		Request request = Server.requestBuilderWithApi("mybuy/goodslist").build();

		client.newCall(request).enqueue(new Callback() {
			
			@Override
			public void onResponse(Call arg0, Response arg1) throws IOException {
				String jsonString=arg1.body().string();
				ObjectMapper mapper = new ObjectMapper();
				List<Goods> goodsList=mapper.readValue(jsonString, new TypeReference<List<Goods>>() {
				});
				data=goodsList;
				listAdapter.notifyDataSetChanged();
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
				holder.tvGoodsName=(TextView) convertView.findViewById(R.id.tvGoodsName);
				holder.tvTitle=(TextView) convertView.findViewById(R.id.tvTitle);
				holder.tvMoney=(TextView) convertView.findViewById(R.id.tvMoney);
				holder.btnLook=(Button) convertView.findViewById(R.id.btnLook);
				holder.btnLook.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						Intent intent=new Intent(getActivity(),BuyEvaluationDetailsActivity.class);
						startActivity(intent);
					}
				});
				convertView.setTag(holder);
			}else{
				holder=(ViewHolder) convertView.getTag();
			}
			Goods goods=data.get(position);
			holder.tvGoodsName.setText(goods.getContent());
			holder.tvTitle.setText(goods.getTitle());
			holder.tvMoney.setText(goods.getCurPrice()+"");
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
			TextView tvGoodsName;
			TextView tvTitle;
			TextView tvMoney;
			Button btnLook;
		}
	};
	
	void onItemClicked(int position){
		
		Intent itnt = new Intent(getActivity(), BuyOrderDetailsActivity.class);
		itnt.putExtra("goods", data.get(position));
		
		
		startActivity(itnt);
	}
}


