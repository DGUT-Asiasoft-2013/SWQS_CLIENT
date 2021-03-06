package com.swqs.schooltrade.fragment;

import com.swqs.schooltrade.R;

import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

public class InputcellSimpletextFragment extends BaseInputcellFragment {
	TextView label;
	EditText edit;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_inputcell_simpletext, container);
		label = (TextView) view.findViewById(R.id.label);
		edit = (EditText) view.findViewById(R.id.edit);
		return view;
		
	}
	@Override
	public void setLabelText(String labelText){
		label.setText(labelText);
	}
	
	public void setHintText(String hintText){
		edit.setHint(hintText);
	}
	
	public void setIsPassword(boolean isPassword){
		if(isPassword){
			edit.setInputType(EditorInfo.TYPE_CLASS_TEXT|EditorInfo.TYPE_TEXT_VARIATION_PASSWORD);
		}else{
			edit.setInputType(EditorInfo.TYPE_CLASS_TEXT);
		}
		
	}
	
	public void setIsNumber(boolean isNumber){
		if(isNumber){
			edit.setInputType(EditorInfo.TYPE_CLASS_TEXT|EditorInfo.TYPE_CLASS_NUMBER);	
		}else{
			edit.setInputType(EditorInfo.TYPE_CLASS_TEXT);
		}
	}
	
	public void setIsEmail(boolean isEmail){
		if(isEmail){
			edit.setInputType(EditorInfo.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
		}else{
			edit.setInputType(EditorInfo.TYPE_CLASS_TEXT);
		}
	}
	
	public String getText(){
		return edit.getText().toString();
	}
	
	}
