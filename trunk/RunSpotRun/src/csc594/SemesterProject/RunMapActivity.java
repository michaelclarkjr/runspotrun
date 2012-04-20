package csc594.SemesterProject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.RectF;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.preference.PreferenceActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.Projection;

public class RunMapActivity extends MapActivity 
{
	public static final int MENU_SETTINGS = Menu.FIRST+1;
	
	MapView mapView;
	private List<Overlay> mapOverlays;
	private Projection projection; 
	
	ArrayList<MyGeoPoint> route;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.mapmain);
        try 
        {
        	//setup map
			mapView = (MapView) findViewById(R.id.mapView);
			mapView.setBuiltInZoomControls(true); //seems to do same thing as above
			//mapView.setSatellite(true);
			
			//get route from calling intent
			ArrayList<MyGeoPoint> route =  getIntent().getParcelableArrayListExtra("Route");					
			
			mapOverlays = mapView.getOverlays();        
			projection = mapView.getProjection();
			mapOverlays.add(new MyOverlay(route));  
			    
			MapController mapController = mapView.getController();
//			mapController.zoomToSpan(route.get(0).getLatitudeE6(), route.get(4).getLongitudeE6());
			mapController.setCenter(route.get(0).getPoint());
			mapController.setZoom(15);
        	
		} catch (Exception e) {		
			new AlertDialog.Builder(this)
	  		  .setTitle("Invalid Entry")
	  		  .setMessage(String.format("RunMapActivity onCreate() %s", e.getMessage()))
	  		  .setNeutralButton("OK", null)
	  		  .show();
		}
    }
    
    public void myClickHandler(View target) {
        switch(target.getId()) {
        case R.id.sat:
            mapView.setSatellite(true);
            break;
        case R.id.street:
            mapView.setStreetView(true);
            break;
        case R.id.traffic:
            mapView.setTraffic(true);
            break;
        case R.id.normal:
            mapView.setSatellite(false);
            mapView.setStreetView(false);
            mapView.setTraffic(false);
            break;
        default:
        	break;
        }

        // The following line should not be required but it is,
        // at least up til Froyo.
        //mapView.postInvalidateDelayed(2000);        
    }
    
    @Override
    protected boolean isRouteDisplayed() {
        return false;
    }
    
  //whether or not the current
    //device location is being displayed
  /*  @Override
    protected boolean isLocationDisplayed() {
    	//return true; 
    	return currentLoc.isMyLocationEnabled();
    }*/
    
    //Testing
    private void showLocationData(Location loc)
    {
    	Toast.makeText(getBaseContext(),
            "New location latitude [" + 
            loc.getLatitude() +
            "] longitude [" + 
            loc.getLongitude()+"]" + "Time [" 
            + loc.getTime() + "]",
            Toast.LENGTH_SHORT).show();
    }
    
    @Override
    public void onResume()
    {
        super.onResume();
//        currentLoc.enableMyLocation();
//        currentLoc.runOnFirstFix(new Runnable() {
//            public void run() {
//                mapController.setCenter(currentLoc.getMyLocation());
//            }								//this returns a GeoPoint
//        });
    }

    
    @Override
    public void onPause()
    {
        super.onPause();
//        currentLoc.disableMyLocation();
    }
    
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu
		.add(Menu.NONE, MENU_SETTINGS, 0, "Preferences")
		.setIntent(new Intent(this, PreferencesActivity.class))		
		.setIcon(R.drawable.settings);
		
		return(super.onCreateOptionsMenu(menu));
	}
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
////        MenuInflater inflater = getMenuInflater();
////        inflater.inflate(R.layout.mapsmenu, menu);
////        return true;
//    	menu
//		.add(Menu.NONE, Menu.FIRST+2, 1, "Reset");
//		//.setIcon(R.drawable.ic_menu_refresh);
//
//		menu
//			.add(Menu.NONE, Menu.FIRST+1, 0, "Add");
//			//.setIcon(R.drawable.ic_menu_add);
//
//		return(super.onCreateOptionsMenu(menu));
//    }

