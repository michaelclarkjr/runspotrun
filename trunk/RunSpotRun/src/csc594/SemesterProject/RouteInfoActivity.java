package csc594.SemesterProject;

import java.util.ArrayList;

import com.google.android.maps.GeoPoint;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class RouteInfoActivity extends Activity {

	public static final int MENU_SETTINGS = Menu.FIRST+1;
	public static final int MENU_ABOUT = Menu.FIRST+2;
	
	private int routeKeyDB;
	private RouteItem route;
	
	private TextView tvDate, tvTime, tvDuration, tvDistance, tvSpeed;	
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
        setContentView(R.layout.routeinfo); 
        
        tvDate = (TextView) findViewById(R.id.tvRouteDate);
        tvTime = (TextView) findViewById(R.id.tvRouteTime);
        tvDuration = (TextView) findViewById(R.id.tvRouteDuration);
        tvDistance = (TextView) findViewById(R.id.tvRouteDistance);
        tvSpeed = (TextView) findViewById(R.id.tvRouteAvgSpeed);
        
	    //get info from intent
	    Intent i = getIntent();
	    routeKeyDB = i.getIntExtra("ROUTEKEY", 0);
	    	   
	    //get database info
	    route =  MainActivity.DataBase.GetRoute(routeKeyDB);
	    
	    //update screen
	    tvDate.setText(route.getDate());
	    tvTime.setText(route.getTime());
	    tvDistance.setText(route.getDistance().toString());
	    
	    //calculate Average speed
	    tvSpeed.setText("");
	    
	    //calculate Duration (can be grabbed from the timer..)
	    tvDuration.setText("");
	}
	
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
	
	public void doMapIt(View button)
	{		 
		ArrayList<MyGeoPoint> route = MainActivity.DataBase.GetPoints(routeKeyDB);
		Intent launchMap = new Intent(this, RunMapActivity.class);
		launchMap.putExtra("Route", route);
		startActivity(launchMap);	
	
	}
	public void doEmailClick(View button)
	{
		//gather route info and email 
		try
		{
			SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(RouteInfoActivity.this);
			String email = sharedPrefs.getString("emailKey", "");
			
			if(email.length() == 0 || !email.contains("@")) //isEmpty() fails on my phone and 2.2.1
			{
				Toast
				.makeText(this, "You can add a valid default email the Menu | Settings", Toast.LENGTH_LONG)
				.show();
				//return;//dont return just keep going
			}
			String subject = "Jogging route from " + tvDate.getText().toString();
			StringBuilder sb = new StringBuilder();
			ArrayList<MyGeoPoint> route = MainActivity.DataBase.GetPoints(routeKeyDB);
			
			GeoPoint point;
			String date, time, distance;
			
			sb.append("Route info:\n");
			for(int i=0;i<route.size(); i++)
			{				
				point = route.get(i).getPoint();
				time = route.get(i).getTime();
				date = "date";// route.get(i).getDate();				
				distance = route.get(i).getDistance();
				sb.append(String.format("%s %s %s %s\n", point.toString(), time, date, distance));
			}
			
			Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:" + email + 
					"?subject=" + Uri.encode(subject) +  
					"&body=" + Uri.encode(sb.toString())));
			startActivity(intent);
		
		} catch (Exception ex) {					
			new AlertDialog.Builder(this)
	  		  .setTitle("Could not send email")
	  		  .setMessage(String.format("%s", ex.getMessage()))
	  		  .setNeutralButton("OK", null)
	  		  .show();
			return;
		}				
	}
	
	public void doDeleteClick(View button)
	{
		//are you sure?
		new AlertDialog.Builder(this)
		  .setTitle("DELETE?")
		  .setMessage("Are you sure you want to delete this route?")
		  .setPositiveButton("Yes", new DialogInterface.OnClickListener() 
		  {
				public void onClick(DialogInterface dialog, int whichButton) 
				{
					//remove route from DB and return
					MainActivity.DataBase.DeleteRoute(routeKeyDB);
					
					Toast
					.makeText(RouteInfoActivity.this, "Route Deleted.", Toast.LENGTH_LONG)
					.show();
					
					//return
					finish();
				}
		  })
		  .setNegativeButton("NO", null)
		  .show();
	}
}
