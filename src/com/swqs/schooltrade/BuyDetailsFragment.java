package com.swqs.schooltrade;

import java.util.Random;

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
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class BuyDetailsFragment extends Fragment {

	protected static final String TAG = "BuyDetailsFragment";
	View view;
	ListView listView;
	
	String[] data;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (view==null){
			view = inflater.inflate(R.layout.fragment_buy_details, null);
			
			listView = (ListView) view.findViewById(R.id.list);
			listView.setAdapter(listAdapter);
			
			Random rand = new Random();
			data = new String[10+rand.nextInt()%20];
			
			for(int i=0; i<data.length; i++){
				data[i] = "THIS ROW IS "+rand.nextInt();
			}
			
			listView.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					Log.e(TAG, "a");
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
			holder.tvTitle.setText(data[position]);
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
			return data[position];
		}
		
		@Override
		public int getCount() {
			return data==null ? 0 : data.length;
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
		String text = data[position];
		
		Intent itnt = new Intent(getActivity(), BuyOrderDetailsActivity.class);
		itnt.putExtra("text", text);
		
		
		startActivity(itnt);
	}
}

