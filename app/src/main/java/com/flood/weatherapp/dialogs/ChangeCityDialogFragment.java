package com.flood.weatherapp.dialogs;

import com.flood.weatherapp.MainActivity;
import com.flood.weatherapp.R;
import com.flood.weatherapp.R.id;
import com.flood.weatherapp.R.layout;
import com.flood.weatherapp.utils.CityPreferences;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

public class ChangeCityDialogFragment extends DialogFragment{
	
	private EditText city;
	private MainActivity activity;
	
	@SuppressLint("NewApi") public static ChangeCityDialogFragment newInstance(){
		ChangeCityDialogFragment f = new ChangeCityDialogFragment();
		return f;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
	}

	@SuppressLint("NewApi") @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		View v = inflater.inflate(R.layout.dialog_change_city, container, false);
		
		city = (EditText) v.findViewById(R.id.city);
		Button ok = (Button) v.findViewById(R.id.ok);
		ok.setOnClickListener((android.view.View.OnClickListener) clickListener);
		
		return v;
	}
	
	@Override
	public void onResume(){
		super.onResume();
		city.setText(CityPreferences.getCity(activity));
	}
	
	@SuppressLint("NewApi") @Override
	public void onAttach(Activity activity){
		super.onAttach(activity);
		this.activity = (MainActivity) activity;
	}
	
	private OnClickListener clickListener = new OnClickListener(){
		
		@Override
		public void onClick(View v){
			if (v.getId() == R.id.ok){
				String val = city.getText().toString();
				activity.changeCity(val);
				
				try {
					getDialog().dismiss();
				} catch(Exception e){
					
				}
			}
		}

		@Override
		public void onClick(DialogInterface dialog, int which) {
			// TODO Auto-generated method stub
			
		}
	};
	
}
