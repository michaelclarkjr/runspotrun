package csc594.SemesterProject;

import java.util.ArrayList;
import java.util.List;

import com.google.android.maps.*;
import com.google.android.maps.MapView.LayoutParams;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

public class RunMapActivity extends MapActivity 
{
	MapView mapView;
	private List<Overlay> mapOverlays;
	private Projection projection; 
	
	List<GeoPoint> route;
//	LocationManager locMgr;
//	//LocationListener locListener = null;
//	MapController mapController;
//	MyLocationOverlay currentLoc;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.mapmain);
        try {
			mapView = (MapView) findViewById(R.id.mapView);
			mapView.setBuiltInZoomControls(true); //seems to do same thing as above
			//mapView.setSatellite(true);
			
			route = new ArrayList<GeoPoint>();
			route.add(new GeoPoint(39312718,	-84281230));
			route.add(new GeoPoint(39313623,	-84282732));
			route.add(new GeoPoint(39313847,	-84283859));
			route.add(new GeoPoint(39314694,	-84284137));
			route.add(new GeoPoint(39315831,	-84281509));
			route.add(new GeoPoint(39318919,	-84279041));
			route.add(new GeoPoint(39321500,	-84273033));
			route.add(new GeoPoint(39319724,	-84271874));
			route.add(new GeoPoint(39314188,	-84277571));
			route.add(new GeoPoint(39312594,	-84280490));			
			
			mapOverlays = mapView.getOverlays();        
			projection = mapView.getProjection();
			mapOverlays.add(new MyOverlay(route));  
			    
			MapController mapController = mapView.getController();
//			mapController.zoomToSpan(route.get(0).getLatitudeE6(), route.get(4).getLongitudeE6());
			mapController.setCenter(route.get(0));
			mapController.setZoom(14);
			
           /* LocationManager locMgr = (LocationManager)
			this.getSystemService(Context.LOCATION_SERVICE);
			Location loc =
				locMgr.getLastKnownLocation(LocationManager.GPS_PROVIDER);
							//this returns Location object
			showLocationData(loc);
			
			mapController = mapView.getController();
			mapController.setZoom(15);

			currentLoc = new MyLocationOverlay(this, mapView);
			mapView.getOverlays().add(currentLoc);
			mapView.postInvalidate();*/
        	
		} catch (Exception e) {		
			new AlertDialog.Builder(this)
	  		  .setTitle("Invalid Entry")
	  		  .setMessage(String.format("setup %s", e.getMessage()))
	  		  .setNeutralButton("OK", null)
	  		  .show();
		}
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
	

class MyOverlay extends Overlay{

	//private List<Overlay> mapOverlays;
	//private Projection projection; 
	List<GeoPoint> points;
	
    public MyOverlay(List<GeoPoint> route){
    	this.points = route;
    }   

    public void draw(Canvas canvas, MapView mapv, boolean shadow){
        super.draw(canvas, mapv, shadow);

        if(shadow == true){return;}
        
        Paint   mPaint = new Paint();
        mPaint.setDither(true);
        mPaint.setColor(Color.RED);
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(2);

        GeoPoint gP1 = new GeoPoint(19240000,-99120000);
        GeoPoint gP2 = new GeoPoint(37423157, -122085008);

        Point p1 = new Point();
        Point p2 = new Point();
        Path path = new Path();

//        projection.toPixels(gP1, p1);
//        projection.toPixels(gP2, p2);       
//
//        path.moveTo(p2.x, p2.y);
//        path.lineTo(p1.x,p1.y);
//
//        canvas.drawPath(path, mPaint);
        
        for(int i = 0; i< points.size() -1; i++)
        {
	        projection.toPixels(points.get(i), p1);
	        projection.toPixels(points.get(i+1), p2);       
	
	        path.moveTo(p1.x,p1.y);
	        path.lineTo(p2.x,p2.y);
	
	      
        }
        canvas.drawPath(path, mPaint);
    }
}
}