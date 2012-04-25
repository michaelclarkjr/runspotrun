package csc594.SemesterProject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.android.maps.GeoPoint;

import csc594.SemesterProject.MyGeoPoint.MyPointType;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.ContentValues;
import android.content.Context;

public class DatabaseHelper extends SQLiteOpenHelper 
{
	private static final String DATABASE_NAME="geobase";
	Context c;
	
	public DatabaseHelper(Context context)
	{
		super(context, DATABASE_NAME, null, 1);
		c = context;
	}

	@Override
	public void onCreate(SQLiteDatabase db) 
	{
		try
		{
			db.execSQL("CREATE TABLE Route (_id INTEGER PRIMARY KEY AUTOINCREMENT, Name TEXT, Date TEXT, StartTime TEXT, EndTime TEXT, Distance TEXT);");
			db.execSQL("CREATE TABLE Point (_id INTEGER PRIMARY KEY AUTOINCREMENT, RouteID INTEGER, Latitude INTEGER, Longitude INTEGER, Time TEXT, Distance TEXT);");
		}
		catch (Exception ex)
		{
			System.out.println(ex.toString());
		}
	}
	
	public static void ForceCreate(SQLiteDatabase db)
	{
		try
		{
			db.execSQL("CREATE TABLE Route (_id INTEGER PRIMARY KEY AUTOINCREMENT, Name TEXT, Date TEXT, StartTime TEXT, EndTime TEXT, Distance TEXT);");
            db.execSQL("CREATE TABLE Point (_id INTEGER PRIMARY KEY AUTOINCREMENT, RouteID INTEGER, Latitude INTEGER, Longitude INTEGER, Time TEXT, Distance TEXT);");
            System.out.print("onCreate Force DB");
		}
		catch (Exception ex)
		{
			System.out.print("onCreate Force DB error ");
			System.out.println(ex.toString());
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) 
	{
		// TO DO Auto-generated method stub
	}
	
	ArrayList<RouteItem> GetRoutes()
	{
		//returns all routes
		
		ArrayList<RouteItem> routes = new ArrayList<RouteItem>();
		
		String SQL = "SELECT * FROM Route ORDER BY Date, StartTime DESC";
        Cursor cur = this.getReadableDatabase().rawQuery(SQL, null);
        
        if (cur.moveToFirst())
        {        
	        while (!cur.isAfterLast())
	        {	        	
	        	routes.add(SetRouteFromCursor(cur));
	        	
	        	cur.moveToNext();
	        }
	        
	        cur.close();
			return routes;
        }
        else
        {	
        	
        	cur.close();
        	return new ArrayList<RouteItem>();
        }	
	}

	RouteItem GetRoute(int routeKey)
	{
		//return a single route for given route key	
		
		String SQL = "SELECT * FROM Route WHERE _id = ?";
        Cursor cur = this.getReadableDatabase().rawQuery(SQL, new String[] { Integer.toString(routeKey) });
        
        if (cur.moveToFirst())
        {
        	//cur.close(); //don't close here since it is being passed..
        	//return SetRouteFromCursor(cur); //still error, was never getting close from this method
        	RouteItem route = SetRouteFromCursor(cur);
        	cur.close(); 
        	return route;
        }
        else
        {
        	cur.close();
        	return new RouteItem();
        }
    }

	ArrayList<MyGeoPoint> GetPoints(int routeKey)
	{
		//return all points for given route key
		//<first>start, normal, <last>stop
		
		ArrayList<MyGeoPoint> points = new ArrayList<MyGeoPoint>();
		
		String SQL = "SELECT * FROM Point WHERE RouteID = ?";
        Cursor cur = this.getReadableDatabase().rawQuery(SQL, new String[] { Integer.toString(routeKey) });
        int count = 0;
        boolean cont = true;
        if (cur.moveToFirst())
        {        
	        //while (!cur.isAfterLast())
        	while(cont)
	        {
	        	
	        	points.add(SetPointFromCursor(cur, "Point" + Integer.toString(count)));
	        	
	        	cont = cur.moveToNext();
	        	count++;
	        }
	        
	        cur.close();
			return points;
        }
        else
        {	
        	cur.close();
        	return new ArrayList<MyGeoPoint>();
        }
	}

	long AddPoint(MyGeoPoint point, int routeKey)
	{
		//adds a point for given route key
		
		ContentValues cv = new ContentValues();
		
		cv.put("RouteID", routeKey);
		cv.put("Latitude", point.getPoint().getLatitudeE6());
		cv.put("Longitude", point.getPoint().getLongitudeE6());
		cv.put("Time", point.getTime());
		cv.put("Distance", point.getDistance());
		
		String distance = point.getDistance();//UpdateDistance(point.getDistance(), routeKey);
		
		UpdateRoute(distance, point.getTime(), routeKey);
		
		return this.getWritableDatabase().insert("Point", "RouteID", cv);
	}

	long AddRoute(RouteItem route)
	{
		//adds a new route, returns the key
		
		ContentValues cv = new ContentValues();
		
		cv.put("Name", route.getName());
		cv.put("Date", route.getDate());
		cv.put("StartTime", route.getTime());
		cv.putNull("EndTime");
		cv.put("Distance", 0);
		return this.getWritableDatabase().insert("Route", "Name", cv);
	}
	
	int DeleteRoute(int routeKey)
	{
		//deletes a single route for given route key
		
		return this.getWritableDatabase().delete("Route", "_id = ?", new String[] { Integer.toString(routeKey) });
	}
	
	private void UpdateRoute(String distance, String endTime, int routeKey)
	{
		ContentValues cv = new ContentValues();
	    cv.put("Distance", distance);
	    cv.put("EndTime", endTime);
	    
	    this.getWritableDatabase().update("Route", cv, "_id = ?", new String[] { Integer.toString(routeKey) });
	}
	
	private RouteItem SetRouteFromCursor(Cursor cur)
	{
		RouteItem route = new RouteItem();
		
		route.setKey((int)cur.getLong(cur.getColumnIndex("_id")));
    	route.setName(cur.getString(cur.getColumnIndex("Name")));
    	route.setDate(cur.getString(cur.getColumnIndex("Date")));
    	route.setTime(cur.getString(cur.getColumnIndex("StartTime")));
    	route.setDistance((int)cur.getLong(cur.getColumnIndex("Distance"))); 
    	
    	//cur.close(); //not close here since this same cursor is continue to be used in GetRoutes()
    	return route;
	}
	
	private MyGeoPoint SetPointFromCursor(Cursor cur, String name)
	{
		int latitude = (int)cur.getLong(cur.getColumnIndex("Latitude"));
		int longitude = (int)cur.getLong(cur.getColumnIndex("Longitude"));
		String time = cur.getString(cur.getColumnIndex("Time"));
		String distance = Double.toString(cur.getDouble(cur.getColumnIndex("Time")));
		
		MyGeoPoint point = new MyGeoPoint(latitude, longitude, time, distance, name, MyGeoPoint.MyPointType.Normal);
    	
    	return point;
	}

	private String UpdateDistance(String currentDistance, int routeKey)
	{
		double oldDistance = 0;
		String SQL = "SELECT * FROM Point WHERE RouteID = ? ORDER BY Time DESC";
        Cursor cur = this.getReadableDatabase().rawQuery(SQL, new String[] { Integer.toString(routeKey) });
        
        if (cur.moveToFirst())
        {        
	        while (!cur.isAfterLast())
	        {
	        	if (cur.isLast())
	        	{
	        		oldDistance = Double.parseDouble(cur.getString(cur.getColumnIndex("Distance")));
	        	}
	        }
	        
	        cur.close();
			return Double.toString(Double.parseDouble(currentDistance) - oldDistance);
        }
        else
        {	
        	return currentDistance;
        }
	}
}
