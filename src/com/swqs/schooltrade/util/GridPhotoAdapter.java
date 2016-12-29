package com.swqs.schooltrade.util;

import java.util.List;

import com.swqs.schooltrade.R;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class GridPhotoAdapter extends BaseAdapter {

	private Context context;
	private List<ImageItem> imageItemList;
	private int max = 3;
	
	public int getMax() {
		return max;
	}

	public GridPhotoAdapter() {
		// TODO Auto-generated constructor stub
	}

	public GridPhotoAdapter(Context context, List<ImageItem> imageItemList) {
		this.context = context;
		this.imageItemList = imageItemList;
	}

	@Override
	public int getCount() {
		if (imageItemList == null) {
			return 1;
		} else if (imageItemList.size() < max) {
			return imageItemList.size() + 1;
		}
		return max;
	}

	@Override
	public Object getItem(int position) {
		if (imageItemList == null) {
			return null;
		}
		return imageItemList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = View.inflate(context, R.layout.item_published_grida, null);
			holder = new ViewHolder();
			holder.image = (ImageView) convertView.findViewById(R.id.item_grida_image);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		if (imageItemList == null) {
			holder.image.setImageResource(R.drawable.selector_btn_add);
		} else if (position == imageItemList.size()) {
			holder.image.setImageResource(R.drawable.selector_btn_add);
			if (position == max) {
				holder.image.setVisibility(View.GONE);
			}
		} else {
			holder.image.setImageBitmap(imageItemList.get(position).getBitmap());
		}

		return convertView;
	}

	public class ViewHolder {
		public ImageView image;
	}
	
	public void setData(List<ImageItem> imageItemList){
		this.imageItemList=imageItemList;
		notifyDataSetChanged();
	}
}
