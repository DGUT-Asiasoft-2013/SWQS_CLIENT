package fragment;



import com.swqs.schooltrade.R;

import android.app.AlertDialog;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import inputcell.InputcellSimpletextFragment;


public class PasswordRecoverStep2Fragment extends Fragment {
	InputcellSimpletextFragment fragPassword,fragPasswordRepeat;

	
	View view;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		if(view==null){
			view = inflater.inflate(R.layout.fragment_password_recover_step2, null);
			fragPassword = (InputcellSimpletextFragment) getFragmentManager().findFragmentById(R.id.input_password);
			fragPasswordRepeat = (InputcellSimpletextFragment) getFragmentManager().findFragmentById(R.id.input_password_repeat);
			
			view.findViewById(R.id.btn_submit).setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					onSubmitClicked();
				}
			});
		}
		
		return view;
	}
	
	public String getText(){
		return fragPassword.getText();
	}
	
	public static interface OnSubmitClickedListener{
		void onSubmitClicked();
	}
	
	OnSubmitClickedListener onSubmitClickedListener;
	
	public void setOnSubmitClickedListener(OnSubmitClickedListener onSubmitClickedListener) {
		this.onSubmitClickedListener = onSubmitClickedListener;
	}
	
	void onSubmitClicked(){
		if(fragPassword.getText().equals(fragPasswordRepeat.getText())){
			if(onSubmitClickedListener!=null){
				onSubmitClickedListener.onSubmitClicked();
			}
		}else{
			new AlertDialog.Builder(getActivity())
			.setMessage("���벻һ��")
			.show();
		}
	}
}
