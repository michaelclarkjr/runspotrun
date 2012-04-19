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
//import android.widget.Toast;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener
{
	private ListView listview;
    private ArrayList mListItem;

    
	//private static final int GET_ROUTE = 1010;
	ArrayList<MyGeoPoint> route;

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

    }    
    
    public void doStartRoute(View view)
    {    	
    	Intent startTracking = new Intent(this, TrackRouteActivity.class);
    	
    	//String Text = "Started Tracking Your Route";
    		//Toast.makeText( getApplicationContext(),Text,Toast.LENGTH_SHORT).show();
	
		//startActivityForResult(startTracking,GET_ROUTE);
    	startActivity(startTracking);
    }
    
   /* public void doEndRoute(View view) //temporarily.. moved to TrackRoute
    {    	
		finishActivity(GET_ROUTE); 
    	
    	String Text = "Stopped Tracking Your Route";
    		Toast.makeText( getApplicationContext(),Text,Toast.LENGTH_SHORT).show();
    }*/
    
	/*@Override
	protected void onActivityResult(int requestCode,
							int resultCode, Intent data) 
	{
		if (requestCode == GET_ROUTE && resultCode == RESULT_OK) 
		{
			//get route data
		}
		else if (resultCode == RESULT_OK) 
		{
			//do nothing
		}
		super.onActivityResult(requestCode, resultCode, data);
	}*/
    
       
    
    
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
                            Intent myIntent = new Intent(MainActivity.this, TrackRouteActivity.class);
                            myIntent.putExtra("NAME", listItem.getName());
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
