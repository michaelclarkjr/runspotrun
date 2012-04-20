package csc594.SemesterProject;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
//import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

//Track Route
import csc594.SemesterProject.MyGeoPoint.MyPointType;


import java.util.Calendar;
import java.text.SimpleDateFormat;
import android.os.SystemClock;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Chronometer;
import android.widget.TableLayout;

public class MainActivity extends Activity implements OnClickListener
{
	private ListView listview;
    private ArrayList mListItem;
    
    //TrackRoute
    private LocationManager mlocMgr;
    private LocationListener mlocListener;
    private ArrayList<MyGeoPoint> route;
    
    private Button pauseBtn;
    private Button startBtn;
    private Button stopBtn;
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

    
	//private static final int GET_ROUTE = 1010;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);     
        
        listview = (ListView) findViewById(R.id.list_view);
        ItemBO temp = new ItemBO();
        mListItem = temp.getItems();
        listview.setAdapter(new ListAdapter(this, R.id.list_view,
                mListItem));
        
        //Track Route
        startBtn = (Button)findViewById(R.id.start);
        stopBtn = (Button)findViewById(R.id.stop);
        stopBtn.setVisibility(View.GONE); 
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
    
  	
    public void doStartRoute(View view)
    {
    	chronTimer.setBase(SystemClock.elapsedRealtime());
		chronTimer.start();
		startBtn.setVisibility(View.GONE); //gone - layout no longer takes up space
		stopBtn.setVisibility(View.VISIBLE); 
		
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
		chronTimer.stop();

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
	}
	
	private String getElapsedTimeString() 
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
		else if((action.equals("pause")) && (pauseBtn.getText().equals("Pause")))
		{
			timeWhenStopped = chronTimer.getBase() - SystemClock.elapsedRealtime();
			chronTimer.stop();
			pauseBtn.setText("Resume");
		}
		else //resume
		{
			chronTimer.setBase(SystemClock.elapsedRealtime() + timeWhenStopped);
			chronTimer.start();
			pauseBtn.setText("Pause");
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
    
   
    
    @Override
	public void onClick(View arg0) {
    	//do nothing
	}
    
    // ***ListAdapter***
    private class ListAdapter extends ArrayAdapter { //--CloneChangeRequired
        private ArrayList mList; //--CloneChangeRequired
        private Context mContext;
 
        public ListAdapter(Context context, int textViewResourceId,
                ArrayList list) { //--CloneChangeRequired
            super(context, textViewResourceId, list);
            this.mList = list;
            this.mContext = context;
        }
 
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;
            try {
                if (view == null) {
                    LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    view = vi.inflate(R.layout.listviewrow, null); //--CloneChangeRequired(list_item)
                }
                final ItemBO listItem = (ItemBO)mList.get(position); //--CloneChangeRequired
                if (listItem != null) {
                    // setting list_item views
                    ((TextView)view.findViewById(R.id.tvDate))
                         .setText(listItem.getDate());
                    ((TextView)view.findViewById(R.id.tvTime))
                    	.setText(listItem.getTime());
                    ((TextView)view.findViewById(R.id.tvDistance))
                    	.setText(listItem.getDistance().toString() + " Miles");
                    view.setOnClickListener(new OnClickListener() {
                        public void onClick(View arg0) { //--clickOnListItem
//                        	Toast
//	                  		  .makeText(MainActivity.this, "onClick list adapter",Toast.LENGTH_LONG)
//	                  		  .show();
                            /*Intent myIntent = new Intent(MainActivity.this, TrackRouteActivity.class);
                            myIntent.putExtra("NAME", listItem.getName());
                            startActivity(myIntent); */
//                            finish();
                        }
                    });
                }
            } catch (Exception e) {
               // Log.i(Splash.ListAdapter.class.toString(), e.getMessage());
            }
            return view;
        }
    }	
    public class ItemBO {
        private String name;
        private String date;
        private String time;
        private double distance;
        
        public String getName() { return name;  }
        public void setName(String name) { this.name = name; }
        
        public String getDate() { return date; }
        public void setDate(String _date) { this.date = _date; }
        
        public String getTime() { return time; }
        public void setTime(String _time) { this.time = _time; }
        
        public Double getDistance() { return distance; }
        public void setDistance(double _distance) { this.distance = _distance; }
        
        
        // / TEMP DEBUG--------------------
        public ArrayList getItems() {
            ArrayList list = new ArrayList();
            ItemBO item;
     
            item = new ItemBO();
            item.setName("item 1");
            item.setDate("4/1/12");
            item.setTime("12:11 pm");
            item.setDistance(1.4);
            list.add(item);
     
            item = new ItemBO();
            item.setName("item 1");
            item.setDate("4/4/12");
            item.setTime("2:11 pm");
            item.setDistance(2.5);
            list.add(item);
     
            item = new ItemBO();
            item.setName("item 1");
            item.setDate("4/6/12");
            item.setTime("3:11 pm");
            item.setDistance(4.0);
            list.add(item);
     
            item = new ItemBO();
            item.setName("item 1");
            item.setDate("4/1/12");
            item.setTime("12:11 pm");
            item.setDistance(1.4);
            list.add(item);
     
            item = new ItemBO();
            item.setName("item 1");
            item.setDate("4/4/12");
            item.setTime("2:11 pm");
            item.setDistance(2.5);
            list.add(item);
     
            item = new ItemBO();
            item.setName("item 1");
            item.setDate("4/6/12");
            item.setTime("3:11 pm");
            item.setDistance(4.0);
            list.add(item);
            item = new ItemBO();
            item.setName("item 1");
            item.setDate("4/1/12");
            item.setTime("12:11 pm");
            item.setDistance(1.4);
            list.add(item);
     
            item = new ItemBO();
            item.setName("item 1");
            item.setDate("4/4/12");
            item.setTime("2:11 pm");
            item.setDistance(2.5);
            list.add(item);
     
            item = new ItemBO();
            item.setName("item 1");
            item.setDate("4/6/12");
            item.setTime("3:11 pm");
            item.setDistance(4.0);
            list.add(item);
            return list;
        }
    }

}