class MyOverlay extends Overlay
{		
	//SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);//getApplicationContext());
	int LineColor = Color.RED;
	int LineWidth = 2;//sharedPrefs.getInt(getString(R.string.linewidthKey), 2);
			
	//sharedPrefs.getInt(getString(R.));
	
	//list of points to display
	List<MyGeoPoint> points;
	//last point selected by user tap
	private MyGeoPoint mSelectedMapLocation; 
	//Paint objects to draw information 
	private Paint mInnerPaint, mBorderPaint, mTextPaint;
	 
	//Constructor
	//Copy points into Overlay, this is what is drawn
    public MyOverlay(List<MyGeoPoint> route)
    {
    	this.points = route;
    	try
    	{
    	//SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplication());//getApplicationContext());
    	//int LineColor = Color.RED;
    	//LineWidth = sharedPrefs.getInt("linewidth", 2);
    	} catch (Exception ex) {					
			new AlertDialog.Builder(RunMapActivity.this)
	  		  .setTitle("Could not add item to Database")
	  		  .setMessage(String.format("%s", ex.getMessage()))
	  		  .setNeutralButton("OK", null)
	  		  .show();
			return;
		}
    	
    }   

    //Draw method - fired when drawing needs to change
    public void draw(Canvas canvas, MapView mapv, boolean shadow){
        super.draw(canvas, mapv, shadow);

        //not shadowing so lets just return
        if(shadow == true){ return; }
        
        Paint mPaint = new Paint();
        mPaint.setDither(true);
        mPaint.setColor(LineColor);
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(LineWidth);

        Point p1 = new Point();
        Point p2 = new Point();
        Path path = new Path();
        
        for(int i = 0; i< points.size() -1; i++)
        {
	        projection.toPixels(points.get(i).getPoint(), p1);
	        projection.toPixels(points.get(i+1).getPoint(), p2);       
	
	        path.moveTo(p1.x,p1.y);
	        path.lineTo(p2.x,p2.y);
	        
	        //draw start/stop markers
	        if(i==0)
	        {//start marker
	        	//offset into bitmp
		        int xOffset = 16;
		        int yOffset = 32;
	            //---add the marker---
	            Bitmap bmp = BitmapFactory.decodeResource(
	                getResources(), R.drawable.start_marker);            
	            canvas.drawBitmap(bmp, p1.x - xOffset, p1.y - yOffset, null); 
	        }
	        else if(i==points.size() - 2)
	        {//stop marker
	        	//offset into bitmp
		        int xOffset = 9;
		        int yOffset = 32;
	            //---add the marker---
	            Bitmap bmp = BitmapFactory.decodeResource(
	                getResources(), R.drawable.stop_marker);        
	            canvas.drawBitmap(bmp, p2.x - xOffset, p2.y - yOffset, null); 
	        }
        }
        //this will draw the points to the screen
        canvas.drawPath(path, mPaint);
        
        //this will draw information box over point if user has tapped on one
        drawInfoWindow(canvas, mapv, shadow);
    }
    
    @Override
    public boolean onTap(GeoPoint p, MapView mapView)  
    {      
    	//copy current point to field so draw method can use it next time it draw
    	mSelectedMapLocation = getHitMapLocation(mapView,p);
		
    	//return if it was handled
		return mSelectedMapLocation != null;
    }
    
