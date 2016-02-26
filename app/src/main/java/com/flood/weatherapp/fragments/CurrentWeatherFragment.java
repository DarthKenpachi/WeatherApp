package com.flood.weatherapp.fragments;

import grabber.WeatherGrabber;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

import android.R;
import android.app.Activity;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import java.text.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.flood.weatherapp.*;
import com.flood.weatherapp.cache.RawCache;
import com.flood.weatherapp.utils.*;

public class CurrentWeatherFragment extends Fragment{
	
	private Calendar c = Calendar.getInstance();
	private Activity activity;
	private TextView cityField, updateField, detailsField, currentTemperatureField, weatherIcon;
	private Handler handler;
	
	public CurrentWeatherFragment(){
		handler = new Handler();
		
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState){
		View rootView = inflater.inflate(R.layout.fragment_current_weather, container, false);
		
		cityField = (TextView) rootView.findViewById(R.id.city_field);
		updateField = (TextView) rootView.findViewById(R.id.updated_field);
		detailsField = (TextView) rootView.findViewById(R.id.details_field);
		currentTemperatureField = (TextView) rootView.findViewById(R.id.current_temperature_field);
		weatherIcon = (TextView) rootView.findViewById(R.id.weather_icon);
		weatherIcon.setTypeface(Utils.getWeatherFont(activity));
		
		return rootView;
		
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public void onResume(){
		super.onResume();
		updateWeatherCity(CityPreferences.getCity(activity));
	}
	
	private void updateWeatherCity(String city) {
		updateWeatherCity(city, false);
		
	}
	
	private void updateWeatherCity(final String city, boolean force){
		startRefresh();
		
		//check from cache
		boolean isInCache = RawCache.isInCache(activity, city + Utils.CURRENT_CACHE_KEY);
		boolean hasNetwork = Utils.isConnected(activity);
		
		//no network and no cache ==> error
		if (!hasNetwork && !isInCache){
			Toast.makeText(activity, R.string.no_network, Toast.LENGTH_SHORT).show();
		}
		
		if (isInCache && !force){
			String json = RawCache.fromCache(activity, city + Utils.CURRENT_CACHE_KEY);
			stopRefresh();
			
			JSONObject data = null;
			
			try {
				data = new JSONObject(json);
				
			} catch (JSONException e){
				data = null;
			}
			
			renderWeather(data);
			
		} else {
			//get from network in separate Thread
			
			new Thread(){
				public void run(){
					final String tmp = WeatherGrabber.loadCurrentWeather(activity, city);
					JSONObject data = null;
					
					try {
						data = new JSONObject(tmp);
						//value = 404 => request not found / 200 = OK
						
						if (data != null && data.getInt("cod") != 200){
							data = null;
						}
					} catch (Exception e){
						data = null;
					}
					
					if (data == null){
						handler.post(new Runnable(){

							@Override
							public void run() {
								stopRefresh();
								Toast.makeText(activity, R.string.place_not_found, Toast.LENGTH_SHORT).show();
								
							}
							
						});
					} else{
						//cache and render
						final JSONObject json = data;
						
						handler.post(new Runnable(){

							@Override
							public void run() {
								RawCache.cache(activity, city + Utils.CURRENT_CACHE_KEY, tmp);
								stopRefresh();
								renderWeather(json);
							}
							
						});
					}
					
				}
			}.start();
			
		}
	}

	private void renderWeather(JSONObject json){
		try {
			//simple get of weather data
			String name = json.getString("name").toUpperCase(Locale.US);
			String country = json.getJSONObject("sys").getString("country");
			
			cityField.setText(name + ", " + country);
			
			//store city
			CityPreferences.setCity(activity,
					Utils.capitalize(name.toLowerCase(Locale.US)) + "," + country);
			
			//check API of OpenWeatherMap for details of these values
			long sunrise = json.getJSONObject("sys").getLong("sunrise") * 1000;
			long sunset = json.getJSONObject("sys").getLong("sunset") * 1000;
			c.setTimeInMillis(sunrise);
			Date dateSunrise = c.getTime();
			c.setTimeInMillis(sunset);
			Date dateSunset = c.getTime();
			
			JSONObject details = json.getJSONArray("weather").getJSONObject(0);
			JSONObject main = json.getJSONObject("main");
			detailsField.setText(details.getString("description").toUpperCase(Locale.US)
					+ "\n"
					+ getString(R.string.humidity)
					+ " : "
					+ main.getString("humidity")
					+ "%"
					+ "\n"
					+ Utils.HOUR_FORMATTER.format(dateSunrise)
					+ " / "
					+ Utils.HOUR_FORMATTER.format(dateSunset));
			
			currentTemperatureField.setText(String.format(Locale.US, "%.2f", main.getDouble("temp")) + " °C");
			
			String updateOn = DateFormat.getDateTimeInstance().format(
					new Date(json.getLong("dt") * 1000));
			
			updateField.setText(getString(R.string.last_update) + " : " + updateOn);
			
			
			//convert id of weather to icon
			weatherIcon.setText(Utils.getWeatherIcon(activity, details.getInt("id"), sunrise, sunset));
			
		} catch(Exception e){
			Toast.makeText(activity, R.string.error_meteo, Toast.LENGTH_SHORT).show();
		}
	}
	
	public void stopRefresh(){
		updateField.setVisibility(View.VISIBLE);
		detailsField.setVisibility(View.VISIBLE);
		currentTemperatureField.setVisibility(View.VISIBLE);
	}
	
	public void startRefresh(){
		updateField.setVisibility(View.GONE);
		detailsField.setVisibility(View.GONE);
		currentTemperatureField.setVisibility(View.GONE);
	}
	
	@Override
	public void onAttach(Activity activity){
		super.onAttach(activity);
		this.activity = activity;
	}
	
	//For weather location
	private void updateWeatherLocation(final Location location){
		startRefresh();
		
		final String strLocation = Utils.getLatLng(activity, location);
		
		//check cache
		boolean isInCache = RawCache.isInCache(activity, strLocation + Utils.CURRENT_CACHE_KEY);
		
		//check from cache
		boolean hasNetwork = Utils.isConnected(activity);
		
		//no network and no cache ==> error
		if(!hasNetwork && !isInCache){
			Toast.makeText(activity, R.string.no_network, Toast.LENGTH_SHORT).show();
			return;
		}
		
		if(isInCache){
			String json = RawCache.fromCache(activity, strLocation + Utils.CURRENT_CACHE_KEY);
			
			stopRefresh();
			JSONObject data = null;
			
			try{
				data = new JSONObject(json);
			} catch(JSONException e){
				data = null;
			}
			
			renderWeather(data);
			
		} else{
			new Thread(){
				public void run(){
					final String tmp = WeatherGrabber.loadCurrentWeatherLocation(activity, location);
					
					JSONObject data = null;
					
					try{
						data = new JSONObject(tmp);
						
						if(data != null && data.getInt("cod") != 200){
							data = null;
						}
					} catch(Exception ex){
						data = null;
					}
					
					if (data == null){
						handler.post(new Runnable(){
							public void run(){
								stopRefresh();
								Toast.makeText(activity, getString(R.string.place_not_found), Toast.LENGTH_LONG).show();
							}
						});
					} else {
						final JSONObject json = data;
						
						handler.post(new Runnable(){
							public void run() {
								RawCache.cache(activity, strLocation + Utils.CURRENT_CACHE_KEY, tmp);
								
								stopRefresh();
								renderWeather(json);
								
							}
							
						});
					}
				}
			}.start();
		}
	}
	
	public void loadLocation(Location location){
		updateWeatherLocation(location);
	}
	
	public void changeCity(String city){
		updateWeatherCity(city);
	}
	
	public void refresh(){
		updateWeatherCity(CityPreferences.getCity(activity), true);
	}
	
	
	

}
