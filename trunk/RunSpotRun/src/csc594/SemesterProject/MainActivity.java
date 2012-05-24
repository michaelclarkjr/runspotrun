package csc594.SemesterProject;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

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

import android.os.SystemClock;
import android.provider.Settings;
import android.location.LocationManager;
import android.widget.Button;
import android.widget.Chronometer;


public class MainActivity extends Activity implements OnClickListener
{
	//publicly available database entry - this will be set by main app onCreate
	public static DatabaseHelper DataBase;
	
	public static final int MENU_SETTINGS = Menu.FIRST+1;
	public static final int MENU_ABOUT = Menu.FIRST+2;
	private ListView listview;
    private ArrayList<RouteItem> mListItem;
    
    private Button startBtn;
    private Button stopBtn;
    private Chronometer chronTimer;
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
		chronTimer = (Chronometer)findViewById(R.id.timer);
		progressBar = (ProgressBar) findViewById(R.id.progressBar1);
		progressBar.setVisibility(View.INVISIBLE); //no spinning wheel 
													//before route is in progress
		DataBase = new DatabaseHelper(this);
		DataBase.ForceCreate(DataBase.getWritableDatabase());
		
		UpdateTripHistory();
		
		mlocMgr = (LocationManager) getSystemService(Context.LOCATION_SERVICE); //helper to check GPS status
    }  
    
    
    @Override
 	public void onResume() 
 	{
 		super.onResume();  
 	}
 	
 	@Override
 	public void onPause()
 	{
 		super.onPause();
 	}
    
 	@Override
 	protected void onDestroy() 
 	{
 	    super.onDestroy();
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
				//gather gps points in service
			startService(new Intent(this, RunningService.class));

			chronTimer.setBase(SystemClock.elapsedRealtime());
			chronTimer.start();
			progressBar.setVisibility(View.VISIBLE);
			startBtn.setVisibility(View.GONE); 
			stopBtn.setVisibility(View.VISIBLE); 
		}
	}

	private void endRoute()
	{
		chronTimer.stop();
		progressBar.setVisibility(View.INVISIBLE);
		stopBtn.setVisibility(View.GONE); 
		startBtn.setVisibility(View.VISIBLE);
		chronTimer.setBase(SystemClock.elapsedRealtime()); //reset to 0
		
		/*Toast.makeText(this, "Route has been added to top of 'Past Trips' - Click on it to" +
				" view statistics, map it, or delete route from history. ", Toast.LENGTH_LONG).show();*/
		
		stopService(new Intent(this, RunningService.class));
		
		UpdateTripHistory(); //newly finished route will be at top of the list
		
		Toast.makeText(this, "Finished Trip!", Toast.LENGTH_SHORT).show();
		
		final RouteItem listItem_NewRoute = (RouteItem)listview.getAdapter().getItem(0);
		Intent myIntent = new Intent(MainActivity.this, RouteInfoActivity.class);
        myIntent.putExtra("ROUTEKEY", listItem_NewRoute.getKey());
        startActivityForResult(myIntent, 0);
	}
		
	public void doEndRoute(View view)
	{    	
		endRoute();
	}
		
	@Override
	protected void onActivityResult(int requestCode,
							int resultCode, Intent data) 
	{
		UpdateTripHistory();
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
    private class ListAdapter extends ArrayAdapter 
    { //--CloneChangeRequired
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
							Intent myIntent = new Intent(MainActivity.this, RouteInfoActivity.class);
                            myIntent.putExtra("ROUTEKEY", listItem.getKey());
                            startActivityForResult(myIntent, 0);
                        }
                    });
                }
            } catch (Exception e) {
               // Log.i(Splash.ListAdapter.class.toString(), e.getMessage());
            }
            return view;
        }
    }	   
} /* End of MainActivity() */

