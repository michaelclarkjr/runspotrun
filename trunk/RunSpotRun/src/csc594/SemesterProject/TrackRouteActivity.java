package csc594.SemesterProject;

import java.util.ArrayList;
import java.util.Calendar;
import java.text.SimpleDateFormat;
import csc594.SemesterProject.MyGeoPoint.MyPointType;
import android.app.Activity;
import android.os.Bundle;
import android.os.SystemClock;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Chronometer;
import android.widget.TableLayout;

//import android.widget.Toast;

public class TrackRouteActivity extends Activity 
{
    private LocationManager mlocMgr;
    private LocationListener mlocListener;
    private ArrayList<MyGeoPoint> route;
    //private boolean isRouteFinished = false;
    
    private TableLayout rtStartLayout;
    private TableLayout rtFinLayout;
    private Button pauseBtn;
    private EditText testET;
    private boolean useHardCodedPts;
    
    private Chronometer chronTimer;
    private long timeWhenStopped = 0;
    private Calendar cal = Calendar.getInstance();
    private SimpleDateFormat fmt1 = new SimpleDateFormat("MM/dd/yyyy");
    private SimpleDateFormat fmt2 = new SimpleDateFormat("hh:mm a");
    
	private int latitude;
    private int longitude;
    private String curDate;
    private String routeName;
    private String curTime;
    private String curDist;
    
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.route);
		
		rtStartLayout = (TableLayout)findViewById(R.id.trackRoute1);
		rtFinLayout = (TableLayout)findViewById(R.id.trackRoute2);
		rtFinLayout.setVisibility(View.INVISIBLE); 
		testET = (EditText)findViewById(R.id.test);
		pauseBtn = (Button)findViewById(R.id.pause);
		
		chronTimer = (Chronometer)findViewById(R.id.timer);
		curDate = fmt1.format(cal.getTime());
		routeName = "testRoute";
		curDist = "1.1"; //test dist
				
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
             					//minTime minDistance - hints not rules *might be better to use something else for set intervals*
        }  

        startRoute();
	}

	
	/* Gets current data about a point in the route: (int) latitude, (int) longitude, (string) time, 
	  (string) distance, (string) name (as used by the MyGeoPoint Class).
	  */
	private void getRoutePointData(Location loc)
	{
		double lat =  loc.getLatitude();
		double lng = loc.getLongitude();
		
		latitude = (int) (lat * 1E6); //compatible with maps api
		longitude = (int) (lng * 1E6);
	
		cal = Calendar.getInstance(); //update time
		curTime = curDate + " " + fmt2.format(cal.getTime()); //+ " " + String.valueOf(latitude) + " " + String.valueOf(longitude);
		//Toast.makeText( getApplicationContext(),curTime,Toast.LENGTH_SHORT).show();
	}
	
	private void startRoute()
	{
		//testET.setText("In Progress");
		chronTimer.start();
		
		 if(!useHardCodedPts)
		 {
			 curTime = curDate + " " + fmt2.format(cal.getTime());
			 route.add(new MyGeoPoint(latitude, longitude, curTime, curDist, routeName, MyPointType.Start));
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
			 route.add(new MyGeoPoint(latitude, longitude, curTime, curDist, routeName, MyPointType.Normal));
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
		//testET.setText("Finished");
		chronTimer.stop();
		rtStartLayout.setVisibility(View.GONE); //gone - layout no longer takes up space
		rtFinLayout.setVisibility(View.VISIBLE); 
		
		testET.setText(getElapsedTimeString());
		
		if(!useHardCodedPts)
		{
			cal = Calendar.getInstance(); //update time
			curTime = curDate + " " + fmt2.format(cal.getTime());
			route.add(new MyGeoPoint(latitude, longitude, curTime, curDist, routeName, MyPointType.Stop));
			System.out.println("STOP");
			mlocMgr.removeUpdates(mlocListener); //unregister
		}
		else
		{
			route.add(new MyGeoPoint(39312594,	-84280490, MyPointType.Stop));
		}
		
		//isRouteFinished = true; //might no longer need this
		
		//return information to calling activity
		//Intent i = getIntent();
		//i.putExtra("Route", route);
		//setResult(RESULT_OK, i);
		
		//finish();
	}
	
	public String getElapsedTimeString() 
	{       
		long elapsedTime = SystemClock.elapsedRealtime() - chronTimer.getBase();
		
	    //String format = String.format("%%0%dd", 2);  
	    elapsedTime = elapsedTime / 1000;  
	    String seconds = String.format("%s second(s)", elapsedTime % 60);  
	    String minutes = String.format("%s minute(s)", (elapsedTime % 3600) / 60);  
	    String hours = String.format("%s hours(s)", elapsedTime / 3600);  
	    String time =  hours + " " + minutes + " " + seconds;  
	    return time;  
	}  
	
	public void doEndRoute(View view)
	{    	
		endRoute();
	}
	
	private void doPauseResetTimer(String action)
	{
		if(action.equals("reset"))
		{
			chronTimer.setBase(SystemClock.elapsedRealtime());
			timeWhenStopped = 0;
		}
		else if((action.equals("pause")) && (pauseBtn.getText().equals("Pause Timer")))
		{
			timeWhenStopped = chronTimer.getBase() - SystemClock.elapsedRealtime();
			chronTimer.stop();
			pauseBtn.setText("Resume Timer");
		}
		else //resume
		{
			chronTimer.setBase(SystemClock.elapsedRealtime() + timeWhenStopped);
			chronTimer.start();
			pauseBtn.setText("Pause Timer");
		}
		
	}
	public void doResetTimer(View view)
	{    	
		doPauseResetTimer("reset");
	}
	 
	
	public void doPauseTimer(View view)
	{    	
		doPauseResetTimer("pause");
	}
	
	public void doGoToMap(View view)
	{    	
		//if(!isRouteFinished) //shouldn't need this now with 
		//{						//keeping launch map btn invisible till it is reset in endRoute call
			//endRoute();
		//}
		
		/*for(int i = 0; i < route.size(); i++)
		{
			MyGeoPoint testPt = route.get(i);
			System.out.println(testPt.getTypeAsString() + " " + testPt.getTimeAsString());
		}*/
		
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

