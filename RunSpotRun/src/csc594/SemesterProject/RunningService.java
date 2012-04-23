package csc594.SemesterProject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import csc594.SemesterProject.MyGeoPoint.MyPointType;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.widget.Toast;

public class RunningService extends Service 
{
	private int latitude;
	private int longitude;
	private String curDate;
	private String routeName;
	private String curTime;
	private String curDist;

	private Calendar cal = Calendar.getInstance();
	private SimpleDateFormat fmt1 = new SimpleDateFormat("MM/dd/yyyy");
	private SimpleDateFormat fmt2 = new SimpleDateFormat("hh:mm:ss a");

	private boolean useHardCodedPts;  //test
	private LocationManager mlocMgr;
	private LocationListener mlocListener;
	private ArrayList<MyGeoPoint> route;

	private TimerTask gpsUpdate;
	private final Handler handler = new Handler();
	private Timer mTimer = new Timer();

	String tag="TestService";

	private NotificationManager mNM;

	// Unique Identification Number for the Notification.
	// We use it on Notification start, and to cancel it.
	private int NOTIFICATION = R.string.local_service_started;

	// This is the object that receives interactions from clients.  See
	// RemoteService for a more complete example.
	private final IBinder mBinder = new LocalBinder();
   
	@Override
	public void onStart(Intent intent, int startId) 
	{      
		super.onStart(intent, startId);  
		Toast.makeText(this, "Service started...", Toast.LENGTH_LONG).show();  
	}

	//    * Class for clients to access.  Because we know this service always
	//    * runs in the same process as its clients, we don't need to deal with
	//    * IPC.

	public class LocalBinder extends Binder {
		RunningService getService() {
			return null;
		}
	}

	@Override
	public void onCreate() 
	{
		//Toast.makeText(this, "Service created...", Toast.LENGTH_LONG).show(); 
		mNM = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);

		// Display a notification about us starting.  We put an icon in the status bar.
		showNotification();

		curDate = fmt1.format(cal.getTime());
		routeName = "testRoute";
		curDist = "1.1"; //test dist

		route = new ArrayList<MyGeoPoint>();

		mlocMgr = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		if(!mlocMgr.isProviderEnabled(LocationManager.GPS_PROVIDER))
		{
			//send user to settings with prompt to turn GPS on
			
		
		}
		
		mlocListener = new MyLocationListener();
 		
        mlocMgr.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mlocListener);
        			//provider, minTime in ms, minDistance in meters, Location Listener
        					//minTime minDistance - hints not rules *might be better to use something else for set intervals*

	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) 
	{
		//Log.i("LocalService", "Received start id " + startId + ": " + intent);
		// We want this service to continue running until it is explicitly
		// stopped, so return sticky.
		Toast.makeText(this, "onStartCommand", Toast.LENGTH_SHORT).show();
		
		if(mlocMgr.isProviderEnabled(LocationManager.GPS_PROVIDER))
		{
			Location startLoc = mlocMgr.getLastKnownLocation(LocationManager.GPS_PROVIDER);
     		
			getRoutePointData(startLoc);
			
			curTime = curDate + " " + fmt2.format(cal.getTime());
			route.add(new MyGeoPoint(latitude, longitude, curTime, curDist, routeName, MyPointType.Start));
		}
		else
		{
			//send user to settings with prompt to turn GPS on... 
			
			route.add(new MyGeoPoint(39312718,	-84281230, MyPointType.Start));
			route.add(new MyGeoPoint(39313623,	-84282732));  //just fill in rest
			route.add(new MyGeoPoint(39313847,	-84283859));
			route.add(new MyGeoPoint(39314694,	-84284137));
			route.add(new MyGeoPoint(39315831,	-84281509));
			route.add(new MyGeoPoint(39318919,	-84279041));
		    route.add(new MyGeoPoint(39321500,	-84273033));
			route.add(new MyGeoPoint(39319724,	-84271874));
			route.add(new MyGeoPoint(39314188,	-84277571));
		}

		/* Timed storing points for rest of the route */
		gpsUpdate = new TimerTask() {
			public void run() {
				handler.post(new Runnable() {
					public void run() {
						addToRoute();  //here where u want to call the method
					}
				});
			}
		};

		int interval = 0;
		try
		{
			SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
			interval = Integer.parseInt(sharedPrefs.getString("intervalKey", "1"));
		}
		catch(Exception ex)  {	}

		mTimer.schedule(gpsUpdate, 0, interval * 1000);  

		return START_STICKY;
	}

	@Override
	public void onDestroy() 
	{
		// Cancel the persistent notification.
		mNM.cancel(NOTIFICATION);

		if(mlocMgr.isProviderEnabled(LocationManager.GPS_PROVIDER))
		{
			cal = Calendar.getInstance(); //update time
			curTime = curDate + " " + fmt2.format(cal.getTime());
			route.add(new MyGeoPoint(latitude, longitude, curTime, curDist, routeName, MyPointType.Stop));
			System.out.println("STOP");
			mlocMgr.removeUpdates(mlocListener); //unregister
		}
		else
		{
			//turn gps on from settings?..
			route.add(new MyGeoPoint(39312594,	-84280490, MyPointType.Stop));
		}

		for(int i = 0; i < route.size(); i++)
		{
			MyGeoPoint testPt = route.get(i);
			System.out.println(testPt.getTypeAsString() + " " + "lat: " 
					+ testPt.getPoint().getLatitudeE6()  + "long: " + testPt.getPoint().getLongitudeE6()
					+ testPt.getTimeAsString());
		}

		// Tell the user we stopped.
		Toast.makeText(this, R.string.local_service_stopped, Toast.LENGTH_SHORT).show();
	}

	
	@Override
	public IBinder onBind(Intent intent) 
	{
		return null;
	}

   
   /* TRACK ROUTE */
   
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
		curTime = curDate + " " + fmt2.format(cal.getTime()); 
	}    

	private void addToRoute()
	{
		route.add(new MyGeoPoint(latitude, longitude, curTime, curDist, routeName, MyPointType.Normal));
	}

	public class MyLocationListener implements LocationListener
	{
		@Override
		public void onLocationChanged(Location loc)
		{
			getRoutePointData(loc); //update route point data
		}	

		@Override	
		public void onProviderDisabled(String provider)
		{
			//send user to settings with prompt to turn GPS on
		}	

		@Override
		public void onProviderEnabled(String provider)
		{	
			//restart?
		}	

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras)	
		{	

		}
	} /* End MyLocationListener */

   
	 /* Show a notification while this service is running. */
	private void showNotification() 
	{
		// In this sample, we'll use the same text for the ticker and the expanded notification
		CharSequence text = getText(R.string.local_service_started);

		// Set the icon, scrolling text and timestamp
		Notification notification = new Notification(R.drawable.start_marker, text,
				System.currentTimeMillis());

		Intent intent = new Intent(this, MainActivity.class); 
		intent.setAction("android.intent.action.MAIN"); 
		intent.addCategory("android.intent.category.LAUNCHER"); 

		// The PendingIntent to launch our activity if the user selects this notification
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
				//new Intent(this, MainActivity.class), 0);//BAD - relaunched app!!
				intent,0);//much better!

		// Set the info for the views that show in the notification panel.
		notification.setLatestEventInfo(this, getText(R.string.local_service_label),
				text, contentIntent);

		// Send the notification.
		mNM.notify(NOTIFICATION, notification);
	}

   
}
