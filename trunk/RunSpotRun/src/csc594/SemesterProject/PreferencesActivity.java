package csc594.SemesterProject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

public class PreferencesActivity extends PreferenceActivity implements OnSharedPreferenceChangeListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preferences);

//        Preference myPref = (Preference) findPreference("interval");
//        myPref.setOnPreferenceClickListener(new OnPreferenceClickListener() {
//                     public boolean onPreferenceClick(Preference preference) {
////                    		Toast
////              			  .makeText(PreferencesActivity.this, "hello",Toast.LENGTH_LONG)
////              			  .show();
//                    	 return true;
//                     }
//                 });
        
//    	SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);//getApplicationContext());
//    	//int LineColor = Color.RED;
//    	int LineWidth = sharedPrefs.getInt("linewidth", 2);
//    	Toast
//		.makeText(this, String.format("%d setting", LineWidth),Toast.LENGTH_LONG)
//  		  .show();
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        // Set up a listener whenever a key changes
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Unregister the listener whenever a key changes
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) 
    {
    	try
    	{
//    	if(key.equals(getString(R.string.intervalKey)))
//    	{
//    		int newInterval = sharedPreferences.getInt(getString(R.string.intervalKey), 1);
//    		Toast
//    		.makeText(this, String.format("%d setting", newInterval),Toast.LENGTH_LONG)
//	  		  .show();
//    	}
//    	else if(key.equals(R.string.linecolorKey))
//    	{
//    		String newLineColor = sharedPreferences.getString(getString(R.string.linecolorKey), "Red");   
//    		Toast
//    		.makeText(this, String.format("%s setting", newLineColor),Toast.LENGTH_LONG)
//	  		  .show();
//    	}
//    	else if(key.equals(R.string.linewidthKey))
//    	{
//    		int newLineWidth = sharedPreferences.getInt(getString(R.string.linewidthKey), 2);
//    		Toast
//    		  .makeText(this, String.format("%d setting", newLineWidth),Toast.LENGTH_LONG)
//	  		  .show();
//    	}
//    	else if(key.equals(R.string.emailKey))
//    	{
//    		String newEmail = sharedPreferences.getString(getString(R.string.emailKey), "");
//    		Toast
//    		.makeText(this, String.format("%s setting", newEmail),Toast.LENGTH_LONG)
//	  		  .show();
//    	}
//    	else
//    	{ return; }//do nothing
    	} catch (Exception ex) {					
			new AlertDialog.Builder(this)
	  		  .setTitle("Could not add item to Database")
	  		  .setMessage(String.format("%s", ex.getMessage()))
	  		  .setNeutralButton("OK", null)
	  		  .show();
			return;
		}
    }
}