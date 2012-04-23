package csc594.SemesterProject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
		db.execSQL("CREATE TABLE Route (_id INTEGER PRIMARY KEY ASC, Name TEXT, Date TEXT, StartTime TEXT, EndTime TEXT);");
		db.execSQL("CREATE TABLE Point (_id INTEGER PRIMARY KEY ASC, FOREIGN KEY(RouteID) REFERENCES Route(_id), Latitude INTEGER, Longitude INTEGER, Time TEXT, Distance REAL);");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) 
	{
		// TODO Auto-generated method stub
	}
	
	static ArrayList<RouteItem> GetRoutes()
	{
		//returns all routes
		return new ArrayList<RouteItem>();
	}

	static RouteItem GetRoute(int routeKey)
	{
		//return a single route for given route key
		return new RouteItem();
	}

	static ArrayList<MyGeoPoint> GetPoints(int routeKey)
	{
		//return all points for given route key
		//<first>start, normal, <last>stop
		
		return new ArrayList<MyGeoPoint>();
	}

	static void AddAPoint(MyGeoPoint point, int routeKey)
	{
		//adds a point for given route key
	}

	static int StartRoute(Date startTime)
	{
		//adds a new route, returns the key
		return 0;
	}
}
