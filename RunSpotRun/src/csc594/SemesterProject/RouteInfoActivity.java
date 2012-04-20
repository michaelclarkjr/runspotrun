package csc594.SemesterProject;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;

public class RouteInfoActivity extends Activity {

	public static final int MENU_SETTINGS = Menu.FIRST+1;
	
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
	}
	
	public void doDeleteClick(View button)
	{
		//are you sure?
		
		//remove route from DB and return
		
		//return
		finish();
	}
}
