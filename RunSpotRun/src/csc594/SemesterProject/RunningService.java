package csc594.SemesterProject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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
//import android.provider.Settings;
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

		latitude = 0;
		longitude = 0;
		//curDate = fmt1.format(cal.getTime());
		routeName = "testRoute";
		curTime = fmt2.format(cal.getTime());
		curDist = "1.1"; //test dist

		//route = new ArrayList<MyGeoPoint>();
	    routeKeyDB = 0;

		mlocMgr = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

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
		//Toast.makeText(this, "onStartCommand", Toast.LENGTH_SHORT).show();
		
		Location startLoc = mlocMgr.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		getRoutePointData(startLoc);
		
		Date curDate = cal.getTime(); //updated in getRoutePointData()
		//route.add(new MyGeoPoint(latitude, longitude, curTime, curDist, routeName, MyPointType.Start));
		RouteItem item = new RouteItem();
	    item.setName(routeName);
	    item.setDate(fmt1.format(cal.getTime()));
	    item.setTime(curTime);
	    item.setDistance(0.0);
		routeKeyDB =  (int) MainActivity.DataBase.AddRoute(item); /*this returns long, need int for the key */
		MainActivity.DataBase.AddPoint(new MyGeoPoint(latitude, longitude, curTime, curDist, routeName, MyPointType.Start), routeKeyDB);

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
			interval = Integer.parseInt(sharedPrefs.getString("intervalKey", "1"));
		}
		catch(Exception ex)  {	}

		mTimer.schedule(gpsUpdate, interval * 1000, interval * 1000);  

		return START_STICKY;
	}

	@Override
	public void onDestroy() 
	{
		// Cancel the persistent notification.
		mNM.cancel(NOTIFICATION);

		cal = Calendar.getInstance(); //update time
		curTime = fmt2.format(cal.getTime());
		//route.add(new MyGeoPoint(latitude, longitude, curTime, curDist, routeName, MyPointType.Stop))
		MainActivity.DataBase.AddPoint(new MyGeoPoint(latitude, longitude, curTime, curDist, routeName, MyPointType.Stop), routeKeyDB);
		mlocMgr.removeUpdates(mlocListener); //unregister
		
		/** TESTING **/
		/*System.out.println("STOP");
		ArrayList<MyGeoPoint> route = MainActivity.DataBase.GetPoints(routeKeyDB);
		for(int i = 0; i < route.size(); i++)
		{
			MyGeoPoint testPt = route.get(i);
			System.out.println(testPt.getTypeAsString() + " " + "lat: " 
					+ testPt.getPoint().getLatitudeE6()  + " long: " + testPt.getPoint().getLongitudeE6()
					+ " " + testPt.getTimeAsString());
		}*/

		// Tell the user we stopped.
		//Toast.makeText(this, R.string.local_service_stopped, Toast.LENGTH_SHORT).show();
	}

	
	@Override
	public IBinder onBind(Intent intent) 
	{
		return null;
	}

   
   /* Helper methods with gathering route point data */
   
   /* Gets current data about a point in the route: (int) latitude, (int) longitude, (string) time, 
	  (string) distance, (string) name (as used by the MyGeoPoint Class).
	  */
	private void getRoutePointData(Location loc)
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

		cal = Calendar.getInstance(); //update time
		//curTime = curDate + " " + fmt2.format(cal.getTime()); 
		curTime = fmt2.format(cal.getTime());
	}    

	private void addToRoute()
	{
		//route.add(new MyGeoPoint(latitude, longitude, curTime, curDist, routeName, MyPointType.Normal));
		MainActivity.DataBase.AddPoint(new MyGeoPoint(latitude, longitude, curTime, curDist, routeName, MyPointType.Normal), routeKeyDB);
	}

	public class MyLocationListener implements LocationListener
	{
		@Override
		public void onLocationChanged(Location loc)
		{
			getRoutePointData(loc); //update route point data
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