    private MyGeoPoint getHitMapLocation(MapView mapView, GeoPoint tapPoint) 
    {    	
    	MyGeoPoint hitMapLocation = null;
		
    	RectF hitTestRecr = new RectF();
		Point screenCoords = new Point();
    	Iterator<MyGeoPoint> iterator = points.iterator();
    	while(iterator.hasNext()) {
    		MyGeoPoint testLocation = iterator.next();
    		
    		mapView.getProjection().toPixels(testLocation.getPoint(), screenCoords);

	    	// Create a testing Rectangle with the size and coordinates of our icon
	    	// Set the testing Rectangle with the size and coordinates of our on screen icon
    		int limit = 32;
    		hitTestRecr.set(-limit,-limit,limit,limit);
    		hitTestRecr.offset(screenCoords.x,screenCoords.y);

	    	//  At last test for a match between our Rectangle and the location clicked by the user
    		mapView.getProjection().toPixels(tapPoint, screenCoords);
    		if (hitTestRecr.contains(screenCoords.x,screenCoords.y)) {
    			hitMapLocation = testLocation;
    			break;
    		}
    	}
    	
    	//  Finally clear the new MouseSelection as its process finished
    	tapPoint = null;
    	
    	return hitMapLocation; 
    }
    
    private void drawInfoWindow(Canvas canvas, MapView	mapView, boolean shadow) {
    	if(mSelectedMapLocation==null){ return; }
		Point selDestinationOffset = new Point();
		mapView.getProjection().toPixels(mSelectedMapLocation.getPoint() , selDestinationOffset);
    	
    	//  Setup the info window - hardcoded... TODO: dynamic height/with
		int INFO_WINDOW_WIDTH = 175;
		int INFO_WINDOW_HEIGHT = 75;
		RectF infoWindowRect = new RectF(0,0,INFO_WINDOW_WIDTH,INFO_WINDOW_HEIGHT);				
		int infoWindowOffsetX = selDestinationOffset.x-INFO_WINDOW_WIDTH/2;
		int infoWindowOffsetY = selDestinationOffset.y-INFO_WINDOW_HEIGHT-32;
		infoWindowRect.offset(infoWindowOffsetX,infoWindowOffsetY);

		//  Drawing the inner info window
		canvas.drawRoundRect(infoWindowRect, 5, 5, getmInnerPaint());
		
		//  Drawing the border for info window
		canvas.drawRoundRect(infoWindowRect, 5, 5, getmBorderPaint());
			
		//  Draw the MapLocation's name
		int TEXT_OFFSET_X = 10;
		int TEXT_OFFSET_Y = 15;
		canvas.drawText(mSelectedMapLocation.getTypeAsString(),infoWindowOffsetX+TEXT_OFFSET_X,infoWindowOffsetY+TEXT_OFFSET_Y,getmTextPaint());
		
		TEXT_OFFSET_Y += 15;
		canvas.drawText(mSelectedMapLocation.getPointAsString(),infoWindowOffsetX+TEXT_OFFSET_X,infoWindowOffsetY+TEXT_OFFSET_Y,getmTextPaint());
		TEXT_OFFSET_Y += 15;
		canvas.drawText(mSelectedMapLocation.getTimeAsString(),infoWindowOffsetX+TEXT_OFFSET_X,infoWindowOffsetY+TEXT_OFFSET_Y,getmTextPaint());
		TEXT_OFFSET_Y += 15;
		canvas.drawText(mSelectedMapLocation.getDistanceAsString(),infoWindowOffsetX+TEXT_OFFSET_X,infoWindowOffsetY+TEXT_OFFSET_Y,getmTextPaint());
    }
	public Paint getmInnerPaint() {
		if ( mInnerPaint == null) {
			mInnerPaint = new Paint();
			mInnerPaint.setARGB(225, 50, 50, 50); //inner color
			mInnerPaint.setAntiAlias(true);
		}
		return mInnerPaint;
	}

	public Paint getmBorderPaint() {
		if ( mBorderPaint == null) {
			mBorderPaint = new Paint();
			mBorderPaint.setARGB(255, 255, 255, 255);
			mBorderPaint.setAntiAlias(true);
			mBorderPaint.setStyle(Style.STROKE);
			mBorderPaint.setStrokeWidth(2);
		}
		return mBorderPaint;
	}

	public Paint getmTextPaint() {
		if ( mTextPaint == null) {
			mTextPaint = new Paint();
			mTextPaint.setARGB(255, 255, 255, 255);
			//mTextPaint.
			mTextPaint.setAntiAlias(true);
		}
		return mTextPaint;
	}
}

}