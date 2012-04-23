package csc594.SemesterProject;

import csc594.SemesterProject.MainActivity.ItemBO;
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
		db.execSQL("CREATE TABLE Point (_id INTEGER PRIMARY KEY ASC, FOREIGN KEY(RouteID) REFERENCES Route(_id), Latitude REAL, Longitude REAL, Time TEXT, Distance REAL);");
		
		
		/* Test Data */
		ContentValues cv = new ContentValues();
 
        cv.put("Name", "Item1");
		cv.put("Date", "2012-04-01");
		cv.put("StartTime", "12:11:00");
		cv.put("EndTime", "12:21:00");
		db.insert("Route", "Name", cv);
		
		cv.put("RouteID", "1");
		cv.put("Latitude", "39312718");
		cv.put("Longitutde", "-84281230");
		cv.put("Time", "12:11:00");
		cv.put("Distance", "0.7");
		db.insert("Point", "Name", cv);
		
		cv.put("RouteID", "1");
		cv.put("Latitude", "39314188");
		cv.put("Longitutde", "-84277571");
		cv.put("Time", "12:16:00");
		cv.put("Distance", "0.7");
		db.insert("Point", "Name", cv);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) 
	{
		// TODO Auto-generated method stub
	}

}
