package page;

import com.swqs.schooltrade.R;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class FeedListFragment extends Fragment {
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		/*resource:fragment
		 * ROOT:加载layout的父viewGroup
		 * attactToRoot:false,不返回父viewgroup
		 * */
		View view = inflater.inflate(R.layout.fragment_page_feedlist, container, false);
		TextView text = (TextView) view.findViewById(R.id.text1);
		text.setText("静态加载。。。");
		return view;
	}
}
