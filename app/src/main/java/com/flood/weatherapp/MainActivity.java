package com.flood.weatherapp;

import java.util.Locale;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentPagerAdapter;
import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.flood.weatherapp.dialogs.ChangeCityDialogFragment;
import com.flood.weatherapp.fragments.CurrentWeatherFragment;
import com.flood.weatherapp.fragments.ForecastWeatherFragment;
import com.flood.weatherapp.utils.LocationGetter;
import com.flood.weatherapp.utils.LocationGetter.LocationResult;
import com.flood.weatherapp.utils.Utils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;


public class MainActivity extends ActionBarActivity implements ActionBar.TabListener, android.location.LocationListener,
ConnectionCallbacks, OnConnectionFailedListener{
	
	private LocationClient locationClient;

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;

    @SuppressWarnings("deprecation")
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set up the action bar.
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        // When swiping between different sections, select the corresponding
        // tab. We can also use ActionBar.Tab#select() to do this if we have
        // a reference to the Tab.
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by
            // the adapter. Also specify this Activity object, which implements
            // the TabListener interface, as the callback (listener) for when
            // this tab is selected.
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(mSectionsPagerAdapter.getPageTitle(i))
                            .setTabListener(this));
        }
        
        locationClient = new LocationClient(this, this, this);
    }
    
    @Override
    protected void onResume(){
    	super.onResume();
    }
    
    @Override
    protected void onStop(){
    	super.onStop();
    	locationClient.disconnect();
    }

    @Override
    protected void onStart(){
    	super.onStart();
    	locationClient.connect();
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent){
    	switch(requestCode){
    	case Utils.CONNECTION_FAILURE_RESOLUTION_REQUEST:
    		switch(resultCode){
    		case Activity.RESULT_OK:
    		break;
    		default://disconnected
    			break;
    		}
    		break;
    	default:
    		//unkown request code
    		break;
    	}
    }
    
    private boolean servicesConnected(){
    	int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
    	return ConnectionResult.SUCCESS == resultCode;
    }
    
    

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	if (item.getItemId() == R.id.change_city){
    		showChangeCityDialog();
    	} else if (item.getItemId() == R.id.refresh) {
    		refresh();
    		
    	} else if (item.getItemId() == R.id.location){
    		startLocation();
    		
    	}
    	
        return false;
    }
    
    private void showChangeCityDialog(){
    	FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
    	Fragment prev = getSupportFragmentManager().findFragmentByTag("changeCityDialog");
    	
    	if (prev != null){
    		ft.remove(prev);
    	}
    	
    	ft.addToBackStack(null);
    	
    	ChangeCityDialogFragment newFragment = ChangeCityDialogFragment.newInstance();
    	newFragment.show(ft, "changeCityDialog");
    }
    
    public void changeCity(String city){
    	if (city != null && !"".equals(city.trim())){
    		city = Utils.capitalize(city.trim());
    		
    		CurrentWeatherFragment currentWeatherFragment = mSectionsPagerAdapter.getCurrentWeatherFragment();
    		
    		//change city for all fragments
    		if (currentWeatherFragment != null){
    			currentWeatherFragment.changeCity(city);
    		}
    		
    		ForecastWeatherFragment forecastWeatherFragment = mSectionsPagerAdapter.getForecastWeatherFragment();
    		
    		if (forecastWeatherFragment != null){
    			forecastWeatherFragment.changeCity(city);
    		}
    	} else {
    		Toast.makeText(this, R.string.you_must_enter_city, Toast.LENGTH_SHORT).show();
    	}
    }
    
    public void refresh(){
    	CurrentWeatherFragment currentWeatherFragment = mSectionsPagerAdapter.getCurrentWeatherFragment();
    	
    	if (currentWeatherFragment != null){
    		currentWeatherFragment.refresh();
    	}
    	
    	ForecastWeatherFragment forecastWeatherFragment = mSectionsPagerAdapter.getForecastFragment();
    	
    	if (forecastWeatherFragment != null){
    		forecastWeatherFragment.refresh();
    	}
    }
    

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in
        // the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
        	Fragment fragment = null;
        	
        	if (position == 0){
        		fragment = new CurrentWeatherFragment();
        	} else if (position == 1){
        		fragment = new ForecastWeatherFragment();
        	}
        	
        	return fragment;
        }

        @Override
        public int getCount() {
            // Show 2 total pages.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.title_section1).toUpperCase(l);
                case 1:
                    return getString(R.string.title_section2).toUpperCase(l);
            }
            return null;
        }
        
        private String getFragmentTag(int pos){
        	return "android:switcher:" + R.id.pager + ":" + pos;
        }
        
        public CurrentWeatherFragment getCurrentWeatherFragment(){
        	return(CurrentWeatherFragment) getSupportFragmentManager().findFragmentByTag(getFragmentTag(0));
        }
        
        public ForecastWeatherFragment getForecastWeatherFragment(){
        	return(ForecastWeatherFragment) getSupportFragmentManager().findFragmentByTag(getFragmentTag(1));
        }
    }


	@Override
	public void onConnectionFailed(ConnectionResult result) {
		if (result.hasResolution()){
			try {
				result.startResolutionForResult(this, Utils.CONNECTION_FAILURE_RESOLUTION_REQUEST);
			} catch(Exception e){
				
			}
		} else {
			//no resolution ==> error message
			
		}
		
	}


	@Override
	public void onConnected() {
		
		
	}


	@Override
	public void onDisconnected() {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		
	}

	private void onLocationChanged1(Location location){
		if (servicesConnected()){
			Location location1 = locationClient.getLastLocation();
			
			if(location1 != null){
				Toast.makeText(MainActivity.this, R.string.new_location_setted, Toast.LENGTH_SHORT).show();
				
				CurrentWeatherFragment currentWeatherFragment = mSectionsPagerAdapter.getCurrentWeatherFragment();
				
				if (currentWeatherFragment != null){
					currentWeatherFragment.loadLocation(location1);
				}
				
				ForecastWeatherFragment forecastWeatherFragment = mSectionsPagerAdapter.getForecastWeatherFragment();
				
				if(forecastWeatherFragment != null){
					forecastWeatherFragment.loadLocation(location1);
				}
			} else {
				detectLocationWithoutPlayServices();
			}
		} else {
			detectLocationWithoutPlayServices();
		}
	}
	
	private void detectLocationWithoutPlayServices(){
		LocationResult locationResult = new LocationResult(){
			public void gotLocation(Location location){
				if (location != null){
					Toast.makeText(MainActivity.this, R.string.new_location_setted, Toast.LENGTH_SHORT).show();
					
					CurrentWeatherFragment currentWeatherFragment = mSectionsPagerAdapter.getCurrentWeatherFragment();
					
					if (currentWeatherFragment != null){
						currentWeatherFragment.loadLocation(location);
					}
					
					ForecastWeatherFragment forecastWeatherFragment = mSectionsPagerAdapter.getForecastWeatherFragment();
					
					if (forecastWeatherFragment != null){
						forecastWeatherFragment.loadLocation(location);
					}
				} else {
					Toast.makeText(MainActivity.this, R.string.gps_error, Toast.LENGTH_SHORT).show();
				}
			}

			@Override
			public void getLocation(Location location) {
				// TODO Auto-generated method stub
				
			}
		};
		
		LocationGetter userLocation = new LocationGetter();
		boolean locationEnabled = userLocation.getLocation(this, locationResult);
		
		if(!locationEnabled){
			Toast.makeText(MainActivity.this, R.string.enable_gps, Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}

}
