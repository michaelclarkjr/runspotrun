package csc594.SemesterProject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import com.google.android.maps.GeoPoint;

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
//import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

public class RunningService extends Service 
{
	private int latitude;
	private int longitude;
	//private String curDate;
	private String routeName;
	private String curTime;
	private String curDist;

	private Calendar cal = Calendar.getInstance();
	private SimpleDateFormat fmt1 = new SimpleDateFormat("MM/dd/yyyy");
	private SimpleDateFormat fmt2 = new SimpleDateFormat("hh:mm:ss a");

	private LocationManager mlocMgr;
	private LocationListener mlocListener;
	//private ArrayList<MyGeoPoint> route;
	private int routeKeyDB;

	private Timer mTimer = new Timer();
	private TimerTask gpsUpdate;
	private final Handler handler1 = new Handler();
	
	//private String tag="TestService";

	private NotificationManager mNM;

	// Unique Identification Number for the Notification.
	// We use it on Notification start, and to cancel it.
	private int NOTIFICATION = R.string.local_service_started;

	// This is the object that receives interactions from clients.  See
	// RemoteService for a more complete example.
	//private final IBinder mBinder = new LocalBinder();
   
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

		//latitude = 0;
		//longitude = 0;
		//curDate = fmt1.format(cal.getTime());
		routeName = "newRoute";
		curTime = fmt2.format(cal.getTime());
		curDist = "0"; //placeHolder

		//route = new ArrayList<MyGeoPoint>();
	    routeKeyDB = 0;

		mlocMgr = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		mlocListener = new MyLocationListener();
 		
        mlocMgr.requestLocationUpdates(LocationManager.GPS_PROVIDER, 60000, 1, mlocListener);
        			//provider, minTime in ms, minDistance in meters, Location Listener
        					//minTime minDistance - hints not rules *0,0 as often as possible*
        //bkground services should be careful abt. setting sufficiently high minTime so that the 
        //the device doesn't consume too much power by keeping GPS radio on all the time.
        //in particular, values under 60000 ms. are not recommended.
	}
	
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) 
	{
		//Log.i("LocalService", "Received start id " + startId + ": " + intent);
		// We want this service to continue running until it is explicitly
		// stopped, so return sticky.
		//Toast.makeText(this, "onStartCommand", Toast.LENGTH_SHORT).show();
		
		//Location startLoc = mlocMgr.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		//updateLatLong(startLoc);
		
		//Date curDate = cal.getTime(); //just need once
		//route.add(new MyGeoPoint(latitude, longitude, curTime, curDist, routeName, MyPointType.Start));
		RouteItem item = new RouteItem();
	    item.setName(routeName);
	    item.setDate(fmt1.format(cal.getTime())); //current date -  just need once
	    item.setTime(curTime);
	    item.setDistance(Double.parseDouble(curDist));
	    
		routeKeyDB =  (int) MainActivity.DataBase.AddRoute(item); /*this returns long, need int for the key */
		
		 
		//lastPoint = new GeoPoint(latitude, longitude);
		//addToRoute();

		/* Timed storing points for rest of the route */
		gpsUpdate = new TimerTask() {
			public void run() {
				handler1.post(new Runnable() {
					public void run() {
						addToRoute(); 
					}
				});
			}
		};

		int interval = 0;
		try
		{
			SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
			interval = Integer.parseInt(sharedPrefs.getString("intervalKey", "10"));
			System.out.println(interval);
		}
		catch(Exception ex)  {	}

		//5 seconds before 1st pt. (more time for GPS to get a 1st Loc.) - then set pts. at user's chosen interval
		mTimer.scheduleAtFixedRate(gpsUpdate, 5 * 1000, interval * 1000);

		return START_STICKY;
	}

	@Override
	public void onDestroy() 
	{
		// Cancel the persistent notification.
		mNM.cancel(NOTIFICATION);
		mTimer.cancel(); //stop adding geo pts.
		
		//cal = Calendar.getInstance(); //update time
		//curTime = fmt2.format(cal.getTime());
		//route.add(new MyGeoPoint(latitude, longitude, curTime, curDist, routeName, MyPointType.Stop))
		//MainActivity.DataBase.AddPoint(new MyGeoPoint(latitude, longitude, curTime, curDist, routeName, MyPointType.Stop), routeKeyDB);
		
		addToRoute();
		mlocMgr.removeUpdates(mlocListener); //unregister
		
		/** TESTING **/
//		System.out.println("STOP");
//		ArrayList<MyGeoPoint> route = MainActivity.DataBase.GetPoints(routeKeyDB);
//		for(int i = 0; i < route.size(); i++)
//		{
//			MyGeoPoint testPt = route.get(i);
//			System.out.println(testPt.getTypeAsString() + " " + "lat: " 
//					+ testPt.getPoint().getLatitudeE6()  + " long: " + testPt.getPoint().getLongitudeE6()
//					+ " " + testPt.getTimeAsString());
//		}

		// Tell the user we stopped.
		//Toast.makeText(this, R.string.local_service_stopped, Toast.LENGTH_SHORT).show();
	}

	
	@Override
	public IBinder onBind(Intent intent) 
	{
		return null;
	}

   
   /* Helper methods with gathering route point data */
   
	private void updateLatLong(Location loc)
	{
		double lat = 0;
		double lng = 0;
		
		if(loc != null)
		{
			lat = loc.getLatitude();
			lng = loc.getLongitude();
		}
		
		latitude = (int) (lat * 1E6); //compatible with maps api
		longitude = (int) (lng * 1E6);

		//cal = Calendar.getInstance(); //update time
		//curTime = fmt2.format(cal.getTime());
	}    

	GeoPoint lastPoint = null;
	
	private void addToRoute()
	{
		GeoPoint newPoint = new GeoPoint(latitude,longitude);
		double distance = 0.0;
		if(lastPoint != null)
		{
			//calc distance
			distance = GetDistance(lastPoint, newPoint);
			curDist = String.format("%.2f", distance); 
			//curDist = Double.toString(distance);
		}
		lastPoint = newPoint;
		
		cal = Calendar.getInstance(); //update time
		curTime = fmt2.format(cal.getTime());
		
		MainActivity.DataBase.AddPoint(new MyGeoPoint(latitude, longitude, curTime, curDist, routeName, MyPointType.Normal), routeKeyDB);
	}

	private double GetDistance(GeoPoint p1, GeoPoint p2)
	{
		try
		{
			float[] dist = new float[3];
			Location.distanceBetween(p1.getLatitudeE6()/1E6, p1.getLongitudeE6()/ 1E6, p2.getLatitudeE6()/ 1E6, p2.getLongitudeE6()/ 1E6, dist);
			//Location.distanceBetween(3932394,-84307652, 39324073,-84289284, dist);			
			return dist[0] * 0.000621371192;//meters to miles
		}
		catch(Exception e)
		{
			return 0;
		}
	}
	
	public class MyLocationListener implements LocationListener
	{
		@Override
		public void onLocationChanged(Location loc)
		{
			//getRoutePointData(loc); //update route point data
			updateLatLong(loc);
		}	

		@Override	
		public void onProviderDisabled(String provider) {}	

		@Override
		public void onProviderEnabled(String provider) {}	

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras)	{}
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

	/*private void promptUserToTurnOnGPS()
	{
		Toast.makeText(this, R.string.gps_off_prompt, Toast.LENGTH_LONG).show();

		final Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
		//intent.addCategory(Intent.CATEGORY_LAUNCHER);
		//intent.setComponent(toLaunch);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		//startActivityForResult(intent, 1);
		startActivity(intent);
	}*/ //worked but wasn't ideal checking from within this service
}
