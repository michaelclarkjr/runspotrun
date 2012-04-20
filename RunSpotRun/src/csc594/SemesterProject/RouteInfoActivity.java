package csc594.SemesterProject;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

public class RouteInfoActivity extends Activity {

	public static final int MENU_SETTINGS = Menu.FIRST+1;
	public static final int MENU_ABOUT = Menu.FIRST+2;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
        setContentView(R.layout.routeinfo); 
	    //get info from intent
	    Intent i = getIntent();
	    
	    //update screen
	    
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
		ArrayList<MyGeoPoint> route = new ArrayList<MyGeoPoint>();
		route.add(new MyGeoPoint(39313623,	-84282732));
		route.add(new MyGeoPoint(39313847,	-84283859));
		route.add(new MyGeoPoint(39314694,	-84284137));
		route.add(new MyGeoPoint(39315831,	-84281509));
		route.add(new MyGeoPoint(39318919,	-84279041));
		route.add(new MyGeoPoint(39321500,	-84273033));
		route.add(new MyGeoPoint(39319724,	-84271874));
		route.add(new MyGeoPoint(39314188,	-84277571));
		 
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
		
		if(email.isEmpty() || !email.contains("@"))
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
		
		//remove route from DB and return
		
		//return
		finish();
	}
}
