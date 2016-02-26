package com.flood.weatherapp.fragments;

import com.flood.weatherapp.utils.CityPreferences;

import android.R;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

@SuppressLint("NewApi") public class ForecastWeatherFragment extends Fragment{
	
	private Activity activity;
	
	public ForecastWeatherFragment(){
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState){
		View rootView = inflater.inflate(R.layout.fragment_forecast_weather, container, false);
		return rootView;
	}
	
	@SuppressLint("NewApi") @Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
	}
	
	@SuppressLint("NewApi") @Override
	public void onResume(){
		super.onResume();
		updateWeatherCity(CityPreferences.getCity(activity));
	}
	
	@Override
	public void onAttach(Activity activity){
		super.onAttach(activity);
		this.activity = activity;
	}

	private void updateWeatherCity(final String city){
	}
	
	private void updateWeatherLocation(final Location location){
	}
	
	public void loadLocation(Location location){
	}
	
	public void changeCity(String city){
	}
	
	public void startRefresh(){
	}
	
	public void stopRefresh(){
	}
	
	public void refresh(){
	}
}
