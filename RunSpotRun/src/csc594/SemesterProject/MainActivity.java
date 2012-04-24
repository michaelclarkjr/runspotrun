package csc594.SemesterProject;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
//import android.location.Location;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

//Track Route
import csc594.SemesterProject.MyGeoPoint.MyPointType;


import java.util.Calendar;
import java.text.SimpleDateFormat;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Chronometer;
import android.widget.TableLayout;

public class MainActivity extends Activity implements OnClickListener
{
	//publicly available database entry - this will be set by main app onCreate
	public static DatabaseHelper DataBase;
	
	//service to grab GPS points
	//private Intent RunIntent;
	
	public static final int MENU_SETTINGS = Menu.FIRST+1;
	public static final int MENU_ABOUT = Menu.FIRST+2;
	private ListView listview;
    private ArrayList<RouteItem> mListItem;
    
 
    //private Button pauseBtn;
    private Button startBtn;
    private Button stopBtn;
    private Chronometer chronTimer;
    //private long timeWhenStopped = 0;
    private ProgressBar progressBar;
    
	private LocationManager mlocMgr;
    
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);     
        
        listview = (ListView) findViewById(R.id.list_view);
        startBtn = (Button)findViewById(R.id.start);
        stopBtn = (Button)findViewById(R.id.stop);
        stopBtn.setVisibility(View.GONE); 
		//pauseBtn = (Button)findViewById(R.id.pause);		
		chronTimer = (Chronometer)findViewById(R.id.timer);
		progressBar = (ProgressBar) findViewById(R.id.progressBar1);
		progressBar.setVisibility(View.INVISIBLE); //simple way to not have a spinning wheel 
													//before route is in progress
		DataBase = new DatabaseHelper(this);
		
		UpdateTripHistory();
		
		mlocMgr = (LocationManager) getSystemService(Context.LOCATION_SERVICE); //helper to check GPS status
    }  
    
    
    @Override
 	public void onResume() 
 	{
 		super.onResume();  //not sure on this yet
 	}
 	
 	@Override
 	public void onPause()
 	{
 		super.onPause();
 	}
    
 	@Override
 	protected void onDestroy() //stop service?
 	{
 	    super.onDestroy();
 	}
 	
 	
    /* TIMER - PAUSE - RESET - Works but doesn't make sense to use; without it pausing
     * and/or deleting & recreating route points */
   /* private void doPauseResetTimer(String action)
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
	}*/
        
	private String getElapsedTimeString() 
	{       
		long elapsedTime = SystemClock.elapsedRealtime() - chronTimer.getBase();
		  
	    elapsedTime = elapsedTime / 1000;  
	    String seconds = String.format("%s second(s)", elapsedTime % 60);  
	    String minutes = String.format("%s minute(s)", (elapsedTime % 3600) / 60);  
	    String hours = String.format("%s hours(s)", elapsedTime / 3600);  
	    String time =  hours + " " + minutes + " " + seconds;  
	    return time;  
	}
	

	 /* START - STOP Btns (start/stop timer and start/end (GPS Track Route) Service) */
	
	private void promptUserToTurnOnGPS()
	{
		new AlertDialog.Builder(this)
		.setTitle("GPS Provider Must Be Enabled")
		.setMessage("This app requires GPS to be enabled " +
				"for accurate route tracking. Would you like to be" +
				" redirected to enable it?")
				.setCancelable(false)  
				.setPositiveButton("Yes", new DialogInterface.OnClickListener() {  
					public void onClick(DialogInterface dialog, int id) {  
						Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);  
						startActivityForResult(intent, 1);  
					}  
				})  
				.setNegativeButton("No", null)
				.show();  
	}

	public void doStartRoute(View view)
	{	
		if(!mlocMgr.isProviderEnabled(LocationManager.GPS_PROVIDER))
		{
			//send user to settings with prompt to turn GPS on
			promptUserToTurnOnGPS();

		}
		else /* Don't continue with route till user turns GPS on */
		{
			//easy way
			startService(new Intent(this, RunningService.class));
			//if(RunIntent == null){RunIntent = new Intent(this, RunningService.class);}
			//startService(RunIntent);  //intend to create this in onCreate()?


			//hard way...
			/* Intent i = new Intent();
		       i.setClassName( "csc594.SemesterProject",
		        "csc594.SemesterProject.RunningService" );
		       bindService( i, null, Context.BIND_AUTO_CREATE);
		       this.startService(i); */

			chronTimer.setBase(SystemClock.elapsedRealtime());
			chronTimer.start();
			progressBar.setVisibility(View.VISIBLE);
			startBtn.setVisibility(View.GONE); //gone - no longer takes up space
			stopBtn.setVisibility(View.VISIBLE); 
		}
	}

	private void endRoute()
	{
		chronTimer.stop();
		progressBar.setVisibility(View.INVISIBLE);
		stopBtn.setVisibility(View.GONE); //gone - no longer takes up space
		startBtn.setVisibility(View.VISIBLE);
		chronTimer.setBase(SystemClock.elapsedRealtime());
		
		/*Toast.makeText(this, "Route has been added to top of 'Past Trips' - Click on it to" +
				" view statistics, map it, or delete route from history. ", Toast.LENGTH_LONG).show();*/
		
		new AlertDialog.Builder(this)
 		  .setTitle("Completed Trip")
 		  .setMessage(R.string.finished_route_in_list)
 		  .setNeutralButton("OK", null)
 		  .show();
		
		
		UpdateTripHistory(); //newly finished route will be at top of the list
		
		stopService(new Intent(this, RunningService.class));
	}
		
	public void doEndRoute(View view)
	{    	
		endRoute();
	}
		
  
	/* SETTINGS */
    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu
		.add(Menu.NONE, MENU_SETTINGS, 0, "Preferences")
		.setIntent(new Intent(this, PreferencesActivity.class))		
		.setIcon(R.drawable.settings);
		
		menu
		.add(Menu.NONE, MENU_ABOUT, 1, "About")
		.setIntent(new Intent(this, AboutActivity.class))		
		.setIcon(R.drawable.about);
		
		return(super.onCreateOptionsMenu(menu));
	}
    
    /* TRIP(s) HISTORY */
    
    void UpdateTripHistory()
    {  
    	//poll database
    	mListItem = DataBase.GetRoutes();
    	
    	//attached adapter
         listview.setAdapter(new ListAdapter(this, R.id.list_view,
                 mListItem));
    }
    
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
                final RouteItem listItem = (RouteItem)mList.get(position); //--CloneChangeRequired
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
							Intent myIntent = new Intent(MainActivity.this, RouteInfoActivity.class);
                            myIntent.putExtra("ROUTEKEY", listItem.getKey());
                            startActivity(myIntent);
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
}

