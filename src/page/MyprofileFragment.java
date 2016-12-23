package page;

import com.swqs.schooltrade.R;
import com.swqs.schooltrade.BuyAndSelledActivity;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

public class MyprofileFragment extends Fragment {

	View view=null;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if(view==null){
			view=inflater.inflate(R.layout.fragment_page_myprofile, null);
			view.findViewById(R.id.tvBuyed).setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					hasBuyed();
				}
			});
			view.findViewById(R.id.tvSelled).setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					hasSelled();
				}
			});
		}
		return view;
	}
	private void hasSelled() {
		Intent intent=new Intent(getActivity(),BuyAndSelledActivity.class);
		intent.putExtra("flag", "sell");
		startActivity(intent);
	}
	private void hasBuyed() {
		Intent intent=new Intent(getActivity(),BuyAndSelledActivity.class);
		intent.putExtra("flag", "buy");
		startActivity(intent);
	}
	
}
