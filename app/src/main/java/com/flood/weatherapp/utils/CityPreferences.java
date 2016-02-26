package com.flood.weatherapp.utils;

import android.app.Activity;

public class CityPreferences {
	
	public static final String DEFAULT_CITY = "Atlanta,GA";
	
	public static String getCity(Activity activity){
		return activity.getPreferences(Activity.MODE_PRIVATE).getString("city", DEFAULT_CITY);
	}
	
	public static void setCity(Activity activity, String city){
		activity.getPreferences(Activity.MODE_PRIVATE).edit().putString("city", city).commit();
	}

}
