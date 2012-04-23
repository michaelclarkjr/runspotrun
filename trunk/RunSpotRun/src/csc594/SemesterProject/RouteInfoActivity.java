package csc594.SemesterProject;

import java.util.ArrayList;
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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class RouteInfoActivity extends Activity {

	public static final int MENU_SETTINGS = Menu.FIRST+1;
	public static final int MENU_ABOUT = Menu.FIRST+2;
	
	private int routeKeyDB;
	private RouteItem route;
	
	private TextView tvDate, tvTime, tvDistance, tvSpeed;
	
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
        setContentView(R.layout.routeinfo); 
        
        tvDate = (TextView) findViewById(R.id.tvRouteDate);
        tvTime = (TextView) findViewById(R.id.tvRouteDuration);
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
//		ArrayList<MyGeoPoint> route = new ArrayList<MyGeoPoint>();
//		route.add(new MyGeoPoint(39313623,	-84282732));
//		route.add(new MyGeoPoint(39313847,	-84283859));
//		route.add(new MyGeoPoint(39314694,	-84284137));
//		route.add(new MyGeoPoint(39315831,	-84281509));
//		route.add(new MyGeoPoint(39318919,	-84279041));
//		route.add(new MyGeoPoint(39321500,	-84273033));
//		route.add(new MyGeoPoint(39319724,	-84271874));
//		route.add(new MyGeoPoint(39314188,	-84277571));
		 
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
			//return;
		}
		String subject = "Some Route Subject";
		StringBuilder sb = new StringBuilder();
		sb.append("Route info here!\n");
		sb.append("More route info here!\n");
		
		Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:" + email + 
				"?subject=" + Uri.encode(subject) +  
				"&body=" + Uri.encode(sb.toString())));
		startActivity(intent);
			
//easy way but shows other intents that are NOT just email.
//		Intent intent = new Intent(Intent.ACTION_SEND);
//		intent.setType("text/plain");
//		intent.putExtra(Intent.EXTRA_EMAIL  , new String[]{email});
//		intent.putExtra(Intent.EXTRA_SUBJECT, "subject of email");
//		intent.putExtra(Intent.EXTRA_TEXT   , "body of email");
//		startActivity(Intent.createChooser(intent, "Send mail..."));
		
		} catch (Exception ex) {					
			new AlertDialog.Builder(this)
	  		  .setTitle("Could not get settings")
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
					//DatabaseHelper.DeleteRoute(routeKeyDB)
					
					//return
					finish();
				}
		  })
		  .setNegativeButton("NO", null)
		  .show();
	}
}
