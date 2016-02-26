package com.flood.weatherapp.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.flood.weatherapp.R;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;



public class Utils {
	
	public static SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat("E d/MM", Locale.getDefault());
	
	public static SimpleDateFormat HOUR_FORMATTER = new SimpleDateFormat("HH:mm", Locale.getDefault());
	
	public static final String FORECAST_CACHE_KEY = "_forecast";
	public static final String CURRENT_CACHE_KEY = "_current";
	public static final String WEATHER_FONT_PATH = "fonts/weatherfont.ttf";
	public static Typeface weatherFont;
	
	//WIND FOR ANGLES
	public static String[] winds = new String[]{"N", "NE", "E", "SE", "S", "SW", "W", "NW", "N"};
	
	public final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
	
	public static Typeface getWeatherFont(Context context){
		if (weatherFont == null){
			weatherFont = Typeface.createFromAsset(context.getAssets(), WEATHER_FONT_PATH);
		}
		
		return weatherFont;
		
	}
	
	public static String capitalize(String original){
		if (original.length() == 0)
			return original;
		
		return original.substring(0, 1).toUpperCase() + original.substring(1);
	}
	
	public static String getWeatherIcon(Context context, int realdId, long sunrise, long sunset){
			int icon = R.string.weather_unknown;
			long currentTime = new Date().getTime();
			boolean isDay = (currentTime >= sunrise && currentTime < sunset) || (sunrise == -1 && sunset == -1);
			
			switch(realdId){
			case 511:
				icon = R.string.weather_snowy;
				break;
			case 520:
				icon = R.string.weather_showers;
				break;
			case 800:
				icon = isDay ? R.string.weather_sunny : R.string.weather_clear_night;
				break;
			case 801:
				icon = isDay ? R.string.weather_cloudy : R.string.weather_cloudy_night;
				break;
			case 900:
				icon = R.string.weather_tornado;
				break;
			case 905:
				icon = R.string.weather_extreme_wind;
				break;
			default:
				int simpleId = realdId / 100;
				
				switch (simpleId){
				case 2:
					icon = R.string.weather_thunder;
					break;
				case 3:
					icon = R.string.weather_drizzle;
					break;
				case 5:
					icon = R.string.weather_rainy;
					break;
				case 6:
					icon = R.string.weather_snowy;
					break;
				case 7:
					icon = R.string.weather_foggy;
					break;
				case 8:
					icon = R.string.weather_cloudy;
					break;
				
				}
				
				break;
			}
			
			return context.getString(icon);
	}
	
	public static String getDirectionFromAngle(float angle){
		int index = 0;
		int tmp = Math.round(angle / 45f);
		
		if (tmp >= 0 && tmp < winds.length){
			index = tmp;
		}
		
		return winds(index);	
	}
	
	public static boolean isConnected(Context context){
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
		
		return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
	}
	
	public static String getLatLng(Context context, Location currentLocation){
		if (currentLocation != null){
			return context.getString(R.string.latitude_longitude,
					currentLocation.getLatitude(),
					currentLocation.getLongitude());
		} else {
			return "";
		}
	}
	
	public static void saveInPreferences(Context context, String property, int value){
		if (context != null && property != null){
			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
			SharedPreferences.Editor editor = prefs.edit();
			editor.putInt(property, value);
			editor.commit();
		}		
	}
	
	public static int getPreferenceValue(String key, int defaultValue,
			Context context){
		int value = defaultValue;
		
		if (key != null && context != null){
			SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
			value = sharedPrefs.getInt(key, defaultValue);
		}
		
		return value;
	}
	
	public static String getPreferenceValue(String key, String defaultValue, Context context){
		String value = defaultValue;
		
		if (key != null && context != null){
			SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
			value = sharedPrefs.getString(key, defaultValue);
		}
		
		return value;
	}
	

}
