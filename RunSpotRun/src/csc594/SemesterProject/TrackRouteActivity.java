package csc594.SemesterProject;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.view.View;
import android.widget.EditText;
//import android.widget.Toast;
import csc594.SemesterProject.MyGeoPoint.MyPointType;

public class TrackRouteActivity extends Activity 
{
    private LocationManager mlocMgr;
    private LocationListener mlocListener;
    private int latitude;
    private int longitude;
    ArrayList<MyGeoPoint> route;
    boolean isRouteFinished = false;
    EditText testET;
    boolean useHardCodedPts;
    
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.route);
		
		testET = (EditText)findViewById(R.id.test);
		   
		mlocMgr = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		
        route = new ArrayList<MyGeoPoint>();
        
        Location startLoc = mlocMgr.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (startLoc == null)
        {
            //startLoc = mlocMgr  //alt. way to get to later
                   // .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        	useHardCodedPts = true;
        	
        }
        else //else if still null could alert usr to turn on gps/wifi (code in bk to send usr to their settings), no signal...
        {		//currently assume got a location from GPS - so it's on - using GPS
        	 useHardCodedPts = false;
        	 
        	 getRoutePointData(startLoc);
        	 
        	 mlocListener = new MyLocationListener();
     		
             mlocMgr.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mlocListener);
             			//provider, minTime in ms, minDistance in meters, Location Listener
        }  

        startRoute();
	}

	
	/* add time, distance other info later */
	private void getRoutePointData(Location loc)
	{
		double lat =  loc.getLatitude();
		double lng = loc.getLongitude();
		
		latitude = (int) (lat * 1E6); //compatible with maps api
		longitude = (int) (lng * 1E6);
	
		//String Text = "My current location is: " + "Latitud = " + lat +
		//	" Longitud = " + lng;
		//Toast.makeText( getApplicationContext(),Text,Toast.LENGTH_SHORT).show();
	}
	
	private void startRoute()
	{
		testET.setText("In Progress");
		 if(!useHardCodedPts)
		 {
			 route.add(new MyGeoPoint(latitude, longitude, MyPointType.Start));
		 }
		 else
		 {
			 route.add(new MyGeoPoint(39312718,	-84281230, MyPointType.Start));
			 addToRoute(); //fill in the rest - otherwise in onLocationChanged()
		 }
	}
	
	private void addToRoute()
	{
		 if(!useHardCodedPts)
		 {
			 route.add(new MyGeoPoint(latitude, longitude));
		 }
		 else
		 {
			 route.add(new MyGeoPoint(39313623,	-84282732));
			 route.add(new MyGeoPoint(39313847,	-84283859));
			 route.add(new MyGeoPoint(39314694,	-84284137));
			 route.add(new MyGeoPoint(39315831,	-84281509));
			 route.add(new MyGeoPoint(39318919,	-84279041));
			 route.add(new MyGeoPoint(39321500,	-84273033));
			 route.add(new MyGeoPoint(39319724,	-84271874));
			 route.add(new MyGeoPoint(39314188,	-84277571));
		 }
	}
	
	private void endRoute()
	{
		testET.setText("Finished");
		if(!useHardCodedPts)
		{
			route.add(new MyGeoPoint(latitude, longitude, MyPointType.Stop));
			mlocMgr.removeUpdates(mlocListener); //unregister
		}
		else
		{
			route.add(new MyGeoPoint(39312594,	-84280490, MyPointType.Stop));
		}
		
		isRouteFinished = true; 
		
		//return information to calling activity
		//Intent i = getIntent();
		//i.putExtra("Route", route);
		//setResult(RESULT_OK, i);
		
		//finish();
	}
	
	 public void doEndRoute(View view)
	 {    	
			endRoute();
	 }
	 
	 public void doGoToMap(View view)
	 {    	
		if(!isRouteFinished)
		{
			endRoute();
		}
		
	    Intent launchMap = new Intent(this, RunMapActivity.class);
	    launchMap.putExtra("Route", route);
		startActivity(launchMap);
	}
	
	@Override
	public void onResume() 
	{
		super.onResume();  //not sure on this yet
		//mlocMgr.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mlocListener);
	}
	
	@Override
	public void onPause()
	{
		super.onPause();
		//mlocMgr.removeUpdates(mlocListener);
	}
	
	public class MyLocationListener implements LocationListener

	{

		@Override
		public void onLocationChanged(Location loc)
		{
			getRoutePointData(loc);
			addToRoute();	
		}
	
	
		@Override
	
		public void onProviderDisabled(String provider)
		{

	
		}
	
	
		@Override
		public void onProviderEnabled(String provider)
		{
	

		}
	
	
		@Override
		public void onStatusChanged(String provider, int status, Bundle extras)
	
		{
	
	
		}

	} /* End MyLocationListener */
    
} /* End TrackRoute */

