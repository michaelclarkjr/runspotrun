package csc594.SemesterProject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

public class MainActivity extends Activity {

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);        
    }
    
    public void doGoToMap(View view)
    {    	
    	Intent launchGetRank = new Intent(this, RunMapActivity.class);
		startActivity(launchGetRank);
    }
    
	@Override
	protected void onActivityResult(int requestCode,
							int resultCode, Intent data) 
	{
		if (resultCode == RESULT_OK) 
		{
			//fromGetRank = data.getExtras().getString("returnStr");
			//etRank.setText(fromGetRank);
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
}
